package JewelryKoGUI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class InvalidInputDialog extends JDialog {

    public InvalidInputDialog(JFrame parent) {
        super(parent, true);
        setUndecorated(true);

        int dialogWidth = 350;
        int dialogHeight = 190;

        setSize(dialogWidth, dialogHeight);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(253, 245, 230));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

                g2.setColor(new Color(210, 180, 124));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 25, 25));

                g2.dispose();
            }
        };
        panel.setLayout(null);
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("Incorrect Credentials!", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(197, 155, 88));
        lblTitle.setBounds(0, (int)(dialogHeight * 0.18), dialogWidth, 35);
        panel.add(lblTitle);

        int buttonWidth = 160;
        int buttonHeight = 44;
        int buttonX = (dialogWidth - buttonWidth) / 2;
        int buttonY = (int)(dialogHeight * 0.52);

        JButton btnTryAgain = createTryAgainButton("Try Again!");
        btnTryAgain.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        btnTryAgain.addActionListener(e -> dispose());
        panel.add(btnTryAgain);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });

        add(panel);
    }

    private JButton createTryAgainButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(new Color(220, 190, 140));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(245, 225, 195));
                } else {
                    g2.setColor(new Color(235, 215, 185));
                }

                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setForeground(new Color(197, 155, 88));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void showDialog(JFrame parent) {
        InvalidInputDialog dialog = new InvalidInputDialog(parent);
        dialog.setVisible(true);
    }
}
