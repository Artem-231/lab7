package app.service;

import core.dao.UserDao;
import core.objects.User;
import core.enums.Role;
import storage.postgres.PostgresUserDao;
import core.utils.JwtUtil;
import core.utils.MD5Util;

import java.util.Optional;
import java.util.Set;

/**
 * Сервис аутентификации и выдачи JWT.
 */
public class AuthService {
    private final UserDao dao = new PostgresUserDao();

    /**
     * Регистрирует пользователя и сразу выдаёт ему JWT,
     * или возвращает null, если регистрация не удалась.
     */
    public String register(String login, String rawPassword) throws Exception {
        if (login.isBlank() || rawPassword.isBlank() || dao.exists(login)) {
            return null;
        }
        // Первый пользователь становится ADMIN, остальные — READER
        Role role = dao.count() == 0 ? Role.ADMIN : Role.READER;
        String hash = MD5Util.hash(rawPassword);
        User user = new User(login, hash, role);
        boolean ok = dao.insert(user);
        if (!ok) return null;

        // Сгенерировать токен с одной ролью
        return JwtUtil.generateToken(login, Set.of(role.name()));
    }

    public String login(String login, String rawPassword) throws Exception {
        Optional<User> uOpt = dao.findByLogin(login);
        if (uOpt.isEmpty()) return null;
        User u = uOpt.get();
        String hash = MD5Util.hash(rawPassword);
        if (!u.getPasswordHash().equals(hash)) {
            return null;
        }
        // У пользователя может быть только одна роль
        return JwtUtil.generateToken(login, Set.of(u.getRole().name()));
    }
}
