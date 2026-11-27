package ir.maktabsharif.bank.util;

import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestUtil {
    public static void truncateAllTables(PostgreSQLContainer postgres) {
        try (var conn = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword());
             var stmt = conn.createStatement()) {

            try (var rs = stmt.executeQuery("""
                    SELECT tablename FROM pg_tables
                    WHERE schemaname = 'public'
                    """)) {
                var tables = new ArrayList<String>();
                while (rs.next()) {
                    tables.add("\"" + rs.getString(1) + "\"");
                }
                if (!tables.isEmpty()) {
                    stmt.execute("TRUNCATE TABLE " + String.join(", ", tables)
                                 + " RESTART IDENTITY CASCADE");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to truncate test database", e);
        }
    }
}
