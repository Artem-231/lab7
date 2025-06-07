package storage.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PostgresConfig {
    private static final HikariDataSource ds;

    static {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:postgresql://localhost:5432/lab7db");
        cfg.setUsername("postgres");
        cfg.setPassword("8F1kXFax2Y");
        cfg.addDataSourceProperty("cachePrepStmts",    "true");
        cfg.addDataSourceProperty("prepStmtCacheSize", "250");
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit","2048");
        ds = new HikariDataSource(cfg);
    }

    public static DataSource getDataSource() {
        return ds;
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
