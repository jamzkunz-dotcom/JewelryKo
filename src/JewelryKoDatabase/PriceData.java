package JewelryKoDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

public class PriceData {

    public static void saveCurrentRate(double rate) {
        String sql = "INSERT OR REPLACE INTO system_settings (setting_key, setting_value) VALUES ('current_gold_rate', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn != null) {
                pstmt.setString(1, String.valueOf(rate));
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error saving current rate: " + e.getMessage());
        }
    }

    public static double loadCurrentRate(double defaultRate) {
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key = 'current_gold_rate'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs != null && rs.next()) {
                return Double.parseDouble(rs.getString("setting_value"));
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error loading current rate, falling back to default: " + e.getMessage());
        }
        return defaultRate; 
    }

    public static void saveAuditLogEntry(String timestamp, String actionEvent, String zRate, String prevRate, String newRate, String difference, String status) {
        String sql = "INSERT INTO price_audit_logs (timestamp, action_event, z_rate, prev_rate, new_rate, difference, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn != null) {
                pstmt.setString(1, timestamp);
                pstmt.setString(2, actionEvent);
                pstmt.setString(3, zRate);
                pstmt.setString(4, prevRate);
                pstmt.setString(5, newRate);
                pstmt.setString(6, difference);
                pstmt.setString(7, status);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error saving audit log entry: " + e.getMessage());
        }
    }

    public static void clearAuditLogs() {
        String sql = "DELETE FROM price_audit_logs";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.out.println("Error clearing audit logs: " + e.getMessage());
        }
    }

    public static void loadAuditLogsIntoModel(DefaultTableModel model) {
        String sql = "SELECT timestamp, action_event, z_rate, prev_rate, new_rate, difference, status FROM price_audit_logs ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (model != null) {
                model.setRowCount(0); 
            }
            while (rs != null && rs.next()) {
                if (model != null) {
                    model.addRow(new Object[]{
                        rs.getString("timestamp"),
                        rs.getString("action_event"),
                        rs.getString("z_rate"),
                        rs.getString("prev_rate"),
                        rs.getString("new_rate"),
                        rs.getString("difference"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading audit logs: " + e.getMessage());
        }
    }
}
