import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Cashier implements Runnable {
	public static long time = System.currentTimeMillis();
	private String cashierNum;

	public Cashier(String num) {
		this.cashierNum = "$$$ cashier_" + num;
	}

	public String getName() {
		return this.cashierNum;
	}

	public void msg(String m) {
		System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
	}

	public void run() {
		// Cashiers are at the store until all customers leave
		while (Main.getNumCustomers() > 0) {
			while (!Main.customerLine.isEmpty()) {
				try {
					// Try to serve the customer in FCFS order
					Customer customer = Main.customerLine.remove(0);
					msg("Now serving " + customer.getName());

					// Simulate serving time
					try {
						Thread.sleep(randomNumber(600, 1000));
						msg("Finished serving " + customer.getName());
						customer.setFinishedShopping(true);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					// Handles if the vector is empty, usually happens when two threads are in the
					// while loop already
					// sleep has to be low to preserve FCFS
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		}
		// At this point all the customers had left, its time for cashiers to leave
		msg("Cashier Leaves");
		Main.decrementCashiers();
	}

	// Generates random number between min and max
	public int randomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}

}