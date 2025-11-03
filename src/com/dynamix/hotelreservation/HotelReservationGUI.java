package com.dynamix.hotelreservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class HotelReservationGUI extends JFrame {
    private HotelReservationModel model;
    private JTabbedPane tabbedPane;
    private static final String ADMIN_PASSWORD = "admin123";

    public HotelReservationGUI() {
        model = new HotelReservationModel();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Hotel Reservation System - Dynamix Networks");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("🏨 Customer Portal", createCustomerPanel());
        tabbedPane.addTab("👨‍💼 Admin Panel", createAdminPanel());

        add(tabbedPane);
    }

    // ==================== CUSTOMER PANEL ====================

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: Register/Login
        panel.add(createCustomerHeaderPanel(), BorderLayout.NORTH);

        // Center: Tabbed content
        JTabbedPane customerTabs = new JTabbedPane();
        customerTabs.addTab("🏠 View Rooms", createRoomsPanel());
        customerTabs.addTab("📅 My Reservations", createMyReservationsPanel());

        panel.add(customerTabs, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Customer Section"));
        panel.setBackground(new Color(240, 248, 255));

        JButton registerBtn = createStyledButton("✍️ Register/Login", new Color(30, 144, 255));
        JButton bookBtn = createStyledButton("📖 Book Room", new Color(34, 139, 34));
        JButton searchBtn = createStyledButton("🔍 Search Availability", new Color(255, 140, 0));

        registerBtn.addActionListener(e -> showRegisterDialog());
        bookBtn.addActionListener(e -> showBookingDialog());
        searchBtn.addActionListener(e -> showSearchAvailabilityDialog());

        panel.add(registerBtn);
        panel.add(bookBtn);
        panel.add(searchBtn);

        return panel;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Room ID", "Type", "Price/Night (₹)", "Capacity", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);

        // Populate table
        refreshRoomsTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(70, 130, 180));
        refreshBtn.addActionListener(e -> refreshRoomsTable(tableModel));
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Res ID", "Customer", "Room ID", "Check-In", "Check-Out", "Total Cost (₹)", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton viewBtn = createStyledButton("👁️ View All", new Color(30, 144, 255));
        JButton cancelBtn = createStyledButton("❌ Cancel Selected", new Color(220, 20, 60));
        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(70, 130, 180));

        viewBtn.addActionListener(e -> refreshReservationsTable(tableModel, null));
        cancelBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String resId = (String) tableModel.getValueAt(selectedRow, 0);
                cancelReservationAction(resId);
                refreshReservationsTable(tableModel, null);
            }
        });
        refreshBtn.addActionListener(e -> refreshReservationsTable(tableModel, null));

        buttonPanel.add(viewBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== ADMIN PANEL ====================

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Check if admin is logged in
        if (!showAdminLogin()) {
            JLabel label = new JLabel("Access Denied. Admin login failed.", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setForeground(new Color(220, 20, 60));
            panel.add(label);
            return panel;
        }

        // Admin header
        panel.add(createAdminHeaderPanel(), BorderLayout.NORTH);

        // Admin content tabs
        JTabbedPane adminTabs = new JTabbedPane();
        adminTabs.addTab("🏠 Room Management", createRoomManagementPanel());
        adminTabs.addTab("👥 Customer Management", createCustomerManagementPanel());
        adminTabs.addTab("📅 All Reservations", createAllReservationsPanel());
        adminTabs.addTab("📊 Reports", createReportsPanel());

        panel.add(adminTabs, BorderLayout.CENTER);

        return panel;
    }

    private boolean showAdminLogin() {
        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(this, passwordField,
                "Enter Admin Password:", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            return password.equals(ADMIN_PASSWORD);
        }
        return false;
    }

    private JPanel createAdminHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Admin Control Panel"));
        panel.setBackground(new Color(255, 228, 225));

        JLabel adminLabel = new JLabel("🔐 Admin Mode Active");
        adminLabel.setFont(new Font("Arial", Font.BOLD, 14));
        adminLabel.setForeground(new Color(220, 20, 60));

        panel.add(adminLabel);

        return panel;
    }

    private JPanel createRoomManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Room ID", "Type", "Price/Night (₹)", "Capacity", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);

        refreshRoomsTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addBtn = createStyledButton("➕ Add Room", new Color(34, 139, 34));
        JButton deleteBtn = createStyledButton("🗑️ Delete Selected", new Color(220, 20, 60));
        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(70, 130, 180));

        addBtn.addActionListener(e -> {
            showAddRoomDialog();
            refreshRoomsTable(tableModel);
        });
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String roomId = (String) tableModel.getValueAt(selectedRow, 0);
                model.removeRoom(roomId);
                refreshRoomsTable(tableModel);
                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
            }
        });
        refreshBtn.addActionListener(e -> refreshRoomsTable(tableModel));

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Customer ID", "Name", "Email", "Phone"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);

        refreshCustomersTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton viewBtn = createStyledButton("👁️ View All", new Color(30, 144, 255));
        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(70, 130, 180));

        viewBtn.addActionListener(e -> refreshCustomersTable(tableModel));
        refreshBtn.addActionListener(e -> refreshCustomersTable(tableModel));

        buttonPanel.add(viewBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAllReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Res ID", "Customer", "Room ID", "Check-In", "Check-Out", "Total Cost (₹)", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);

        refreshReservationsTable(tableModel, null);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton viewBtn = createStyledButton("👁️ View All", new Color(30, 144, 255));
        JButton cancelBtn = createStyledButton("❌ Cancel Selected", new Color(220, 20, 60));
        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(70, 130, 180));

        viewBtn.addActionListener(e -> refreshReservationsTable(tableModel, null));
        cancelBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String resId = (String) tableModel.getValueAt(selectedRow, 0);
                cancelReservationAction(resId);
                refreshReservationsTable(tableModel, null);
            }
        });
        refreshBtn.addActionListener(e -> refreshReservationsTable(tableModel, null));

        buttonPanel.add(viewBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton generateBtn = createStyledButton("📊 Generate Report", new Color(138, 43, 226));

        generateBtn.addActionListener(e -> {
            Map<String, Object> stats = model.getBookingStats();

            @SuppressWarnings("unchecked")
            Map<String, Long> roomTypeBookings = (Map<String, Long>) stats.get("roomTypeBookings");

            StringBuilder report = new StringBuilder();
            report.append("╔════════════════════════════════════════════════════════╗\n");
            report.append("║              BOOKING STATISTICS REPORT                ║\n");
            report.append("╚════════════════════════════════════════════════════════╝\n\n");
            report.append("Total Confirmed Reservations:  ").append(stats.get("totalReservations")).append("\n");
            report.append("Total Revenue:                 ₹").append(String.format("%.2f", stats.get("totalRevenue"))).append("\n");
            report.append("Cancelled Reservations:        ").append(stats.get("cancelledCount")).append("\n\n");
            report.append("Bookings by Room Type:\n");
            report.append("─".repeat(54)).append("\n");

            roomTypeBookings.forEach((type, count) ->
                report.append(String.format("  %-30s: %d bookings\n", type, count))
            );

            reportArea.setText(report.toString());
        });

        buttonPanel.add(generateBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== DIALOG METHODS ====================

    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Register/Login", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        panel.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(15);
        panel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(15);
        panel.add(phoneField, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        JTextArea addressArea = new JTextArea(3, 15);
        addressArea.setLineWrap(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        panel.add(addressScroll, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton registerBtn = new JButton("✅ Register");
        JButton cancelBtn = new JButton("❌ Cancel");

        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressArea.getText().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            Customer customer = model.addCustomer(name, email, phone, address);
            JOptionPane.showMessageDialog(dialog, 
                    "✅ Registration successful!\nYour ID: " + customer.getCustomerId());
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(registerBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showBookingDialog() {
        JDialog dialog = new JDialog(this, "Book a Room", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Customer ID
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1;
        JTextField custIdField = new JTextField(15);
        panel.add(custIdField, gbc);

        // Room Selection
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Select Room:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> roomCombo = new JComboBox<>();
        for (Room room : model.getAllRooms()) {
            roomCombo.addItem(room.toString());
        }
        panel.add(roomCombo, gbc);

        // Check-In
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Check-In (dd-MM-yyyy):"), gbc);
        gbc.gridx = 1;
        JTextField checkInField = new JTextField(15);
        panel.add(checkInField, gbc);

        // Check-Out
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Check-Out (dd-MM-yyyy):"), gbc);
        gbc.gridx = 1;
        JTextField checkOutField = new JTextField(15);
        panel.add(checkOutField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton bookBtn = new JButton("📖 Book Now");
        JButton cancelBtn = new JButton("❌ Cancel");

        bookBtn.addActionListener(e -> {
            try {
                String customerId = custIdField.getText().trim();
                String roomStr = (String) roomCombo.getSelectedItem();
                String roomId = roomStr.split(" - ")[0];
                LocalDate checkIn = LocalDate.parse(checkInField.getText().trim(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                Reservation res = model.bookReservation(customerId, roomId, checkIn, checkOut);
                if (res != null) {
                    JOptionPane.showMessageDialog(dialog,
                            "✅ Booking successful!\nReservation ID: " + res.getReservationId() +
                            "\nTotal Cost: ₹" + String.format("%.2f", res.getTotalCost()));
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Booking failed! Please check details.");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showSearchAvailabilityDialog() {
        JDialog dialog = new JDialog(this, "Search Availability", true);
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Check-In
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Check-In (dd-MM-yyyy):"), gbc);
        gbc.gridx = 1;
        JTextField checkInField = new JTextField(15);
        panel.add(checkInField, gbc);

        // Check-Out
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Check-Out (dd-MM-yyyy):"), gbc);
        gbc.gridx = 1;
        JTextField checkOutField = new JTextField(15);
        panel.add(checkOutField, gbc);

        // Button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton searchBtn = new JButton("🔍 Search");

        searchBtn.addActionListener(e -> {
            try {
                LocalDate checkIn = LocalDate.parse(checkInField.getText().trim(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                List<Room> available = model.getAvailableRoomsForDates(checkIn, checkOut);
                StringBuilder message = new StringBuilder("Available Rooms:\n\n");

                for (Room room : available) {
                    message.append(room.toString()).append("\n");
                }

                if (available.isEmpty()) {
                    message = new StringBuilder("No available rooms for the selected dates.");
                }

                JTextArea textArea = new JTextArea(message.toString());
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(dialog, scrollPane, "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format!");
            }
        });

        buttonPanel.add(searchBtn);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddRoomDialog() {
        JDialog dialog = new JDialog(this, "Add Room", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Room Type
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Room Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(
                new String[]{"Single", "Double", "Suite", "Deluxe"});
        panel.add(typeCombo, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Price per Night (₹):"), gbc);
        gbc.gridx = 1;
        JTextField priceField = new JTextField(15);
        panel.add(priceField, gbc);

        // Capacity
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        panel.add(capacitySpinner, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("➕ Add");
        JButton cancelBtn = new JButton("❌ Cancel");

        addBtn.addActionListener(e -> {
            try {
                String type = (String) typeCombo.getSelectedItem();
                double price = Double.parseDouble(priceField.getText().trim());
                int capacity = (Integer) capacitySpinner.getValue();

                model.addRoom(type, price, capacity);
                JOptionPane.showMessageDialog(dialog, "✅ Room added successfully!");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price!");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // ==================== TABLE REFRESH METHODS ====================

    private void refreshRoomsTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        for (Room room : model.getAllRooms()) {
            tableModel.addRow(new Object[]{
                room.getRoomId(),
                room.getRoomType(),
                String.format("%.2f", room.getPricePerNight()),
                room.getCapacity(),
                room.isAvailable() ? "Available" : "Occupied"
            });
        }
    }

    private void refreshCustomersTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        for (Customer customer : model.getAllCustomers()) {
            tableModel.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoneNumber()
            });
        }
    }

    private void refreshReservationsTable(DefaultTableModel tableModel, String customerId) {
        tableModel.setRowCount(0);
        List<Reservation> reservations;

        if (customerId != null) {
            reservations = model.getReservationsByCustomer(customerId);
        } else {
            reservations = model.getAllReservations();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Reservation res : reservations) {
            tableModel.addRow(new Object[]{
                res.getReservationId(),
                res.getCustomer().getCustomerId(),
                res.getRoom().getRoomId(),
                res.getCheckInDate().format(formatter),
                res.getCheckOutDate().format(formatter),
                String.format("%.2f", res.getTotalCost()),
                res.getStatus()
            });
        }
    }

    private void cancelReservationAction(String reservationId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            model.cancelReservation(reservationId);
            JOptionPane.showMessageDialog(this, "✅ Reservation cancelled successfully!");
        }
    }

    // ==================== UTILITY METHODS ====================

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new HotelReservationGUI().setVisible(true);
        });
    }
}
