package com.tourism.ui;

import com.tourism.dao.GuideDAO;
import com.tourism.model.Guide;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

public class GuideManagementPanel extends JPanel {

    private JTable guideTable;
    private DefaultTableModel tableModel;
    private GuideDAO guideDAO;

    // Input fields
    private JTextField idField;
    private JTextField nameField;
    private JTextField contactField;
    private JTextField languageField;
    private JTextArea experienceArea;

    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

     public GuideManagementPanel() {
        guideDAO = new GuideDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setupTable();
        JScrollPane scrollPane = new JScrollPane(guideTable);

        JPanel formPanel = setupFormPanel();
        JPanel buttonPanel = setupButtonPanel();

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        loadGuides();

        guideTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && guideTable.getSelectedRow() != -1) {
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
        tableModel.addColumn("Contact");
        tableModel.addColumn("Language(s)");
        // Experience might be too long

        guideTable = new JTable(tableModel);
        guideTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guideTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        guideTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        guideTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        guideTable.getColumnModel().getColumn(3).setPreferredWidth(120);
    }

     private JPanel setupFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        idField = new JTextField(5);
        idField.setEditable(false);
        nameField = new JTextField(20);
        contactField = new JTextField(20);
        languageField = new JTextField(15);
        experienceArea = new JTextArea(3, 20);
        experienceArea.setLineWrap(true);
        experienceArea.setWrapStyleWord(true);
        JScrollPane experienceScrollPane = new JScrollPane(experienceArea);

        // Row 0: ID and Name
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(idField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Name*:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; formPanel.add(nameField, gbc);

        // Row 1: Contact and Language
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(contactField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Language(s):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(languageField, gbc);

         // Row 2: Experience Label
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST; formPanel.add(new JLabel("Experience:"), gbc);
        gbc.anchor = GridBagConstraints.WEST; // Reset anchor

        // Row 3: Experience Text Area
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(experienceScrollPane, gbc);
        gbc.gridwidth = 1; gbc.gridheight = 1; gbc.fill = GridBagConstraints.NONE; // Reset

        return formPanel;
    }

     private JPanel setupButtonPanel() {
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Guide");
        updateButton = new JButton("Update Guide");
        deleteButton = new JButton("Delete Guide");
        clearButton = new JButton("Clear Form");

        addButton.addActionListener(this::addGuide);
        updateButton.addActionListener(this::updateGuide);
        deleteButton.addActionListener(this::deleteGuide);
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        return buttonPanel;
    }

     private void loadGuides() {
        tableModel.setRowCount(0);
        List<Guide> guides = guideDAO.getAllGuides();
        if (guides != null) {
            for (Guide g : guides) {
                Vector<Object> row = new Vector<>();
                row.add(g.getGuideId());
                row.add(g.getName());
                row.add(g.getContact());
                row.add(g.getLanguage());
                tableModel.addRow(row);
            }
        } else {
             JOptionPane.showMessageDialog(this, "Failed to load guides.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

     private void populateFormFromSelectedRow() {
         int selectedRow = guideTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(getStringValue(tableModel.getValueAt(selectedRow, 1)));
            contactField.setText(getStringValue(tableModel.getValueAt(selectedRow, 2)));
            languageField.setText(getStringValue(tableModel.getValueAt(selectedRow, 3)));

            Guide selectedGuide = guideDAO.getGuideById(Integer.parseInt(idField.getText()));
             if(selectedGuide != null) {
                 experienceArea.setText(selectedGuide.getExperience());
             } else {
                  experienceArea.setText("");
             }
        }
    }

     private String getStringValue(Object value) {
        return (value == null) ? "" : value.toString();
    }

     private void clearForm() {
        idField.setText("");
        nameField.setText("");
        contactField.setText("");
        languageField.setText("");
        experienceArea.setText("");
        guideTable.clearSelection();
    }

    private Guide getGuideFromForm() {
         if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Guide Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Guide guide = new Guide();
        try {
             if (!idField.getText().trim().isEmpty()) {
                 guide.setGuideId(Integer.parseInt(idField.getText().trim()));
            }
            guide.setName(nameField.getText().trim());
            guide.setContact(contactField.getText().trim());
            guide.setLanguage(languageField.getText().trim());
            guide.setExperience(experienceArea.getText().trim());
            return guide;
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "Invalid ID format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

     // --- Action Methods ---
    private void addGuide(ActionEvent e) {
         Guide newGuide = getGuideFromForm();
         if (newGuide != null) {
              newGuide.setGuideId(0);
             boolean success = guideDAO.addGuide(newGuide);
            if (success) {
                JOptionPane.showMessageDialog(this, "Guide added successfully!");
                loadGuides();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add guide.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
         }
    }

     private void updateGuide(ActionEvent e) {
        int selectedRow = guideTable.getSelectedRow();
        if (selectedRow < 0) {
             JOptionPane.showMessageDialog(this, "Please select a guide to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
         Guide updatedGuide = getGuideFromForm();
         if (updatedGuide != null) {
             boolean success = guideDAO.updateGuide(updatedGuide);
            if (success) {
                JOptionPane.showMessageDialog(this, "Guide updated successfully!");
                loadGuides();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update guide.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
         }
    }

    private void deleteGuide(ActionEvent e) {
        int selectedRow = guideTable.getSelectedRow();
        if (selectedRow < 0) {
             JOptionPane.showMessageDialog(this, "Please select a guide to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int guideId = (int) tableModel.getValueAt(selectedRow, 0);
        String guideName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete guide '" + guideName + "' (ID: " + guideId + ")?\nThis might fail if they are assigned to bookings.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = guideDAO.deleteGuide(guideId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Guide deleted successfully!");
                loadGuides();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete guide.\nThey might be assigned to existing bookings.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}