package com.tourism.ui;

import com.tourism.dao.BookingDAO;
import com.tourism.dao.PaymentDAO;
import com.tourism.model.Booking;
import com.tourism.model.Payment;

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

public class PaymentManagementPanel extends JPanel {

    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private PaymentDAO paymentDAO;
    private BookingDAO bookingDAO;

    // Input fields
    private JTextField paymentIdField;
    private JComboBox<Booking> bookingComboBox; // Select Booking to pay for/view payment
    private JTextField amountField;
    private JComboBox<String> methodComboBox;
    private JComboBox<String> statusComboBox;
    private JTextField paymentDateField; // Display only

    // Buttons
    private JButton addPaymentButton; // Only enable if selected booking has no payment
    private JButton updatePaymentButton; // Maybe update status/method?
    private JButton deletePaymentButton;
    private JButton clearButton;
    private JButton viewBookingPaymentButton; // Button to load payment for selected booking

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


     public PaymentManagementPanel() {
        paymentDAO = new PaymentDAO();
        bookingDAO = new BookingDAO(); // Needed for ComboBox

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setupTable(); // Table might show *all* payments initially
        JScrollPane scrollPane = new JScrollPane(paymentTable);

        JPanel formPanel = setupFormPanel();
        JPanel buttonPanel = setupButtonPanel();

        add(scrollPane, BorderLayout.CENTER); // Table at center
        add(formPanel, BorderLayout.NORTH);  // Form at top
        add(buttonPanel, BorderLayout.SOUTH); // Buttons at bottom

        loadBookingComboBox();
        loadAllPayments(); // Load all payments initially into table

        // Listener to check payment status when a booking is selected
         bookingComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                checkPaymentForSelectedBooking();
            }
        });
    }

    private void setupTable() {
        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableModel.addColumn("Payment ID");
        tableModel.addColumn("Booking ID");
        tableModel.addColumn("Amount");
        tableModel.addColumn("Method");
        tableModel.addColumn("Status");
        tableModel.addColumn("Payment Date");


        paymentTable = new JTable(tableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Date column wider
    }

     private JPanel setupFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        paymentIdField = new JTextField(5);
        paymentIdField.setEditable(false);
        bookingComboBox = new JComboBox<>();
        amountField = new JTextField(10);
        methodComboBox = new JComboBox<>(new String[]{"Credit Card", "Bank Transfer", "Cash", "Other"});
        statusComboBox = new JComboBox<>(new String[]{"Completed", "Pending", "Failed"});
        paymentDateField = new JTextField(15);
        paymentDateField.setEditable(false);


        // Row 0: Payment ID and Booking Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Payment ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(paymentIdField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Select Booking*:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(bookingComboBox, gbc);
        gbc.gridwidth = 1;

        // Row 1: Amount, Method, Status
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Amount (Birr)*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(amountField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Method:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(methodComboBox, gbc);
        gbc.gridx = 4; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 5; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(statusComboBox, gbc);


        // Row 2: Payment Date (Display Only)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Payment Date:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(paymentDateField, gbc);
        gbc.gridwidth = 1; // Reset gridwidth


        return formPanel;
    }


    private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Renamed button for clarity
        viewBookingPaymentButton = new JButton("View/Load Booking Payment");
        viewBookingPaymentButton.setToolTipText("Load payment details for the selected booking into the form.");
        viewBookingPaymentButton.addActionListener(e -> checkPaymentForSelectedBooking(true)); // Pass true to populate form

        addPaymentButton = new JButton("Add Payment");
        addPaymentButton.setToolTipText("Add payment for the selected booking (only if no payment exists).");
        addPaymentButton.addActionListener(this::addPayment);
        addPaymentButton.setEnabled(false); // Initially disabled

        updatePaymentButton = new JButton("Update Payment");
        updatePaymentButton.setToolTipText("Update the status or method of the loaded payment.");
        updatePaymentButton.addActionListener(this::updatePayment);
        updatePaymentButton.setEnabled(false); // Initially disabled

        deletePaymentButton = new JButton("Delete Payment");
        deletePaymentButton.setToolTipText("Delete the loaded payment.");
        deletePaymentButton.addActionListener(this::deletePayment);
        deletePaymentButton.setEnabled(false); // Initially disabled

        clearButton = new JButton("Clear Form");
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(viewBookingPaymentButton);
        buttonPanel.add(addPaymentButton);
        buttonPanel.add(updatePaymentButton);
        buttonPanel.add(deletePaymentButton);
        buttonPanel.add(clearButton);
        return buttonPanel;
    }

     private void loadBookingComboBox() {
        bookingComboBox.removeAllItems();
        // Add placeholder first
        bookingComboBox.addItem(new Booking(0, 0, 0, null, null, null, "-- Select Booking --"));
        List<Booking> bookings = bookingDAO.getAllBookings(); // Maybe filter for unpaid? For now, load all.
        if (bookings != null) {
             // Using a simpler representation for the combo box item
             bookings.forEach(b -> bookingComboBox.addItem(
                 new Booking(b.getBookingId(), b.getTouristId(), b.getPackageId(), null, null, b.getTotalCost(), "ID: " + b.getBookingId() + " (Cost: " + b.getTotalCost() + ")")
             ));
        }
     }

     // Load all payments into the table
     private void loadAllPayments() {
          tableModel.setRowCount(0);
         List<Payment> payments = paymentDAO.getAllPayments();
         if (payments != null) {
             for(Payment p : payments) {
                 addPaymentToTable(p);
             }
         } else {
             JOptionPane.showMessageDialog(this, "Failed to load payments.", "Error", JOptionPane.ERROR_MESSAGE);
         }
     }

      // Add or update a single payment row in the table
    private void addPaymentToTable(Payment p) {
        Vector<Object> row = new Vector<>();
        row.add(p.getPaymentId());
        row.add(p.getBookingId());
        row.add(p.getAmount());
        row.add(p.getPaymentMethod());
        row.add(p.getStatus());
        row.add(p.getPaymentDate() != null ? dateFormat.format(p.getPaymentDate()) : null);

        // Check if payment already exists in table to update it, otherwise add new
        boolean updated = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
             if (tableModel.getValueAt(i, 0).equals(p.getPaymentId())) {
                  for (int j = 0; j < row.size(); j++) {
                       tableModel.setValueAt(row.get(j), i, j);
                  }
                  updated = true;
                  break;
             }
        }
        if (!updated) {
             tableModel.addRow(row);
        }
    }

    // Remove payment row from table
     private void removePaymentFromTable(int paymentId) {
         for (int i = 0; i < tableModel.getRowCount(); i++) {
             if (tableModel.getValueAt(i, 0).equals(paymentId)) {
                  tableModel.removeRow(i);
                  break;
             }
         }
     }


     // Checks if payment exists for selected booking and enables/disables buttons
     // Optionally populates the form if populateForm is true
    private void checkPaymentForSelectedBooking(boolean populateForm) {
         Booking selectedBooking = (Booking) bookingComboBox.getSelectedItem();
         clearFormFieldsOnly(); // Clear form fields but keep selection

        if (selectedBooking != null && selectedBooking.getBookingId() != 0) {
            Payment existingPayment = paymentDAO.getPaymentByBookingId(selectedBooking.getBookingId());

            if (existingPayment != null) {
                // Payment exists
                addPaymentButton.setEnabled(false);
                updatePaymentButton.setEnabled(true);
                deletePaymentButton.setEnabled(true);
                if (populateForm) {
                     populateFormWithPayment(existingPayment);
                } else {
                     // Maybe just show a status message
                     // System.out.println("Payment already exists for Booking ID: " + selectedBooking.getBookingId());
                }
                 // Select the corresponding row in the table if present
                selectTableRowByPaymentId(existingPayment.getPaymentId());

            } else {
                // No payment exists
                addPaymentButton.setEnabled(true);
                updatePaymentButton.setEnabled(false);
                deletePaymentButton.setEnabled(false);
                 // Optionally pre-fill amount from booking
                 if(selectedBooking.getTotalCost() != null) {
                     amountField.setText(selectedBooking.getTotalCost().toString());
                 }
                if (populateForm) {
                    // Form is already cleared by clearFormFieldsOnly()
                     paymentIdField.setText(""); // Ensure no payment ID is shown
                     paymentDateField.setText("");
                }
            }
        } else {
            // No valid booking selected
            addPaymentButton.setEnabled(false);
            updatePaymentButton.setEnabled(false);
            deletePaymentButton.setEnabled(false);
        }
    }
    // Overload for event listener use (doesn't populate form)
    private void checkPaymentForSelectedBooking() {
        checkPaymentForSelectedBooking(false);
    }

    // Helper to select table row
    private void selectTableRowByPaymentId(int paymentId) {
         for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(paymentId)) {
                paymentTable.setRowSelectionInterval(i, i);
                paymentTable.scrollRectToVisible(paymentTable.getCellRect(i, 0, true)); // Scroll to make it visible
                return;
            }
        }
    }


    // Populate form fields with Payment data
     private void populateFormWithPayment(Payment p) {
        paymentIdField.setText(String.valueOf(p.getPaymentId()));
        // Keep booking selection as is
        amountField.setText(p.getAmount() != null ? p.getAmount().toString() : "");
        methodComboBox.setSelectedItem(p.getPaymentMethod());
        statusComboBox.setSelectedItem(p.getStatus());
        paymentDateField.setText(p.getPaymentDate() != null ? dateFormat.format(p.getPaymentDate()) : "");
    }


     // Clears only form fields, keeps combo box selection
     private void clearFormFieldsOnly() {
        paymentIdField.setText("");
        amountField.setText("");
        methodComboBox.setSelectedIndex(0); // Default to first method
        statusComboBox.setSelectedIndex(0); // Default to first status
        paymentDateField.setText("");
        paymentTable.clearSelection(); // Clear table selection as well
    }

    // Full clear including combo box and button states
    private void clearForm() {
        clearFormFieldsOnly();
        bookingComboBox.setSelectedIndex(0); // Reset booking selection
         // Reset button states
         addPaymentButton.setEnabled(false);
         updatePaymentButton.setEnabled(false);
         deletePaymentButton.setEnabled(false);
    }


    private Payment getPaymentFromForm(boolean isUpdate) {
         Booking selectedBooking = (Booking) bookingComboBox.getSelectedItem();
         if (selectedBooking == null || selectedBooking.getBookingId() == 0) {
             JOptionPane.showMessageDialog(this, "Please select a Booking.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Payment payment = new Payment();
        payment.setBookingId(selectedBooking.getBookingId()); // Always set booking ID

         // Payment ID needed for update/delete
         if (isUpdate || !paymentIdField.getText().trim().isEmpty()) {
              if (paymentIdField.getText().trim().isEmpty()) {
                   JOptionPane.showMessageDialog(this, "No payment loaded to update/delete.", "Error", JOptionPane.ERROR_MESSAGE);
                   return null;
              }
               try {
                    payment.setPaymentId(Integer.parseInt(paymentIdField.getText().trim()));
               } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid Payment ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
               }
         }


        // Amount
        try {
            String amountStr = amountField.getText().trim();
            if (amountStr.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Amount cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                 return null;
            }
             payment.setAmount(new BigDecimal(amountStr));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for Amount.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // Method and Status
        payment.setPaymentMethod((String) methodComboBox.getSelectedItem());
        payment.setStatus((String) statusComboBox.getSelectedItem());

        // Payment Date (not set from form, handled by DAO on add, retrieved for update)
        if (isUpdate && !paymentDateField.getText().isEmpty()) {
             try {
                 payment.setPaymentDate(new Timestamp(dateFormat.parse(paymentDateField.getText()).getTime()));
             } catch (Exception pe) {
                  System.err.println("Error parsing payment date for update: " + pe.getMessage());
                  // Refetch if needed
                  Payment original = paymentDAO.getPaymentById(payment.getPaymentId());
                  if(original != null) payment.setPaymentDate(original.getPaymentDate());
             }
        }

        return payment;
    }


    // --- Action Methods ---
    private void addPayment(ActionEvent e) {
        Payment newPayment = getPaymentFromForm(false); // false = not update
        if (newPayment != null) {
             // Double check: Ensure no payment exists for this booking before adding
             Payment existing = paymentDAO.getPaymentByBookingId(newPayment.getBookingId());
             if (existing != null) {
                  JOptionPane.showMessageDialog(this, "Payment already exists for Booking ID: " + newPayment.getBookingId(), "Error", JOptionPane.ERROR_MESSAGE);
                  populateFormWithPayment(existing); // Show existing payment
                  addPaymentButton.setEnabled(false);
                  updatePaymentButton.setEnabled(true);
                  deletePaymentButton.setEnabled(true);
                  return;
             }

            boolean success = paymentDAO.addPayment(newPayment);
            if (success) {
                JOptionPane.showMessageDialog(this, "Payment added successfully! ID: " + newPayment.getPaymentId());
                addPaymentToTable(newPayment); // Add to table
                clearForm(); // Reset form and button states
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add payment. Ensure Booking ID is valid.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

     private void updatePayment(ActionEvent e) {
         Payment updatedPaymentData = getPaymentFromForm(true); // true = is update
          if (updatedPaymentData != null) {
               // Fetch original to compare or just update based on ID
               Payment originalPayment = paymentDAO.getPaymentById(updatedPaymentData.getPaymentId());
               if(originalPayment == null) {
                    JOptionPane.showMessageDialog(this, "Cannot find original payment to update.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
               }

               // Apply changes - usually status or method might be updated
               originalPayment.setAmount(updatedPaymentData.getAmount()); // Allow amount update?
               originalPayment.setPaymentMethod(updatedPaymentData.getPaymentMethod());
               originalPayment.setStatus(updatedPaymentData.getStatus());


                boolean success = paymentDAO.updatePayment(originalPayment); // Update using modified original
                if (success) {
                    JOptionPane.showMessageDialog(this, "Payment updated successfully!");
                    addPaymentToTable(originalPayment); // Update table row
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update payment.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
          }
     }


     private void deletePayment(ActionEvent e) {
          if (paymentIdField.getText().trim().isEmpty()) {
             JOptionPane.showMessageDialog(this, "No payment loaded to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
         int paymentId = -1;
         try {
              paymentId = Integer.parseInt(paymentIdField.getText().trim());
         } catch (NumberFormatException nfe) {
              JOptionPane.showMessageDialog(this, "Invalid Payment ID in form.", "Error", JOptionPane.ERROR_MESSAGE);
              return;
         }


        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Payment ID: " + paymentId + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = paymentDAO.deletePayment(paymentId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Payment deleted successfully!");
                removePaymentFromTable(paymentId); // Remove from table
                clearForm(); // Reset form and buttons
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete payment.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}