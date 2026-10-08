package JewelryKoDatabase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryData {

    static {
        initializeInventoryTable();
    }

    public static class JewelryItem {
        public String name;
        public String id;
        public String gender;
        public String type;
        public String karat;
        public String size;
        public String weight;
        public double price;
        public double basePrice;
        public String imagePath;
        public String status;

        public JewelryItem(String id, String name, String gender, String type, String karat,
                           String size, String weight, double price, double basePrice, String imagePath, String status) {
            this.id = id;
            this.name = name;
            this.gender = gender;
            this.type = type;
            this.karat = karat;
            this.size = size;
            this.weight = weight;
            this.price = price;
            this.basePrice = basePrice;
            this.imagePath = normalizeImagePath(imagePath);
            this.status = status;
        }
    }

    public static class FinancialSummary {
        public double totalSalesRevenue;
        public double totalBaseCost;
        public double totalProfit;
        public int itemsSold;

        public FinancialSummary(double totalSalesRevenue, double totalBaseCost, double totalProfit, int itemsSold) {
            this.totalSalesRevenue = totalSalesRevenue;
            this.totalBaseCost = totalBaseCost;
            this.totalProfit = totalProfit;
            this.itemsSold = itemsSold;
        }
    }

    public static FinancialSummary calculateFinancialSummary() {
        double totalRevenue = 0.0;
        double totalCost = 0.0;
        int soldCount = 0;

        String sql = "SELECT price, base_price, status FROM inventory";
        
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String status = rs.getString("status");
                if (status != null && "Sold".equalsIgnoreCase(status.trim())) {
                    double price = rs.getDouble("price");
                    double basePrice = rs.getDouble("base_price");
                    
                    totalRevenue += price;
                    totalCost += basePrice;
                    soldCount++;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error calculating financial summary: " + e.getMessage());
        }

        double totalProfit = totalRevenue - totalCost;
        return new FinancialSummary(totalRevenue, totalCost, totalProfit, soldCount);
    }

    public static String normalizeImagePath(String path) {
        if (path == null) return "";
        String clean = path.trim();
        
        if ((clean.startsWith("\"") && clean.endsWith("\"")) || (clean.startsWith("'") && clean.endsWith("'"))) {
            if (clean.length() > 2) {
                clean = clean.substring(1, clean.length() - 1).trim();
            } else {
                clean = "";
            }
        }
        
        if (clean.isEmpty()) return "";

        if (clean.startsWith("http://") || clean.startsWith("https://") || clean.startsWith("file:") || clean.startsWith("/")) {
            return clean;
        }

        String fileName = clean.contains("/") ? clean.substring(clean.lastIndexOf('/') + 1) : 
                          clean.contains("\\") ? clean.substring(clean.lastIndexOf('\\') + 1) : clean;

        return "/JewelryKoDatabase/image/" + fileName;
    }

    public static double parseWeight(String weightStr) {
        if (weightStr == null) return 0.0;
        String clean = weightStr.replaceAll("(?i)[^0-9.]", "").trim();
        if (clean.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static double getKaratMultiplier(String karat) {
        if (karat == null) return 0.0;
        String k = karat.toUpperCase().trim();
        if (k.contains("10")) return 0.37;
        else if (k.contains("14")) return 0.58;
        else if (k.contains("18")) return 0.75;
        else if (k.contains("21")) return 0.875;
        return 0.0;
    }

    public static double calculatePrice(String karat, String weightStr, double currentRate) {
        double weight = parseWeight(weightStr);
        return currentRate * getKaratMultiplier(karat) * weight;
    }

    public static double calculateBasePrice(String karat, String weightStr, double baseRate) {
        double weight = parseWeight(weightStr);
        return baseRate * getKaratMultiplier(karat) * weight;
    }

    public static void initializeInventoryTable() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS inventory (" +
                    "item_id TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "gender TEXT, " +
                    "type TEXT, " +
                    "karat TEXT, " +
                    "size TEXT, " +
                    "weight TEXT, " +
                    "price REAL, " +
                    "base_price REAL, " +
                    "image_path TEXT, " +
                    "status TEXT" +
                    ")";
            stmt.execute(sql);

            try {
                stmt.execute("SELECT base_price FROM inventory LIMIT 1");
            } catch (SQLException e) {
                stmt.execute("ALTER TABLE inventory ADD COLUMN base_price REAL DEFAULT 0.0");
            }

            String checkCount = "SELECT COUNT(*) FROM inventory";
            try (ResultSet rs = stmt.executeQuery(checkCount)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    loadCSVIntoDatabase(conn, "src/inventory.csv");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error initializing inventory table: " + e.getMessage());
        }
    }

    private static void loadCSVIntoDatabase(Connection conn, String csvFilePath) {
        String insertSql = "INSERT OR IGNORE INTO inventory(item_id, name, gender, type, karat, size, weight, price, base_price, image_path, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] values = line.split(",");
                if (values.length >= 11) {
                    String name = values[0].trim();
                    String id = values[1].trim();
                    String gender = values[2].trim();
                    String type = values[3].trim();
                    String karat = values[4].trim();
                    String size = values[5].trim();
                    String weightStr = values[6].trim();
                    
                    double priceInput = 0.0;
                    try {
                        priceInput = Double.parseDouble(values[7].trim());
                    } catch (Exception ignored) {}

                    double basePriceRate = 0.0;
                    try {
                        basePriceRate = Double.parseDouble(values[8].trim());
                    } catch (Exception ignored) {}

                    double calculatedBasePrice = calculateBasePrice(karat, weightStr, basePriceRate);

                    String imagePath = values[9].trim();
                    String status = values[10].trim();

                    pstmt.setString(1, id);
                    pstmt.setString(2, name);
                    pstmt.setString(3, gender);
                    pstmt.setString(4, type);
                    pstmt.setString(5, karat);
                    pstmt.setString(6, size);
                    pstmt.setString(7, weightStr);
                    pstmt.setDouble(8, priceInput);
                    pstmt.setDouble(9, calculatedBasePrice);
                    pstmt.setString(10, normalizeImagePath(imagePath));
                    pstmt.setString(11, status);
                    
                    pstmt.addBatch();
                    count++;
                }
            }
            pstmt.executeBatch();
            System.out.println(count + " inventory items successfully loaded from " + csvFilePath);
            
        } catch (IOException e) {
            System.out.println("Could not find " + csvFilePath + " file. Starting with default items.");
            try (Statement stmt = conn.createStatement()) {
                String insertDefault = "INSERT INTO inventory(item_id, name, gender, type, karat, size, weight, price, base_price, image_path, status) VALUES " +
                        "('JNL001', 'Cross Pendant Necklace', 'Unisex', 'Necklace', '10k', '16.5', '2.29g', 18500.0, 1793.18, '/Image/JNL001.jpg', 'Available')," +
                        "('JNL002', 'Heart Sunburst Necklace', 'Female', 'Necklace', '14k', '18.8', '3.55g', 24000.0, 4359.70, '/Image/JNL002.jpg', 'Available')";
                stmt.execute(insertDefault);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Error batch inserting CSV items: " + e.getMessage());
        }
    }

    public static void addItem(JewelryItem item, double currentRate) {
        if (!"Sold".equalsIgnoreCase(item.status)) {
            item.price = calculatePrice(item.karat, item.weight, currentRate);
        }
        item.basePrice = calculateBasePrice(item.karat, item.weight, item.basePrice);
        item.imagePath = normalizeImagePath(item.imagePath);

        String sql = "INSERT OR REPLACE INTO inventory(item_id, name, gender, type, karat, size, weight, price, base_price, image_path, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.id);
            pstmt.setString(2, item.name);
            pstmt.setString(3, item.gender);
            pstmt.setString(4, item.type);
            pstmt.setString(5, item.karat);
            pstmt.setString(6, item.size);
            pstmt.setString(7, item.weight);
            pstmt.setDouble(8, item.price);
            pstmt.setDouble(9, item.basePrice);
            pstmt.setString(10, item.imagePath);
            pstmt.setString(11, item.status);
            pstmt.executeUpdate();

            appendItemToCSV(item);

        } catch (SQLException e) {
            System.out.println("Error adding item: " + e.getMessage());
        }
    }

    private static void appendItemToCSV(JewelryItem item) {
        String csvFilePath = "src/inventory.csv";
        
        try (FileWriter fw = new FileWriter(csvFilePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.printf(java.util.Locale.US, "%s,%s,%s,%s,%s,%s,%s,%.2f,%.2f,%s,%s%n",
                    escapeCsv(item.name),
                    escapeCsv(item.id),
                    escapeCsv(item.gender),
                    escapeCsv(item.type),
                    escapeCsv(item.karat),
                    escapeCsv(item.size),
                    escapeCsv(item.weight),
                    item.price,
                    item.basePrice,
                    escapeCsv(item.imagePath),
                    escapeCsv(item.status)
            );
            pw.flush();
            System.out.println("Item successfully appended to CSV file: " + item.id);
        } catch (IOException e) {
            System.out.println("ERROR: Could not append item to CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String escapeCsv(String val) {
        if (val == null) return "";
        return val.contains(",") ? "\"" + val + "\"" : val;
    }

    public static List<JewelryItem> getItemList() {
        initializeInventoryTable();
        List<JewelryItem> itemList = new ArrayList<>();
        String sql = "SELECT * FROM inventory";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                double basePrice = 0.0;
                try {
                    basePrice = rs.getDouble("base_price");
                } catch (SQLException ignored) {}

                itemList.add(new JewelryItem(
                    rs.getString("item_id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("type"),
                    rs.getString("karat"),
                    rs.getString("size"),
                    rs.getString("weight"),
                    rs.getDouble("price"),
                    basePrice,
                    rs.getString("image_path"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving items: " + e.getMessage());
        }
        return itemList;
    }

    public static void updateItem(JewelryItem item, double currentRate) {
        if (!"Sold".equalsIgnoreCase(item.status)) {
            item.price = calculatePrice(item.karat, item.weight, currentRate);
        }
        item.basePrice = calculateBasePrice(item.karat, item.weight, item.basePrice);
        item.imagePath = normalizeImagePath(item.imagePath);

        String sql = "UPDATE inventory SET name = ?, gender = ?, type = ?, karat = ?, size = ?, weight = ?, price = ?, base_price = ?, image_path = ?, status = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.name);
            pstmt.setString(2, item.gender);
            pstmt.setString(3, item.type);
            pstmt.setString(4, item.karat);
            pstmt.setString(5, item.size);
            pstmt.setString(6, item.weight);
            pstmt.setDouble(7, item.price);
            pstmt.setDouble(8, item.basePrice);
            pstmt.setString(9, item.imagePath);
            pstmt.setString(10, item.status);
            pstmt.setString(11, item.id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating item: " + e.getMessage());
        }
    }

    public static void updateAllItemPrices(double currentRate) {
        String selectSql = "SELECT item_id, karat, weight FROM inventory WHERE status IS NULL OR TRIM(LOWER(status)) != 'sold'";
        String updateSql = "UPDATE inventory SET price = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql);
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            while (rs.next()) {
                String itemId = rs.getString("item_id");
                String karat = rs.getString("karat");
                String weight = rs.getString("weight");

                double newCalculatedPrice = calculatePrice(karat, weight, currentRate);

                pstmt.setDouble(1, newCalculatedPrice);
                pstmt.setString(2, itemId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Available inventory item prices successfully updated (Sold items protected).");
        } catch (SQLException e) {
            System.out.println("Error updating inventory prices: " + e.getMessage());
        }
    }

    public static void deleteItem(String itemId) {
        String sql = "DELETE FROM inventory WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting item: " + e.getMessage());
        }
    }
}
