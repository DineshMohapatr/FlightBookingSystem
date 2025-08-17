import java.util.List;
import java.util.Scanner;

public class Main {

    private static final FlightService service = new FlightService();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        System.out.println("===== Flight Ticket Booking System =====");

        while (running) {
            printMenu();
            System.out.print("Choose option (1-5): ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    handleSearch(sc);
                    break;
                case "2":
                    handleBook(sc);
                    break;
                case "3":
                    handleViewBookings();
                    break;
                case "4":
                    handleCancel(sc);
                    break;
                case "5":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
            System.out.println(); // spacer
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1) Search Flights");
        System.out.println("2) Book Ticket");
        System.out.println("3) View Bookings");
        System.out.println("4) Cancel Booking");
        System.out.println("5) Exit");
    }

    private static void handleSearch(Scanner sc) {
        System.out.print("Source city: ");
        String source = sc.nextLine();
        System.out.print("Destination city: ");
        String dest = sc.nextLine();

        List<Flight> matches = service.search(source, dest);
        if (matches.isEmpty()) {
            System.out.println("No flights found.");
        } else {
            System.out.println("Available flights:");
            for (Flight f : matches) {
                System.out.println("  " + f);
            }
        }
    }

    private static void handleBook(Scanner sc) {
        try {
            System.out.print("Enter flight number: ");
            int flightNo = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Passenger name: ");
            String name = sc.nextLine().trim();

            System.out.print("Passenger age: ");
            int age = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Passenger phone: ");
            String phone = sc.nextLine().trim();

            System.out.print("Seats to book: ");
            int seats = Integer.parseInt(sc.nextLine().trim());

            Passenger p = new Passenger(name, age, phone);
            Booking b = service.book(flightNo, p, seats);
            if (b != null) {
                System.out.println("✅ Booking confirmed!");
                System.out.println(b);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter valid numbers for flight number/age/seats.");
        }
    }

    private static void handleViewBookings() {
        if (DataStore.getBookings().isEmpty()) {
            System.out.println("No bookings yet.");
        } else {
            System.out.println("Your bookings:");
            for (Booking b : DataStore.getBookings()) {
                System.out.println("  " + b);
            }
        }
    }

    private static void handleCancel(Scanner sc) {
        System.out.print("Enter Booking ID to cancel: ");
        String id = sc.nextLine().trim();
        boolean ok = DataStore.removeBooking(id);
        if (ok) {
            System.out.println("✅ Booking cancelled and seats restored.");
        } else {
            System.out.println("❌ Booking ID not found.");
        }
    }
}
