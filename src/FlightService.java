import java.util.ArrayList;
import java.util.List;

public class FlightService {

    public List<Flight> search(String source, String destination) {
        List<Flight> result = new ArrayList<>();
        for (Flight f : DataStore.getFlights()) {
            if (f.getSource().equalsIgnoreCase(source.trim())
                    && f.getDestination().equalsIgnoreCase(destination.trim())) {
                result.add(f);
            }
        }
        return result;
    }

    public Booking book(int flightNumber, Passenger passenger, int seats) {
        Flight selected = null;
        for (Flight f : DataStore.getFlights()) {
            if (f.getFlightNumber() == flightNumber) {
                selected = f;
                break;
            }
        }
        if (selected == null) {
            System.out.println("❌ Flight not found.");
            return null;
        }
        if (seats <= 0) {
            System.out.println("❌ Seats must be at least 1.");
            return null;
        }
        if (selected.getSeatsAvailable() < seats) {
            System.out.println("❌ Not enough seats available.");
            return null;
        }
        selected.setSeatsAvailable(selected.getSeatsAvailable() - seats);
        Booking b = new Booking(selected, passenger, seats);
        DataStore.addBooking(b);
        return b;
    }
}
