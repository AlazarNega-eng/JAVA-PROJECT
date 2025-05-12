package com.tourism.ui;

import com.tourism.dao.TouristDAO;
import com.tourism.model.Tourist;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

public class TouristManagementPanel extends JPanel {

    private JTable touristTable;
    private DefaultTableModel tableModel;
    private TouristDAO touristDAO;

    // Input fields
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField nationalityField;

    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    public TouristManagementPanel() {
        touristDAO = new TouristDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setupTable();
        JScrollPane scrollPane = new JScrollPane(touristTable);

        JPanel formPanel = setupFormPanel();
        JPanel buttonPanel = setupButtonPanel();

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        loadTourists();

        touristTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && touristTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });
    }

    private void setupTable() {
        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Email");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Nationality");
        // Address might be too long for a column, show in form

        touristTable = new JTable(tableModel);
        touristTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         // Set preferred column widths (optional)
        touristTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        touristTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        touristTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        touristTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        touristTable.getColumnModel().getColumn(4).setPreferredWidth(100);
    }

    private JPanel setupFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        idField = new JTextField(5);
        idField.setEditable(false);
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(15);
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        nationalityField = new JTextField(15);

        // Row 0: ID and Name
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(idField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Name*:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; formPanel.add(nameField, gbc);

        // Row 1: Email and Phone
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(emailField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(phoneField, gbc);

         // Row 2: Nationality and Address Label
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Nationality:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(nationalityField, gbc);
        gbc.gridx = 2; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST; formPanel.add(new JLabel("Address:"), gbc);
        gbc.anchor = GridBagConstraints.WEST; // Reset anchor

        // Row 3: Address Text Area
        gbc.gridx = 3; gbc.gridy = 2; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH; // Span rows and fill
        formPanel.add(addressScrollPane, gbc);
        gbc.gridheight = 1; gbc.fill = GridBagConstraints.NONE; // Reset


        return formPanel;
    }

     private JPanel setupButtonPanel() {
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Tourist");
        updateButton = new JButton("Update Tourist");
        deleteButton = new JButton("Delete Tourist");
        clearButton = new JButton("Clear Form");

        addButton.addActionListener(this::addTourist);
        updateButton.addActionListener(this::updateTourist);
        deleteButton.addActionListener(this::deleteTourist);
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        return buttonPanel;
    }

    private void loadTourists() {
        tableModel.setRowCount(0);
        List<Tourist> tourists = touristDAO.getAllTourists();
        if (tourists != null) {
            for (Tourist t : tourists) {
                Vector<Object> row = new Vector<>();
                row.add(t.getTouristId());
                row.add(t.getName());
                row.add(t.getEmail());
                row.add(t.getPhone());
                row.add(t.getNationality());
                // Store full object or address in hidden column if needed, or fetch on selection
                tableModel.addRow(row);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to load tourists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

     private void populateFormFromSelectedRow() {
         int selectedRow = touristTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            // Handle potential nulls from DB, though table model might convert them
            emailField.setText(getStringValue(tableModel.getValueAt(selectedRow, 2)));
            phoneField.setText(getStringValue(tableModel.getValueAt(selectedRow, 3)));
            nationalityField.setText(getStringValue(tableModel.getValueAt(selectedRow, 4)));

            // Fetch address separately if not stored directly/fully in table
             Tourist selectedTourist = touristDAO.getTouristById(Integer.parseInt(idField.getText()));
             if(selectedTourist != null) {
                 addressArea.setText(selectedTourist.getAddress());
             } else {
                 addressArea.setText(""); // Clear if fetch fails
             }
        }
    }

    // Helper to handle potential null values from table model
    private String getStringValue(Object value) {
        return (value == null) ? "" : value.toString();
    }

     private void clearForm() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        nationalityField.setText("");
        touristTable.clearSelection();
    }

    private Tourist getTouristFromForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tourist Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        // Add more validation (e.g., email format) if needed

        Tourist tourist = new Tourist();
        try {
            // Only set ID if the ID field is populated (i.e., for updates)
            if (!idField.getText().trim().isEmpty()) {
                 tourist.setTouristId(Integer.parseInt(idField.getText().trim()));
            }
            tourist.setName(nameField.getText().trim());
            tourist.setEmail(emailField.getText().trim());
            tourist.setPhone(phoneField.getText().trim());
            tourist.setAddress(addressArea.getText().trim());
            tourist.setNationality(nationalityField.getText().trim());
            return tourist;
        } catch (NumberFormatException e) {
            // This shouldn't happen if idField is not editable by user, but good practice
            JOptionPane.showMessageDialog(this, "Invalid ID format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // --- Action Methods ---
    private void addTourist(ActionEvent e) {
         Tourist newTourist = getTouristFromForm();
         if (newTourist != null) {
              newTourist.setTouristId(0); // Ensure ID is not set for add operation
             boolean success = touristDAO.addTourist(newTourist);
            if (success) {
                JOptionPane.showMessageDialog(this, "Tourist added successfully!");
                loadTourists();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add tourist (e.g., duplicate email?).", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
         }
    }

    private void updateTourist(ActionEvent e) {
        int selectedRow = touristTable.getSelectedRow();
        if (selectedRow < 0) {
             JOptionPane.showMessageDialog(this, "Please select a tourist to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Tourist updatedTourist = getTouristFromForm();
         if (updatedTourist != null) {
              boolean success = touristDAO.updateTourist(updatedTourist);
            if (success) {
                JOptionPane.showMessageDialog(this, "Tourist updated successfully!");
                loadTourists();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update tourist (e.g., duplicate email?).", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
         }
    }

    private void deleteTourist(ActionEvent e) {
         int selectedRow = touristTable.getSelectedRow();
        if (selectedRow < 0) {
             JOptionPane.showMessageDialog(this, "Please select a tourist to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int touristId = (int) tableModel.getValueAt(selectedRow, 0);
        String touristName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete tourist '" + touristName + "' (ID: " + touristId + ")?\nThis might fail if they have existing bookings.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = touristDAO.deleteTourist(touristId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Tourist deleted successfully!");
                loadTourists();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete tourist.\nThey likely have existing bookings or other dependencies.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}