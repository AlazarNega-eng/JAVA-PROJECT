package com.tourism.ui;

import com.tourism.dao.HotelDAO;
import com.tourism.model.Hotel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

public class HotelManagementPanel extends JPanel {

    private JTable hotelTable;
    private DefaultTableModel tableModel;
    private HotelDAO hotelDAO;

    // Input fields
    private JTextField idField;
    private JTextField nameField;
    private JTextField locationField;
    private JTextField contactField;
    private JTextField ratingField;

    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    public HotelManagementPanel() {
        hotelDAO = new HotelDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setupTable();
        JScrollPane scrollPane = new JScrollPane(hotelTable);

        JPanel formPanel = setupFormPanel();
        JPanel buttonPanel = setupButtonPanel();

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        loadHotels();

        hotelTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && hotelTable.getSelectedRow() != -1) {
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
        tableModel.addColumn("Location");
        tableModel.addColumn("Contact");
        tableModel.addColumn("Rating");

        hotelTable = new JTable(tableModel);
        hotelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hotelTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        hotelTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        hotelTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        hotelTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        hotelTable.getColumnModel().getColumn(4).setPreferredWidth(60);
    }

    private JPanel setupFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        idField = new JTextField(5);
        idField.setEditable(false);
        nameField = new JTextField(20);
        locationField = new JTextField(20);
        contactField = new JTextField(15);
        ratingField = new JTextField(5); // For ratings like 4.5

        // Row 0: ID and Name
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(idField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Name*:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridwidth = 3; formPanel.add(nameField, gbc);
        gbc.gridwidth = 1;

        // Row 1: Location, Contact, Rating
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth=1; formPanel.add(locationField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(contactField, gbc);
        gbc.gridx = 4; gbc.gridy = 1; formPanel.add(new JLabel("Rating (e.g., 4.5):"), gbc);
        gbc.gridx = 5; gbc.gridy = 1; formPanel.add(ratingField, gbc);


        return formPanel;
    }

    private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Hotel");
        updateButton = new JButton("Update Hotel");
        deleteButton = new JButton("Delete Hotel");
        clearButton = new JButton("Clear Form");

        addButton.addActionListener(this::addHotel);
        updateButton.addActionListener(this::updateHotel);
        deleteButton.addActionListener(this::deleteHotel);
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        return buttonPanel;
    }

    private void loadHotels() {
        tableModel.setRowCount(0);
        List<Hotel> hotels = hotelDAO.getAllHotels();
        if (hotels != null) {
            for (Hotel h : hotels) {
                Vector<Object> row = new Vector<>();
                row.add(h.getHotelId());
                row.add(h.getName());
                row.add(h.getLocation());
                row.add(h.getContact());
                row.add(h.getRating()); // BigDecimal can be added directly
                tableModel.addRow(row);
            }
        } else {
             JOptionPane.showMessageDialog(this, "Failed to load hotels.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

     private void populateFormFromSelectedRow() {
         int selectedRow = hotelTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(getStringValue(tableModel.getValueAt(selectedRow, 1)));
            locationField.setText(getStringValue(tableModel.getValueAt(selectedRow, 2)));
            contactField.setText(getStringValue(tableModel.getValueAt(selectedRow, 3)));
            ratingField.setText(getStringValue(tableModel.getValueAt(selectedRow, 4)));
        }
    }

     private String getStringValue(Object value) {
        return (value == null) ? "" : value.toString();
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        locationField.setText("");
        contactField.setText("");
        ratingField.setText("");
        hotelTable.clearSelection();
    }

     private Hotel getHotelFromForm() {
         if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hotel Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Hotel hotel = new Hotel();
        try {
            if (!idField.getText().trim().isEmpty()) {
                 hotel.setHotelId(Integer.parseInt(idField.getText().trim()));
            }
            hotel.setName(nameField.getText().trim());
            hotel.setLocation(locationField.getText().trim());
            hotel.setContact(contactField.getText().trim());

             // Parse rating, allow empty or null
            String ratingStr = ratingField.getText().trim();
            if (!ratingStr.isEmpty()) {
                hotel.setRating(new BigDecimal(ratingStr));
            } else {
                 hotel.setRating(null); // Explicitly set to null if empty
            }

            return hotel;
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "Invalid number format for Rating.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    // --- Action Methods ---
    private void addHotel(ActionEvent e) {
        Hotel newHotel = getHotelFromForm();
        if (newHotel != null) {
            newHotel.setHotelId(0);
            boolean success = hotelDAO.addHotel(newHotel);
            if (success) {
                JOptionPane.showMessageDialog(this, "Hotel added successfully!");
                loadHotels();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add hotel.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateHotel(ActionEvent e) {
         int selectedRow = hotelTable.getSelectedRow();
        if (selectedRow < 0) {
             JOptionPane.showMessageDialog(this, "Please select a hotel to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
         Hotel updatedHotel = getHotelFromForm();
         if (updatedHotel != null) {
              boolean success = hotelDAO.updateHotel(updatedHotel);
            if (success) {
                JOptionPane.showMessageDialog(this, "Hotel updated successfully!");
                loadHotels();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update hotel.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
         }
    }

    private void deleteHotel(ActionEvent e) {
        int selectedRow = hotelTable.getSelectedRow();
        if (selectedRow < 0) {
             JOptionPane.showMessageDialog(this, "Please select a hotel to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int hotelId = (int) tableModel.getValueAt(selectedRow, 0);
        String hotelName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete hotel '" + hotelName + "' (ID: " + hotelId + ")?\nThis might fail if it's linked to packages.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            // Note: Might need to handle package_hotel links manually if ON DELETE is not CASCADE
            boolean success = hotelDAO.deleteHotel(hotelId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Hotel deleted successfully!");
                loadHotels();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete hotel.\nIt might be linked to existing packages.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}