package model;

import javafx.beans.property.*;

public class Food {
    private final int id;
    private final StringProperty name;
    private final StringProperty price;
    private final StringProperty category;
    private final StringProperty description;
    private final StringProperty imageUrl;

    public Food(int id, String name, String price, String category, String description, String imageUrl) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleStringProperty(price);
        this.category = new SimpleStringProperty(category);
        this.description = new SimpleStringProperty(description);
        this.imageUrl = new SimpleStringProperty(imageUrl);
    }

    public Food(String name, String price, String category, String description, String imageUrl) {
        this(0, name, price, category, description, imageUrl);
    }

    public int getId() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty priceProperty() { return price; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty descriptionProperty() { return description; }
    public String getName() { return name.get(); }
    public String getPrice() { return price.get(); }
    public String getCategory() { return category.get(); }
    public String getDescription() { return description.get(); }
    public String getImageUrl() { return imageUrl.get(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return id == food.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
