// app/src/main/java/app/commands/RegisterCommand.java
package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.AuthService;

public class RegisterCommand implements Command {
    private final AuthService auth;

    public RegisterCommand(AuthService auth) {
        this.auth = auth;
    }

    @Override
    public CommandResponse<String> executeCommand(CommandRequest<?> request) {
        Object raw = request.getPayload();
        if (!(raw instanceof String[] args) || args.length != 2) {
            return new CommandResponse<>(false,
                    "Для register нужно два аргумента: <login> <password>.", null);
        }
        String login = args[0], password = args[1];
        try {
            String token = auth.register(login, password);
            if (token == null) {
                return new CommandResponse<>(false,
                        "Не удалось зарегистрировать (логин занят или неверные данные).", null);
            }
            return new CommandResponse<>(true,
                    "Пользователь '" + login + "' зарегистрирован.",
                    token);
        } catch (Exception e) {
            return new CommandResponse<>(false,
                    "Ошибка при регистрации: " + e.getMessage(), null);
        }
    }

    @Override
    public String getDescription() {
        return "register <login> <password> – зарегистрироваться";
    }
}
