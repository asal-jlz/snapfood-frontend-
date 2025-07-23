package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;
import model.Order;
import model.User;
import Session.Session;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CourierDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ImageView profileImageView;
    @FXML private Button logoutButton;
    @FXML private Button editProfileButton;
    @FXML private Label monthlyEarningsLabel;

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, String> restaurantAddressCol;
    @FXML private TableColumn<Order, String> buyerAddressCol;
    @FXML private TableColumn<Order, Double> deliveryFeeCol;
    @FXML private TableColumn<Order, String> statusCol;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    public void initialize() {
        User courier = Session.getUser();

        // Set welcome name
        welcomeLabel.setText("Welcome, " + courier.getFullName());

        // Set earnings
        monthlyEarningsLabel.setText("Monthly Earnings: $" + courier.getMonthlyEarnings());

        // Load profile image
        try {
            if (courier.getProfilePhotoPath() != null) {
                File file = new File(courier.getProfilePhotoPath());
                if (file.exists()) {
                    profileImageView.setImage(new Image(new FileInputStream(file)));
                }
            }
        } catch (Exception e) {
            System.out.println("Profile image load failed: " + e.getMessage());
        }

        // Bind columns
        restaurantAddressCol.setCellValueFactory(data -> data.getValue().restaurantAddressProperty());
        buyerAddressCol.setCellValueFactory(data -> data.getValue().buyerAddressProperty());
        deliveryFeeCol.setCellValueFactory(data -> data.getValue().deliveryFeeProperty().asObject());
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());

        // Fetch orders
        fetchOrdersFromBackend(courier.getId());
    }

    private void fetchOrdersFromBackend(String courierId) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/api/orders/courier/" + courierId); // change this to your real endpoint
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + Session.getUser().getToken()); // if using JWT
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    List<Order> orders = objectMapper.readValue(connection.getInputStream(), new TypeReference<List<Order>>() {});
                    ObservableList<Order> observableOrders = FXCollections.observableArrayList(orders);

                    javafx.application.Platform.runLater(() -> {
                        ordersTable.setItems(observableOrders);
                    });
                } else {
                    System.out.println("Failed to fetch orders: " + connection.getResponseCode());
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleLogout(javafx.event.ActionEvent event) throws IOException {
        Session.setUser(null);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    private void handleEditProfile(javafx.event.ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpdateYourProfile.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }
}
