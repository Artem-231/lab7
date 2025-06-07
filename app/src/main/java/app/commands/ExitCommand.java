package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;

public class ExitCommand implements Command {
    @Override
    public CommandResponse<Void> executeCommand(CommandRequest<?> request) {
        // Специальный маркер для CommandManager/App
        return new CommandResponse<>(true, "exit", null);
    }

    @Override
    public String getDescription() {
        return "exit – завершить сеанс";
    }
}
