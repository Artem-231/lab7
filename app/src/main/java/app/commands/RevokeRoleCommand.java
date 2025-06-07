package app.commands;

import core.dao.UserDao;
import core.enums.Role;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;

public class RevokeRoleCommand implements Command {
    private final UserDao userDao;

    public RevokeRoleCommand(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public CommandResponse<Void> executeCommand(CommandRequest<?> request) {
        Object raw = request.getPayload();
        if (!(raw instanceof String[] args) || args.length != 2) {
            return new CommandResponse<>(false,
                    "Для revoke_role нужны аргументы: <login> <role>.",
                    null);
        }

        String login    = args[0];
        String roleName = args[1].toUpperCase();

        if (userDao.findByLogin(login).isEmpty()) {
            return new CommandResponse<>(false,
                    "Пользователь '" + login + "' не найден.",
                    null);
        }

        Role roleToRevoke;
        try {
            roleToRevoke = Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            return new CommandResponse<>(false,
                    "Неверная роль: " + roleName,
                    null);
        }

        boolean success = userDao.revokeRole(login, roleToRevoke);
        if (!success) {
            return new CommandResponse<>(false,
                    "Нельзя отозвать роль " + roleToRevoke + " у пользователя '" + login + "'.",
                    null);
        }

        return new CommandResponse<>(true,
                "У пользователя '" + login + "' отозвана роль " + roleToRevoke,
                null);
    }

    @Override
    public String getDescription() {
        return "revoke_role <login> <role> – отозвать у пользователя роль";
    }
}
