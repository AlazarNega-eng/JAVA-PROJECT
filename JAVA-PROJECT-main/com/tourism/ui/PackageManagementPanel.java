package com.tourism.ui;

import com.tourism.dao.PackageDAO;
import com.tourism.model.Package;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector; // Use Vector for DefaultTableModel rows

public class PackageManagementPanel extends JPanel {

    private JTable packageTable;
    private DefaultTableModel tableModel;
    private PackageDAO packageDAO;

    // Input fields
    private JTextField idField; // Hidden or read-only for updates/deletes
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField locationField;
    private JTextField durationField;
    private JTextField priceField;

    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton; // To clear input fields


    public PackageManagementPanel() {
        packageDAO = new PackageDAO();
        setLayout(new BorderLayout(10, 10)); // Add gaps between components
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // --- Table Setup ---
        setupTable();
        JScrollPane scrollPane = new JScrollPane(packageTable);

        // --- Form Setup ---
        JPanel formPanel = setupFormPanel();

        // --- Button Panel ---
        JPanel buttonPanel = setupButtonPanel();

        // --- Layout ---
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH); // Form at the top
        add(buttonPanel, BorderLayout.SOUTH); // Buttons at the bottom

        // Load initial data
        loadPackages();

        // Add listener for table selection
        packageTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && packageTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });
    }

    private void setupTable() {
        // Non-editable table model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Location");
        tableModel.addColumn("Duration (days)");
        tableModel.addColumn("Price (Birr)");
        tableModel.addColumn("Description"); // Add description column

        packageTable = new JTable(tableModel);
        packageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one row selection
         // Set preferred column widths (optional)
        packageTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        packageTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        packageTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Location
        packageTable.getColumnModel().getColumn(3).setPreferredWidth(80); // Duration
        packageTable.getColumnModel().getColumn(4).setPreferredWidth(80); // Price
        packageTable.getColumnModel().getColumn(5).setPreferredWidth(250); // Description
    }

     private JPanel setupFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left

        // Initialize components
        idField = new JTextField(5); // Small field for ID
        idField.setEditable(false); // ID is usually not user-editable directly
        nameField = new JTextField(20);
        descriptionArea = new JTextArea(4, 20); // Rows, Columns
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        locationField = new JTextField(15);
        durationField = new JTextField(5);
        priceField = new JTextField(10);

        // Add components using GridBagLayout
        // Row 0: ID and Name
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(idField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridwidth = 3; formPanel.add(nameField, gbc); // Span multiple columns
        gbc.gridwidth = 1; // Reset gridwidth

        // Row 1: Location and Duration
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(locationField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Duration (days):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(durationField, gbc);
        gbc.gridx = 4; gbc.gridy = 1; formPanel.add(new JLabel("Price (Birr):"), gbc);
        gbc.gridx = 5; gbc.gridy = 1; formPanel.add(priceField, gbc);

        // Row 2: Description (takes more vertical space)
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST; formPanel.add(new JLabel("Description:"), gbc); // Align label top-left
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 5; gbc.fill = GridBagConstraints.HORIZONTAL; // Span columns and fill horizontally
        formPanel.add(descriptionScrollPane, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; // Reset fill and span
        gbc.anchor = GridBagConstraints.WEST; // Reset anchor


        return formPanel;
    }

    private JPanel setupButtonPanel() {
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center align buttons with gaps

        addButton = new JButton("Add Package");
        updateButton = new JButton("Update Package");
        deleteButton = new JButton("Delete Package");
        clearButton = new JButton("Clear Form");

        // Add Action Listeners
        addButton.addActionListener(this::addPackage);
        updateButton.addActionListener(this::updatePackage);
        deleteButton.addActionListener(this::deletePackage);
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        return buttonPanel;
    }

    private void loadPackages() {
        // Clear existing rows
        tableModel.setRowCount(0);

        List<Package> packages = packageDAO.getAllPackages();
        if (packages != null) {
            for (Package pkg : packages) {
                Vector<Object> row = new Vector<>();
                row.add(pkg.getPackageId());
                row.add(pkg.getName());
                row.add(pkg.getLocation());
                row.add(pkg.getDuration());
                row.add(pkg.getPrice());
                row.add(pkg.getDescription());
                tableModel.addRow(row);
            }
        } else {
             JOptionPane.showMessageDialog(this, "Failed to load packages from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelectedRow() {
         int selectedRow = packageTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            locationField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            durationField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            priceField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            // Handle potential null description
            Object descObj = tableModel.getValueAt(selectedRow, 5);
            descriptionArea.setText(descObj != null ? descObj.toString() : "");
        }
    }

     private void clearForm() {
        idField.setText("");
        nameField.setText("");
        descriptionArea.setText("");
        locationField.setText("");
        durationField.setText("");
        priceField.setText("");
        packageTable.clearSelection(); // Deselect table row
    }

     private Package getPackageFromForm() {
         // Basic validation
        if (nameField.getText().trim().isEmpty() ||
            durationField.getText().trim().isEmpty() ||
            priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Duration, and Price cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Package pkg = new Package();
        try {
            // ID is only needed for update/delete, handle parsing errors
            if (!idField.getText().trim().isEmpty()) {
                 pkg.setPackageId(Integer.parseInt(idField.getText().trim()));
            }

            pkg.setName(nameField.getText().trim());
            pkg.setDescription(descriptionArea.getText().trim());
            pkg.setLocation(locationField.getText().trim());
            pkg.setDuration(Integer.parseInt(durationField.getText().trim()));
            pkg.setPrice(new BigDecimal(priceField.getText().trim()));

            return pkg;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for Duration or Price.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    // --- Action Methods ---

    private void addPackage(ActionEvent e) {
         Package newPkg = getPackageFromForm();
         if (newPkg != null) {
              // Clear the ID field for adding a new record
             newPkg.setPackageId(0); // Or rely on the DAO to ignore it if using PK auto-increment

            boolean success = packageDAO.addPackage(newPkg);
            if (success) {
                JOptionPane.showMessageDialog(this, "Package added successfully!");
                loadPackages(); // Refresh table
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add package.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
         }
    }

    private void updatePackage(ActionEvent e) {
        int selectedRow = packageTable.getSelectedRow();
        if (selectedRow < 0) {
             JOptionPane.showMessageDialog(this, "Please select a package to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Package updatedPkg = getPackageFromForm();
        if (updatedPkg != null) {
            boolean success = packageDAO.updatePackage(updatedPkg);
            if (success) {
                JOptionPane.showMessageDialog(this, "Package updated successfully!");
                loadPackages(); // Refresh table
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update package.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePackage(ActionEvent e) {
         int selectedRow = packageTable.getSelectedRow();
        if (selectedRow < 0) {
             JOptionPane.showMessageDialog(this, "Please select a package to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int packageId = (int) tableModel.getValueAt(selectedRow, 0);
        String packageName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete package '" + packageName + "' (ID: " + packageId + ")?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = packageDAO.deletePackage(packageId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Package deleted successfully!");
                loadPackages(); // Refresh table
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete package. It might be used in existing bookings.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}