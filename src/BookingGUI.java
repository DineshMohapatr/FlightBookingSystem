import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BookingGUI extends JFrame {

    // Services / data
    private final FlightService flightService = new FlightService();

    // Search + select flight
    private final JTextField srcField = new JTextField();
    private final JTextField dstField = new JTextField();
    private final JButton searchBtn = new JButton("Search Flights");
    private final JComboBox<Flight> flightDropdown = new JComboBox<>();

    // Passenger form
    private final JTextField nameField = new JTextField();
    private final JTextField ageField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField seatsField = new JTextField("1");
    private final JButton bookBtn = new JButton("Book Ticket");

    // Bookings table
    private final String[] bookingCols = {
            "Booking ID", "Flight No", "Airline", "Route", "Passenger", "Seats", "Total (₹)", "Time"
    };
    private final DefaultTableModel bookingModel = new DefaultTableModel(bookingCols, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable bookingTable = new JTable(bookingModel);
    private final JButton cancelBtn = new JButton("Cancel Selected");

    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public BookingGUI() {
        super("Flight Ticket Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        // Load initial flights into dropdown (all flights)
        populateFlightsDropdown(DataStore.getFlights());
        // Load current bookings (if any)
        reloadBookingsTable();

        // Actions
        searchBtn.addActionListener(e -> doSearch());
        bookBtn.addActionListener(e -> doBook());
        cancelBtn.addActionListener(e -> doCancel());

        setVisible(true);
    }

    private JPanel buildTopPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));

        // Search row
        JPanel searchRow = new JPanel(new GridLayout(1, 5, 8, 8));
        searchRow.add(labelWrap("Source", srcField));
        searchRow.add(labelWrap("Destination", dstField));
        searchRow.add(searchBtn);

        // Flight select row
        JPanel selectRow = new JPanel(new GridLayout(1, 2, 8, 8));
        selectRow.add(new JLabel("Select Flight:"));
        selectRow.add(flightDropdown);

        p.add(searchRow, BorderLayout.NORTH);
        p.add(selectRow, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildCenterPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.add(new JLabel("Passenger Name:"));
        form.add(nameField);
        form.add(new JLabel("Age:"));
        form.add(ageField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);
        form.add(new JLabel("Seats:"));
        form.add(seatsField);

        p.add(form, BorderLayout.CENTER);
        p.add(bookBtn, BorderLayout.EAST);
        return p;
    }

    private JPanel buildBottomPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        bookingTable.setRowHeight(22);
        p.add(new JScrollPane(bookingTable), BorderLayout.CENTER);
        p.add(cancelBtn, BorderLayout.EAST);
        return p;
    }

    private JPanel labelWrap(String label, JComponent field) {
        JPanel wrap = new JPanel(new BorderLayout(4, 4));
        wrap.add(new JLabel(label), BorderLayout.NORTH);
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    // ---------- Actions ----------

    private void doSearch() {
        String src = srcField.getText().trim();
        String dst = dstField.getText().trim();

        if (src.isEmpty() || dst.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Source and Destination.",
                    "Missing fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Flight> matches = flightService.search(src, dst);
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No flights found for " + src + " → " + dst,
                    "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
        populateFlightsDropdown(matches);
    }

    private void doBook() {
        Flight selected = (Flight) flightDropdown.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a flight.",
                    "No Flight Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String phone = phoneField.getText().trim();
        String seatsStr = seatsField.getText().trim();

        if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty() || seatsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all passenger details.",
                    "Missing fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int age, seats;
        try {
            age = Integer.parseInt(ageStr);
            seats = Integer.parseInt(seatsStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age and Seats must be numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Passenger p = new Passenger(name, age, phone);
        Booking b = flightService.book(selected.getFlightNumber(), p, seats);

        if (b == null) {
            // FlightService already printed the reason; show a generic dialog
            JOptionPane.showMessageDialog(this, "Booking failed. Check seats and try again.",
                    "Booking Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Success
        JOptionPane.showMessageDialog(this, "Booking confirmed! ID: " + b.getBookingId(),
                "Success", JOptionPane.INFORMATION_MESSAGE);

        // Refresh UI
        reloadBookingsTable();
        // Refresh flights in dropdown to reflect updated seats
        // Keep current search filter if set, otherwise show all
        if (!srcField.getText().trim().isEmpty() && !dstField.getText().trim().isEmpty()) {
            populateFlightsDropdown(flightService.search(srcField.getText().trim(), dstField.getText().trim()));
        } else {
            populateFlightsDropdown(DataStore.getFlights());
        }

        // Clear form
        nameField.setText("");
        ageField.setText("");
        phoneField.setText("");
        seatsField.setText("1");
    }

    private void doCancel() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking row to cancel.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String bookingId = (String) bookingModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel booking " + bookingId + "?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = DataStore.removeBooking(bookingId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Booking cancelled.",
                    "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            reloadBookingsTable();

            // Also refresh flights dropdown (seats restored)
            if (!srcField.getText().trim().isEmpty() && !dstField.getText().trim().isEmpty()) {
                populateFlightsDropdown(flightService.search(srcField.getText().trim(), dstField.getText().trim()));
            } else {
                populateFlightsDropdown(DataStore.getFlights());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Booking ID not found.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Helpers ----------

    private void populateFlightsDropdown(List<Flight> flights) {
        DefaultComboBoxModel<Flight> model = new DefaultComboBoxModel<>();
        for (Flight f : flights) {
            model.addElement(f); // JComboBox will use Flight.toString()
        }
        flightDropdown.setModel(model);
        if (model.getSize() > 0) flightDropdown.setSelectedIndex(0);
    }

    private void reloadBookingsTable() {
        bookingModel.setRowCount(0);
        for (Booking b : DataStore.getBookings()) {
            Flight f = b.getFlight();
            Passenger p = b.getPassenger();
            bookingModel.addRow(new Object[]{
                    b.getBookingId(),
                    f.getFlightNumber(),
                    f.getAirline(),
                    f.getSource() + " → " + f.getDestination(),
                    p.getName() + " (Age " + p.getAge() + ", " + p.getPhone() + ")",
                    b.getSeats(),
                    b.getTotalPrice(),
                    b.toString().contains("|")
                        ? b.toString().substring(b.toString().lastIndexOf("|") + 1).trim() // fallback if needed
                        : "" // if your Booking already stores/prints time differently
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookingGUI::new);
    }
}