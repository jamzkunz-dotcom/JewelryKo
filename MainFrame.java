package JewelryKoGUI;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import JewelryKoDatabase.DatabaseConnection;

public class MainFrame extends JFrame {

    public MainFrame() {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
    }

        public static void main(String[] args) {
            DatabaseConnection.initializeDatabase();

            java.awt.EventQueue.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
}