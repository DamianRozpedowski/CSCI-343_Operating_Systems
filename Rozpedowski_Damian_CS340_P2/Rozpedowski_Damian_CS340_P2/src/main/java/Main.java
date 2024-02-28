// CS340 Operating Systems Project 2 Damian Rozpedowski Pet-Adoption Event
// *** - customer | $$$ - cashier | @@@ - Adoption Clerk
// Used the above so that it's easier to identify threads in the console

import java.util.concurrent.Semaphore;

public class Main {
    public static long time = System.currentTimeMillis();
    public static int num_customers = 20;
    public static int num_cashiers = 3;
    // Cashier resource, only 3 customers can be served at a time, rest will wait
    public static Semaphore cashierSemaphore = new Semaphore(3, true);
    // Ensures that the cashier does not release cashierSemaphore or do any work before a customer is able to acquire
    public static Semaphore mutex = new Semaphore(0, true);
    // Ensures that the customer doesn't continue running doing other tasks before the cashier releases
    public static Semaphore mutex2 = new Semaphore(0, true);
    // Ensure Adoption Clerk signals a spot for the area when customer finishes
    public static Semaphore mutex3 = new Semaphore(0, true);
    // Adoption Area resource, only 3 customers allowed inside, rest will wait
    public static Semaphore adoptionRoomSemaphore = new Semaphore(3, true);
    // Semaphore to control last customer departure, when departed signals cashier
    public static Semaphore customerLeaveSemaphore = new Semaphore(0, true);
    // Semaphore to signal cashier departure, when departed signals clerk
    public static Semaphore cashierLeaveSemaphore = new Semaphore(0, true);


    public static void main(String args[]) {
        // Create adoption clerk thread | Only one
        AdoptionClerk adoptionClerk = new AdoptionClerk();
        Thread adoptionClerkThread = new Thread(adoptionClerk);
        adoptionClerkThread.start();

        // Creating cashier threads | Should create 3 (default)
        for (int i = 1; i <= num_cashiers; i++) {
            Cashier cashier = new Cashier(Integer.toString(i));
            Thread cashierThread = new Thread(cashier);
            cashierThread.start();
        }
        // Creating customer threads | Should create 20 (default)
        for (int i = 1; i <= num_customers; i++) {
            Customer customer = new Customer(Integer.toString(i));
            Thread customerThread = new Thread(customer);
            customerThread.start();
        }
    }
}