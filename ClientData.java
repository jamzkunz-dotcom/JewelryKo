package JewelryKoDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

public class ClientData {

    public static void loadClientsIntoModel(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT client_name, address, record_id, date, price FROM transactions";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String clientName = rs.getString("client_name");
                String address = rs.getString("address");
                String transactionId = rs.getString("record_id");
                String dateOfPurchase = rs.getString("date");
                double totalPrice = rs.getDouble("price");

                model.addRow(new Object[]{
                    clientName != null ? clientName : "N/A",
                    address != null ? address : "N/A",
                    transactionId != null ? transactionId : "N/A",
                    dateOfPurchase != null ? dateOfPurchase : "N/A",
                    "₱" + String.format("%.2f", totalPrice)
                });
            }
        } catch (SQLException e) {
            System.out.println("Error loading client transactions: " + e.getMessage());
        }
    }
}