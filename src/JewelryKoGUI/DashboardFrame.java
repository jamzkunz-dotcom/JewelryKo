package JewelryKoGUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class DashboardFrame extends JFrame {
    private JPanel navbarPanel;
    private JButton[] menuButtons;
    private String[] menuLabels = {"Dashboard", "Inventory", "Record", "Status", "User", "Price Management", "Log Out"};

    private JPanel contentContainer;
    private CardLayout cardLayout;

    private InventoryPanel inventoryPanel;
    private RecordPanel recordPanel;
    private StatusPanel statusPanel;
    private UserPanel userPanel;
    private PriceManagementPanel priceManagementPanel;

    private JLayeredPane carouselPane;
    private ImagePanel imageDisplayPanel;
    private JButton btnPrev;
    private JButton btnNext;
    private int currentImageIndex = 0;

    private String[] featuredImagePaths = {
        "/Image/Dashft1.png",
        "/Image/Dashft2.png",
        "/Image/Dashft3.png",
        "/Image/Dashft4.png",
        "/Image/Dashft5.png"
    };

    private final Color COLOR_ACTIVE_BG = new Color(222, 184, 135);
    private final Color COLOR_ACTIVE_FG = new Color(80, 45, 10);
    private final Color COLOR_INACTIVE_BG = new Color(245, 222, 179);
    private final Color COLOR_INACTIVE_FG = Color.WHITE;

    public DashboardFrame() {
        setTitle("JewelryKo - Navigation Hub");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setExtendedState(Frame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        navbarPanel = new JPanel(new GridLayout(1, menuLabels.length, 2, 0));
        navbarPanel.setBackground(new Color(200, 160, 110));
        navbarPanel.setPreferredSize(new Dimension(getWidth(), 45));

        menuButtons = new JButton[menuLabels.length];

        for (int i = 0; i < menuLabels.length; i++) {
            final int index = i;
            menuButtons[i] = new JButton(menuLabels[i]);
            menuButtons[i].setFont(new Font("SansSerif", Font.BOLD, 14));
            menuButtons[i].setFocusPainted(false);
            menuButtons[i].setBorderPainted(false);

            if (i == 0) {
                setActiveTab(menuButtons[i]);
            } else {
                setInactiveTab(menuButtons[i]);
            }

            menuButtons[i].addActionListener(e -> handleMenuClick(index));
            navbarPanel.add(menuButtons[i]);
        }

        add(navbarPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);

        carouselPane = new JLayeredPane();
        carouselPane.setLayout(null);

        imageDisplayPanel = new ImagePanel();
        carouselPane.add(imageDisplayPanel, Integer.valueOf(1));

        btnPrev = createOverlayArrowButton("<");
        btnPrev.addActionListener(e -> navigateCarousel(-1));
        carouselPane.add(btnPrev, Integer.valueOf(2));

        btnNext = createOverlayArrowButton(">");
        btnNext.addActionListener(e -> navigateCarousel(1));
        carouselPane.add(btnNext, Integer.valueOf(2));

        carouselPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionCarouselComponents();
            }
        });

        inventoryPanel = new InventoryPanel();
        recordPanel = new RecordPanel();
        statusPanel = new StatusPanel();
        userPanel = new UserPanel();
        priceManagementPanel = new PriceManagementPanel();

        contentContainer.add(carouselPane, "DASHBOARD");
        contentContainer.add(inventoryPanel, "INVENTORY");
        contentContainer.add(recordPanel, "RECORD");
        contentContainer.add(statusPanel, "STATUS");
        contentContainer.add(userPanel, "USER");
        contentContainer.add(priceManagementPanel, "PRICE_MANAGEMENT");

        cardLayout.show(contentContainer, "DASHBOARD");

        add(contentContainer, BorderLayout.CENTER);
        updateCarouselImage();
    }

    private void repositionCarouselComponents() {
        int width = carouselPane.getWidth();
        int height = carouselPane.getHeight();

        if (width <= 0 || height <= 0) {
			return;
		}

        imageDisplayPanel.setBounds(0, 0, width, height);

        int arrowWidth = 50;
        int arrowHeight = 60;
        int arrowY = (height - arrowHeight) / 2;

        btnPrev.setBounds(30, arrowY, arrowWidth, arrowHeight);
        btnNext.setBounds(width - arrowWidth - 30, arrowY, arrowWidth, arrowHeight);
    }

    private void handleMenuClick(int selectedIndex) {
        for (int i = 0; i < menuButtons.length; i++) {
            if (i == selectedIndex) {
                setActiveTab(menuButtons[i]);
            } else {
                setInactiveTab(menuButtons[i]);
            }
        }

        switch (selectedIndex) {
            case 0:
                cardLayout.show(contentContainer, "DASHBOARD");
                break;
            case 1:
                if (inventoryPanel != null) {
                    inventoryPanel.refreshInventory();
                }
                cardLayout.show(contentContainer, "INVENTORY");
                break;
            case 2:
                if (recordPanel != null) {
                    recordPanel.refreshTable();
                }
                cardLayout.show(contentContainer, "RECORD");
                break;
            case 3:
                cardLayout.show(contentContainer, "STATUS");
                break;
            case 4:
                cardLayout.show(contentContainer, "USER");
                break;
            case 5:
                cardLayout.show(contentContainer, "PRICE_MANAGEMENT");
                break;
            case 6:
                this.dispose();
                new LoginFrame().setVisible(true);
                break;
        }
    }

    private void setActiveTab(JButton button) {
        button.setBackground(COLOR_ACTIVE_BG);
        button.setForeground(COLOR_ACTIVE_FG);
    }

    private void setInactiveTab(JButton button) {
        button.setBackground(COLOR_INACTIVE_BG);
        button.setForeground(COLOR_INACTIVE_FG);
    }

    private JButton createOverlayArrowButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 26));
        btn.setForeground(Color.DARK_GRAY);
        btn.setBackground(new Color(255, 255, 255, 190));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void navigateCarousel(int direction) {
        currentImageIndex += direction;
        if (currentImageIndex < 0) {
            currentImageIndex = featuredImagePaths.length - 1;
        } else if (currentImageIndex >= featuredImagePaths.length) {
            currentImageIndex = 0;
        }
        updateCarouselImage();
    }

    private void updateCarouselImage() {
        URL imgUrl = getClass().getResource(featuredImagePaths[currentImageIndex]);
        if (imgUrl != null) {
            try {
                BufferedImage img = ImageIO.read(imgUrl);
                imageDisplayPanel.setImage(img, null);
            } catch (IOException e) {
                imageDisplayPanel.setImage(null, "Error loading image: " + featuredImagePaths[currentImageIndex]);
            }
        } else {
            imageDisplayPanel.setImage(null, "File Not Found: src" + featuredImagePaths[currentImageIndex]);
        }
    }

    private static class ImagePanel extends JPanel {
        private BufferedImage currentImage;
        private String fallbackText;

        public ImagePanel() {
            setBackground(new Color(40, 30, 20));
        }

        public void setImage(BufferedImage img, String fallbackText) {
            this.currentImage = img;
            this.fallbackText = fallbackText;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(currentImage, 0, 0, getWidth(), getHeight(), null);
                g2d.dispose();
            } else if (fallbackText != null) {
                g.setColor(new Color(245, 222, 179));
                g.setFont(new Font("SansSerif", Font.BOLD, 18));
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(fallbackText)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g.drawString(fallbackText, x, y);
            }
        }
    }
}
