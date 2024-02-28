import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

class AdoptionClerk implements Runnable {
	public static long time = System.currentTimeMillis();

	// Changes # of customers allowed inside the Adoption Center,
	// num_visitors_inside counts how many customers are in the area, change # of
	// pets available
	int num_visitors = 3;
	private static final AtomicInteger num_customers_inside = new AtomicInteger(0);
	private static final AtomicInteger num_pets = new AtomicInteger(12);
	private String name;

	public static Vector<Customer> adoptionQueue;

	public AdoptionClerk() {
		this.name = "@@@ adoption_clerk";
	}

	public String getName() {
		return name;
	}

	public void msg(String m) {
		System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
	}

	public void run() {
		// While to check if add a check if cashiers are still in the store
		while (Main.getNumCashiers() > 0) {
			// Added a while loop here so that AdoptionClerk only leaves when num_cashiers
			// is 0, stops announcing when all pets are adopted and customers that are in
			// the long wait in customer class, will wait until the long wait is finished
			// sleep(15000) before leaving
			while (num_pets.get() > 0) {
				if (Main.getNumCashiers() <= 0) break;
				announceCustomer();
			}
		}
		// At this point all cashiers had left and its finally time for the clerk to
		// leave, at this point everyone should have left.
		msg("Adoption Clerk Left");
	}

	// Customer gets announced and gets removed from the announcer vector and gets
	// it's long sleep interrupted
	public void announceCustomer() {
		while (!Main.customerAnnouncer.isEmpty() && num_customers_inside.get() < num_visitors) {
			Customer customer = Main.customerAnnouncer.remove(0);
			msg("Announcing " + customer.getName());
			incrementCustomersInside();
			// Interrupts from the long sleep in Customer class
			customer.interruptThread();

		}
	}

	// Methods for changing or getting atomic values, pets are visitors
	public static void decrementPets() {
		num_pets.decrementAndGet();
	}

	public static int getNumPets() {
		return num_pets.get();
	}

	public static void incrementCustomersInside() {
		num_customers_inside.incrementAndGet();
	}

	public static void decrementCustomersInside() {
		num_customers_inside.decrementAndGet();
	}

	public static int getCustomersInside() {
		return num_customers_inside.get();
	}

}
