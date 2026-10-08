package JewelryKoGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import JewelryKoDatabase.AdminData;

public class LoginFrame extends JFrame {
    private JTextField txtAdminId;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private BackgroundPanel mainPanel;

    private final String BG_IMAGE_PATH = "src/Image/LoginBG.png";

    public LoginFrame() {
        setTitle("JewelryKo - Admin Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setExtendedState(Frame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));

        mainPanel = new BackgroundPanel(BG_IMAGE_PATH);
        mainPanel.setLayout(null);

        txtAdminId = createRoundedTextField("Admin ID");
        txtPassword = createRoundedPasswordField("Password");
        btnLogin = createRoundedButton("Log In");

        btnLogin.addActionListener(e -> performLogin());

        mainPanel.add(txtAdminId);
        mainPanel.add(txtPassword);
        mainPanel.add(btnLogin);

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionComponents();
            }
        });

        getRootPane().setDefaultButton(btnLogin);
        add(mainPanel);
    }

    private void repositionComponents() {
        int panelWidth = mainPanel.getWidth();
        int panelHeight = mainPanel.getHeight();

        int fieldWidth = 260;
        int fieldHeight = 36;
        int buttonWidth = 110;
        int buttonHeight = 36;

        int centerX = (panelWidth - fieldWidth) / 2;
        int startY = (int) (panelHeight * 0.72);

        txtAdminId.setBounds(centerX, startY, fieldWidth, fieldHeight);
        txtPassword.setBounds(centerX, startY + 46, fieldWidth, fieldHeight);
        btnLogin.setBounds((panelWidth - buttonWidth) / 2, startY + 95, buttonWidth, buttonHeight);

        mainPanel.repaint();
    }

    private void performLogin() {
        String adminId = txtAdminId.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (adminId.isEmpty() || adminId.equals("Admin ID") || password.isEmpty() || password.equals("Password")) {
            InvalidInputDialog.showDialog(this);
            return;
        }

        if (AdminData.validateAdmin(adminId, password)) {
            this.dispose();

            SwingUtilities.invokeLater(() -> {
                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);
            });
        } else {
            InvalidInputDialog.showDialog(this);
        }
    }

    private JTextField createRoundedTextField(String placeholder) {
        JTextField field = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 222, 208, 230));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        field.setForeground(Color.GRAY);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        return field;
    }

    private JPasswordField createRoundedPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 222, 208, 230));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setEchoChar((char) 0);
        field.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        field.setForeground(Color.GRAY);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String pass = new String(field.getPassword());
                if (pass.equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                String pass = new String(field.getPassword());
                if (pass.isEmpty()) {
                    field.setText(placeholder);
                    field.setEchoChar((char) 0);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        return field;
    }

    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 222, 208));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(new Color(80, 50, 20));
        return btn;
    }

    private class BackgroundPanel extends JPanel {
        private Image bgImage;

        public BackgroundPanel(String imagePath) {
            ImageIcon icon = new ImageIcon(imagePath);
            if (icon.getIconWidth() > 0) {
                bgImage = icon.getImage();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(new Color(195, 160, 130));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
