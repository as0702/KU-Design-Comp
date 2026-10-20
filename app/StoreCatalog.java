import java.util.ArrayList;

public class StoreCatalog {
    private ArrayList<String> items;
    private ArrayList<Integer> prices;

    public StoreCatalog() {
        items = new ArrayList<String>();
        prices = new ArrayList<Integer>();

        items.add("Sword");
        prices.add(45);

        items.add("Plate Armor");
        prices.add(120);

        items.add("Chain Mail");
        prices.add(75);

        items.add("Cooking Pot");
        prices.add(15);
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public ArrayList<Integer> getPrices() {
        return prices;
    }
}