package controller;

import Session.Session;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.Courier;
import model.LoginResponse;
import model.Seller;
import model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static Session.Session.loggedInUser;

public class LoginController {

    @FXML
    private TextField phoneNumberField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink signUpLink;

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    void handleSignupLink(ActionEvent event) {
        try {
            Parent signUpRoot = FXMLLoader.load(getClass().getResource("/fxmls/SignUp.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(signUpRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load SignUp page.");
        }
    }

    @FXML
    private void onLoginClick(ActionEvent event) {
        System.out.println("Login button clicked");
        String phone = phoneNumberField.getText();
        String password = passwordField.getText();

        if (phone.isEmpty() || password.isEmpty()) {
            System.out.println("Missing phone or password");
            showAlert("Please enter both phone number and password.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"phone\":\"%s\", \"password\":\"%s\"}", phone, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/users/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        System.out.println("Response code: " + response.statusCode());
                        System.out.println("Response body: " + response.body());

                        if (response.statusCode() == 200) {
                            Gson gson = new Gson();
                            LoginResponse loginData = gson.fromJson(response.body(), LoginResponse.class);

                            // Set user session
                            User user = User.getInstance();
                            user.setPhone(phone);
                            user.setToken(loginData.getToken());
                            user.setRole(loginData.getRole());
                            user.setId(loginData.getId());
                            Session.setUser(user);

                            Platform.runLater(() -> {
                                try {
                                    String fxmlPath = switch (user.getRole().toLowerCase()) {
                                        case "buyer" -> "/fxmls/buyer-dashboard.fxml";
                                        case "courier" -> "/fxmls/Courier-dashboard.fxml";
                                        case "vendor", "seller" -> "/fxmls/seller-dashboard.fxml";
                                        default -> {
                                            showAlert("Unknown role: " + user.getRole());
                                            yield "/fxmls/login.fxml";
                                        }
                                    };

                                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                                    Parent root = loader.load();


                                    // Inject user into controller
                                    if (user.getRole().equalsIgnoreCase("seller")) {
                                        SellerDashboardController controller = loader.getController();

                                        Seller seller = new Seller();
                                        seller.setId(user.getId());
                                        seller.setPhone(user.getPhone());
                                        seller.setToken(user.getToken());
                                        seller.setFullName(loginData.getName());

                                        //controller.setSeller(seller);
                                        Session.setCurrentSeller(seller);
                                        Session.setUser(null);
                                        controller.setUserData(seller);
                                    }else if (user.getRole().equalsIgnoreCase("courier")) {
                                        Courier courier = new Courier();
                                        courier.setId(user.getId());
                                        courier.setPhone(user.getPhone());
                                        courier.setToken(user.getToken());
                                        courier.setFullName(loginData.getName());

                                        //controller.setCourier(courier);
                                        Session.setCurrentCourier(courier);
                                        Session.setUser(courier);
                                        //controller.setUserData(courier);// if Courier extends User, else setUser(null)
                                    } else if (user.getRole().equalsIgnoreCase("buyer")) {
                                        BuyerDashboardController controller = loader.getController();

                                        User buyer = new User(); // Or your concrete Buyer class if exists
                                        buyer.setId(user.getId());
                                        buyer.setPhone(user.getPhone());
                                        buyer.setToken(user.getToken());
                                        buyer.setFullName(loginData.getName());

                                        Session.setUser(buyer);

                                        controller.setUserData(buyer);

                                    } else {
                                        showAlert("Unknown role: " + user.getRole());
                                    }

                                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    stage.setScene(new Scene(root));
                                    stage.show();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    showAlert("Failed to load dashboard.");
                                }
                            });

                        } else {
                            Platform.runLater(() -> showAlert("Login failed: Invalid credentials."));
                        }
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        Platform.runLater(() -> showAlert("Connection error: " + e.getMessage()));
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Unexpected error during login.");
        }
    }
}
