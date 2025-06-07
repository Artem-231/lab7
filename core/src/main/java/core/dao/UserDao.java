// core/src/main/java/core/dao/UserDao.java
package core.dao;

import core.enums.Role;
import core.objects.User;

import java.util.Optional;

public interface UserDao {
    // авторизация будет через саму сущность User:
    Optional<User> findByLogin(String login);

    // регистрация командой (CLI-команды GradeRole, RegisterCommand и т.п.)
    boolean exists(String login);
    long    count();
    boolean insert(User user);

    // управление ролями
    boolean updateRole(String login, Role newRole);
    boolean revokeRole(String login, Role toRevoke);
}
