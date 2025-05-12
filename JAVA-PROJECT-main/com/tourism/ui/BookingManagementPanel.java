package com.tourism.ui;

import com.tourism.dao.BookingDAO;
import com.tourism.dao.GuideDAO;
import com.tourism.dao.PackageDAO;
import com.tourism.dao.TouristDAO;
import com.tourism.model.Booking;
import com.tourism.model.Guide;
import com.tourism.model.Package; // Use alias if needed: import com.tourism.model.Package as TourPackage;
import com.tourism.model.Tourist;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

public class BookingManagementPanel extends JPanel {

    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private BookingDAO bookingDAO;
    private TouristDAO touristDAO;
    private PackageDAO packageDAO;
    private GuideDAO guideDAO;

    // Input fields
    private JTextField idField;
    private JComboBox<Tourist> touristComboBox;
    private JComboBox<Package> packageComboBox;
    private JComboBox<Guide> guideComboBox; // Allow null selection
    private JTextField totalCostField;
    private JComboBox<String> statusComboBox;
    private JTextField bookingDateField; // Display only

    // Buttons
    private JButton addButton;
    private JButton updateButton; // Likely just for status/guide change
    private JButton deleteButton;
    private JButton clearButton;

    // Date Formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public BookingManagementPanel() {
        bookingDAO = new BookingDAO();
        touristDAO = new TouristDAO();
        packageDAO = new PackageDAO();
        guideDAO = new GuideDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setupTable();
        JScrollPane scrollPane = new JScrollPane(bookingTable);

        JPanel formPanel = setupFormPanel();
        JPanel buttonPanel = setupButtonPanel();

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        loadComboBoxData(); // Load data for dropdowns
        loadBookings();     // Load initial table data

        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookingTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });

        // Listener for package selection to update cost
        packageComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Package selectedPackage = (Package) packageComboBox.getSelectedItem();
                if (selectedPackage != null && selectedPackage.getPackageId() != 0) { // Check not the placeholder
                    totalCostField.setText(selectedPackage.getPrice().toString());
                } else {
                     totalCostField.setText(""); // Clear cost if placeholder selected
                }
            }
        });
    }

    private void setupTable() {
        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableModel.addColumn("Booking ID");
        tableModel.addColumn("Tourist ID"); // Simplification: Showing IDs
        tableModel.addColumn("Package ID"); // Simplification: Showing IDs
        tableModel.addColumn("Guide ID");   // Simplification: Showing IDs (or null)
        tableModel.addColumn("Booking Date");
        tableModel.addColumn("Total Cost");
        tableModel.addColumn("Status");

        bookingTable = new JTable(tableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Adjust column widths as needed
         bookingTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Date
    }

     private JPanel setupFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontal space

        idField = new JTextField(5);
        idField.setEditable(false);
        touristComboBox = new JComboBox<>();
        packageComboBox = new JComboBox<>();
        guideComboBox = new JComboBox<>();
        totalCostField = new JTextField(10);
        totalCostField.setEditable(true); // Allow override maybe, or setEditable(false) if always from package
        statusComboBox = new JComboBox<>(new String[]{"Pending", "Confirmed", "Cancelled", "Completed"});
        bookingDateField = new JTextField(15);
        bookingDateField.setEditable(false);

        // Row 0: ID and Tourist
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(idField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Tourist*:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(touristComboBox, gbc);
        gbc.gridwidth = 1;

        // Row 1: Package and Guide
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Package*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(packageComboBox, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 3; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Guide:"), gbc);
        gbc.gridx = 4; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(guideComboBox, gbc);
        gbc.gridwidth = 1;


        // Row 2: Total Cost, Status, Booking Date
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Total Cost (Birr):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(totalCostField, gbc);
        gbc.gridx = 2; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(statusComboBox, gbc);
        gbc.gridx = 4; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Booking Date:"), gbc);
        gbc.gridx = 5; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(bookingDateField, gbc);


        return formPanel;
    }

     private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Booking");
        updateButton = new JButton("Update Status/Guide"); // More specific name
        deleteButton = new JButton("Delete Booking");
        clearButton = new JButton("Clear Form");

        addButton.addActionListener(this::addBooking);
        updateButton.addActionListener(this::updateBooking);
        deleteButton.addActionListener(this::deleteBooking);
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        return buttonPanel;
    }

     private void loadComboBoxData() {
        // Tourists
        touristComboBox.removeAllItems(); // Clear existing
        List<Tourist> tourists = touristDAO.getAllTourists();
        touristComboBox.addItem(new Tourist(0, "-- Select Tourist --", null, null, null, null)); // Placeholder
        if (tourists != null) {
            tourists.forEach(touristComboBox::addItem);
        }

        // Packages
        packageComboBox.removeAllItems();
        List<Package> packages = packageDAO.getAllPackages();
         packageComboBox.addItem(new Package(0, "-- Select Package --", null, null, 0, BigDecimal.ZERO)); // Placeholder
        if (packages != null) {
            packages.forEach(packageComboBox::addItem);
        }

        // Guides (Allow Null/None)
        guideComboBox.removeAllItems();
        guideComboBox.addItem(new Guide(0, "-- No Guide --", null, null, null)); // Placeholder for null/no guide
        List<Guide> guides = guideDAO.getAllGuides();
        if (guides != null) {
            guides.forEach(guideComboBox::addItem);
        }
    }


    private void loadBookings() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getAllBookings();
        if (bookings != null) {
            for (Booking b : bookings) {
                Vector<Object> row = new Vector<>();
                row.add(b.getBookingId());
                row.add(b.getTouristId());
                row.add(b.getPackageId());
                row.add(b.getGuideId()); // Can be null
                row.add(b.getBookingDate() != null ? dateFormat.format(b.getBookingDate()) : null);
                row.add(b.getTotalCost());
                row.add(b.getStatus());
                tableModel.addRow(row);
            }
        } else {
             JOptionPane.showMessageDialog(this, "Failed to load bookings.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
            Booking booking = bookingDAO.getBookingById(bookingId); // Fetch full details if needed

            if (booking != null) {
                idField.setText(String.valueOf(booking.getBookingId()));

                // Select Tourist in ComboBox
                selectComboBoxItem(touristComboBox, booking.getTouristId());

                // Select Package in ComboBox
                 selectComboBoxItem(packageComboBox, booking.getPackageId());

                // Select Guide in ComboBox (handle null)
                 selectComboBoxItem(guideComboBox, booking.getGuideId()); // guideId can be null


                totalCostField.setText(booking.getTotalCost() != null ? booking.getTotalCost().toString() : "");
                statusComboBox.setSelectedItem(booking.getStatus());
                bookingDateField.setText(booking.getBookingDate() != null ? dateFormat.format(booking.getBookingDate()) : "");

                // Disable Add button, enable Update/Delete
                addButton.setEnabled(false);
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                // Optionally disable Tourist/Package combo boxes for update
                touristComboBox.setEnabled(false);
                packageComboBox.setEnabled(false);

            } else {
                clearForm(); // Clear if fetch failed
                JOptionPane.showMessageDialog(this, "Could not retrieve details for the selected booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Generic helper to select item in ComboBox based on ID
    // Assumes ComboBox items have a method like getId() or specific types
     private <T> void selectComboBoxItem(JComboBox<T> comboBox, Integer targetId) {
         if (targetId == null) { // Handle null ID (e.g., no guide)
              // Find the placeholder item (often ID 0 or a specific object)
              for (int i = 0; i < comboBox.getItemCount(); i++) {
                   T item = comboBox.getItemAt(i);
                   if (item instanceof Guide && ((Guide) item).getGuideId() == 0) {
                        comboBox.setSelectedIndex(i);
                        return;
                   }
                   // Add similar checks for other types if they have a "none" placeholder
              }
              comboBox.setSelectedIndex(0); // Default to first item if no specific null placeholder found
              return;
         }

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            T item = comboBox.getItemAt(i);
            int itemId = -1; // Default invalid ID

             // Check the type of the item and get its ID
            if (item instanceof Tourist) itemId = ((Tourist) item).getTouristId();
            else if (item instanceof Package) itemId = ((Package) item).getPackageId();
            else if (item instanceof Guide) itemId = ((Guide) item).getGuideId();
            // Add other types if needed

            if (itemId == targetId) {
                comboBox.setSelectedItem(item);
                return; // Found the item
            }
        }
        // If ID not found (e.g., related tourist deleted), maybe select placeholder or log warning
         System.err.println("Warning: Could not find item with ID " + targetId + " in ComboBox " + comboBox.getClass().getSimpleName());
        comboBox.setSelectedIndex(0); // Select placeholder
    }


     private void clearForm() {
        idField.setText("");
        touristComboBox.setSelectedIndex(0); // Select placeholder
        packageComboBox.setSelectedIndex(0); // Select placeholder
        guideComboBox.setSelectedIndex(0);   // Select "-- No Guide --"
        totalCostField.setText("");
        statusComboBox.setSelectedIndex(0); // Default to "Pending"
        bookingDateField.setText("");
        bookingTable.clearSelection();

         // Re-enable/disable buttons and fields for adding
         addButton.setEnabled(true);
         updateButton.setEnabled(false);
         deleteButton.setEnabled(false);
         touristComboBox.setEnabled(true);
         packageComboBox.setEnabled(true);
    }

    // Get data primarily for ADD operation. Update might only need ID, status, guide.
    private Booking getBookingFromForm(boolean isUpdate) {
        Booking booking = new Booking();

        // ID is needed for update
        if (isUpdate) {
             if (idField.getText().trim().isEmpty()) {
                  JOptionPane.showMessageDialog(this, "No booking selected for update.", "Error", JOptionPane.ERROR_MESSAGE);
                  return null;
             }
              try {
                   booking.setBookingId(Integer.parseInt(idField.getText().trim()));
              } catch (NumberFormatException e) {
                   JOptionPane.showMessageDialog(this, "Invalid Booking ID.", "Error", JOptionPane.ERROR_MESSAGE);
                  return null;
              }
        }

        // Get selected items from ComboBoxes
        Tourist selectedTourist = (Tourist) touristComboBox.getSelectedItem();
        Package selectedPackage = (Package) packageComboBox.getSelectedItem();
        Guide selectedGuide = (Guide) guideComboBox.getSelectedItem();

        // Validation for ADD
        if (!isUpdate) {
             if (selectedTourist == null || selectedTourist.getTouristId() == 0) {
                 JOptionPane.showMessageDialog(this, "Please select a Tourist.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return null;
            }
             if (selectedPackage == null || selectedPackage.getPackageId() == 0) {
                 JOptionPane.showMessageDialog(this, "Please select a Package.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return null;
            }
             booking.setTouristId(selectedTourist.getTouristId());
             booking.setPackageId(selectedPackage.getPackageId());
        }


        // Guide ID (can be null if "-- No Guide --" (ID 0) is selected)
        if (selectedGuide != null && selectedGuide.getGuideId() != 0) {
             booking.setGuideId(selectedGuide.getGuideId());
        } else {
            booking.setGuideId(null); // Explicitly null
        }

        // Total Cost
        try {
            String costStr = totalCostField.getText().trim();
            if (!costStr.isEmpty()) {
                 booking.setTotalCost(new BigDecimal(costStr));
            } else if (!isUpdate) {
                 // Cost is usually mandatory for a new booking, get from package if empty
                 if(selectedPackage != null && selectedPackage.getPrice() != null) {
                      booking.setTotalCost(selectedPackage.getPrice());
                 } else {
                      JOptionPane.showMessageDialog(this, "Total Cost cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                      return null;
                 }
            } else {
                 // For update, maybe allow cost to be unset if needed, or fetch original
                 // For now, let's assume cost is present or derived
                 Booking originalBooking = bookingDAO.getBookingById(booking.getBookingId());
                 if(originalBooking != null) booking.setTotalCost(originalBooking.getTotalCost());

            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for Total Cost.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // Status
        booking.setStatus((String) statusComboBox.getSelectedItem());

        // Booking Date (not set from form, handled by DAO on add, retrieved for update)
         if (isUpdate && !bookingDateField.getText().isEmpty()) {
              try {
                   booking.setBookingDate(new Timestamp(dateFormat.parse(bookingDateField.getText()).getTime()));
              } catch (Exception pe) {
                   System.err.println("Error parsing booking date for update: " + pe.getMessage());
                   // Fetch original date again if parsing fails
                    Booking originalBooking = bookingDAO.getBookingById(booking.getBookingId());
                     if(originalBooking != null) booking.setBookingDate(originalBooking.getBookingDate());
              }
         }


        return booking;
    }

    // --- Action Methods ---
    private void addBooking(ActionEvent e) {
        Booking newBooking = getBookingFromForm(false); // false = not an update
        if (newBooking != null) {
            // Cost might already be set from package selection listener or getBookingFromForm
            boolean success = bookingDAO.addBooking(newBooking);
            if (success) {
                JOptionPane.showMessageDialog(this, "Booking added successfully! ID: " + newBooking.getBookingId());
                loadBookings(); // Reload table
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add booking. Ensure Tourist/Package exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateBooking(ActionEvent e) {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get relevant fields for update (mainly Status, Guide maybe)
        Booking updatedBookingData = getBookingFromForm(true); // true = is update

        if (updatedBookingData != null) {
             // Important: Fetch the original full booking to avoid overwriting unchanged fields unintentionally
             // For this panel, we are primarily updating Status and Guide
             Booking originalBooking = bookingDAO.getBookingById(updatedBookingData.getBookingId());
             if (originalBooking == null) {
                  JOptionPane.showMessageDialog(this, "Cannot find original booking to update.", "Error", JOptionPane.ERROR_MESSAGE);
                  return;
             }

             // Apply changes from form to the original booking object
             originalBooking.setStatus(updatedBookingData.getStatus());
             originalBooking.setGuideId(updatedBookingData.getGuideId()); // Update guide
             // Optionally allow updating cost if needed: originalBooking.setTotalCost(updatedBookingData.getTotalCost());


            boolean success = bookingDAO.updateBooking(originalBooking); // Update using the modified original object
            if (success) {
                JOptionPane.showMessageDialog(this, "Booking updated successfully!");
                loadBookings();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update booking.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


     private void deleteBooking(ActionEvent e) {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Booking ID: " + bookingId + "?\nAssociated payments might also be affected depending on DB setup.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            // Note: Consider deleting related payments first if ON DELETE is not CASCADE
            // new PaymentDAO().deletePaymentByBookingId(bookingId);
            boolean success = bookingDAO.deleteBooking(bookingId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Booking deleted successfully!");
                loadBookings();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete booking.\nCheck for related payments or other dependencies.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}