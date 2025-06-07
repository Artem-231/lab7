package app.managers;

import app.commands.*;
import app.service.AuthService;
import app.service.LabWorkService;
import core.dao.UserDao;
import core.enums.Role;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import storage.postgres.PostgresUserDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Управляет всеми серверными командами.
 */
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public Map<String, Command> getCommands() {
        return commands;
    }

    public CommandManager() {
        // 1) Сервисы
        AuthService authService       = new AuthService();
        LabWorkService labWorkService = new LabWorkService();
        // 2) DAO для управления ролями
        UserDao userDao = new PostgresUserDao();

        // 3) Регистрируем команды аутентификации
        commands.put("register",      new RegisterCommand(authService));
        commands.put("login",         new LoginCommand(authService));
        // 4) Регистрируем команды управления ролями
        commands.put("grant_role",    new GrantRoleCommand(userDao));
        commands.put("revoke_role",   new RevokeRoleCommand(userDao));

        // 5) Регистрируем команды работы с LabWork
        commands.put("show",                          new ShowCommand(labWorkService));
        commands.put("add",                           new AddCommand(labWorkService));
        commands.put("update",                        new UpdateCommand(labWorkService));
        commands.put("remove_by_id",                  new RemoveByIdCommand(labWorkService));
        commands.put("add_if_max",                    new AddIfMaxCommand(labWorkService));
        commands.put("clear",                         new ClearCommand(labWorkService));
        commands.put("info",                          new InfoCommand(labWorkService));
        commands.put("min_by_id",                     new MinByIdCommand(labWorkService));
        commands.put("count_less_than_difficulty",    new CountLessThanDifficultyCommand(labWorkService));
        commands.put("average_of_minimal_point",      new AverageOfMinimalPointCommand(labWorkService));
        commands.put("execute_script",                new ExecuteScriptCommand(this));
        commands.put("exit",                          new ExitCommand());
    }

    // app/src/main/java/app/managers/CommandManager.java
    private static final Map<String, Role> MIN_ROLE;
    static {
        Map<String, Role> m = new HashMap<>();
        // читательские команды (любая роль)
        m.put("show",         Role.READER);
        m.put("info",         Role.READER);
        m.put("average_of_minimal_point", Role.READER);
        m.put("count_less_than_difficulty",Role.READER);
        m.put("min_by_id",    Role.READER);

        // редакторские (добавление/изменение/удаление)
        m.put("add",          Role.EDITOR);
        m.put("update",       Role.EDITOR);
        m.put("remove_by_id", Role.EDITOR);
        m.put("add_if_max",   Role.EDITOR);
        m.put("clear",        Role.EDITOR);

        // административные
        m.put("grant_role",   Role.ADMIN);
        m.put("revoke_role",  Role.ADMIN);
        m.put("register",     Role.ADMIN);
        m.put("login",        Role.ADMIN);
        MIN_ROLE = Collections.unmodifiableMap(m);
    }

    /**
     * Dispatch по имени команды (из CommandRequest.getCommandName()).
     */
    @SuppressWarnings("unchecked")
    public <T> CommandResponse<T> dispatch(CommandRequest<T> req) {
        String commandName = req.getCommandName();

        // 1) Разрешаем login и register без проверки прав
        if ("login".equals(commandName) || "register".equals(commandName)) {
            Command cmd = commands.get(commandName);
            if (cmd == null) {
                return new CommandResponse<>(false,
                        "Неизвестная команда: " + commandName,
                        null);
            }
            return (CommandResponse<T>) cmd.executeCommand(req);
        }

        // 2) Для всех остальных команд — проверяем минимальную роль
        Role needed = MIN_ROLE.getOrDefault(commandName, Role.EDITOR);
        Optional<Role> userRole = req.getRoles().stream()
                .map(Role::valueOf)
                .findFirst();

        if (userRole.isEmpty() || userRole.get().compareTo(needed) < 0) {
            return new CommandResponse<>(false,
                    "У вас нет прав для команды " + commandName,
                    null);
        }

        // 3) Выполняем команду
        Command cmd = commands.get(commandName);
        if (cmd == null) {
            return new CommandResponse<>(false,
                    "Неизвестная команда: " + commandName,
                    null);
        }
        return (CommandResponse<T>) cmd.executeCommand(req);
    }

}
