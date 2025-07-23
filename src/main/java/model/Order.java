package model;

import javafx.beans.property.*;

public class Order {
    private final StringProperty restaurantAddress;
    private final StringProperty buyerAddress;
    private final DoubleProperty deliveryFee;
    private final StringProperty status;

    public Order(String restaurantAddress, String buyerAddress, double deliveryFee, String status) {
        this.restaurantAddress = new SimpleStringProperty(restaurantAddress);
        this.buyerAddress = new SimpleStringProperty(buyerAddress);
        this.deliveryFee = new SimpleDoubleProperty(deliveryFee);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty restaurantAddressProperty() { return restaurantAddress; }
    public StringProperty buyerAddressProperty() { return buyerAddress; }
    public DoubleProperty deliveryFeeProperty() { return deliveryFee; }
    public StringProperty statusProperty() { return status; }
}
