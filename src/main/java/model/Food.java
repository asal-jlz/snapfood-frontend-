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

    public String getImageUrl() {
        return imageUrl;
    }

    // ✅ JavaFX property accessors
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty priceProperty() {
        return price;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    // ✅ Standard Java-style getters (fixes your errors)
    public String getName() {
        return name.get();
    }

    public String getPrice() {
        return price.get();
    }

    public String getCategory() {
        return category.get();
    }

    public String getDescription() {
        return description.get();
    }
}
