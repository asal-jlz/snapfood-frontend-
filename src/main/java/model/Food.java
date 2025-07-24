package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Food {
    private final SimpleStringProperty name, price, category, description;
    private final String imageUrl;

    public Food(String name, String price, String category, String description, String imageUrl) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleStringProperty(price);
        this.category = new SimpleStringProperty(category);
        this.description = new SimpleStringProperty(description);
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() { return imageUrl; }

    public StringProperty nameProperty() { return name; }
    public StringProperty priceProperty() { return price; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty descriptionProperty() { return description; }
}
