import java.util.Random;
import java.util.concurrent.Semaphore;

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
        // Cashiers are checking the line until all customers are served
        while (Main.num_customers > 0) {
            try {
                // Ensures that cashier does not serve unless a customer is on the line
                Main.mutex.acquire();
                // Serves customer
                try {
                    if (Main.num_customers > 0) {
                        msg("Now Serving ");
                        Thread.sleep(randomNumber(600, 1000));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Moves the queue, customer served, next customer is allowed
                if (Main.num_customers > 0) {
                    Main.cashierSemaphore.release();
                    msg("Finished Serving ");
                    // Allows the customer to leave or visit Adoption Clerk
                    Main.mutex2.release();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // All customers left, Cashiers leave when the customerLeaveSemaphore is signaled by last customer
        try {
            Main.customerLeaveSemaphore.acquire();
            msg("Cashier Leaves");
            Main.num_cashiers--;
            // Signals the Adoption Clerk and releases their mutex
            Main.cashierLeaveSemaphore.release();
            // Release if Clerk is blocked
            Main.mutex3.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    // Generates random number between min and max
    public int randomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

}