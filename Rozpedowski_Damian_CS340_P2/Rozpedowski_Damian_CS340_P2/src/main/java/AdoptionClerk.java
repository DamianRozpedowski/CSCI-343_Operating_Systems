import java.util.Random;
import java.util.concurrent.Semaphore;

class AdoptionClerk implements Runnable {
    public static long time = System.currentTimeMillis();
    public static int num_pets = 12;
    private String name;

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
        while (Main.num_cashiers > 0) {
            while (num_pets > 0) {
                if (Main.num_cashiers <= 0) break;
                try {
                    // Makes sure Clerk isn't doing anything until a customer shows up | Also singled by Cashier at the end
                    Main.mutex3.acquire();
                    // When customer leaves clerk can signal an open spot, only signals via customers
                    if(Main.num_customers > 0) {
                        Main.adoptionRoomSemaphore.release();
                        msg("Spot is Open");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // At this point all cashiers had left and its finally time for the clerk to
        // leave, at this point everyone should have left.
        try {
            Main.cashierLeaveSemaphore.acquire();
            msg("Clerk Leaves");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
