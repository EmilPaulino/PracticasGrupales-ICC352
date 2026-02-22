package edu.pucmm.eict.web.util;
import java.sql.*;
import java.time.LocalDateTime;

public class LoginLogger {
    private static final String URL = System.getenv("JDBC_DATABASE_URL");

    public static void log(String username) {

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO login_log (username, fecha) VALUES (?, ?)")) {

            stmt.setString(1, username);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
