// app/src/main/java/app/commands/LoginCommand.java
package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.AuthService;

public class LoginCommand implements Command {
    private final AuthService auth;

    public LoginCommand(AuthService auth) {
        this.auth = auth;
    }

    @Override
    public CommandResponse<String> executeCommand(CommandRequest<?> request) {
        Object raw = request.getPayload();
        if (!(raw instanceof String[] args) || args.length != 2) {
            return new CommandResponse<>(false,
                    "Для login нужно два аргумента: <login> <password>.", null);
        }
        String login = args[0], password = args[1];
        try {
            String token = auth.login(login, password);
            if (token == null) {
                return new CommandResponse<>(false,
                        "Неверный логин или пароль.", null);
            }
            return new CommandResponse<>(true,
                    "Успешный вход.",
                    token);
        } catch (Exception e) {
            return new CommandResponse<>(false,
                    "Ошибка при логине: " + e.getMessage(), null);
        }
    }

    @Override
    public String getDescription() {
        return "login <login> <password> – войти в систему";
    }
}
