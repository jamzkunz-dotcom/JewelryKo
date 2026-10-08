package JewelryKoGUI;

import JewelryKoDatabase.InventoryData;
import JewelryKoDatabase.TransactionData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewItemPanel extends JDialog {

    private final InventoryData.JewelryItem item;
    private final InventoryPanel inventoryPanel;
    private JLabel lblStatus;
    private JLabel lblPriceValue;
    private JLabel lblBasePriceValue;

    public ViewItemPanel(Frame owner, InventoryData.JewelryItem item, InventoryPanel inventoryPanel) {
        super(owner, "Item Details - " + item.id, true);
        this.item = item;
        this.inventoryPanel = inventoryPanel;
        initUI();
    }

    public ViewItemPanel(Dialog owner, InventoryData.JewelryItem item, InventoryPanel inventoryPanel) {
        super(owner, "Item Details - " + item.id, true);
        this.item = item;
        this.inventoryPanel = inventoryPanel;
        initUI();
    }

    private void initUI() {
        setSize(520, 460);
        setResizable(false);
        setLocationRelativeTo(getOwner());

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(253, 245, 230));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel imgContainer = new JPanel(new BorderLayout());
        imgContainer.setPreferredSize(new Dimension(170, 0));
        imgContainer.setBackground(new Color(245, 235, 220));
        imgContainer.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 140), 1));

        JLabel lblImage = new JLabel("", SwingConstants.CENTER);
        java.net.URL imgUrl = getClass().getResource(item.imagePath);
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(img));
        } else {
            lblImage.setText("<html><center>[No Image<br>Available]</center></html>");
            lblImage.setForeground(new Color(160, 130, 90));
            lblImage.setFont(new Font("SansSerif", Font.BOLD, 12));
        }
        imgContainer.add(lblImage, BorderLayout.CENTER);
        mainPanel.add(imgContainer, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        addDetailRow(infoPanel, gbc, 0, "Item Name:", item.name, true);
        addDetailRow(infoPanel, gbc, 1, "Item ID:", item.id, false);
        addDetailRow(infoPanel, gbc, 2, "Type:", item.type, false);
        addDetailRow(infoPanel, gbc, 3, "Gender:", item.gender, false);
        addDetailRow(infoPanel, gbc, 4, "Karat:", item.karat, false);
        addDetailRow(infoPanel, gbc, 5, "Size:", item.size, false);
        addDetailRow(infoPanel, gbc, 6, "Weight:", item.weight + (item.weight.endsWith("g") ? "" : "g"), false);
        
        lblPriceValue = new JLabel(String.format("PHP %,.2f", item.price));
        lblPriceValue.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPriceValue.setForeground(new Color(180, 120, 0));
        addCustomDetailRow(infoPanel, gbc, 7, "Price:", lblPriceValue);

        lblBasePriceValue = new JLabel(String.format("PHP %,.2f", item.basePrice));
        lblBasePriceValue.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblBasePriceValue.setForeground(new Color(120, 100, 70));
        addCustomDetailRow(infoPanel, gbc, 8, "Base Price:", lblBasePriceValue);

        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel lblStatusTitle = new JLabel("Status:");
        lblStatusTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStatusTitle.setForeground(new Color(120, 80, 40));
        infoPanel.add(lblStatusTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        
        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setOpaque(true);
        lblStatus.setPreferredSize(new Dimension(130, 26));
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 11));
        
        updateStatusLabelStyle();
        infoPanel.add(lblStatus, gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        JButton btnSell = new JButton("Sell Item");
        btnSell.setPreferredSize(new Dimension(100, 32));
        btnSell.setBackground(new Color(60, 140, 80));
        btnSell.setForeground(Color.WHITE);
        btnSell.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSell.setFocusPainted(false);
        btnSell.addActionListener(e -> {
            String oldStatus = item.status;
            showSellDialog("Sold", oldStatus);
        });

        JButton btnRemove = new JButton("Remove Item");
        btnRemove.setPreferredSize(new Dimension(115, 32));
        btnRemove.setBackground(new Color(180, 70, 60));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRemove.setFocusPainted(false);
        btnRemove.addActionListener(e -> showRemovePasswordDialog());

        JButton btnClose = new JButton("Close");
        btnClose.setPreferredSize(new Dimension(85, 32));
        btnClose.setBackground(new Color(200, 150, 90));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        actionPanel.add(btnSell);
        actionPanel.add(btnRemove);
        actionPanel.add(btnClose);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void showSellDialog(String targetStatus, String oldStatus) {
        JDialog sellDialog = new JDialog(this, "Item " + targetStatus, true);
        sellDialog.setSize(400, 520);
        sellDialog.setResizable(false);
        sellDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(253, 245, 230));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("Mark as " + targetStatus);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(new Color(180, 120, 60));
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        JTextField txtDate = new JTextField(currentDate);
        JTextField txtRecordId = new JTextField("REC-" + System.currentTimeMillis() % 100000);
        JTextField txtClientName = new JTextField();
        JTextField txtAddress = new JTextField();
        
        JLabel lblSizeValue = new JLabel(item.size != null && !item.size.isEmpty() ? item.size : "N/A");
        lblSizeValue.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSizeValue.setForeground(new Color(50, 40, 30));

        JLabel lblWeightValue = new JLabel(item.weight != null ? (item.weight.endsWith("g") ? item.weight : item.weight + "g") : "0.0g");
        lblWeightValue.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblWeightValue.setForeground(new Color(50, 40, 30));

        String[] shippingOptions = {"PICK-UP", "LBC", "FedEx", "J&T"};
        JComboBox<String> cmbShippingMethod = new JComboBox<>(shippingOptions);

        String[] paymentOptions = {"CASH", "GCASH", "BPI", "PSB"};
        JComboBox<String> cmbPaymentMethod = new JComboBox<>(paymentOptions);

        JTextField txtAmountPaid = new NumericTextField("0.00");

        JTextField txtPaymentStatus = new JTextField("Unpaid");
        txtPaymentStatus.setEditable(false);

        addSellFormRow(panel, gbc, 1, "Date of Purchase:", txtDate);
        addSellFormRow(panel, gbc, 2, "Record ID:", txtRecordId);
        addSellFormRow(panel, gbc, 3, "Client Name:", txtClientName);
        addSellFormRow(panel, gbc, 4, "Address:", txtAddress);
        addSellFormRow(panel, gbc, 5, "Size:", lblSizeValue);
        addSellFormRow(panel, gbc, 6, "Weight:", lblWeightValue);
        addSellFormRow(panel, gbc, 7, "Shipping Method:", cmbShippingMethod);
        addSellFormRow(panel, gbc, 8, "Payment Method:", cmbPaymentMethod);
        addSellFormRow(panel, gbc, 9, "Amount Paid:", txtAmountPaid);
        addSellFormRow(panel, gbc, 10, "Payment Status:", txtPaymentStatus);

        txtAmountPaid.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateStatus(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateStatus(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateStatus(); }

            private void calculateStatus() {
                try {
                    String input = txtAmountPaid.getText().trim();
                    if (input.isEmpty()) {
                        txtPaymentStatus.setText("Unpaid");
                        return;
                    }
                    double paid = Double.parseDouble(input);
                    long paidCents = Math.round(paid * 100.0);
                    long priceCents = Math.round(item.price * 100.0);

                    if (paidCents > priceCents) {
                        txtPaymentStatus.setText("Too Much");
                    } else if (paidCents == priceCents) {
                        txtPaymentStatus.setText("Paid");
                    } else if (paidCents > 0) {
                        txtPaymentStatus.setText("Partial");
                    } else {
                        txtPaymentStatus.setText("Unpaid");
                    }
                } catch (NumberFormatException ex) {
                    txtPaymentStatus.setText("Invalid");
                }
            }
        });

        gbc.gridy = 11;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 4, 4, 4);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.setPreferredSize(new Dimension(110, 32));
        btnConfirm.setBackground(new Color(60, 140, 80));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnConfirm.setFocusPainted(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(110, 32));
        btnCancel.setBackground(new Color(200, 150, 90));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCancel.setFocusPainted(false);

        btnConfirm.addActionListener(evt -> {
            String client = txtClientName.getText().trim();
            if (client.isEmpty()) {
                JOptionPane.showMessageDialog(sellDialog, "Please enter Client Name.", "Input Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double paidAmount = 0.0;
            try {
                String amountPaidText = txtAmountPaid.getText().trim();
                if (!amountPaidText.isEmpty()) {
                    paidAmount = Double.parseDouble(amountPaidText);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(sellDialog, "Invalid input must be numerical.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                txtAmountPaid.requestFocus();
                return;
            }

            long paidCents = Math.round(paidAmount * 100.0);
            long priceCents = Math.round(item.price * 100.0);

            if (paidCents > priceCents) {
                JOptionPane.showMessageDialog(sellDialog, "The payment is too much! It exceeds the item price.", "Payment Error", JOptionPane.ERROR_MESSAGE);
                txtAmountPaid.requestFocus();
                return;
            }

            double balance = Math.max(0.0, (priceCents - paidCents) / 100.0);
            String computedStatus = (paidCents == priceCents) ? "Paid" : (paidCents > 0 ? "Partial" : "Unpaid");

            String selectedShipping = (String) cmbShippingMethod.getSelectedItem();
            String selectedPayment = (String) cmbPaymentMethod.getSelectedItem();

            TransactionData.addTransaction(new TransactionData.Transaction(
                    txtDate.getText().trim(),
                    txtRecordId.getText().trim(),
                    client,
                    txtAddress.getText().trim(),
                    item.id,
                    item.name,
                    item.type,
                    item.gender,
                    item.price,
                    paidAmount,
                    balance,
                    selectedPayment,
                    computedStatus,
                    selectedShipping,
                    targetStatus,
                    "No"
            ));

            item.status = targetStatus;
            InventoryData.updateItem(item, balance);
            
            sellDialog.dispose();

            JOptionPane.showMessageDialog(this, "Item successfully marked as " + targetStatus + " and saved!", "Success", JOptionPane.INFORMATION_MESSAGE);

            if (inventoryPanel != null) {
                inventoryPanel.refreshGrid();
            }

            dispose();
        });

        btnCancel.addActionListener(evt -> {
            item.status = oldStatus; 
            updateStatusLabelStyle();
            sellDialog.dispose();
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnConfirm);

        panel.add(btnPanel, gbc);

        sellDialog.add(panel);
        sellDialog.setVisible(true);
    }

    private static class NumericTextField extends JTextField {
        public NumericTextField(String text) {
            super(text);
        }

        @Override
        protected PlainDocument createDefaultModel() {
            return new PlainDocument() {
                @Override
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                    if (str == null) return;
                    
                    String currentText = getText(0, getLength());
                    String newText = currentText.substring(0, offs) + str + currentText.substring(offs);

                    if (isValidDecimalString(newText)) {
                        super.insertString(offs, str, a);
                    }
                }

                private boolean isValidDecimalString(String text) {
                    if (text.isEmpty() || text.equals(".")) return true;
                    if (text.chars().filter(ch -> ch == '.').count() > 1) return false;
                    try {
                        Double.parseDouble(text);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            };
        }
    }

    private void addSellFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent inputComponent) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(new Color(180, 120, 60));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        inputComponent.setPreferredSize(new Dimension(170, 26));
        panel.add(inputComponent, gbc);
    }

    private void showRemovePasswordDialog() {
        JDialog passDialog = new JDialog(this, "Management Verification", true);
        passDialog.setSize(300, 190);
        passDialog.setResizable(false);
        passDialog.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(new Color(253, 245, 230));
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbcPass = new GridBagConstraints();
        gbcPass.gridx = 0;
        gbcPass.fill = GridBagConstraints.HORIZONTAL;
        gbcPass.insets = new Insets(2, 4, 4, 4);

        JLabel lblMsg = new JLabel("Are you sure you want to", SwingConstants.CENTER);
        lblMsg.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblMsg.setForeground(new Color(180, 120, 60));
        pnl.add(lblMsg, gbcPass);

        JLabel lblSubMsg = new JLabel("remove this item?", SwingConstants.CENTER);
        lblSubMsg.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSubMsg.setForeground(new Color(180, 120, 60));
        pnl.add(lblSubMsg, gbcPass);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setHorizontalAlignment(JTextField.CENTER);
        txtPassword.setPreferredSize(new Dimension(180, 28));
        pnl.add(txtPassword, gbcPass);

        JButton btnConfirmRemove = new JButton("Remove");
        btnConfirmRemove.setPreferredSize(new Dimension(100, 30));
        btnConfirmRemove.setBackground(new Color(230, 210, 180));
        btnConfirmRemove.setForeground(new Color(140, 90, 40));
        btnConfirmRemove.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnConfirmRemove.setFocusPainted(false);
        btnConfirmRemove.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 140), 1));

        btnConfirmRemove.addActionListener(evt -> {
            String enteredPassword = new String(txtPassword.getPassword());
            
            if ("admin1234".equals(enteredPassword)) {
                InventoryData.deleteItem(item.id);
                passDialog.dispose();
                
                if (inventoryPanel != null) {
                    inventoryPanel.refreshGrid();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(passDialog, "Invalid password!", "Access Denied", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        });

        gbcPass.insets = new Insets(8, 4, 4, 4);
        pnl.add(btnConfirmRemove, gbcPass);

        passDialog.add(pnl);
        passDialog.setVisible(true);
    }

    private void updateStatusLabelStyle() {
        String currentStatus = (item.status == null || item.status.isEmpty()) ? "Available" : item.status;
        lblStatus.setText(currentStatus.substring(0, 1).toUpperCase() + currentStatus.substring(1).toLowerCase());

        switch (currentStatus.toLowerCase()) {
            case "processing":
                lblStatus.setBackground(new Color(220, 140, 40));
                lblStatus.setForeground(Color.WHITE);
                break;
            case "sold":
                lblStatus.setBackground(new Color(180, 60, 60));
                lblStatus.setForeground(Color.WHITE);
                break;
            case "available":
            default:
                lblStatus.setBackground(new Color(60, 140, 80));
                lblStatus.setForeground(Color.WHITE);
                break;
        }
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, String valueText, boolean isHighlight) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel lblTitle = new JLabel(labelText);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitle.setForeground(new Color(120, 80, 40));
        panel.add(lblTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        JLabel lblValue = new JLabel(valueText);
        lblValue.setFont(new Font("SansSerif", isHighlight ? Font.BOLD : Font.PLAIN, 12));
        lblValue.setForeground(isHighlight ? new Color(180, 120, 0) : new Color(50, 40, 30));
        panel.add(lblValue, gbc);
    }

    private void addCustomDetailRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent valueComponent) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel lblTitle = new JLabel(labelText);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitle.setForeground(new Color(120, 80, 40));
        panel.add(lblTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(valueComponent, gbc);
    }
}
