import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static final List<Flight> flights = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();

    static {
        // Seed sample flights
        flights.add(new Flight(1001, "IndiGo",   "Bhubaneswar", "Delhi",    5499, 20));
        flights.add(new Flight(1002, "Air India","Bhubaneswar", "Mumbai",   6299, 15));
        flights.add(new Flight(1003, "Vistara",  "Delhi",       "Bengaluru",6999, 10));
        flights.add(new Flight(1004, "Akasa",    "Mumbai",      "Delhi",    4999, 25));
        flights.add(new Flight(1005, "SpiceJet", "Kolkata",     "Bhubaneswar", 2999, 18));
    }

    public static List<Flight> getFlights() {
        return flights;
    }

    public static List<Booking> getBookings() {
        return bookings;
    }

    public static void addBooking(Booking b) {
        bookings.add(b);
    }

    public static boolean removeBooking(String bookingId) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getBookingId().equalsIgnoreCase(bookingId)) {
                // restore seats
                Booking b = bookings.get(i);
                Flight f = b.getFlight();
                f.setSeatsAvailable(f.getSeatsAvailable() + b.getSeats());
                bookings.remove(i);
                return true;
            }
        }
        return false;
    }
}
