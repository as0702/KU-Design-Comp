import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;

public class MyProgram {
    public static void main(String[] args) {
        // Sets up the scanner for the further prompts
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        StoreCatalog catalog = new StoreCatalog();
        OrderQueue queue = new OrderQueue();

        System.out.println("Orbit");
        System.out.println("Welcome to the Mautern Blacksmith Store");

        boolean running = true;
        //Keeps showing the menu until the user chooses to exit
        while (running) {
            System.out.println("\nWorkshop");
        //Lets the user know if the queue is empty, or if not, displays the current orders
            if (queue.isEmpty()) {
                System.out.println("Gunter's To-Do List is Empty");
            } else {
                ArrayList<BlacksmithOrder> orders = queue.getList();
                //Shows which orders are being forged and which are waiting
                for (int i = 0; i < orders.size(); i++) {
                    BlacksmithOrder order = orders.get(i);
                    if (i == 0) {
                        System.out.print("..forging..");
                    } else {
                        System.out.print("QUEUE #" + (i + 1) + " ");
                    }
                    System.out.println(order.getProductTitle() + " - Delivery: " + order.getDeliveryDistance() + " leagues away");
                }
            }
            //Displays available items available for purchase
            System.out.println("\nAvailable Items");

            ArrayList<String> items = catalog.getItems();
            ArrayList<Integer> prices = catalog.getPrices();
            //Shows the possible options to purchase an item, forge, or leave the app
            for (int i = 0; i < items.size(); i++) {
                System.out.println((i + 1) + ". " + items.get(i)
                        + " (" + prices.get(i) + " Gold)");
            }

            System.out.println("5. Gunter: Forge & Ship Next Item");
            System.out.println("6. Exit App");
            System.out.print("Select an option (1-6): ");

            String input = scanner.nextLine().trim();
            //Adds an order whenever a user selects one of the first four choices(items)
            if (input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4")) {
                int itemNumber = Integer.parseInt(input);
                String selectedItem = items.get(itemNumber - 1);
                //Generates a random delivery distance for the order
                int distance = random.nextInt(48) + 3;
                BlacksmithOrder order = new BlacksmithOrder(selectedItem, distance);
                //Adds the new order to the end of the queue
                queue.addOrder(order);
                System.out.println("\nOrder Added: " + selectedItem + " registered via GPS at " + distance + " leagues.");
            } else if (input.equals("5")) {
                //Removes and processes the next order in the queue
                BlacksmithOrder order = queue.removeNextOrder();
                if (order == null) {
                    System.out.println("\nNo items in queue. Gunter is resting.");
                } else {
                    System.out.println("\nSUCCESS: Forged and dispatched " + order.getProductTitle() + " over a distance of " + order.getDeliveryDistance() + " leagues!");
                }
            } else if (input.equals("6")) {
                System.out.println("Exiting Store...");
                running = false;
            } else {
                System.out.println("\nInvalid Option");
            }
        }
        //Closes the Scanner
        scanner.close();
    }
}