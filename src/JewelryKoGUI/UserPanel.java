package JewelryKoGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import JewelryKoDatabase.AdminData;
import JewelryKoDatabase.ClientData;

public class UserPanel extends JPanel {

    private Window parentWindow;
    private JTable adminTable;
    private DefaultTableModel adminModel;

    private JTable clientTable;
    private DefaultTableModel clientModel;

    private static final String MASTER_PASSCODE = "jewelryko";

    private final Color COLOR_BG = new Color(245, 235, 220);
    private final Color COLOR_PANEL_BG = new Color(253, 245, 230);
    private final Color COLOR_BORDER = new Color(210, 180, 140);
    private final Color COLOR_TEXT_DARK = new Color(180, 120, 60);
    private final Color COLOR_BTN_ACTIVE = new Color(200, 150, 90);

    public UserPanel() {
        this(null);
    }

    public UserPanel(Window parentWindow) {
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

        JLabel lblTitle = new JLabel("JEWELRYKO - USER MANAGEMENT PANEL");
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

        JPanel containerPanel = new JPanel(new BorderLayout(10, 10));
        containerPanel.setBackground(COLOR_PANEL_BG);
        containerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 2),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel topHeaderPanel = new JPanel(new BorderLayout());
        topHeaderPanel.setOpaque(false);

        JLabel lblHeader = new JLabel("JEWELRYKO - MANAGEMENT PANEL");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblHeader.setForeground(COLOR_TEXT_DARK);
        topHeaderPanel.add(lblHeader, BorderLayout.WEST);

        JButton btnLock = new JButton("Lock Panel");
        btnLock.setPreferredSize(new Dimension(110, 30));
        btnLock.setBackground(new Color(220, 190, 170));
        btnLock.setForeground(COLOR_TEXT_DARK);
        btnLock.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnLock.setFocusPainted(false);
        btnLock.addActionListener(e -> showLockedView());

        JPanel lockWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        lockWrapper.setOpaque(false);
        lockWrapper.add(btnLock);
        topHeaderPanel.add(lockWrapper, BorderLayout.EAST);

        containerPanel.add(topHeaderPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COLOR_PANEL_BG);
        tabbedPane.setForeground(COLOR_TEXT_DARK);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));

        adminModel = new DefaultTableModel(new String[]{"Start Date", "Position", "Admin Name", "Admin ID", "Passcode", "Salary Rate"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        adminTable = new JTable(adminModel);
        styleTable(adminTable);

        JPanel adminPanel = new JPanel(new BorderLayout(10, 10));
        adminPanel.setBackground(COLOR_PANEL_BG);
        adminPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JScrollPane adminScroll = new JScrollPane(adminTable);
        adminScroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        adminPanel.add(adminScroll, BorderLayout.CENTER);

        JPanel adminButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        adminButtonPanel.setOpaque(false);

        JButton addAdminButton = createStyledButton("Add Admin");
        JButton removeAdminButton = createStyledButton("Remove Admin");
        JButton refreshAdminButton = createStyledButton("Refresh Admins");

        adminButtonPanel.add(addAdminButton);
        adminButtonPanel.add(removeAdminButton);
        adminButtonPanel.add(refreshAdminButton);
        adminPanel.add(adminButtonPanel, BorderLayout.SOUTH);

        addAdminButton.addActionListener(e -> showAddAdminDialog());
        removeAdminButton.addActionListener(e -> removeSelectedAdmin());
        refreshAdminButton.addActionListener(e -> loadAdminData());
        tabbedPane.addTab("Admins", adminPanel);

        clientModel = new DefaultTableModel(new String[]{"Client Name", "Address", "Transaction ID", "Date", "Total Price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        clientTable = new JTable(clientModel);
        styleTable(clientTable);

        JPanel clientPanel = new JPanel(new BorderLayout(10, 10));
        clientPanel.setBackground(COLOR_PANEL_BG);
        clientPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JScrollPane clientScroll = new JScrollPane(clientTable);
        clientScroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        clientPanel.add(clientScroll, BorderLayout.CENTER);

        JPanel clientButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        clientButtonPanel.setOpaque(false);
        JButton refreshClientButton = createStyledButton("Refresh Clients");
        clientButtonPanel.add(refreshClientButton);
        clientPanel.add(clientButtonPanel, BorderLayout.SOUTH);

        refreshClientButton.addActionListener(e -> loadClientData());
        tabbedPane.addTab("Clients", clientPanel);

        containerPanel.add(tabbedPane, BorderLayout.CENTER);
        add(containerPanel, BorderLayout.CENTER);

        loadAdminData();
        loadClientData();

        revalidate();
        repaint();
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
        btnSubmit.setBackground(COLOR_BTN_ACTIVE);
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

    private void styleTable(JTable table) {
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setGridColor(COLOR_BORDER);
        table.setSelectionBackground(new Color(230, 210, 180));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.DARK_GRAY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(COLOR_BTN_ACTIVE);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(150, 36));
        btn.setBackground(COLOR_BTN_ACTIVE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        return btn;
    }

    private void showAddAdminDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Admin", true);
        dialog.setSize(420, 420);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL_BG);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        JTextField txtStartDate = new JTextField(currentDate);
        JTextField txtPosition = new JTextField();
        JTextField txtAdminName = new JTextField();
        JTextField txtAdminId = new JTextField();
        JPasswordField txtPasscode = new JPasswordField();
        JTextField txtSalaryRate = new JTextField();

        addFormRow(panel, gbc, 0, "Start Date:", txtStartDate);
        addFormRow(panel, gbc, 1, "Position:", txtPosition);
        addFormRow(panel, gbc, 2, "Admin Name:", txtAdminName);
        addFormRow(panel, gbc, 3, "Admin ID:", txtAdminId);
        addFormRow(panel, gbc, 4, "Passcode:", txtPasscode);
        addFormRow(panel, gbc, 5, "Salary Rate:", txtSalaryRate);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(90, 32));
        btnCancel.setBackground(new Color(220, 210, 195));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnSave = new JButton("Save");
        btnSave.setPreferredSize(new Dimension(90, 32));
        btnSave.setBackground(COLOR_BTN_ACTIVE);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSave.setFocusPainted(false);

        btnSave.addActionListener(e -> {
            try {
                String startDate = txtStartDate.getText().trim();
                String position = txtPosition.getText().trim();
                String adminName = txtAdminName.getText().trim();
                String adminId = txtAdminId.getText().trim();
                String passcode = new String(txtPasscode.getPassword()).trim();
                String salaryRate = txtSalaryRate.getText().trim();

                if (adminName.isEmpty() || adminId.isEmpty() || passcode.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Admin Name, ID, and Passcode cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean success = AdminData.addAdmin(startDate, position, adminName, adminId, passcode, salaryRate);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Admin added successfully! Credentials are now active for login.");
                    dialog.dispose();
                    loadAdminData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add admin. Admin ID might already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding admin: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 0, 0, 0);
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void removeSelectedAdmin() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an admin from the table to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window win = parentWindow != null ? parentWindow : SwingUtilities.getWindowAncestor(this);
        if (!authenticateMasterPasscode(win)) {
            return;
        }

        String adminId = adminModel.getValueAt(selectedRow, 3).toString();
        String adminName = adminModel.getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove admin: " + adminName + " (ID: " + adminId + ")?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = AdminData.deleteAdmin(adminId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Admin removed successfully.");
                    loadAdminData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove admin from database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error removing admin: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(COLOR_TEXT_DARK);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        component.setPreferredSize(new Dimension(180, 26));
        panel.add(component, gbc);
    }

    private void loadAdminData() {
        AdminData.loadAdminsIntoModel(adminModel);
    }

    private void loadClientData() {
        ClientData.loadClientsIntoModel(clientModel);
    }
}
