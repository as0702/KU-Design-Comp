//imports:
// web server libraries
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
//Error handling libraries
import java.io.IOException;
//Response
import java.io.OutputStream;
//Implement IP socket address
import java.net.InetSocketAddress;
//Self explanatory
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
//Reading styling
import java.nio.file.Files;
import java.nio.file.Paths;
//Store inputs
import java.util.HashMap;
import java.util.Map;

public class MyProgram
{
    //intiaialize forge time to 60 seconds
    private static final long FORGE_TIME_MS = 60000;

    //create catalog and queue objects to be used in the program
    private static StoreCatalog catalog = new StoreCatalog();
    private static OrderQueue queue = new OrderQueue();
    private static long forgeStartTime = System.currentTimeMillis();

    //minigame: clicks needed to smash one square
    private static final int MAX_CLICKS = 5;
    private static int clickCount = 0;
    private static int squaresSmashed = 0;

    private static int gold_count = 0;

    //Intialize server
    //throws IOException if the server fails to start
    public static void main(String[] args) throws IOException
    {
          //listen for requests on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", MyProgram::handleHome);
        server.createContext("/style.css", MyProgram::handleStyle);
        server.createContext("/order", MyProgram::handleOrder);
        server.createContext("/status", MyProgram::handleStatus);
        server.createContext("/smash", MyProgram::handleSmash);
        server.start();
        //System.out.println("Gunter's Forge is open at http://localhost:8000");
    }

    //send response
    private static void send(HttpExchange exchange, String type, String text) throws IOException
    {
        //turn text into bytes
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        //set content type
        exchange.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
        //sends status OK
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream out = exchange.getResponseBody();
        //writes the bites and closes the connection
        out.write(bytes);
        out.close();
    }

    //apply styling
    private static void handleStyle(HttpExchange exchange) throws IOException
    {
        send(exchange, "text/css", Files.readString(Paths.get("web/style.css")));
    }

    //create home page
    private static void handleHome(HttpExchange exchange) throws IOException
    {
        //itterate through list of items and prices to display on the home page
        String items = "";
        for (int i = 0; i < catalog.getItems().size(); i++) {
            String item = catalog.getItems().get(i);
            int price = catalog.getPrices().get(i);
            items += "<label class='product'><input type='radio' name='item' value='" + item + "' required> "
                    + item + " - " + price + " gold</label>";
        }

        //replcace the placeholder in HTML with item list
        String html = Files.readString(Paths.get("web/index.html")).replace("<!--CATALOG-->", items);
        send(exchange, "text/html", html);
    }

    //show a browser alert, then go back to the home page
    private static String alertPage(String message)
    {
        return "<!DOCTYPE html><html><head><title>Gunter's Forge</title></head>"
                + "<body><script>alert('" + escapeJs(message) + "'); location.replace('/');</script></body></html>";
    }

    //create form
    private static Map<String, String> readForm(String text)
    {
        Map<String, String> values = new HashMap<>();
        if (text == null) {
            return values;
        }
        for (String pair : text.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                values.put(parts[0], URLDecoder.decode(parts[1], StandardCharsets.UTF_8));
            }
        }
        return values;
    }

    //convert string value to normal integer if it contains only numbers
    private static int toNumber(String text)
    {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    //Escape text so it is safe inside a JavaScript string
    private static String escapeJs(String text)
    {
        return text.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n")
                .replace("\r", "").replace("<", "\\x3c");
    }

    //handleOrder
    private static void handleOrder(HttpExchange exchange) throws IOException
    {
        updateForge();

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> form = readForm(body);
        String name = form.getOrDefault("name", "").trim();
        String item = form.getOrDefault("item", "");
        int distance = toNumber(form.getOrDefault("distance", ""));

        if (name.isEmpty() || name.length() > 40 || !catalog.getItems().contains(item) || distance < 0 || distance > 100) {
            send(exchange, "text/html", alertPage("Hold on! Please enter your name, pick an item, and choose a distance from 0 to 100 miles."));
            return;
        }

        BlacksmithOrder order = new BlacksmithOrder(name, item, distance);
        queue.addOrder(order);

        //confirm the order with an alert
        int position = queue.getList().indexOf(order) + 1;
        send(exchange, "text/html", alertPage("Order placed! Your order number is #" + order.getOrderNumber()
                + ".\nYou are #" + position + " on Gunter's to-do list."));
    }

    //handleStatus
    private static void handleStatus(HttpExchange exchange) throws IOException
    {
        updateForge();

        Map<String, String> query = readForm(exchange.getRequestURI().getRawQuery());
        int number = toNumber(query.getOrDefault("number", ""));
        BlacksmithOrder order = queue.findOrder(number);

        String message;
        if (order == null) {
            message = "Not found. There is no order with that number.";
        } else if (queue.isCompleted(number)) {
            message = "Order #" + number + " is ready! Your " + order.getProductTitle() + " is finished.";
        } else {
            int position = queue.getList().indexOf(order) + 1;
            long msLeft = position * FORGE_TIME_MS - (System.currentTimeMillis() - forgeStartTime);
            long minutesLeft = (msLeft + 59999) / 60000;
            message = "Order #" + number + ": " + order.getProductTitle() + " for " + order.getCustomerName()
                    + "\n#" + position + " on Gunter's to-do list"
                    + "\nReady in about " + minutesLeft + " minute(s)";
        }
        send(exchange, "text/html", alertPage(message));
    }

    ///updateForge
    private static void updateForge()
    {
        long now = System.currentTimeMillis();
        if (queue.isEmpty()) {
            forgeStartTime = now;
            return;
        }
        while (!queue.isEmpty() && now - forgeStartTime >= FORGE_TIME_MS) {
            queue.removeNextOrder();
            forgeStartTime += FORGE_TIME_MS;
        }
    }

    //handleSmash: minigame click, square darkens each hit and breaks on the last one
    private static synchronized void handleSmash(HttpExchange exchange) throws IOException
    {
        clickCount++;
        boolean broken = clickCount >= MAX_CLICKS;
        if (broken) {
            squaresSmashed++;
            gold_count += 10;
            clickCount = 0;
        }
        //lightness drops from 80% each click, back to 80% for a new square
        int lightness = 80 - clickCount * 12;
        boolean almostBroken = clickCount == MAX_CLICKS - 1;

        send(exchange, "application/json", "{\"lightness\":" + lightness
                + ",\"squaresSmashed\":" + squaresSmashed
                + ",\"broken\":" + broken
                + ",\"almostBroken\":" + almostBroken + "}");
    }

    /*
    Original code:

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
    */

}
