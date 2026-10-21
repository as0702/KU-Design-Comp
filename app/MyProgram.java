//imports:
// web server libraries
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MyProgram
{
    //intiaialize forge time to 60 seconds
    private static final long FORGE_TIME_MS = 60000;

    //create catalog and queue objects to be used in the program
    private static StoreCatalog catalog = new StoreCatalog();
    private static OrderQueue queue = new OrderQueue();
    private static long forgeStartTime = System.currentTimeMillis();
    private static final String PLAYER_COOKIE = "gunterPlayer";
    private static final ConcurrentMap<String, PlayerAccount> players = new ConcurrentHashMap<>();

    //Intialize server
    //throws IOException if the server fails to start
    public static void main(String[] args) throws IOException
    {
        int port = args.length == 0 ? 8000 : Integer.parseInt(args[0]);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", MyProgram::handleHome);
        server.createContext("/style.css", MyProgram::handleStyle);
        server.createContext("/shop.js", MyProgram::handleShopScript);
        server.createContext("/gold-balance", MyProgram::handleGoldBalance);
        server.createContext("/order", MyProgram::handleOrder);
        server.createContext("/status", MyProgram::handleStatus);
        server.start();
        System.out.println("Gunter's Forge is open at http://localhost:" + port);
    }

    //send response
    private static void send(HttpExchange exchange, String type, String text) throws IOException
    {
        send(exchange, 200, type, text);
    }

    private static void send(HttpExchange exchange, int status, String type, String text) throws IOException
    {
        //turn text into bytes
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        //set content type
        exchange.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
        //sends status OK
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream out = exchange.getResponseBody();
        //writes the bites and closes the connection
        out.write(bytes);
        out.close();
    }

    private static void handleStyle(HttpExchange exchange) throws IOException
    {
        send(exchange, "text/css", Files.readString(Paths.get("web/style.css")));
    }

    private static void handleShopScript(HttpExchange exchange) throws IOException
    {
        send(exchange, "text/javascript", Files.readString(Paths.get("web/shop.js")));
    }

    private static void handleGoldBalance(HttpExchange exchange) throws IOException
    {
        PlayerAccount player = getPlayer(exchange);
        send(exchange, "application/json", "{\"gold\":" + player.getGold() + "}");
    }

    private static void handleHome(HttpExchange exchange) throws IOException
    {
        if (!exchange.getRequestURI().getPath().equals("/")) {
            send(exchange, 404, "text/plain", "Not found");
            return;
        }
        getPlayer(exchange);
        String items = "";
        for (int i = 0; i < catalog.getItems().size(); i++) {
            String item = catalog.getItems().get(i);
            int price = catalog.getPrices().get(i);
            items += "<label class='product'><input type='radio' name='item' value='" + item
                    + "' data-price='" + price + "' required> "
                    + item + " - " + price + " gold</label>";
        }

        String html = Files.readString(Paths.get("web/index.html")).replace("<!--CATALOG-->", items);
        send(exchange, "text/html", html);
    }

    //create a page
    private static String page(String content)
    {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<title>Gunter's Forge</title><link rel='stylesheet' href='/style.css'></head>"
                + "<body><header><h1>Gunter's Forge</h1></header>"
                + "<main><section class='card'>" + content + "</section></main>"
                + "<footer><a href='/'>Back to the shop</a></footer></body></html>";
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

    private static int toNumber(String text)
    {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    //Escape HTML
    private static String escape(String text)
    {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("'", "&#39;").replace("\"", "&quot;");
    }

    //handleOrder
    private static void handleOrder(HttpExchange exchange) throws IOException
    {
        updateForge();

        PlayerAccount player = getPlayer(exchange);
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> form = readForm(body);
        String name = form.getOrDefault("name", "").trim();
        String item = form.getOrDefault("item", "");
        int distance = toNumber(form.getOrDefault("distance", ""));

        if (name.isEmpty() || name.length() > 40 || !catalog.getItems().contains(item) || distance < 0 || distance > 100) {
            send(exchange, "text/html", page("<h2>Hold on!</h2><p>Please enter your name, pick an item, and choose a distance from 0 to 100 miles.</p>"));
            return;
        }

        int price = priceOf(item);
        if (!player.spendGold(price)) {
            send(exchange, 409, "text/html", page("<h2>Not enough gold</h2>"
                    + "<p>Your gold balance must be at least the item's price to place this order.</p>"));
            return;
        }

        BlacksmithOrder order = new BlacksmithOrder(escape(name), item, distance);
        queue.addOrder(order);

        //redirect
        exchange.getResponseHeaders().set("Location", "/status?number=" + order.getOrderNumber());
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private static int priceOf(String item)
    {
        int index = catalog.getItems().indexOf(item);
        return index < 0 ? -1 : catalog.getPrices().get(index);
    }

    private static PlayerAccount getPlayer(HttpExchange exchange)
    {
        String playerId = null;
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null) {
            for (String cookie : cookieHeader.split(";")) {
                String trimmedCookie = cookie.trim();
                if (trimmedCookie.startsWith(PLAYER_COOKIE + "=")) {
                    String value = trimmedCookie.substring(PLAYER_COOKIE.length() + 1);
                    try {
                        playerId = UUID.fromString(value).toString();
                    } catch (IllegalArgumentException ignored) {
                        playerId = null;
                    }
                    break;
                }
            }
        }

        if (playerId == null) {
            playerId = UUID.randomUUID().toString();
            exchange.getResponseHeaders().add("Set-Cookie",
                    PLAYER_COOKIE + "=" + playerId + "; Path=/; HttpOnly; SameSite=Lax");
        }
        return players.computeIfAbsent(playerId, ignored -> new PlayerAccount());
    }

    //handleStatus
    private static void handleStatus(HttpExchange exchange) throws IOException
    {
        updateForge();

        Map<String, String> query = readForm(exchange.getRequestURI().getRawQuery());
        int number = toNumber(query.getOrDefault("number", ""));
        BlacksmithOrder order = queue.findOrder(number);

        String content;
        if (order == null) {
            content = "<h2>Not found</h2><p>There is no order with that number.</p>";
        } else if (queue.isCompleted(number)) {
            content = "<h2>Order #" + number + "</h2>"
                    + "<p class='big'>Ready! Your " + order.getProductTitle() + " is finished.</p>";
        } else {
            int position = queue.getList().indexOf(order) + 1;
            long msLeft = position * FORGE_TIME_MS - (System.currentTimeMillis() - forgeStartTime);
            long minutesLeft = (msLeft + 59999) / 60000;
            content = "<h2>Order #" + number + "</h2>"
                    + "<p>" + order.getProductTitle() + " for " + order.getCustomerName() + "</p>"
                    + "<p class='big'>#" + position + " on Gunter's to-do list</p>"
                    + "<p>Ready in about " + minutesLeft + " minute(s)</p>"
                    + "<a href='/status?number=" + number + "'><button>Refresh</button></a>";
        }
        send(exchange, "text/html", page(content));
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

    private static final class PlayerAccount
    {
        private int gold;

        private synchronized int getGold()
        {
            return gold;
        }

        private synchronized boolean spendGold(int amount)
        {
            if (amount < 0 || gold < amount) {
                return false;
            }
            gold -= amount;
            return true;
        }
    }
}
