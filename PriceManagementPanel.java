package JewelryKoGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import JewelryKoDatabase.InventoryData;
import JewelryKoDatabase.PriceData; 

public class PriceManagementPanel extends JPanel {
    private Window parentWindow;
    private static final String MASTER_PASSCODE = "jewelryko";
    private final Color COLOR_BG = new Color(245, 235, 220);
    private final Color COLOR_PANEL_BG = new Color(253, 245, 230);
    private final Color COLOR_BORDER = new Color(210, 180, 140);
    private final Color COLOR_TEXT_DARK = new Color(180, 120, 60);

    public PriceManagementPanel() {
        this(null);
    }

    public PriceManagementPanel(Window parentWindow) {
        this.parentWindow = parentWindow;
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        showLockedView();

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (!isShowing()) {
                    showLockedView();
                }
            }
        });
    }

    private void showLockedView() {
        removeAll();
        JPanel lockedPanel = new JPanel(new GridBagLayout());
        lockedPanel.setBackground(COLOR_PANEL_BG);
        lockedPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 2),
            new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblTitle = new JLabel("JEWELRYKO - PRICE MANAGEMENT PANEL");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_TEXT_DARK);
        lockedPanel.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("UNAUTHORIZED ACCESS IS PROHIBITED IN THIS SECTION");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(Color.DARK_GRAY);
        lockedPanel.add(lblSub, gbc);

        JButton btnUnlock = createStyledButton("Unlock Panel");
        btnUnlock.setPreferredSize(new Dimension(180, 40));
        btnUnlock.addActionListener(e -> {
            Window win = parentWindow != null ? parentWindow : SwingUtilities.getWindowAncestor(this);
            if (authenticateMasterPasscode(win)) {
                showUnlockedView();
            }
        });

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrapper.setOpaque(false);
        btnWrapper.add(btnUnlock);
        lockedPanel.add(btnWrapper, gbc);

        add(lockedPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showUnlockedView() {
        removeAll();
        
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JButton btnLock = new JButton("Lock Panel");
        btnLock.setPreferredSize(new Dimension(110, 30));
        btnLock.setBackground(new Color(220, 190, 170));
        btnLock.setForeground(COLOR_TEXT_DARK);
        btnLock.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnLock.setFocusPainted(false);
        
        btnLock.addActionListener(e -> {
            saveAllPanelData();
            showLockedView();
        });

        JPanel lockWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        lockWrapper.setOpaque(false);
        lockWrapper.add(btnLock);
        topBar.add(lockWrapper, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        PriceUpdateSubPanel priceUpdateSubPanel = new PriceUpdateSubPanel();
        tabbedPane.addTab("Price Update", priceUpdateSubPanel);
        tabbedPane.addTab("Price History (Audit Log)", new PriceHistorySubPanel());
        tabbedPane.addTab("Financial Summary", new FinancialSummarySubPanel());

        tabbedPane.addChangeListener(e -> {
            saveAllPanelData();
        });

        add(tabbedPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void saveAllPanelData() {
        try {
            double currentRate = PriceUpdateSubPanel.getCurrentRateValue();
            PriceData.saveCurrentRate(currentRate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean authenticateMasterPasscode(Window targetWindow) {
        JDialog authDialog = new JDialog(targetWindow, "Master Passcode Required", Dialog.ModalityType.APPLICATION_MODAL);
        authDialog.setSize(350, 180);
        authDialog.setResizable(false);
        authDialog.setLocationRelativeTo(targetWindow);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL_BG);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JPasswordField txtMasterPasscode = new JPasswordField();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel("Master Passcode:");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(COLOR_TEXT_DARK);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        txtMasterPasscode.setPreferredSize(new Dimension(170, 26));
        panel.add(txtMasterPasscode, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(85, 30));
        btnCancel.setBackground(new Color(220, 210, 195));
        btnCancel.setFocusPainted(false);

        JButton btnSubmit = new JButton("Unlock");
        btnSubmit.setPreferredSize(new Dimension(85, 30));
        btnSubmit.setBackground(new Color(200, 150, 90));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSubmit.setFocusPainted(false);

        final boolean[] isUnlocked = {false};
        btnSubmit.addActionListener(e -> {
            String enteredPasscode = new String(txtMasterPasscode.getPassword()).trim();
            if (enteredPasscode.equals(MASTER_PASSCODE)) {
                isUnlocked[0] = true;
                authDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(authDialog, "Incorrect Master Passcode.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                txtMasterPasscode.setText("");
            }
        });

        btnCancel.addActionListener(e -> authDialog.dispose());
        txtMasterPasscode.addActionListener(e -> btnSubmit.doClick());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 0, 0, 0);
        panel.add(btnPanel, gbc);

        authDialog.add(panel);
        authDialog.setVisible(true);
        return isUnlocked[0];
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(150, 36));
        btn.setBackground(new Color(200, 150, 90));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        return btn;
    }

    private static class PriceUpdateSubPanel extends JPanel {
        private JLabel lblCurrentRateVal, lbl10kRateVal, lbl14kRateVal, lbl18kRateVal, lbl21kRateVal;
        private JTextField txtNewRate;
        private static double currentRateValue = 7300.00;
        private static final double GOLD_PRICE_Z_RATE = 8950.20;

        public PriceUpdateSubPanel() {
            currentRateValue = PriceData.loadCurrentRate(7300.00);

            setLayout(new BorderLayout(15, 15));
            setBackground(new Color(253, 245, 230));
            setBorder(new EmptyBorder(15, 15, 15, 15));

            JPanel containerBox = new JPanel(new BorderLayout(20, 15));
            containerBox.setBackground(new Color(253, 245, 230));
            containerBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 150), 1),
                new EmptyBorder(20, 20, 20, 20)
            ));

            JLabel lblPanelTitle = new JLabel("Price Update Management");
            lblPanelTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
            lblPanelTitle.setForeground(new Color(160, 100, 40));
            containerBox.add(lblPanelTitle, BorderLayout.NORTH);

            JPanel contentCenter = new JPanel(new BorderLayout(25, 0));
            contentCenter.setOpaque(false);

            ScaledImagePanel chartPanel = new ScaledImagePanel("/Image/goldRate.png");
            chartPanel.setPreferredSize(new Dimension(560, 340));
            chartPanel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            contentCenter.add(chartPanel, BorderLayout.CENTER);

            JPanel rightFormPanel = new JPanel(new GridBagLayout());
            rightFormPanel.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = GridBagConstraints.RELATIVE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(4, 5, 4, 5);

            JLabel lblZRateLabel = new JLabel("GoldPriceZ Rate: " + String.format("%.2f", GOLD_PRICE_Z_RATE));
            lblZRateLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblZRateLabel.setForeground(new Color(120, 90, 60));
            rightFormPanel.add(lblZRateLabel, gbc);

            lblCurrentRateVal = new JLabel("Current Rate: " + String.format("%.2f", currentRateValue));
            lblCurrentRateVal.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblCurrentRateVal.setForeground(new Color(120, 90, 60));
            rightFormPanel.add(lblCurrentRateVal, gbc);

            JPanel newRateSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            newRateSubPanel.setOpaque(false);
            JLabel lblNewRateTitle = new JLabel("New Gold Rate: PHP");
            lblNewRateTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblNewRateTitle.setForeground(new Color(120, 90, 60));
            txtNewRate = new JTextField(8);
            txtNewRate.setPreferredSize(new Dimension(85, 26));
            newRateSubPanel.add(lblNewRateTitle);
            newRateSubPanel.add(txtNewRate);
            rightFormPanel.add(newRateSubPanel, gbc);

            lbl10kRateVal = new JLabel("10k Rate: " + String.format("%.2f", currentRateValue * 0.37));
            lbl10kRateVal.setFont(new Font("SansSerif", Font.BOLD, 13));
            lbl10kRateVal.setForeground(new Color(120, 90, 60));
            rightFormPanel.add(lbl10kRateVal, gbc);

            lbl14kRateVal = new JLabel("14k Rate: " + String.format("%.2f", currentRateValue * 0.58));
            lbl14kRateVal.setFont(new Font("SansSerif", Font.BOLD, 13));
            lbl14kRateVal.setForeground(new Color(120, 90, 60));
            rightFormPanel.add(lbl14kRateVal, gbc);

            lbl18kRateVal = new JLabel("18k Rate: " + String.format("%.2f", currentRateValue * 0.75));
            lbl18kRateVal.setFont(new Font("SansSerif", Font.BOLD, 13));
            lbl18kRateVal.setForeground(new Color(120, 90, 60));
            rightFormPanel.add(lbl18kRateVal, gbc);

            lbl21kRateVal = new JLabel("21k Rate: " + String.format("%.2f", currentRateValue * 0.875));
            lbl21kRateVal.setFont(new Font("SansSerif", Font.BOLD, 13));
            lbl21kRateVal.setForeground(new Color(120, 90, 60));
            rightFormPanel.add(lbl21kRateVal, gbc);

            GridBagConstraints gbcGlue = new GridBagConstraints();
            gbcGlue.gridx = 0;
            gbcGlue.gridy = GridBagConstraints.RELATIVE;
            gbcGlue.weighty = 1.0;
            JPanel dummyGlue = new JPanel();
            dummyGlue.setOpaque(false);
            rightFormPanel.add(dummyGlue, gbcGlue);

            contentCenter.add(rightFormPanel, BorderLayout.EAST);
            containerBox.add(contentCenter, BorderLayout.CENTER);

            JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            bottomButtonPanel.setOpaque(false);
            JButton btnUpdatePrice = new JButton("Update Price");
            btnUpdatePrice.setPreferredSize(new Dimension(130, 34));
            btnUpdatePrice.setBackground(new Color(200, 150, 90));
            btnUpdatePrice.setForeground(Color.WHITE);
            btnUpdatePrice.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnUpdatePrice.setFocusPainted(false);
            btnUpdatePrice.addActionListener(e -> handleUpdate());
            bottomButtonPanel.add(btnUpdatePrice);

            containerBox.add(bottomButtonPanel, BorderLayout.SOUTH);
            add(containerBox, BorderLayout.CENTER);
        }

        public static double getCurrentRateValue() {
            return currentRateValue;
        }

        private void handleUpdate() {
            String rateText = txtNewRate.getText().trim();
            if (rateText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a value for New Rate.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double rate = Double.parseDouble(rateText);
                if (rate < 0) {
                    JOptionPane.showMessageDialog(this, "The new rate cannot be negative.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double oldRate = currentRateValue;
                currentRateValue = rate;
                
                PriceData.saveCurrentRate(currentRateValue);
                
                lblCurrentRateVal.setText("Current Rate: " + String.format("%.2f", currentRateValue));
                lbl10kRateVal.setText("10k Rate: " + String.format("%.2f", currentRateValue * 0.37));
                lbl14kRateVal.setText("14k Rate: " + String.format("%.2f", currentRateValue * 0.58));
                lbl18kRateVal.setText("18k Rate: " + String.format("%.2f", currentRateValue * 0.75));
                lbl21kRateVal.setText("21k Rate: " + String.format("%.2f", currentRateValue * 0.875));

                InventoryData.updateAllItemPrices(currentRateValue);
                
                PriceHistorySubPanel.addAuditLogEntry(GOLD_PRICE_Z_RATE, oldRate, currentRateValue, "Success");

                txtNewRate.setText("");
                JOptionPane.showMessageDialog(this, "Gold rate successfully updated, available prices recalculated, and saved to database!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numerical value.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class ScaledImagePanel extends JPanel {
        private BufferedImage image;
        private String fallbackMessage = "Loading chart image...";

        public ScaledImagePanel(String imagePath) {
            setBackground(Color.BLACK);
            try {
                URL imgUrl = getClass().getResource(imagePath);
                if (imgUrl != null) {
                    image = ImageIO.read(imgUrl);
                } else {
                    fallbackMessage = "[Image Not Found] " + imagePath;
                }
            } catch (Exception e) {
                fallbackMessage = "Error loading chart image";
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = image.getWidth();
                int imgHeight = image.getHeight();

                double scaleX = (double) panelWidth / imgWidth;
                double scaleY = (double) panelHeight / imgHeight;
                double scale = Math.min(scaleX, scaleY);

                int drawWidth = (int) (imgWidth * scale);
                int drawHeight = (int) (imgHeight * scale);
                int x = (panelWidth - drawWidth) / 2;
                int y = (panelHeight - drawHeight) / 2;

                g2d.drawImage(image, x, y, drawWidth, drawHeight, this);
                g2d.dispose();
            } else {
                g.setColor(new Color(245, 222, 179));
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                g.drawString(fallbackMessage, 20, getHeight() / 2);
            }
        }
    }

    private static class PriceHistorySubPanel extends JPanel {
        private static DefaultTableModel auditTableModel;
        private JTable auditTable;

        public PriceHistorySubPanel() {
            setLayout(new BorderLayout(10, 10));
            setBackground(new Color(253, 245, 230));
            setBorder(new EmptyBorder(12, 12, 12, 12));

            JPanel topHeader = new JPanel(new BorderLayout());
            topHeader.setOpaque(false);
            JLabel lblHeader = new JLabel("Price Update Audit Log & History");
            lblHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
            lblHeader.setForeground(new Color(180, 120, 60));
            topHeader.add(lblHeader, BorderLayout.WEST);

            JButton btnClearLog = new JButton("Clear Log");
            btnClearLog.setPreferredSize(new Dimension(100, 28));
            btnClearLog.setBackground(new Color(220, 190, 170));
            btnClearLog.setForeground(new Color(180, 120, 60));
            btnClearLog.setFont(new Font("SansSerif", Font.BOLD, 11));
            btnClearLog.setFocusPainted(false);
            btnClearLog.addActionListener(e -> {
                auditTableModel.setRowCount(0);
                PriceData.clearAuditLogs();
            });

            JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            btnWrapper.setOpaque(false);
            btnWrapper.add(btnClearLog);
            topHeader.add(btnWrapper, BorderLayout.EAST);

            add(topHeader, BorderLayout.NORTH);

            String[] columns = {"Timestamp", "Action Event", "GoldPriceZ Rate", "Previous Rate", "New Rate", "Difference", "Status"};
            
            auditTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            auditTable = new JTable(auditTableModel);
            auditTable.setRowHeight(24);

            PriceData.loadAuditLogsIntoModel(auditTableModel);

            JScrollPane scrollPane = new JScrollPane(auditTable);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 140), 1));
            add(scrollPane, BorderLayout.CENTER);
        }

        public static void addAuditLogEntry(double zRate, double prevRate, double newRate, String status) {
            if (auditTableModel != null) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                
                double diffAmount = newRate - prevRate;
                String diffStr;
                if (prevRate == 0.0) {
                    diffStr = String.format("+₱%,.2f (Initial)", newRate);
                } else {
                    double diffPercent = (diffAmount / prevRate) * 100.0;
                    diffStr = String.format("%s₱%,.2f (%.2f%%)", (diffAmount >= 0 ? "+" : ""), diffAmount, diffPercent);
                }

                String zRateStr = String.format("PHP %,.2f", zRate);
                String prevRateStr = String.format("PHP %,.2f", prevRate);
                String newRateStr = String.format("PHP %,.2f", newRate);

                auditTableModel.addRow(new Object[]{
                    timestamp,
                    "Gold Rate Update",
                    zRateStr,
                    prevRateStr,
                    newRateStr,
                    diffStr,
                    status
                });

                PriceData.saveAuditLogEntry(timestamp, "Gold Rate Update", zRateStr, prevRateStr, newRateStr, diffStr, status);
            }
        }
    }

    private static class FinancialSummarySubPanel extends JPanel {
        private JTable summaryTable;
        private DefaultTableModel tableModel;
        private JLabel lblTotalSold, lblTotalCost, lblTotalRevenue, lblTotalProfit;

        public FinancialSummarySubPanel() {
            setLayout(new BorderLayout(10, 10));
            setBackground(new Color(253, 245, 230));
            setBorder(new EmptyBorder(12, 12, 12, 12));

            JPanel topHeader = new JPanel(new BorderLayout());
            topHeader.setOpaque(false);
            JLabel lblHeader = new JLabel("Financial Summary & Profitability Analysis");
            lblHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
            lblHeader.setForeground(new Color(180, 120, 60));
            topHeader.add(lblHeader, BorderLayout.WEST);

            JButton btnRefresh = new JButton("Refresh Data");
            btnRefresh.setPreferredSize(new Dimension(120, 30));
            btnRefresh.setBackground(new Color(200, 150, 90));
            btnRefresh.setForeground(Color.WHITE);
            btnRefresh.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnRefresh.setFocusPainted(false);
            btnRefresh.addActionListener(e -> refreshData());

            JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            btnWrapper.setOpaque(false);
            btnWrapper.add(btnRefresh);
            topHeader.add(btnWrapper, BorderLayout.EAST);
            add(topHeader, BorderLayout.NORTH);

            String[] columns = {"Item ID", "Name", "Type", "Base Price (Cost)", "Current Price", "Profit Margin (Price - Base)", "Status"};
            
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            summaryTable = new JTable(tableModel);
            summaryTable.setRowHeight(24);

            add(new JScrollPane(summaryTable), BorderLayout.CENTER);

            JPanel bottomSummaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
            bottomSummaryPanel.setOpaque(false);
            bottomSummaryPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

            lblTotalSold = new JLabel("Items Sold: 0", SwingConstants.CENTER);
            lblTotalCost = new JLabel("Total Cost: PHP 0.00", SwingConstants.CENTER);
            lblTotalRevenue = new JLabel("Total Revenue: PHP 0.00", SwingConstants.CENTER);
            lblTotalProfit = new JLabel("Net Profit: PHP 0.00", SwingConstants.CENTER);

            Font cardFont = new Font("SansSerif", Font.BOLD, 12);
            lblTotalSold.setFont(cardFont);
            lblTotalCost.setFont(cardFont);
            lblTotalRevenue.setFont(cardFont);
            lblTotalProfit.setFont(cardFont);
            lblTotalProfit.setForeground(new Color(40, 130, 60));

            bottomSummaryPanel.add(createCard(lblTotalSold));
            bottomSummaryPanel.add(createCard(lblTotalCost));
            bottomSummaryPanel.add(createCard(lblTotalRevenue));
            bottomSummaryPanel.add(createCard(lblTotalProfit));

            add(bottomSummaryPanel, BorderLayout.SOUTH);
            refreshData();
        }

        private JPanel createCard(JLabel label) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(new Color(255, 250, 240));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 200, 160), 1),
                new EmptyBorder(8, 8, 8, 8)
            ));
            card.add(label, BorderLayout.CENTER);
            return card;
        }

        public void refreshData() {
            tableModel.setRowCount(0);
            
            for (InventoryData.JewelryItem item : InventoryData.getItemList()) {
                double margin = item.price - item.basePrice;
                tableModel.addRow(new Object[]{
                    item.id,
                    item.name,
                    item.type,
                    String.format("PHP %,.2f", item.basePrice),
                    String.format("PHP %,.2f", item.price),
                    String.format("PHP %,.2f", margin),
                    (item.status == null || item.status.trim().isEmpty()) ? "Available" : item.status
                });
            }

            InventoryData.FinancialSummary summary = InventoryData.calculateFinancialSummary();
            lblTotalSold.setText("Items Sold: " + summary.itemsSold);
            lblTotalCost.setText(String.format("Total Cost: PHP %,.2f", summary.totalBaseCost));
            lblTotalRevenue.setText(String.format("Total Revenue: PHP %,.2f", summary.totalSalesRevenue));
            lblTotalProfit.setText(String.format("Net Profit: PHP %,.2f", summary.totalProfit));
        }
    }
}