package model;

public class Seller {
    private String restaurantName;
    private String logoUrl;

    public Seller(String restaurantName, String logoUrl) {
        this.restaurantName = restaurantName;
        this.logoUrl = logoUrl;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
