package JewelryKoGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import JewelryKoDatabase.TransactionData;

public class StatusPanel extends JPanel {

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton priorityButton;
    private JButton editStatusButton;

    private String selectedMonth = "";
    private String selectedDay = "";
    private String selectedYear = "";
    private String selectedStatus = "";
    private String selectedShipping = "";
    private String selectedType = "";

    private ButtonGroup bgStatus = new ButtonGroup();
    private ButtonGroup bgShipping = new ButtonGroup();
    private ButtonGroup bgType = new ButtonGroup();

    private final Color COLOR_BG = new Color(245, 235, 220);
    private final Color COLOR_PANEL_BG = new Color(253, 245, 230);
    private final Color COLOR_BORDER = new Color(210, 180, 140);
    private final Color COLOR_TEXT_DARK = new Color(180, 120, 60);
    private final Color COLOR_BTN_IDLE = new Color(238, 214, 172);
    private final Color COLOR_BTN_ACTIVE = new Color(200, 150, 90);

    public StatusPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane sidebarScroll = new JScrollPane(createSidebarPanel());
        sidebarScroll.setPreferredSize(new Dimension(310, 0));
        sidebarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(12);
        sidebarScroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        add(sidebarScroll, BorderLayout.WEST);

        add(createTablePanel(), BorderLayout.CENTER);

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                loadOrders();
            }
        });

        filterOrders();
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(COLOR_PANEL_BG);
        sidebar.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 0, 4, 0);

        sidebar.add(createGroupHeader("Search"), gbc);
        gbc.gridy++;

        JPanel pnlSearch = new JPanel();
        pnlSearch.setLayout(new BoxLayout(pnlSearch, BoxLayout.X_AXIS));
        pnlSearch.setOpaque(false);
        txtSearch = new JTextField();
        txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        txtSearch.addActionListener(e -> filterOrders());
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterOrders(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterOrders(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterOrders(); }
        });

        pnlSearch.add(txtSearch);
        pnlSearch.add(Box.createHorizontalStrut(4));
        sidebar.add(pnlSearch, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 4, 0);
        sidebar.add(createGroupHeader("Date of Purchase"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        JPanel pnlDate = new JPanel(new GridLayout(1, 3, 4, 0));
        pnlDate.setOpaque(false);

        String[] months = {"MONTH", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        JComboBox<String> cbMonth = new JComboBox<>(months);
        cbMonth.addActionListener(e -> {
            selectedMonth = cbMonth.getSelectedIndex() > 0 ? (String) cbMonth.getSelectedItem() : "";
            filterOrders();
        });

        String[] days = new String[32];
        days[0] = "DAY";
        for (int i = 1; i <= 31; i++) {
            days[i] = String.format("%02d", i);
        }
        JComboBox<String> cbDay = new JComboBox<>(days);
        cbDay.addActionListener(e -> {
            selectedDay = cbDay.getSelectedIndex() > 0 ? (String) cbDay.getSelectedItem() : "";
            filterOrders();
        });

        String[] years = {"YEAR", "2024", "2025", "2026", "2027"};
        JComboBox<String> cbYear = new JComboBox<>(years);
        cbYear.addActionListener(e -> {
            selectedYear = cbYear.getSelectedIndex() > 0 ? (String) cbYear.getSelectedItem() : "";
            filterOrders();
        });

        pnlDate.add(cbMonth);
        pnlDate.add(cbDay);
        pnlDate.add(cbYear);
        sidebar.add(pnlDate, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 4, 0);
        sidebar.add(createGroupHeader("Jewelry Type"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        JPanel pnlType = new JPanel(new GridLayout(3, 3, 4, 4));
        pnlType.setOpaque(false);
        String[] types = {"Necklace", "Ring", "Bracelet", "Bangle", "Earrings", "Anklet", "Pendant", "Cuff", "Brooch"};
        for (String t : types) {
            JToggleButton btn = createFilterButton(t, bgType);
            btn.addActionListener(e -> {
                selectedType = btn.isSelected() ? t : "";
                filterOrders();
            });
            pnlType.add(btn);
        }
        sidebar.add(pnlType, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 4, 0);
        sidebar.add(createGroupHeader("Order Status"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        JPanel pnlStatus = new JPanel(new GridLayout(2, 3, 4, 4));
        pnlStatus.setOpaque(false);
        for (String s : new String[]{"Reserved", "Processing", "Sold", "Shipped", "Delivered", "Cancelled"}) {
            JToggleButton btn = createFilterButton(s, bgStatus);
            btn.addActionListener(e -> {
                selectedStatus = btn.isSelected() ? s : "";
                filterOrders();
            });
            pnlStatus.add(btn);
        }
        sidebar.add(pnlStatus, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 4, 0);
        sidebar.add(createGroupHeader("Shipping Method"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        JPanel pnlShipping = new JPanel(new GridLayout(2, 2, 4, 4));
        pnlShipping.setOpaque(false);
        for (String sm : new String[]{"Pick-up", "LBC", "FedEx", "J&T"}) {
            JToggleButton btn = createFilterButton(sm, bgShipping);
            btn.addActionListener(e -> {
                selectedShipping = btn.isSelected() ? sm : "";
                filterOrders();
            });
            pnlShipping.add(btn);
        }
        sidebar.add(pnlShipping, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(16, 0, 8, 0);
        JButton btnReset = new JButton("Reset Filters");
        btnReset.setPreferredSize(new Dimension(0, 32));
        btnReset.setBackground(new Color(180, 70, 60));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> resetFilters(cbMonth, cbDay, cbYear));
        sidebar.add(btnReset, gbc);
        gbc.gridy++;

        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        sidebar.add(new JLabel(), gbc);

        return sidebar;
    }

    private JLabel createGroupHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(COLOR_TEXT_DARK);
        return label;
    }

    private JToggleButton createFilterButton(String text, ButtonGroup group) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setBackground(COLOR_BTN_IDLE);
        btn.setForeground(new Color(60, 40, 20));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        group.add(btn);
        return btn;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 2),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel("ORDER STATUS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {
            "Date of Purchase",
            "Order ID",
            "Client Name",
            "Address",
            "Jewelry ID",
            "Shipping Method",
            "Total Price",
            "Status",
            "Priority"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setRowHeight(26);
        orderTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        orderTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        orderTable.getTableHeader().setBackground(COLOR_BTN_ACTIVE);
        orderTable.getTableHeader().setForeground(Color.WHITE);
        orderTable.setGridColor(COLOR_BORDER);
        orderTable.setSelectionBackground(new Color(230, 210, 180));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < orderTable.getColumnCount(); i++) {
            orderTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);

        priorityButton = new JButton("Mark as Priority");
        priorityButton.setPreferredSize(new Dimension(150, 36));
        priorityButton.setBackground(COLOR_BTN_ACTIVE);
        priorityButton.setForeground(Color.WHITE);
        priorityButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        priorityButton.setFocusPainted(false);

        editStatusButton = new JButton("Edit Status");
        editStatusButton.setPreferredSize(new Dimension(130, 36));
        editStatusButton.setBackground(COLOR_BTN_IDLE);
        editStatusButton.setForeground(new Color(60, 40, 20));
        editStatusButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        editStatusButton.setFocusPainted(false);

        buttonPanel.add(priorityButton);
        buttonPanel.add(editStatusButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        priorityButton.addActionListener(e -> markAsPriority());
        editStatusButton.addActionListener(e -> showEditStatusDialog());

        return panel;
    }

    public void loadOrders() {
        filterOrders();
    }

    public void filterOrders() {
        tableModel.setRowCount(0);
        List<TransactionData.Transaction> transactions = TransactionData.getTransactionList();
        if (transactions == null) {
            return;
        }

        String query = txtSearch.getText().trim().toLowerCase();

        for (TransactionData.Transaction t : transactions) {
            String status = (t.status != null && !t.status.isEmpty()) ? t.status : "Processing";

            String lowerStatus = status.toLowerCase();
            boolean isNotAvailable = lowerStatus.contains("reserved") ||
                                     lowerStatus.contains("processing") ||
                                     lowerStatus.contains("sold") ||
                                     lowerStatus.contains("shipped") ||
                                     lowerStatus.contains("delivered") ||
                                     lowerStatus.contains("cancelled");
            if (!isNotAvailable) {
                continue;
            }

            boolean matchesDate = true;
            if (t.date != null && t.date.length() >= 10) {
                String tYear = t.date.substring(0, 4);
                String tMonth = t.date.substring(5, 7);
                String tDay = t.date.substring(8, 10);

                if (!selectedYear.isEmpty() && !tYear.equals(selectedYear)) {
                    matchesDate = false;
                }
                if (!selectedMonth.isEmpty() && !tMonth.equals(selectedMonth)) {
                    matchesDate = false;
                }
                if (!selectedDay.isEmpty() && !tDay.equals(selectedDay)) {
                    matchesDate = false;
                }
            } else {
                if (!selectedYear.isEmpty() || !selectedMonth.isEmpty() || !selectedDay.isEmpty()) {
                    matchesDate = false;
                }
            }

            boolean matchesStatus = selectedStatus.isEmpty() || status.equalsIgnoreCase(selectedStatus);
            boolean matchesType = selectedType.isEmpty() || (t.type != null && t.type.equalsIgnoreCase(selectedType));
            String shipping = (t.shippingMethod != null && !t.shippingMethod.isEmpty()) ? t.shippingMethod : "Pick-up";
            boolean matchesShipping = selectedShipping.isEmpty() || shipping.equalsIgnoreCase(selectedShipping);

            String jId = t.jewelryId != null ? t.jewelryId : t.itemId;
            boolean matchesSearch = query.isEmpty() ||
                    (jId != null && jId.toLowerCase().contains(query)) ||
                    (t.clientName != null && t.clientName.toLowerCase().contains(query));

            if (matchesDate && matchesStatus && matchesType && matchesShipping && matchesSearch) {
                String priority = (t.priority != null && !t.priority.isEmpty()) ? t.priority : "No";

                tableModel.addRow(new Object[]{
                    t.date,
                    t.recordId != null ? t.recordId : (t.jewelryId != null ? t.jewelryId : t.itemId),
                    t.clientName,
                    t.address,
                    jId,
                    shipping,
                    String.format("PHP %,.2f", t.totalPrice > 0 ? t.totalPrice : t.price),
                    status,
                    priority
                });
            }
        }
    }

    private void showEditStatusDialog() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dateOfPurchase = tableModel.getValueAt(selectedRow, 0).toString();
        String orderIdOrDisplay = tableModel.getValueAt(selectedRow, 1).toString();
        String clientName = tableModel.getValueAt(selectedRow, 2).toString();
        String jewelryId = tableModel.getValueAt(selectedRow, 4).toString();

        TransactionData.Transaction targetTransaction = null;
        List<TransactionData.Transaction> transactions = TransactionData.getTransactionList();
        
        if (transactions != null) {
            for (TransactionData.Transaction t : transactions) {
                boolean matchesRecordId = (t.recordId != null && t.recordId.equals(orderIdOrDisplay));
                boolean matchesDetails = 
                    (t.date != null && t.date.equals(dateOfPurchase)) &&
                    (t.clientName != null && t.clientName.equalsIgnoreCase(clientName)) &&
                    ((t.jewelryId != null && t.jewelryId.equals(jewelryId)) || 
                     (t.itemId != null && t.itemId.equals(jewelryId)));

                if (matchesRecordId || matchesDetails) {
                    targetTransaction = t;
                    break;
                }
            }
        }

        if (targetTransaction == null) {
            JOptionPane.showMessageDialog(this, "Could not locate matching transaction record in storage.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog statusDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Order Status", true);
        statusDialog.setSize(380, 240);
        statusDialog.setResizable(false);
        statusDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL_BG);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        JLabel lblTitle = new JLabel("Update Status for: " + clientName);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT_DARK);
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.0;
        panel.add(new JLabel("New Status:"), gbc);

        String[] statuses = {"Reserved", "Processing", "Sold", "Shipped", "Delivered", "Cancelled"};
        JComboBox<String> cbStatus = new JComboBox<>(statuses);
        cbStatus.setSelectedItem(targetTransaction.status != null ? targetTransaction.status : "Processing");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(cbStatus, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton btnSave = new JButton("Save Status");
        btnSave.setBackground(COLOR_BTN_ACTIVE);
        btnSave.setForeground(Color.WHITE);

        TransactionData.Transaction finalSelected = targetTransaction;
        btnSave.addActionListener(e -> {
            String newStatus = (String) cbStatus.getSelectedItem();
            if (newStatus != null && !newStatus.trim().isEmpty()) {
                finalSelected.status = newStatus.trim();
                
                if (newStatus.trim().equalsIgnoreCase("Cancelled")) {
                    finalSelected.paymentStatus = "Cancelled";
                }

                TransactionData.updateTransaction(finalSelected);

                tableModel.setValueAt(newStatus.trim(), selectedRow, 7);

                JOptionPane.showMessageDialog(statusDialog, "Status updated successfully!");
                statusDialog.dispose();
                
                filterOrders();
            }
        });
        btnPanel.add(btnSave);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(btnPanel, gbc);

        statusDialog.add(panel);
        statusDialog.setVisible(true);
    }

    private void markAsPriority() {
        int selectedRow = orderTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an order first.",
                "No Order Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String dateOfPurchase = tableModel.getValueAt(selectedRow, 0).toString();
        String orderIdOrDisplay = tableModel.getValueAt(selectedRow, 1).toString();
        String clientName = tableModel.getValueAt(selectedRow, 2).toString();
        String jewelryId = tableModel.getValueAt(selectedRow, 4).toString();

        TransactionData.Transaction targetTransaction = null;
        List<TransactionData.Transaction> transactions = TransactionData.getTransactionList();
        
        if (transactions != null) {
            for (TransactionData.Transaction t : transactions) {
                boolean matchesRecordId = (t.recordId != null && t.recordId.equals(orderIdOrDisplay));
                boolean matchesDetails = 
                    (t.date != null && t.date.equals(dateOfPurchase)) &&
                    (t.clientName != null && t.clientName.equalsIgnoreCase(clientName)) &&
                    ((t.jewelryId != null && t.jewelryId.equals(jewelryId)) || 
                     (t.itemId != null && t.itemId.equals(jewelryId)));

                if (matchesRecordId || matchesDetails) {
                    targetTransaction = t;
                    break;
                }
            }
        }

        if (targetTransaction == null) {
            JOptionPane.showMessageDialog(this, "Could not locate matching transaction record.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentPriority = tableModel.getValueAt(selectedRow, 8).toString();

        if (currentPriority.equals("Yes")) {
            targetTransaction.priority = "No";
            tableModel.setValueAt("No", selectedRow, 8);
            JOptionPane.showMessageDialog(this, "Order priority has been removed.");
        } else {
            targetTransaction.priority = "Yes";
            tableModel.setValueAt("Yes", selectedRow, 8);
            JOptionPane.showMessageDialog(this, "Order has been marked as PRIORITY.");
        }

        TransactionData.updateTransaction(targetTransaction);
        filterOrders();
    }

    private void resetFilters(JComboBox<String> cbMonth, JComboBox<String> cbDay, JComboBox<String> cbYear) {
        txtSearch.setText("");
        cbMonth.setSelectedIndex(0);
        cbDay.setSelectedIndex(0);
        cbYear.setSelectedIndex(0);

        bgStatus.clearSelection();
        bgType.clearSelection();
        bgShipping.clearSelection();

        selectedMonth = "";
        selectedDay = "";
        selectedYear = "";
        selectedStatus = "";
        selectedType = "";
        selectedShipping = "";

        filterOrders();
    }
}