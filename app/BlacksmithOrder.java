public class BlacksmithOrder {
    //Stores the name of the item being ordered
    private String productTitle;
    
    //Shows how far the item needs to be delivered
    private int deliveryDistance;

    //Sets the item's name and delivery distance when an order is created
    public BlacksmithOrder(String title, int distance) {
        productTitle = title;
        deliveryDistance = distance;
    }
    //Returns the name of the item
    public String getProductTitle() {
        return productTitle;
    }
    //Returns the delivery distance of the item
    public int getDeliveryDistance() {
        return deliveryDistance;
    }
}
