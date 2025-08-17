import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Booking {
    private String bookingId;
    private Flight flight;
    private Passenger passenger;
    private int seats;
    private double totalPrice;
    private LocalDateTime createdAt;

    public Booking(Flight flight, Passenger passenger, int seats) {
        this.bookingId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.flight = flight;
        this.passenger = passenger;
        this.seats = seats;
        this.totalPrice = flight.getPrice() * seats;
        this.createdAt = LocalDateTime.now();
    }

    public String getBookingId() { return bookingId; }
    public Flight getFlight() { return flight; }
    public Passenger getPassenger() { return passenger; }
    public int getSeats() { return seats; }
    public double getTotalPrice() { return totalPrice; }

    @Override
    public String toString() {
        String time = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return "[" + bookingId + "] " + passenger + " | " + flight +
               " | Seats: " + seats + " | Total: ₹" + totalPrice + " | " + time;
    }
}
