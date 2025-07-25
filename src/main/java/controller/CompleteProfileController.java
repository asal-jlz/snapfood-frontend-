package controller;

import Session.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import model.Seller;
import model.User;

public class CompleteProfileController {

    @FXML
    private Button uploadPhotoButton;

    @FXML
    private VBox bankInfoSection;

    @FXML
    private TextField bankNameField;

    @FXML
    private TextField accountNumberField;

    @FXML
    private VBox brandInfoSection;

    @FXML
    private TextField brandNameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private Button continueButton;

    @FXML
    private ImageView profileImageView;

    private String role;

    public void setRole(String role) {
        this.role = role;
        System.out.println("User role set to: " + role);

        if (role.equalsIgnoreCase("buyer")) {
            // Show sections but make them non-editable
            bankInfoSection.setVisible(true);
            bankNameField.setDisable(true);
            accountNumberField.setDisable(true);
            brandInfoSection.setVisible(true);
            brandNameField.setDisable(true);
            descriptionField.setDisable(true);
        } else if (role.equalsIgnoreCase("courier")) {
            bankInfoSection.setVisible(true);
            bankNameField.setDisable(false);
            accountNumberField.setDisable(false);
            brandInfoSection.setVisible(true);
            brandNameField.setDisable(true);
            descriptionField.setDisable(true);
        } else if (role.equalsIgnoreCase("seller")) {
            bankInfoSection.setVisible(true);
            bankNameField.setDisable(false);
            accountNumberField.setDisable(false);
            brandInfoSection.setVisible(true);
            brandNameField.setDisable(false);
            descriptionField.setDisable(false);
        }
    }


    @FXML
    public void onUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            profileImageView.setImage(image);
            System.out.println("Image uploaded: " + selectedFile.getName());
        }
    }

    @FXML
    public void onContinue() {
        if (Session.getUser() == null) {
            showAlert("User session is missing. Please login again.");
            return;
        }

        String bankName = bankNameField.getText();
        String accountNumber = accountNumberField.getText();
        String brandName = brandNameField.getText();
        String description = descriptionField.getText();

        String json = String.format(
                "{\"role\":\"%s\", \"bankName\":\"%s\", \"accountNumber\":\"%s\", \"brandName\":\"%s\", \"description\":\"%s\"}",
                role, bankName, accountNumber, brandName, description
        );

        try {
            HttpClient client = HttpClient.newHttpClient();
            String token = Session.getUser().getToken();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/users/" + Session.getUser().getId() + "/profile"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Session.getUser().getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        System.out.println("Response status: " + response.statusCode());
                        System.out.println("Response body: " + response.body());

                        if (response.statusCode() == 200) {
                            System.out.println("Profile completed successfully.");
                            String responseBody = response.body();
                            if (role.equalsIgnoreCase("seller")) {
                                // Manually parse it or use a library (simplified example):
                                Seller seller = new Seller();
                                seller.setId(Session.getUser().getId());
                                seller.setFullName(Session.getUser().getFullName());
                                seller.setPhone(Session.getUser().getPhone());
                                seller.setAddress(Session.getUser().getAddress());
                                seller.setRestaurantName(brandNameField.getText());
                                seller.setToken(Session.getUser().getToken()); // Optional, if you store token in Seller

                                Session.setCurrentSeller(seller); // ✅ THIS is the missing line
                            }

                            javafx.application.Platform.runLater(() -> {
                                try {
                                    String fxmlPath = switch (role.toLowerCase()) {
                                        case "buyer" -> "/fxmls/buyer-dashboard.fxml";
                                        case "courier" -> "/fxmls/Courier-dashboard.fxml";
                                        case "seller" -> "/fxmls/seller-dashboard.fxml";
                                        default -> "/fxmls/login.fxml";
                                    };

                                    Parent dashboardRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
                                    Stage stage = (Stage) continueButton.getScene().getWindow();
                                    stage.setScene(new Scene(dashboardRoot));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    showAlert("Failed to load dashboard.");
                                }
                            });
                        } else {
                            javafx.application.Platform.runLater(() -> {
                                showAlert("Failed to complete profile: " + response.body());
                            });
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error completing profile.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Profile Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String token;
    public void setToken(String token) {
        this.token = token;
    }

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

}
