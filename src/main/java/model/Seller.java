package model;

import java.util.ArrayList;
import java.util.List;

public class Seller extends User {
    private String brandName;   // changed from restaurantName to brandName to align with backend
    private String profilePhotoUrl;  // changed from logoUrl to profilePhotoUrl for consistency
    private List<Food> foods;
    private int restaurantId;

    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public List<Food> getFoods() {
        if (foods == null) {
            foods = new ArrayList<>();
        }
        return foods;
    }
    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public int getRestaurantId() {
        return restaurantId;
    }
    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }
}
