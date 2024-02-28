import java.util.Random;
import java.util.concurrent.Semaphore;

public class Customer implements Runnable {
    public static long time = System.currentTimeMillis();
    private Thread customerThread;
    private String num;

    public Customer(String num) {
        this.num = "*** customer_" + num;
    }

    public String getName() {
        return this.num;
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
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
            // Buy food and toys
            buyFoodAndToys();
            // Leaves
            Main.num_customers--;

        } else if (number % 2 == 0) {
            // Interested in adopting ONLY
            enterAdoption();
            // Leaves
            Main.num_customers--;
        } else {
            // first shopping then checks pets and may adopt
            buyFoodAndToys();
            enterAdoption();
            // Leaves
            Main.num_customers--;
        }
        // When all customers have left, the last customer signals the cashier departure
        if (Main.num_customers <= 0) {
            msg("Last Customer exits");
            // Releases the mutex for the 3 Cashier Threads
            Main.mutex.release(Main.num_cashiers);
            // Signals the x cashiers to leave
            Main.customerLeaveSemaphore.release(Main.num_cashiers);
        }

    }

    public void buyFoodAndToys() {
        // Customer Browsing Aisle
        msg("Browsing Aisle");
        int randomShoppingTime = randomNumber(700, 1400);
        try {
            Thread.sleep(randomShoppingTime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //For java-code implementation there is not a required order since a random thread from the sem queue will be released.
        try {
            Main.cashierSemaphore.acquire();
            msg("Lined up at Cashier");
            // Cashier can serve
            Main.mutex.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            // Customer can continue
            Main.mutex2.acquire();
            msg("Finished Shopping");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void enterAdoption() {
        // Makes sure there are pets to adopt / if Adoption Clerk is accepting
        if (AdoptionClerk.num_pets > 0) {
            // Waits in the area or enters if available space and checks pets
            try {
                msg("Interested in adopting");
                Main.adoptionRoomSemaphore.acquire();
                checkPets();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            msg("No More Pets Available | Leaves Adoption Center");
        }

    }

    // Customer checks pets and determines if they want to adopt
    public void checkPets() {
        // Customer is checking pets for a random amount of time
        msg("Enters Adoption Room | Checking Pets");
        try {
            Thread.sleep(randomNumber(800, 1300));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Customer generates random number, determines if customer adopts or leaves
        int number = randomNumber(1, 10);
        if (number < 6 && AdoptionClerk.num_pets > 0) {

            // Customer Adopts a pet, so num_pets is decremented, fills out form and leaves
            AdoptionClerk.num_pets--;
            msg("Adopted a pet | Pets Remaining:" + AdoptionClerk.num_pets);
            // Fill form
            fillForm();
            // Customer Leaves
            msg("Leaves Adoption Center");
            // Clerk can announce
            Main.mutex3.release();

        } else {
            // Customer Leaves
            msg("Not Interested or No Pets Available | Leaving Adoption");
            // Clerk can announce
            Main.mutex3.release();
        }
    }

    // Customer wants to adopt, they fill out forms, but before that they take
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

    // Generates random number between min and max
    public int randomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    // Gets reference of thread so that it can be used
    // in an interrupt in adoption clerk
    public void setThreadReference(Thread thread) {
        this.customerThread = thread;
    }

    public void interruptThread() {
        // if (customerThread != null) {
        customerThread.interrupt();
        // }
    }

}