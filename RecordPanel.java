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
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import JewelryKoDatabase.TransactionData;

public class RecordPanel extends JPanel {

    private JTable recordTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    private String selectedMonth = "";
    private String selectedDay = "";
    private String selectedYear = "";
    private String selectedGender = "";
    private String selectedType = "";
    private String selectedPaymentStatus = "";
    private String selectedPaymentMethod = "";

    private ButtonGroup bgGender = new ButtonGroup();
    private ButtonGroup bgType = new ButtonGroup();
    private ButtonGroup bgPaymentStatus = new ButtonGroup();
    private ButtonGroup bgPaymentMethod = new ButtonGroup();

    private final Color COLOR_BG = new Color(245, 235, 220);
    private final Color COLOR_PANEL_BG = new Color(253, 245, 230);
    private final Color COLOR_BORDER = new Color(210, 180, 140);
    private final Color COLOR_TEXT_DARK = new Color(180, 120, 60);
    private final Color COLOR_BTN_IDLE = new Color(238, 214, 172);
    private final Color COLOR_BTN_ACTIVE = new Color(200, 150, 90);

    public RecordPanel() {
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

        refreshTable();
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
        txtSearch.addActionListener(e -> filterRecords());

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterRecords(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterRecords(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterRecords(); }
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
            filterRecords();
        });

        String[] days = new String[32];
        days[0] = "DAY";
        for (int i = 1; i <= 31; i++) {
            days[i] = String.format("%02d", i);
        }
        JComboBox<String> cbDay = new JComboBox<>(days);
        cbDay.addActionListener(e -> {
            selectedDay = cbDay.getSelectedIndex() > 0 ? (String) cbDay.getSelectedItem() : "";
            filterRecords();
        });

        String[] years = {"YEAR", "2024", "2025", "2026", "2027"};
        JComboBox<String> cbYear = new JComboBox<>(years);
        cbYear.addActionListener(e -> {
            selectedYear = cbYear.getSelectedIndex() > 0 ? (String) cbYear.getSelectedItem() : "";
            filterRecords();
        });

        pnlDate.add(cbMonth);
        pnlDate.add(cbDay);
        pnlDate.add(cbYear);
        sidebar.add(pnlDate, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 4, 0);
        sidebar.add(createGroupHeader("Gender"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        JPanel pnlGender = new JPanel(new GridLayout(1, 3, 4, 4));
        pnlGender.setOpaque(false);
        for (String g : new String[]{"Male", "Female", "Unisex"}) {
            JToggleButton btn = createFilterButton(g, bgGender);
            btn.addActionListener(e -> {
                selectedGender = btn.isSelected() ? g : "";
                filterRecords();
            });
            pnlGender.add(btn);
        }
        sidebar.add(pnlGender, gbc);
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
                filterRecords();
            });
            pnlType.add(btn);
        }
        sidebar.add(pnlType, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 4, 0);
        sidebar.add(createGroupHeader("Payment Status"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        JPanel pnlPaymentStatus = new JPanel(new GridLayout(2, 2, 4, 4));
        pnlPaymentStatus.setOpaque(false);
        for (String ps : new String[]{"Unpaid", "Partial", "Paid", "Cancelled"}) {
            JToggleButton btn = createFilterButton(ps, bgPaymentStatus);
            btn.addActionListener(e -> {
                selectedPaymentStatus = btn.isSelected() ? ps : "";
                filterRecords();
            });
            pnlPaymentStatus.add(btn);
        }
        sidebar.add(pnlPaymentStatus, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 4, 0);
        sidebar.add(createGroupHeader("Payment Method"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        JPanel pnlPaymentMethod = new JPanel(new GridLayout(1, 4, 4, 4));
        pnlPaymentMethod.setOpaque(false);
        for (String pm : new String[]{"CASH", "GCASH", "BPI", "PSB"}) {
            JToggleButton btn = createFilterButton(pm, bgPaymentMethod);
            btn.addActionListener(e -> {
                selectedPaymentMethod = btn.isSelected() ? pm : "";
                filterRecords();
            });
            pnlPaymentMethod.add(btn);
        }
        sidebar.add(pnlPaymentMethod, gbc);
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

        JLabel lblHeader = new JLabel("Transaction Records");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblHeader.setForeground(COLOR_TEXT_DARK);
        panel.add(lblHeader, BorderLayout.NORTH);

        String[] columns = {
            "Date", "Record ID", "Client Name", "Address",
            "Jewelry ID", "Total Price", "Amount Paid",
            "Balance", "Payment Method", "Payment Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recordTable = new JTable(tableModel);
        recordTable.setRowHeight(26);
        recordTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        recordTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        recordTable.getTableHeader().setBackground(COLOR_BTN_ACTIVE);
        recordTable.getTableHeader().setForeground(Color.WHITE);
        recordTable.setGridColor(COLOR_BORDER);
        recordTable.setSelectionBackground(new Color(230, 210, 180));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < recordTable.getColumnCount(); i++) {
            recordTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(recordTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        bottomPanel.setOpaque(false);

        JButton btnRecordPayment = new JButton("Record Payment");
        btnRecordPayment.setPreferredSize(new Dimension(150, 36));
        btnRecordPayment.setBackground(COLOR_BTN_ACTIVE);
        btnRecordPayment.setForeground(Color.WHITE);
        btnRecordPayment.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnRecordPayment.setFocusPainted(false);
        btnRecordPayment.addActionListener(e -> showRecordPaymentDialog());

        bottomPanel.add(btnRecordPayment);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JTextField createDialogField(String text, boolean editable) {
        JTextField field = new JTextField(text);
        field.setEditable(editable);
        field.setPreferredSize(new Dimension(240, 26));
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return field;
    }

    private void addRecordFormRow(JPanel panel, GridBagConstraints gbc, int y, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void showRecordPaymentDialog() {
        int selectedRow = recordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String recordId = (String) tableModel.getValueAt(selectedRow, 1);
        TransactionData.Transaction selectedTransaction = null;
        for (TransactionData.Transaction t : TransactionData.getTransactionList()) {
            if (t.recordId.equals(recordId)) {
                selectedTransaction = t;
                break;
            }
        }

        if (selectedTransaction == null) {
            return;
        }

        JDialog recordDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Record Payment", true);
        recordDialog.setSize(450, 480);
        recordDialog.setResizable(false);
        recordDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL_BG);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        JLabel lblTitle = new JLabel("Record Payment");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_TEXT_DARK);
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        JTextField txtDate = createDialogField(currentDate, false);
        JTextField txtClient = createDialogField(selectedTransaction.clientName, false);
        JTextField txtAddress = createDialogField(selectedTransaction.address, false);
        JTextField txtJewelryId = createDialogField(selectedTransaction.jewelryId, false);
        JTextField txtTotalPrice = createDialogField(String.format("%.2f", selectedTransaction.totalPrice), false);
        JTextField txtAmountPaid = createDialogField("", true);
        JTextField txtRemainingBalance = createDialogField(String.format("%.2f", selectedTransaction.balance), false);
        JTextField txtPaymentMethod = createDialogField(selectedTransaction.paymentMethod, false);
        JTextField txtPaymentStatus = createDialogField(selectedTransaction.paymentStatus, false);

        TransactionData.Transaction finalSelected = selectedTransaction;
        txtAmountPaid.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) { calculate(); }
            @Override
            public void removeUpdate(DocumentEvent e) { calculate(); }
            @Override
            public void insertUpdate(DocumentEvent e) { calculate(); }

            private void calculate() {
                try {
                    String input = txtAmountPaid.getText().trim();
                    if (input.isEmpty()) {
                        txtRemainingBalance.setText(String.format("%.2f", finalSelected.balance));
                        txtPaymentStatus.setText(finalSelected.paymentStatus);
                        return;
                    }
                    double additionalPay = Double.parseDouble(input);
                    if (additionalPay < 0) {
                        txtRemainingBalance.setText("Invalid (-)");
                        txtPaymentStatus.setText("Invalid");
                        return;
                    }
                    double projectedAmountPaid = finalSelected.amountPaid + additionalPay;
                    double newBalance = Math.max(0, finalSelected.totalPrice - projectedAmountPaid);
                    
                    txtRemainingBalance.setText(String.format("%.2f", newBalance));

                    if (projectedAmountPaid <= 0.0) {
                        txtPaymentStatus.setText("Unpaid (Reserved)");
                    } else if (newBalance > 0.005) {
                        txtPaymentStatus.setText("Partially Paid");
                    } else {
                        txtPaymentStatus.setText("Paid");
                    }
                } catch (NumberFormatException ex) {
                    txtRemainingBalance.setText("Invalid");
                    txtPaymentStatus.setText("Invalid");
                }
            }
        });

        addRecordFormRow(panel, gbc, 1, "Date:", txtDate);
        addRecordFormRow(panel, gbc, 2, "Client Name:", txtClient);
        addRecordFormRow(panel, gbc, 3, "Address:", txtAddress);
        addRecordFormRow(panel, gbc, 4, "Jewelry ID:", txtJewelryId);
        addRecordFormRow(panel, gbc, 5, "Total Price:", txtTotalPrice);
        addRecordFormRow(panel, gbc, 6, "Amount Paid:", txtAmountPaid);
        addRecordFormRow(panel, gbc, 7, "Balance:", txtRemainingBalance);
        addRecordFormRow(panel, gbc, 8, "Payment Method:", txtPaymentMethod);
        addRecordFormRow(panel, gbc, 9, "Payment Status:", txtPaymentStatus);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton btnSubmit = new JButton("Save Payment");
        btnSubmit.setBackground(COLOR_BTN_ACTIVE);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.addActionListener(e -> {
            try {
                String input = txtAmountPaid.getText().trim();
                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(recordDialog, "Please enter an amount for Amount Paid.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double addedPaid = Double.parseDouble(input);

                if (addedPaid < 0) {
                    JOptionPane.showMessageDialog(recordDialog, "Payment amount cannot be negative.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double tolerance = 0.005;
                if (addedPaid > (finalSelected.balance + tolerance)) {
                    JOptionPane.showMessageDialog(recordDialog,
                        "Payment amount cannot exceed the remaining balance of PHP " + String.format("%,.2f", finalSelected.balance) + ".",
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                finalSelected.amountPaid += addedPaid;
                finalSelected.balance = Math.max(0.0, finalSelected.totalPrice - finalSelected.amountPaid);

                if (finalSelected.amountPaid <= 0.0) {
                    finalSelected.paymentStatus = "Unpaid";
                    finalSelected.status = "Reserved";
                } else if (finalSelected.balance > 0.005) {
                    finalSelected.paymentStatus = "Partial";
                    finalSelected.status = "Processing";
                } else {
                    finalSelected.paymentStatus = "Paid";
                    finalSelected.status = "Sold";
                }

                TransactionData.updateTransaction(finalSelected);

                JOptionPane.showMessageDialog(recordDialog, "Payment recorded successfully! Status updated to " + finalSelected.paymentStatus + " (" + finalSelected.status + ").");
                recordDialog.dispose();
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(recordDialog, "Please enter a valid numeric value for Amount Paid.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(btnSubmit);

        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(btnPanel, gbc);

        recordDialog.add(panel);
        recordDialog.setVisible(true);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<TransactionData.Transaction> transactions = TransactionData.getTransactionList();
        if (transactions != null) {
            for (TransactionData.Transaction t : transactions) {
                tableModel.addRow(new Object[]{
                    t.date, t.recordId, t.clientName, t.address,
                    t.jewelryId, String.format("PHP %,.2f", t.totalPrice), String.format("PHP %,.2f", t.amountPaid),
                    String.format("PHP %,.2f", t.balance), t.paymentMethod, t.paymentStatus
                });
            }
        }
    }

    private void filterRecords() {
        tableModel.setRowCount(0);
        List<TransactionData.Transaction> transactions = TransactionData.getTransactionList();
        if (transactions == null) {
            return;
        }

        String query = txtSearch.getText().trim().toLowerCase();

        for (TransactionData.Transaction t : transactions) {
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

            boolean matchesGender = selectedGender.isEmpty() || (t.gender != null && t.gender.equalsIgnoreCase(selectedGender));
            boolean matchesType = selectedType.isEmpty() || (t.type != null && t.type.equalsIgnoreCase(selectedType));
            boolean matchesPaymentStatus = selectedPaymentStatus.isEmpty() || (t.paymentStatus != null && t.paymentStatus.equalsIgnoreCase(selectedPaymentStatus));
            boolean matchesPaymentMethod = selectedPaymentMethod.isEmpty() || (t.paymentMethod != null && t.paymentMethod.equalsIgnoreCase(selectedPaymentMethod));

            boolean matchesSearch = query.isEmpty() ||
                    (t.clientName != null && t.clientName.toLowerCase().contains(query)) ||
                    (t.jewelryId != null && t.jewelryId.toLowerCase().contains(query)) ||
                    (t.recordId != null && t.recordId.toLowerCase().contains(query));

            if (matchesDate && matchesGender && matchesType && matchesPaymentStatus && matchesPaymentMethod && matchesSearch) {
                tableModel.addRow(new Object[]{
                    t.date, t.recordId, t.clientName, t.address,
                    t.jewelryId, String.format("PHP %,.2f", t.totalPrice), String.format("PHP %,.2f", t.amountPaid),
                    String.format("PHP %,.2f", t.balance), t.paymentMethod, t.paymentStatus
                });
            }
        }
    }

    private void resetFilters(JComboBox<String> cbMonth, JComboBox<String> cbDay, JComboBox<String> cbYear) {
        txtSearch.setText("");
        cbMonth.setSelectedIndex(0);
        cbDay.setSelectedIndex(0);
        cbYear.setSelectedIndex(0);

        bgGender.clearSelection();
        bgType.clearSelection();
        bgPaymentStatus.clearSelection();
        bgPaymentMethod.clearSelection();

        selectedMonth = "";
        selectedDay = "";
        selectedYear = "";
        selectedGender = "";
        selectedType = "";
        selectedPaymentStatus = "";
        selectedPaymentMethod = "";

        filterRecords();
    }
}