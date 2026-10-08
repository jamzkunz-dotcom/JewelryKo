package JewelryKoDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:jewelryko.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return conn;
    }

    public static Connection getConnection() {
        return connect();
    }

    public static void initializeDatabase() {
        String createAdminTable = "CREATE TABLE IF NOT EXISTS admins (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " start_date TEXT,\n"
                + " position TEXT,\n"
                + " admin_name TEXT,\n"
                + " admin_id TEXT UNIQUE,\n"
                + " passcode TEXT,\n"
                + " salary_rate TEXT\n"
                + ");";

        String createItemTable = "CREATE TABLE IF NOT EXISTS inventory (\n"
                + " item_id TEXT PRIMARY KEY,\n"
                + " name TEXT NOT NULL,\n"
                + " gender TEXT,\n"
                + " type TEXT,\n"
                + " karat TEXT,\n"
                + " size TEXT,\n"
                + " weight TEXT,\n"
                + " price REAL,\n"
                + " base_price REAL,\n"
                + " image_path TEXT,\n"
                + " status TEXT\n"
                + ");";

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " item_id TEXT,\n"
                + " item_name TEXT,\n"
                + " price REAL,\n"
                + " date TEXT,\n"
                + " client_name TEXT,\n"
                + " address TEXT,\n"
                + " shipping_method TEXT,\n"
                + " payment_method TEXT,\n"
                + " ref_number TEXT\n"
                + ");";

        String createSettingsTable = "CREATE TABLE IF NOT EXISTS system_settings (" +
                                     "setting_key TEXT PRIMARY KEY, " +
                                     "setting_value TEXT NOT NULL)";

        String createAuditLogTable = "CREATE TABLE IF NOT EXISTS price_audit_logs (" +
                                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                     "timestamp TEXT, " +
                                     "action_event TEXT, " +
                                     "z_rate TEXT, " +
                                     "prev_rate TEXT, " +
                                     "new_rate TEXT, " +
                                     "difference TEXT, " +
                                     "status TEXT)";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createAdminTable);
            stmt.execute(createItemTable);
            stmt.execute(createTransactionsTable);
            stmt.execute(createSettingsTable);
            stmt.execute(createAuditLogTable);

            try {
                stmt.execute("SELECT base_price FROM inventory LIMIT 1");
            } catch (SQLException e) {
                stmt.execute("ALTER TABLE inventory ADD COLUMN base_price REAL DEFAULT 0.0");
            }

            stmt.execute("INSERT OR IGNORE INTO admins(admin_id, passcode, position, admin_name, start_date, salary_rate) VALUES('admin', '1234', 'System Administrator', 'Default Admin', '2026-01-01', '0.00');");

            System.out.println("Database and tables initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public static Map<String, String> getAdminCredentials() {
        return null;
    }
}