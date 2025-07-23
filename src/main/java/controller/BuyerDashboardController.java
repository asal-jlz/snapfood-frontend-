package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

import java.io.File;
import java.io.IOException;

public class BuyerDashboardController {

    @FXML private Button logoutButton;
    @FXML private Button goToPaymentButton;
    @FXML private Button editProfileButton;

    @FXML private TextField searchField;
    @FXML private ListView<String> categoryListView;
    @FXML private ListView<String> restaurantListView;
    @FXML private VBox menuVBox;
    @FXML private VBox categoryVBox;
    @FXML private ListView<String> cartListView;
    @FXML private Label preTaxLabel;
    @FXML private Label withTaxLabel;
    @FXML private Label welcomeLabel;
    @FXML private ImageView profileImageView;

    private double cartTotal = 0;

    @FXML
    public void initialize() {
        categoryListView.getItems().addAll("Pizza", "Burger", "Sushi");
        restaurantListView.getItems().addAll("Pizza House", "Burger King", "Tokyo Sushi");

        categoryListView.setOnMouseClicked(e -> loadFoodsByCategory());
        restaurantListView.setOnMouseClicked(e -> loadFoodsByRestaurant());


        String fullName = User.getInstance().getFullName(); // e.g., "Welcome, John"
        String profileImagePath = User.getInstance().getProfileImagePath(); // e.g., "/path/to/image.png"

        welcomeLabel.setText("Welcome, " + fullName);

        if (profileImagePath != null && new File(profileImagePath).exists()) {
            profileImageView.setImage(new Image(new File(profileImagePath).toURI().toString()));
        }
    }

    @FXML
    private void onLogoutClicked() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPaymentClicked() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/payment.fxml"));
            Stage stage = (Stage) goToPaymentButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditProfileClicked() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/UpdateYourProfile.fxml"));
            Stage stage = (Stage) editProfileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSearch(KeyEvent event) {
        String query = searchField.getText().toLowerCase();
        menuVBox.getChildren().clear();

        if (query.contains("pizza")) {
            addFoodToMenu("Pepperoni Pizza", 12.0);
        } else if (query.contains("sushi")) {
            addFoodToMenu("Salmon Sushi", 15.5);
        } else {
            menuVBox.getChildren().add(new Label("No food found"));
        }
    }

    private void loadFoodsByCategory() {
        String selected = categoryListView.getSelectionModel().getSelectedItem();
        menuVBox.getChildren().clear();
        if (selected == null) return;

        switch (selected) {
            case "Pizza" -> addFoodToMenu("Margherita", 10.0);
            case "Burger" -> addFoodToMenu("Cheeseburger", 8.5);
            case "Sushi" -> addFoodToMenu("Tuna Roll", 14.0);
        }
    }

    private void loadFoodsByRestaurant() {
        String selected = restaurantListView.getSelectionModel().getSelectedItem();
        menuVBox.getChildren().clear();
        if (selected == null) return;

        switch (selected) {
            case "Pizza House" -> addFoodToMenu("4 Cheese Pizza", 11.0);
            case "Burger King" -> addFoodToMenu("Whopper", 9.0);
            case "Tokyo Sushi" -> addFoodToMenu("Dragon Roll", 16.0);
        }
    }

    private void addFoodToMenu(String name, double price) {
        Button addButton = new Button("Add");
        Label foodLabel = new Label(name + " - $" + price);
        HBox foodBox = new HBox(10, foodLabel, addButton);

        addButton.setOnAction(e -> {
            cartListView.getItems().add(name + " - $" + price);
            cartTotal += price;
            updateCartLabels();
        });

        menuVBox.getChildren().add(foodBox);
    }

    private void updateCartLabels() {
        preTaxLabel.setText("Total (before tax): $" + String.format("%.2f", cartTotal));
        double withTax = cartTotal * 1.1;
        withTaxLabel.setText("Total (with tax): $" + String.format("%.2f", withTax));
    }
}
