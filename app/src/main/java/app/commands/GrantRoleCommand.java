package app.commands;

import core.dao.UserDao;
import core.enums.Role;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;

public class GrantRoleCommand implements Command {
    private final UserDao userDao;

    public GrantRoleCommand(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public CommandResponse<Void> executeCommand(CommandRequest<?> request) {
        Object raw = request.getPayload();
        if (!(raw instanceof String[] args) || args.length != 2) {
            return new CommandResponse<>(false,
                    "Для grant_role ожидается два аргумента: <login> <role>.",
                    null);
        }

        String login    = args[0];
        String roleName = args[1].toUpperCase();

        if (userDao.findByLogin(login).isEmpty()) {
            return new CommandResponse<>(false,
                    "Пользователь '" + login + "' не найден.",
                    null);
        }

        Role role;
        try {
            role = Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            return new CommandResponse<>(false,
                    "Неизвестная роль: " + roleName,
                    null);
        }

        boolean updated = userDao.updateRole(login, role);
        if (!updated) {
            return new CommandResponse<>(false,
                    "Не удалось выдать роль пользователю '" + login + "'.",
                    null);
        }

        return new CommandResponse<>(true,
                "Пользователю '" + login + "' успешно выдана роль " + role,
                null);
    }

    @Override
    public String getDescription() {
        return "grant_role <login> <role> – выдать роль пользователю";
    }
}
