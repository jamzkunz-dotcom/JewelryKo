package JewelryKoDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ShowTablesConsole {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();

        System.out.println("\n--- Database Tables ---");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%';")) {

            boolean hasTables = false;
            while (rs.next()) {
                hasTables = true;
                String tableName = rs.getString("name");
                System.out.println("- Table found: " + tableName);
            }

            if (!hasTables) {
                System.out.println("No tables found in the database.");
            }

        } catch (Exception e) {
            System.out.println("Error retrieving tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}