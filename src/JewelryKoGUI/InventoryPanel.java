package JewelryKoGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import JewelryKoDatabase.InventoryData;

public class InventoryPanel extends JPanel {

    private JPanel gridContainer;
    private JTextField txtSearch;

    private String selectedGender = "";
    private String selectedType = "";
    private String selectedKarat = "";
    private String selectedSize = "";

    private ButtonGroup bgGender;
    private ButtonGroup bgType;
    private ButtonGroup bgKarat;

    private JComboBox<String> cbSizeFilter;
    private JSlider sliderPrice;
    private JSlider sliderWeight;

    private JLabel lblPriceVal;
    private JLabel lblWeightVal;

    private double activeCurrentRate = 0.00;

    private final Color COLOR_BTN_IDLE = new Color(238, 214, 172);
    private final Color COLOR_BTN_ACTIVE = new Color(200, 150, 90);

    public InventoryPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 235, 220));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane sidebarScrollPane = new JScrollPane(createSidebarPanel());
        sidebarScrollPane.setPreferredSize(new Dimension(300, 0));
        sidebarScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidebarScrollPane.getVerticalScrollBar().setUnitIncrement(12);
        sidebarScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 190, 140), 1));
        add(sidebarScrollPane, BorderLayout.WEST);

        gridContainer = new JPanel(new GridLayout(0, 3, 15, 15));
        gridContainer.setBackground(new Color(245, 235, 220));

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(new Color(245, 235, 220));
        gridWrapper.add(gridContainer, BorderLayout.NORTH);

        JScrollPane rightScrollPane = new JScrollPane(gridWrapper);
        rightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        rightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rightScrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 124), 2));
        rightScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(rightScrollPane, BorderLayout.CENTER);

        refreshGrid();
    }

    public void setActiveCurrentRate(double rate) {
        this.activeCurrentRate = rate;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(new Color(253, 245, 230));
        sidebar.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 0, 4, 0);

        sidebar.add(createLeftAlignedLabel("Search"), gbc);
        gbc.gridy++;

        JPanel pnlSearch = new JPanel();
        pnlSearch.setLayout(new BoxLayout(pnlSearch, BoxLayout.X_AXIS));
        pnlSearch.setOpaque(false);
        txtSearch = new JTextField();
        txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        txtSearch.addActionListener(e -> filterGrid());

        pnlSearch.add(txtSearch);
        pnlSearch.add(Box.createHorizontalStrut(4));
        sidebar.add(pnlSearch, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 4, 0);
        sidebar.add(createLeftAlignedLabel("Gender"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        JPanel genderPanel = new JPanel(new GridLayout(1, 3, 4, 4));
        genderPanel.setOpaque(false);
        String[] genders = {"Male", "Female", "Unisex"};
        bgGender = new ButtonGroup();
        for (String g : genders) {
            JToggleButton btn = createFilterButton(g);
            btn.addActionListener(e -> {
                selectedGender = btn.isSelected() ? g : "";
                filterGrid();
            });
            bgGender.add(btn);
            genderPanel.add(btn);
        }
        sidebar.add(genderPanel, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 4, 0);
        sidebar.add(createLeftAlignedLabel("Jewelry Type"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        JPanel typePanel = new JPanel(new GridLayout(3, 3, 4, 4));
        typePanel.setOpaque(false);
        String[] types = {"Necklace", "Ring", "Bracelet", "Bangle", "Earrings", "Anklet", "Pendant", "Cuff", "Brooch"};
        bgType = new ButtonGroup();
        for (String t : types) {
            JToggleButton btn = createFilterButton(t);
            btn.addActionListener(e -> {
                selectedType = btn.isSelected() ? t : "";
                filterGrid();
            });
            bgType.add(btn);
            typePanel.add(btn);
        }
        sidebar.add(typePanel, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 4, 0);
        sidebar.add(createLeftAlignedLabel("Gold Karat"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        JPanel karatPanel = new JPanel(new GridLayout(1, 4, 4, 4));
        karatPanel.setOpaque(false);
        String[] karats = {"10K", "14K", "18K", "21K"};
        bgKarat = new ButtonGroup();
        for (String k : karats) {
            JToggleButton btn = createFilterButton(k);
            btn.addActionListener(e -> {
                selectedKarat = btn.isSelected() ? k : "";
                filterGrid();
            });
            bgKarat.add(btn);
            karatPanel.add(btn);
        }
        sidebar.add(karatPanel, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 2, 0);
        sidebar.add(createLeftAlignedLabel("Size"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        String[] sizeOptions = new String[62];
        sizeOptions[0] = "Any";
        int index = 1;
        for (double s = 1.0; s <= 30.0; s += 0.5) {
            if (s == (int) s) {
                sizeOptions[index++] = String.valueOf((int) s);
            } else {
                sizeOptions[index++] = String.valueOf(s);
            }
        }
        cbSizeFilter = new JComboBox<>(sizeOptions);
        cbSizeFilter.addActionListener(e -> {
            selectedSize = cbSizeFilter.getSelectedIndex() == 0 ? "" : (String) cbSizeFilter.getSelectedItem();
            filterGrid();
        });
        sidebar.add(cbSizeFilter, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 2, 0);
        lblPriceVal = createLeftAlignedLabel("Max Price: PHP 1,000,000.00");
        sidebar.add(lblPriceVal, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        sliderPrice = new JSlider(0, 1000000, 1000000);
        configureSlider(sliderPrice, () -> {
            lblPriceVal.setText(String.format("Max Price: PHP %,d.00", sliderPrice.getValue()));
            filterGrid();
        });
        sidebar.add(sliderPrice, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(10, 0, 2, 0);
        lblWeightVal = createLeftAlignedLabel("Max Weight: 150.0g");
        sidebar.add(lblWeightVal, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);

        sliderWeight = new JSlider(0, 150, 150);
        configureSlider(sliderWeight, () -> {
            lblWeightVal.setText(String.format("Max Weight: %.1fg", (double) sliderWeight.getValue()));
            filterGrid();
        });
        sidebar.add(sliderWeight, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(16, 0, 8, 0);
        JPanel actionButtonPanel = new JPanel(new GridLayout(1, 2, 6, 0));
        actionButtonPanel.setOpaque(false);

        JButton btnAddItem = new JButton("Add Item");
        btnAddItem.setPreferredSize(new Dimension(0, 38));
        btnAddItem.setBackground(new Color(200, 150, 90));
        btnAddItem.setForeground(Color.WHITE);
        btnAddItem.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAddItem.setFocusPainted(false);
        btnAddItem.addActionListener(e -> showAddItemDialog());

        JButton btnReset = new JButton("Reset Filters");
        btnReset.setPreferredSize(new Dimension(0, 38));
        btnReset.setBackground(new Color(180, 70, 60));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> resetFilters());

        actionButtonPanel.add(btnAddItem);
        actionButtonPanel.add(btnReset);

        sidebar.add(actionButtonPanel, gbc);
        gbc.gridy++;

        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        sidebar.add(Box.createGlue(), gbc);

        return sidebar;
    }

    private JLabel createLeftAlignedLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(new Color(120, 80, 40));
        return lbl;
    }

    private JToggleButton createFilterButton(String text) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 10));
        btn.setBackground(COLOR_BTN_IDLE);
        btn.setForeground(new Color(80, 50, 20));
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(4, 2, 4, 2));
        btn.setBorder(BorderFactory.createLineBorder(new Color(210, 170, 120), 1));

        btn.addItemListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(COLOR_BTN_ACTIVE);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(COLOR_BTN_IDLE);
                btn.setForeground(new Color(80, 50, 20));
            }
        });
        return btn;
    }

    private void configureSlider(JSlider slider, Runnable onUpdate) {
        slider.setOpaque(false);
        slider.addChangeListener(e -> onUpdate.run());
    }

    private void resetFilters() {
        txtSearch.setText("");

        selectedGender = "";
        selectedType = "";
        selectedKarat = "";
        selectedSize = "";

        if (bgGender != null) bgGender.clearSelection();
        if (bgType != null) bgType.clearSelection();
        if (bgKarat != null) bgKarat.clearSelection();

        if (cbSizeFilter != null) cbSizeFilter.setSelectedIndex(0);
        sliderPrice.setValue(1000000);
        sliderWeight.setValue(150);

        lblPriceVal.setText("Max Price: PHP 1,000,000.00");
        lblWeightVal.setText("Max Weight: 150.0g");

        refreshGrid();
    }

    public void refreshInventory() {
        refreshGrid();
    }

    public void refreshGrid() {
        gridContainer.removeAll();
        for (InventoryData.JewelryItem item : InventoryData.getItemList()) {
            if ("Available".equalsIgnoreCase(item.status)) {
                gridContainer.add(createItemCard(item));
            }
        }
        gridContainer.revalidate();
        gridContainer.repaint();
    }

    private void filterGrid() {
        String query = txtSearch.getText().trim().toLowerCase();
        gridContainer.removeAll();

        for (InventoryData.JewelryItem item : InventoryData.getItemList()) {
            boolean matchesStatus = "Available".equalsIgnoreCase(item.status);
            
            boolean matchesSearch = query.isEmpty() ||
                                    (item.name != null && item.name.toLowerCase().contains(query)) ||
                                    (item.id != null && item.id.toLowerCase().contains(query));

            boolean matchesGender = selectedGender.isEmpty() ||
                                    (item.gender != null && item.gender.trim().equalsIgnoreCase(selectedGender));

            boolean matchesType = selectedType.isEmpty() ||
                                  (item.type != null && item.type.trim().equalsIgnoreCase(selectedType));

            boolean matchesKarat = selectedKarat.isEmpty() ||
                                   (item.karat != null && item.karat.trim().equalsIgnoreCase(selectedKarat));

            boolean matchesSize = true;
            if (!selectedSize.isEmpty()) {
                try {
                    double targetSize = Double.parseDouble(selectedSize);
                    double itemSize = Double.parseDouble(item.size.trim());
                    matchesSize = (Math.abs(itemSize - targetSize) < 0.01);
                } catch (Exception e) {
                    matchesSize = item.size != null && item.size.trim().equalsIgnoreCase(selectedSize);
                }
            }

            boolean matchesPrice = item.price <= sliderPrice.getValue();

            double itemWeight = 0;
            try {
                if (item.weight != null) {
                    itemWeight = Double.parseDouble(item.weight.trim());
                }
            } catch (Exception ignored) {}
            boolean matchesWeight = itemWeight <= sliderWeight.getValue();

            if (matchesStatus && matchesSearch && matchesGender && matchesType && matchesKarat && matchesSize && matchesPrice && matchesWeight) {
                gridContainer.add(createItemCard(item));
            }
        }
        gridContainer.revalidate();
        gridContainer.repaint();
    }

    private JPanel createItemCard(InventoryData.JewelryItem item) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(new Color(253, 245, 230));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 190, 140), 1));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblImage = new JLabel("", SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(150, 140));

        java.net.URL imgUrl = getClass().getResource(item.imagePath);
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image img = icon.getImage().getScaledInstance(140, 130, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(img));
        } else {
            lblImage.setText("[No Image]");
        }

        JLabel lblDetails = new JLabel("<html><center><b>" + item.name + "</b><br>"
                + item.id + "<br><font color='#B8860B'>PHP "
                + String.format("%,.2f", item.price) + "</font></center></html>", SwingConstants.CENTER);

        card.add(lblImage, BorderLayout.CENTER);
        card.add(lblDetails, BorderLayout.SOUTH);

        Runnable openViewAction = () -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(InventoryPanel.this);
            Frame parentFrame = null;

            if (parentWindow instanceof Frame) {
                parentFrame = (Frame) parentWindow;
            } else if (parentWindow instanceof Dialog) {
                Window owner = parentWindow.getOwner();
                if (owner instanceof Frame) {
                    parentFrame = (Frame) owner;
                }
            }

            ViewItemPanel viewPanel = new ViewItemPanel(parentFrame, item, InventoryPanel.this);
            viewPanel.setVisible(true);
        };

        MouseAdapter clickAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openViewAction.run();
            }
        };

        card.addMouseListener(clickAdapter);
        lblImage.addMouseListener(clickAdapter);
        lblDetails.addMouseListener(clickAdapter);

        return card;
    }

    private void showAddItemDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Item", true);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(253, 245, 230));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField txtName = new JTextField();
        JTextField txtId = new JTextField();
        JComboBox<String> cbGender = new JComboBox<>(new String[]{"Male", "Female", "Unisex"});
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Necklace", "Ring", "Bracelet", "Bangle", "Earrings", "Anklet", "Pendant", "Cuff", "Brooch"});
        JComboBox<String> cbKarat = new JComboBox<>(new String[]{"10K", "14K", "18K", "21K"});
        
        JTextField txtSize = new JTextField();
        JTextField txtWeight = new JTextField();
        JTextField txtBasePrice = new JTextField("0.00");
        JTextField txtImage = new JTextField("/Image/featured1.jpg");

        addFormRow(panel, gbc, 0, "Jewelry Name:", txtName);
        addFormRow(panel, gbc, 1, "Jewelry ID:", txtId);
        addFormRow(panel, gbc, 2, "Gender:", cbGender);
        addFormRow(panel, gbc, 3, "Jewelry Type:", cbType);
        addFormRow(panel, gbc, 4, "Gold Karat:", cbKarat);
        addFormRow(panel, gbc, 5, "Size:", txtSize);
        addFormRow(panel, gbc, 6, "Weight (g):", txtWeight);
        addFormRow(panel, gbc, 7, "Base Price (PHP):", txtBasePrice);
        addFormRow(panel, gbc, 8, "Image Source:", txtImage);

        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 0, 0, 0);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(90, 32));
        btnCancel.setBackground(new Color(220, 210, 195));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnAdd = new JButton("Add");
        btnAdd.setPreferredSize(new Dimension(90, 32));
        btnAdd.setBackground(new Color(200, 150, 90));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAdd.setFocusPainted(false);

        btnAdd.addActionListener(e -> {
            String name = txtName.getText().trim();
            String id = txtId.getText().trim();
            String size = txtSize.getText().trim();
            String weight = txtWeight.getText().trim();
            String basePriceStr = txtBasePrice.getText().trim();
            String imgPath = txtImage.getText().trim();

            if (name.isEmpty() || id.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and ID cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!size.isEmpty()) {
                try {
                    Double.parseDouble(size);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid input must be numerical.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    txtSize.requestFocus();
                    return;
                }
            }

            if (weight.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Weight cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                txtWeight.requestFocus();
                return;
            }
            double parsedWeight = 0.0;
            try {
                parsedWeight = Double.parseDouble(weight);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid weight must be numerical.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                txtWeight.requestFocus();
                return;
            }

            double parsedBasePrice = 0.0;
            if (!basePriceStr.isEmpty()) {
                try {
                    parsedBasePrice = Double.parseDouble(basePriceStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid base price must be numerical.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    txtBasePrice.requestFocus();
                    return;
                }
            }

            InventoryData.addItem(new InventoryData.JewelryItem(
                name,
                id,
                (String) cbGender.getSelectedItem(),
                (String) cbType.getSelectedItem(),
                (String) cbKarat.getSelectedItem(),
                size.isEmpty() ? "N/A" : size,
                weight,
                0.0, 
                parsedBasePrice,
                imgPath.isEmpty() ? "/Image/featured1.jpg" : imgPath,
                "Available"
            ), activeCurrentRate);

            dialog.dispose();
            refreshGrid();
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnAdd);
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setSize(420, 520);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(new Color(120, 80, 40));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        component.setPreferredSize(new Dimension(180, 26));
        panel.add(component, gbc);
    }
}
