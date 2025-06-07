package core.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {
    private static final String URL      = "jdbc:postgresql://localhost:5432/yourdb";
    private static final String USER     = "youruser";
    private static final String PASSWORD = "yourpass";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection get() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
