// postgres-module/src/main/java/storage/postgres/PostgresUserDao.java
package storage.postgres;

import core.dao.UserDao;
import core.objects.User;
import core.enums.Role;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class PostgresUserDao implements UserDao {
    private final DataSource ds = PostgresConfig.getDataSource();

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT password_hash, role FROM users WHERE login = ?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String hash = rs.getString("password_hash");
                Role   r    = Role.valueOf(rs.getString("role"));
                return Optional.of(new User(login, hash, r));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String login) {
        String sql = "SELECT 1 FROM users WHERE login = ?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection c = ds.getConnection();
             Statement  st = c.createStatement();
             ResultSet  rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insert(User user) {
        String sql = "INSERT INTO users(login,password_hash,role) VALUES(?,?,?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().name());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean updateRole(String login, Role newRole) {
        String sql = "UPDATE users SET role = ? WHERE login = ?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newRole.name());
            ps.setString(2, login);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean revokeRole(String login, Role ignore) {
        // просто понижаем до READER
        return updateRole(login, Role.READER);
    }
}
