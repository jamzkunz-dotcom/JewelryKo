package JewelryKoDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

public class AdminData {

    static {
        initializeAdminTable();
    }

    public static void initializeAdminTable() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS admins (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "start_date TEXT, " +
                    "position TEXT, " +
                    "admin_name TEXT, " +
                    "admin_id TEXT UNIQUE, " +
                    "passcode TEXT, " +
                    "salary_rate TEXT" +
                    ")";
            stmt.execute(sql);

            String checkDefault = "SELECT COUNT(*) FROM admins WHERE LOWER(admin_id) = 'admin'";
            try (ResultSet rs = stmt.executeQuery(checkDefault)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertDefault = "INSERT INTO admins (start_date, position, admin_name, admin_id, passcode, salary_rate) " +
                                           "VALUES ('2026-01-01', 'System Administrator', 'Default Admin', 'admin', '1234', '0.00')";
                    stmt.execute(insertDefault);
                    System.out.println("Default admin account (admin / 1234) created successfully.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error initializing admins table: " + e.getMessage());
        }
    }

    public static boolean addAdmin(String startDate, String position, String adminName, String adminId, String passcode, String salaryRate) {
        String sql = "INSERT INTO admins (start_date, position, admin_name, admin_id, passcode, salary_rate) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, position);
            pstmt.setString(3, adminName);
            pstmt.setString(4, adminId);
            pstmt.setString(5, passcode);
            pstmt.setString(6, salaryRate);

            pstmt.executeUpdate();
            System.out.println("Admin added successfully to database.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding admin: " + e.getMessage());
            return false;
        }
    }

    public static boolean removeAdmin(String adminId) {
        String sql = "DELETE FROM admins WHERE admin_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, adminId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error removing admin: " + e.getMessage());
            return false;
        }
    }

    public static void loadAdminsIntoModel(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT start_date, position, admin_name, admin_id, passcode, salary_rate FROM admins";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("start_date"),
                    rs.getString("position"),
                    rs.getString("admin_name"),
                    rs.getString("admin_id"),
                    rs.getString("passcode"),
                    rs.getString("salary_rate")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error loading admins: " + e.getMessage());
        }
    }

    public static boolean deleteAdmin(String adminId) {
        String query = "DELETE FROM admins WHERE admin_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, adminId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean validateAdmin(String adminId, String password) {
        initializeAdminTable();

        if (adminId == null || password == null) {
            return false;
        }

        String sql = "SELECT * FROM admins WHERE LOWER(TRIM(admin_id)) = LOWER(TRIM(?)) AND TRIM(passcode) = TRIM(?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Login Error: Database connection is null.");
                return false;
            }

            pstmt.setString(1, adminId.trim());
            pstmt.setString(2, password.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Login successful for admin: " + adminId);
                    return true;
                } else {
                    System.out.println("Login failed: Invalid ID or password.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Login SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}