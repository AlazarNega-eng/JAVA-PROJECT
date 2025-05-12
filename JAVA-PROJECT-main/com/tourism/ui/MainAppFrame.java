package com.tourism.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.tourism.util.DBConnection; // Import DBConnection

public class MainAppFrame extends JFrame {

    private JTabbedPane tabbedPane;

    public MainAppFrame() {
        setTitle("Travel and Tourism Management (Ethiopian Heritage)"); // Updated Title
        setSize(950, 700); // Increased size slightly for complex panels
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle close manually
        setLocationRelativeTo(null); // Center the window

        // Add a window listener to close the DB connection on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit the application?", "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    DBConnection.closeConnection(); // Close connection
                    dispose(); // Close the frame
                    System.exit(0); // Terminate the application
                }
            }
        });

        tabbedPane = new JTabbedPane();

        // --- Add Panels for different management sections ---
        // Create instances of all panels
        PackageManagementPanel packagePanel = new PackageManagementPanel();
        TouristManagementPanel touristPanel = new TouristManagementPanel();
        GuideManagementPanel guidePanel = new GuideManagementPanel();
        HotelManagementPanel hotelPanel = new HotelManagementPanel();
        BookingManagementPanel bookingPanel = new BookingManagementPanel();
        PaymentManagementPanel paymentPanel = new PaymentManagementPanel();

        // Add panels as tabs
        tabbedPane.addTab("Packages", packagePanel);
        tabbedPane.addTab("Tourists", touristPanel);
        tabbedPane.addTab("Guides", guidePanel);
        tabbedPane.addTab("Hotels", hotelPanel);
        tabbedPane.addTab("Bookings", bookingPanel);
        tabbedPane.addTab("Payments", paymentPanel);
        // -----------------------------------------------------

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Set Look and Feel (Optional, for better appearance)
        try {
             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Look and feel not set.");
        }

        // Run the application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Test DB Connection first (optional but good practice)
            try {
                DBConnection.getConnection(); // Try establishing connection
                System.out.println("Initial DB connection successful.");

                 // If connection is okay, create and show the main frame
                MainAppFrame frame = new MainAppFrame();
                frame.setVisible(true);

            } catch (Exception ex) {
                 System.err.println("Failed to connect to the database on startup.");
                 ex.printStackTrace();
                 JOptionPane.showMessageDialog(null,
                    "Failed to connect to the database.\nPlease ensure XAMPP MySQL is running and configured correctly.\nError: " + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
                 System.exit(1); // Exit if DB connection fails on startup
            }
        });
    }
}