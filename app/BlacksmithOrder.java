public class BlacksmithOrder {

    //track order numbers sequentially
    private static int nextOrderNumber = 1;

    //Define order attributes (number, customer name, product title, delivery distance)
    private int orderNumber;  
    private String customerName;    
    private String productTitle;
    
    //Shows how far the item needs to be delivered
    private int deliveryDistance;

    //Sets the item's name, delivery distance, set and increase order number, and customer name when an order is created
    public BlacksmithOrder(String name, String title, int distance) {
        orderNumber = nextOrderNumber;
        nextOrderNumber++;
        customerName = name;
        productTitle = title;
        deliveryDistance = distance;
    }

    //Functions for returning variables

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductTitle() {
        return productTitle;
    }
    //Returns the delivery distance of the item
    public int getDeliveryDistance() {
        return deliveryDistance;
    }
}
