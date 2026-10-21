import java.util.ArrayList;

public class StoreCatalog {
    //Stores the names and the prices of the available items
    private ArrayList<String> items;
    private ArrayList<Integer> prices;

    //Creates the list and adds the items that were sold at the blacksmith shop
    public StoreCatalog() {
        items = new ArrayList<String>();
        prices = new ArrayList<Integer>();

        //Adds each item and its price to the catalog
        items.add("Sword");
        prices.add(45);

        items.add("Plate Armor");
        prices.add(120);

        items.add("Chain Mail");
        prices.add(75);

        items.add("Cooking Pot");
        prices.add(15);
    }

    //Returns the list of names for the available items
    public ArrayList<String> getItems() {
        return items;
    }

    //Returns the list of prices for the available items
    public ArrayList<Integer> getPrices() {
        return prices;
    }

    //Returns the price of one item, prices are in the same order as items
    public int getPrice(String item) {
        return prices.get(items.indexOf(item));
    }
}
