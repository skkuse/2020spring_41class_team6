package team6.skku_fooding.models;

public class Order {
    public int orderId;
    public int userId;
    public String userAddress;
    public DeliveryStatus deliveryStatus;
    public int productId;

    public Order() {}
    public Order(int orderId) {
        // TODO: Retrieve Data from Database and make object.
    }
}
