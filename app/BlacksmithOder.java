public class BlacksmithOrder {
    private String productTitle;
    private int deliveryDistance;

    public BlacksmithOrder(String title, int distance) {
        productTitle = title;
        deliveryDistance = distance;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public int getDeliveryDistance() {
        return deliveryDistance;
    }
}