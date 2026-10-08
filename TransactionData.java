package JewelryKoDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransactionData {

    static {
        initializeDatabaseTable();
    }

    public static class Transaction {
        public String date;
        public String recordId;
        public String clientName;
        public String address;

        public String itemId;
        public String jewelryId;

        public String itemName;
        public String type;
        public String gender;

        public double price;
        public double totalPrice;

        public double amountPaid;
        public double balance;
        public String paymentMethod;
        public String paymentStatus;
        public String shippingMethod;
        public String status;
        public String archived;
        public String priority;
        public String refNumber;

        public Transaction(String itemId, String itemName, double price, String date,
                           String clientName, String address, String shippingMethod,
                           String paymentMethod, String refNumber, double amountPaid) {
            this.itemId = itemId;
            this.jewelryId = itemId;
            this.itemName = itemName;
            this.price = price;
            this.totalPrice = price;
            this.date = date;
            this.clientName = clientName;
            this.address = address;
            this.shippingMethod = shippingMethod;
            this.paymentMethod = paymentMethod;
            this.refNumber = (refNumber != null) ? refNumber : "";

            this.recordId = "REC-" + System.currentTimeMillis() % 100000;
            this.type = "";
            this.gender = "";
            this.amountPaid = Math.max(0.0, Math.min(price, amountPaid));
            this.balance = Math.max(0.0, this.totalPrice - this.amountPaid);

            if (this.amountPaid <= 0.0) {
                this.paymentStatus = "Unpaid";
                this.status = "Reserved";
            } else if (this.balance > 0.0) {
                this.paymentStatus = "Partial";
                this.status = "Processing";
            } else {
                this.paymentStatus = "Paid";
                this.status = "Sold";
            }

            this.archived = "No";
            this.priority = "No";
        }

        public Transaction(String itemId, String itemName, double price, String date,
                           String clientName, String address, String shippingMethod,
                           String paymentMethod, String refNumber) {
            this(itemId, itemName, price, date, clientName, address, shippingMethod, paymentMethod, refNumber, 0.0);
        }

        public Transaction(String date, String recordId, String clientName, String address,
                           String itemId, String itemName, String type, String gender,
                           double price, double amountPaid, double balance,
                           String paymentMethod, String paymentStatus, String shippingMethod,
                           String status, String archived) {
            this.date = date;
            this.recordId = recordId;
            this.clientName = clientName;
            this.address = address;
            this.itemId = itemId;
            this.jewelryId = itemId;
            this.itemName = itemName;
            this.type = type != null ? type : "";
            this.gender = gender != null ? gender : "";
            this.price = price;
            this.totalPrice = price > 0 ? price : (amountPaid + balance);
            this.amountPaid = amountPaid;
            this.balance = balance > 0 ? balance : Math.max(0.0, this.totalPrice - this.amountPaid);
            this.paymentMethod = paymentMethod != null ? paymentMethod : "";
            this.shippingMethod = shippingMethod != null ? shippingMethod : "";
            this.archived = archived != null ? archived : "No";
            this.priority = "No";
            this.refNumber = "";

            String rawStatus = status != null ? status.trim() : "";
            boolean isAdvancedStatus = rawStatus.equalsIgnoreCase("Shipped") ||
                                       rawStatus.equalsIgnoreCase("Delivered") ||
                                       rawStatus.equalsIgnoreCase("Cancelled");

            if (isAdvancedStatus) {
                this.status = rawStatus;
                if ("Cancelled".equalsIgnoreCase(rawStatus)) {
                    this.paymentStatus = "Cancelled";
                } else {
                    this.paymentStatus = (paymentStatus != null && !paymentStatus.isEmpty()) ? paymentStatus : 
                                         (this.balance <= 0.0 ? "Paid" : "Partial");
                }
            } else {
                if (this.amountPaid <= 0.0) {
                    this.paymentStatus = "Unpaid";
                    this.status = "Reserved";
                } else if (this.balance > 0.0) {
                    this.paymentStatus = "Partial";
                    this.status = "Processing";
                } else {
                    this.paymentStatus = (paymentStatus != null && !paymentStatus.isEmpty()) ? paymentStatus : "Paid";
                    this.status = "Sold";
                }
            }
        }
    }

    public static void initializeDatabaseTable() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            String createTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "record_id TEXT, " +
                    "date TEXT, " +
                    "client_name TEXT, " +
                    "address TEXT, " +
                    "item_id TEXT, " +
                    "item_name TEXT, " +
                    "type TEXT, " +
                    "gender TEXT, " +
                    "price REAL, " +
                    "amount_paid REAL, " +
                    "balance REAL, " +
                    "payment_method TEXT, " +
                    "payment_status TEXT, " +
                    "shipping_method TEXT, " +
                    "status TEXT, " +
                    "archived TEXT, " +
                    "priority TEXT, " +
                    "ref_number TEXT" +
                    ")";
            stmt.execute(createTable);

            checkAndAddColumn(conn, "transactions", "record_id", "TEXT");
            checkAndAddColumn(conn, "transactions", "type", "TEXT");
            checkAndAddColumn(conn, "transactions", "gender", "TEXT");
            checkAndAddColumn(conn, "transactions", "amount_paid", "REAL");
            checkAndAddColumn(conn, "transactions", "balance", "REAL");
            checkAndAddColumn(conn, "transactions", "payment_status", "TEXT");
            checkAndAddColumn(conn, "transactions", "status", "TEXT");
            checkAndAddColumn(conn, "transactions", "archived", "TEXT");
            checkAndAddColumn(conn, "transactions", "priority", "TEXT");
            checkAndAddColumn(conn, "transactions", "ref_number", "TEXT");

        } catch (SQLException e) {
            System.out.println("Error initializing transaction table: " + e.getMessage());
        }
    }

    private static void checkAndAddColumn(Connection conn, String tableName, String columnName, String columnType) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            boolean found = false;
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(columnName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
                System.out.println("Migrated schema: Added column '" + columnName + "' to table '" + tableName + "'.");
            }
        } catch (SQLException e) {
        }
    }

    public static void addTransaction(Transaction tx) {
        String sql = "INSERT INTO transactions(record_id, date, client_name, address, item_id, item_name, type, gender, price, amount_paid, balance, payment_method, payment_status, shipping_method, status, archived, priority, ref_number) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tx.recordId);
            pstmt.setString(2, tx.date);
            pstmt.setString(3, tx.clientName);
            pstmt.setString(4, tx.address);
            pstmt.setString(5, tx.itemId);
            pstmt.setString(6, tx.itemName);
            pstmt.setString(7, tx.type);
            pstmt.setString(8, tx.gender);
            pstmt.setDouble(9, tx.price);
            pstmt.setDouble(10, tx.amountPaid);
            pstmt.setDouble(11, tx.balance);
            pstmt.setString(12, tx.paymentMethod);
            pstmt.setString(13, tx.paymentStatus);
            pstmt.setString(14, tx.shippingMethod);
            pstmt.setString(15, tx.status);
            pstmt.setString(16, tx.archived);
            pstmt.setString(17, tx.priority);
            pstmt.setString(18, tx.refNumber);

            pstmt.executeUpdate();
            updateInventoryStatusForTransaction(conn, tx);
            System.out.println("Transaction successfully saved to database.");
        } catch (SQLException e) {
            System.out.println("Error adding transaction: " + e.getMessage());
        }
    }

    public static void updateTransaction(Transaction tx) {
        String currentStatus = tx.status != null ? tx.status.trim() : "";
        boolean isAdvancedStatus = currentStatus.equalsIgnoreCase("Shipped") ||
                                   currentStatus.equalsIgnoreCase("Delivered") ||
                                   currentStatus.equalsIgnoreCase("Cancelled");

        if (isAdvancedStatus) {
            if ("Cancelled".equalsIgnoreCase(currentStatus)) {
                tx.paymentStatus = "Cancelled";
            }
        } else {
            if (tx.amountPaid <= 0.0) {
                tx.paymentStatus = "Unpaid";
                tx.status = "Reserved";
            } else if (tx.balance > 0.0) {
                tx.paymentStatus = "Partial";
                tx.status = "Processing";
            } else {
                tx.paymentStatus = "Paid";
                tx.status = "Sold";
            }
        }

        String sql = "UPDATE transactions SET date = ?, client_name = ?, address = ?, item_id = ?, item_name = ?, type = ?, gender = ?, price = ?, amount_paid = ?, balance = ?, payment_method = ?, payment_status = ?, shipping_method = ?, status = ?, archived = ?, priority = ? WHERE record_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tx.date);
            pstmt.setString(2, tx.clientName);
            pstmt.setString(3, tx.address);
            pstmt.setString(4, tx.itemId);
            pstmt.setString(5, tx.itemName);
            pstmt.setString(6, tx.type);
            pstmt.setString(7, tx.gender);
            pstmt.setDouble(8, tx.price);
            pstmt.setDouble(9, tx.amountPaid);
            pstmt.setDouble(10, tx.balance);
            pstmt.setString(11, tx.paymentMethod);
            pstmt.setString(12, tx.paymentStatus);
            pstmt.setString(13, tx.shippingMethod);
            pstmt.setString(14, tx.status);
            pstmt.setString(15, tx.archived);
            pstmt.setString(16, tx.priority);
            pstmt.setString(17, tx.recordId);

            pstmt.executeUpdate();
            updateInventoryStatusForTransaction(conn, tx);

            System.out.println("Transaction successfully updated in database.");
        } catch (SQLException e) {
            System.out.println("Error updating transaction: " + e.getMessage());
        }
    }

    private static void updateInventoryStatusForTransaction(Connection conn, Transaction tx) {
        String inventoryStatus;
        String currentStatus = tx.status != null ? tx.status.trim() : "";

        if ("Cancelled".equalsIgnoreCase(currentStatus)) {
            inventoryStatus = "Available";
        } else if ("Processing".equalsIgnoreCase(currentStatus)) {
            inventoryStatus = "Processing";
        } else if ("Reserved".equalsIgnoreCase(currentStatus)) {
            inventoryStatus = "Reserved";
        } else if ("Shipped".equalsIgnoreCase(currentStatus) || "Delivered".equalsIgnoreCase(currentStatus)) {
            inventoryStatus = "Sold";
        } else {
            inventoryStatus = currentStatus.isEmpty() ? "Sold" : currentStatus;
        }

        String sql = "UPDATE inventory SET status = ? WHERE item_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, inventoryStatus);
            pstmt.setString(2, tx.itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not sync inventory status: " + e.getMessage());
        }
    }

    public static List<Transaction> getTransactionList() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String recordId = hasColumn(rs, "record_id") && rs.getString("record_id") != null ? rs.getString("record_id") : "REC-" + (1000 + rs.getRow());
                String date = rs.getString("date");
                String clientName = rs.getString("client_name");
                String address = rs.getString("address");
                String itemId = rs.getString("item_id");
                String itemName = rs.getString("item_name");
                String type = hasColumn(rs, "type") ? rs.getString("type") : "";
                String gender = hasColumn(rs, "gender") ? rs.getString("gender") : "";
                double price = rs.getDouble("price");
                double amountPaid = hasColumn(rs, "amount_paid") ? rs.getDouble("amount_paid") : 0.0;
                double balance = hasColumn(rs, "balance") ? rs.getDouble("balance") : price;
                String paymentMethod = rs.getString("payment_method");
                String paymentStatus = hasColumn(rs, "payment_status") ? rs.getString("payment_status") : "";
                String shippingMethod = rs.getString("shipping_method");
                String status = hasColumn(rs, "status") && rs.getString("status") != null ? rs.getString("status") : "";
                String archived = hasColumn(rs, "archived") ? rs.getString("archived") : "No";
                String priority = hasColumn(rs, "priority") && rs.getString("priority") != null ? rs.getString("priority") : "No";

                Transaction tx = new Transaction(
                    date, recordId, clientName, address, itemId, itemName, type, gender,
                    price, amountPaid, balance, paymentMethod, paymentStatus, shippingMethod, status, archived
                );
                tx.priority = priority;
                list.add(tx);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving transactions: " + e.getMessage());
        }
        return list;
    }

    private static boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}