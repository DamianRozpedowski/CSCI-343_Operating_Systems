// CS340 Operating Systems Project 1 Damian Rozpedowski Pet-Adoption Event
// *** - customer | $$$ - cashier | @@@ - Adoption Clerk
// Used the above so that its easier to identify threads in the console

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
	public static long time = System.currentTimeMillis();

	public static Vector<Customer> customerEnterExit = new Vector<>();
	public static Vector<Customer> customerLine = new Vector<>();
	public static Vector<Customer> customerAnnouncer = new Vector<>();

	// Changes # of Customers and Cashiers
	private static final AtomicInteger num_customers = new AtomicInteger(20);
	private static final AtomicInteger num_cashiers = new AtomicInteger(3);

	// Atomic Methods for customers and cashiers
	public static int getNumCustomers() {
		return num_customers.get();
	}

	public static void decrementCustomers() {
		num_customers.decrementAndGet();
	}

	public static int getNumCashiers() {
		return num_cashiers.get();
	}

	public static void decrementCashiers() {
		num_cashiers.decrementAndGet();
	}

	public static void main(String args[]) {
		// Create adoption clerk thread | Only one
		AdoptionClerk adoptionClerk = new AdoptionClerk();
		Thread adoptionClerkThread = new Thread(adoptionClerk);
		adoptionClerkThread.start();

		// Creating cashier threads | Should create 3 (default)
		for (int i = 1; i <= getNumCashiers(); i++) {
			Cashier cashier = new Cashier(Integer.toString(i));
			Thread cashierThread = new Thread(cashier);
			cashierThread.start();
		}
		// Creating customer threads | Should create 20 (default)
		for (int i = 1; i <= getNumCustomers(); i++) {
			Customer customer = new Customer(Integer.toString(i));
			Thread customerThread = new Thread(customer);
			customerThread.start();
		}
	}
}
