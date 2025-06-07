package storage.postgres;

import core.dao.LabWorkDao;
import core.enums.Color;
import core.enums.Country;
import core.enums.Difficulty;
import core.objects.Coordinates;
import core.objects.LabWork;
import core.objects.Person;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresLabWorkDao implements LabWorkDao {
    private final DataSource ds = PostgresConfig.getDataSource();

    @Override
    public Optional<Long> insert(LabWork lw) {
        String sql = """
            INSERT INTO labworks (
              name, x, y, creation_date,
              minimal_point, description,
              difficulty, author_name, author_weight,
              author_eye_color, author_hair_color,
              author_nationality, owner_login
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, lw.getName());
            ps.setDouble(2, lw.getCoordinates().getX());
            ps.setLong(3, lw.getCoordinates().getY());
            ps.setTimestamp(4, Timestamp.valueOf(lw.getCreationDate()));
            ps.setFloat(5, lw.getMinimalPoint());
            ps.setString(6, lw.getDescription());
            ps.setString(7,
                    lw.getDifficulty() == null ? null : lw.getDifficulty().name());
            ps.setString(8, lw.getAuthor().getName());
            ps.setInt(9, lw.getAuthor().getWeight());
            ps.setString(10,
                    lw.getAuthor().getEyeColor() == null
                            ? "" : lw.getAuthor().getEyeColor().name());
            ps.setString(11, lw.getAuthor().getHairColor().name());
            ps.setString(12, lw.getAuthor().getNationality().name());
            ps.setString(13, lw.getOwnerLogin());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong(1));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting LabWork", e);
        }
    }

    @Override
    public boolean update(LabWork lw) {
        String sql = """
            UPDATE labworks SET
              name               = ?,
              x                  = ?,
              y                  = ?,
              minimal_point      = ?,
              description        = ?,
              difficulty         = ?,
              author_name        = ?,
              author_weight      = ?,
              author_eye_color   = ?,
              author_hair_color  = ?,
              author_nationality = ?
            WHERE id = ? AND owner_login = ?
            """;
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, lw.getName());
            ps.setDouble(2, lw.getCoordinates().getX());
            ps.setLong(3, lw.getCoordinates().getY());
            ps.setFloat(4, lw.getMinimalPoint());
            ps.setString(5, lw.getDescription());
            ps.setString(6,
                    lw.getDifficulty() == null ? null : lw.getDifficulty().name());

            ps.setString(7, lw.getAuthor().getName());
            ps.setInt(8, lw.getAuthor().getWeight());
            ps.setString(9, lw.getAuthor().getEyeColor() == null
                    ? "" : lw.getAuthor().getEyeColor().name());
            ps.setString(10, lw.getAuthor().getHairColor().name());
            ps.setString(11, lw.getAuthor().getNationality().name());

            ps.setLong(12, lw.getId());
            ps.setString(13, lw.getOwnerLogin());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating LabWork", e);
        }
    }

    @Override
    public boolean delete(long id, String ownerLogin) {
        String sql = "DELETE FROM labworks WHERE id = ? AND owner_login = ?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setString(2, ownerLogin);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting LabWork", e);
        }
    }

    @Override
    public List<LabWork> fetchAll() {
        String sql = "SELECT * FROM labworks ORDER BY id";
        List<LabWork> list = new ArrayList<>();
        try (Connection c = ds.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int    id       = rs.getInt("id");
                String name     = rs.getString("name");
                double x        = rs.getDouble("x");
                long   y        = rs.getLong("y");
                Timestamp ts    = rs.getTimestamp("creation_date");
                LocalDateTime creationDate = ts.toLocalDateTime();
                float  mp       = rs.getFloat("minimal_point");
                String description = rs.getString("description");
                String dif      = rs.getString("difficulty");
                Difficulty difficulty = dif == null ? null
                        : Difficulty.valueOf(dif);

                Person author = new Person(
                        rs.getString("author_name"),
                        rs.getInt("author_weight"),
                        rs.getString("author_eye_color").isEmpty()
                                ? null : Color.valueOf(rs.getString("author_eye_color")),
                        Color.valueOf(rs.getString("author_hair_color")),
                        Country.valueOf(rs.getString("author_nationality"))
                );

                LabWork lw = new LabWork(
                        id,
                        name,
                        new Coordinates(x, y),
                        creationDate,
                        mp,
                        description,
                        difficulty,
                        author
                );
                // В текущей модели author.getName() == owner_login
                lw.setOwnerLogin(rs.getString("owner_login"));
                list.add(lw);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching LabWorks", e);
        }
    }
}
