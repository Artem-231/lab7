// src/main/java/app/commands/ExecuteScriptCommand.java
package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.managers.CommandManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ExecuteScriptCommand implements Command {
    private static final Set<String> runningScripts = new HashSet<>();
    private final CommandManager manager;

    public ExecuteScriptCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public CommandResponse<String> executeCommand(CommandRequest<?> request) {
        Object raw = request.getPayload();
        if (!(raw instanceof String fileName)) {
            return new CommandResponse<>(false,
                    "Для execute_script нужен путь к файлу", null);
        }

        if (!runningScripts.add(fileName)) {
            return new CommandResponse<>(false,
                    "Обнаружена рекурсия при исполнении скрипта: " + fileName, null);
        }

        File script = new File(fileName);
        if (!script.exists() || !script.isFile()) {
            runningScripts.remove(fileName);
            return new CommandResponse<>(false,
                    "Файл не найден: " + fileName, null);
        }

        StringBuilder out = new StringBuilder();
        try (Scanner sc = new Scanner(script)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+", 2);
                String cmd = parts[0].toLowerCase();
                Object arg = parts.length > 1 ? parts[1] : null;

                CommandRequest<?> subReq = new CommandRequest<>(
                        cmd,
                        arg,
                        request.getLogin(),
                        request.getRoles()
                );
                var resp = manager.dispatch(subReq);
                out.append(cmd).append(" -> ").append(resp.getMessage()).append("\n");
            }
        } catch (FileNotFoundException e) {
            out.append("Ошибка чтения скрипта: ").append(e.getMessage());
        } finally {
            runningScripts.remove(fileName);
        }

        return new CommandResponse<>(true,
                "Результат выполнения скрипта:\n" + out,
                out.toString());
    }

    @Override
    public String getDescription() {
        return "execute_script <file> – выполнить команды из файла";
    }
}
