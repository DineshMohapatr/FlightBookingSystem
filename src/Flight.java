public class Flight {
    private int flightNumber;
    private String airline;
    private String source;
    private String destination;
    private double price;
    private int seatsAvailable;

    public Flight(int flightNumber, String airline, String source, String destination, double price, int seatsAvailable) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.source = source;
        this.destination = destination;
        this.price = price;
        this.seatsAvailable = seatsAvailable;
    }

    public int getFlightNumber() { return flightNumber; }
    public String getAirline() { return airline; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public double getPrice() { return price; }
    public int getSeatsAvailable() { return seatsAvailable; }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    @Override
    public String toString() {
        return flightNumber + " | " + airline + " | " + source + " → " + destination +
               " | ₹" + price + " | Seats: " + seatsAvailable;
    }
}
