import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Customer implements Runnable {
	public static long time = System.currentTimeMillis();
	public static Vector<Customer> adoptionQueue = new Vector<>();
	private Thread customerThread;
	private String num;
	private AtomicBoolean finishedShopping = new AtomicBoolean(false);

	public Customer(String num) {
		this.num = "*** customer_" + num;
	}

	public String getName() {
		return this.num;
	}

	public void msg(String m) {
		System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
	}

	public void setFinishedShopping(boolean x) {
		finishedShopping.set(x);
	}

	public void run() {
		setThreadReference(Thread.currentThread());
		// Customer commutes to the pet store
		msg("Commuting");
		int customerCommute = randomNumber(200, 7500);
		try {
			Thread.sleep(customerCommute);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Customer arrived at store
		msg("Arrived at store");

		// Customer generates their random number which determines what they do at the
		// store
		int number = randomNumber(1, 10);
		if (number < 4) {
			// buy food and toys
			buyFoodAndToys();
			customerLeaves();

		} else if (number % 2 == 0) {
			// interested in adopting ONLY
			enterAdoption();
			// Leaves from the enterAdoption() function two types of leaves 1. Adopted and
			// leaves 2. Did not adopt and leaves

		} else {
			// first shopping then checks pets and may adopt
			buyFoodAndToys();
			enterAdoption();
			// Leaves from the enterAdoption() function two types of leaves 1. Adopted and
			// leaves 2. Did not adopt and leaves
		}

		// When all customers have left, the last customer is announced, this should
		// lead to cashiers and the clerk leaving
		if (Main.getNumCustomers() <= 0) {
			msg("Last Customer exits");
		}

	}

	// =============================================================== INTERACTION
	// W/ CASHIER ===========================================================

	// Customer is rushing, browsing aisles, lining up at the cashier, waits on
	// line, gets called on by the cashier and finishes checking out
	public void buyFoodAndToys() {

		// Customer Rushing
		msg("Rushing");
		int originalPriority = Thread.currentThread().getPriority();
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		int randomRushingTime = randomNumber(500, 900);
		try {
			Thread.sleep(randomRushingTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread.currentThread().setPriority(originalPriority);

		// Customer Browsing Aisle
		msg("Browsing Aisle");
		int randomShoppingTime = randomNumber(1000, 2000);
		try {
			Thread.sleep(randomShoppingTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Customer lines up at cashier
		Main.customerLine.add(this);

		msg("Waiting on line");
		// Busy waiting until called by cashier and finishes shopping
		while (Main.customerLine.contains(this) || finishedShopping.get() == false) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Customer got called
		msg("Finished Shopping");

	}

	// =============================================================== INTERACTION
	// W/ ADOPTION CLERK ===========================================================

	// Customer waits on the adoption line to be called in, if called checks the
	// available pets
	public void enterAdoption() {
		msg("Interested in adopting. Waiting to be called");
		Main.customerAnnouncer.add(this);
		try {
			// Keep in mind, when clerk is no longer announcing the customers, the ones that
			// are in line will wait for this long sleep to finish
			Thread.sleep(15000);
			msg("All pets are adopted | Not allowed in Adoption Center | Leaves");
			Main.decrementCustomers();
		} catch (InterruptedException e) {
			int visitorsInside = AdoptionClerk.getCustomersInside();
			msg("Got called. Enters Adoption Center | Customers In Area: " + visitorsInside);
			checkPets();
		}
		// FCFS 3 Customers max at a time
	}

	// Customer checks pets and determines if they want to adopt
	public void checkPets() {
		// Customer is checking pets for a random amount of time
		msg("Checking Pets");
		try {
			Thread.sleep(randomNumber(800, 1300));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Customer generates random number, determines if customer adopts or leaves
		int number = randomNumber(1, 10);
		if (number < 6 && AdoptionClerk.getNumPets() > 0) {
			// Customer Adopts a pet, so num_pets is decremented, fills out form and leaves
			AdoptionClerk.decrementPets();
			int numpet = AdoptionClerk.getNumPets();
			msg("Adopted a pet | Pets Remaining:" + numpet);
			adoptionQueue.add(this);
			// Fill form
			fillForm();
			// Customer Leaves
			adoptingCustomersLeave();

		} else {
			// Does not adopt a pet, leaves
			AdoptionClerk.decrementCustomersInside();
			int visitorsInside = AdoptionClerk.getCustomersInside();
			int spotsLeft = 3 - visitorsInside;
			msg("Not Interested, Leaving Adoption | Spots Available: " + spotsLeft);
			Main.decrementCustomers();
		}
	}

	// Customer wants to adopt so they fill out forms, but before that they take
	// their coffee break
	public void fillForm() {
		// Coffee Break
		msg("Takes coffee break");
		Thread.yield();
		Thread.yield();

		msg("Began filling out forms");
		// Fill forms
		try {
			Thread.sleep(randomNumber(600, 1000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg("Finished filling out forms");
	}

	// Customers should leave in decreasing order based on their customer#, but this
	// method does not do that, this
	// is just a standard leave with no order, placeholder for the actual method
	public void adoptingCustomersLeave() {
		AdoptionClerk.decrementCustomersInside();
		int visitorsInside = AdoptionClerk.getCustomersInside();
		int spotsLeft = 3 - visitorsInside;
		msg("Leaving Adoption and Store | Spots Available: " + spotsLeft);
		Main.decrementCustomers();
	}

	// =============================================================== OTHER METHODS
	// ===========================================================

	// Customer leaves normally, used when customer only wants to buy food and toys
	public void customerLeaves() {
		Main.decrementCustomers();
		msg("Leaving the store");
	}

	// Generates random number between min and max
	public int randomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}

	// Gets reference of thread so that it can be used
	// in a interrupt in adoption clerk
	public void setThreadReference(Thread thread) {
		this.customerThread = thread;
	}

	public void interruptThread() {
		// if (customerThread != null) {
		customerThread.interrupt();
		// }
	}

}