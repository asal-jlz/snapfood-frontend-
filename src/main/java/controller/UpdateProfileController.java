package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Seller;

import java.io.File;
import java.io.IOException;

public class UpdateProfileController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField bankNameField;

    @FXML
    private TextField accountNumberField;

    @FXML
    private VBox bankInfoVBox;

    @FXML
    private Button uploadPhotoButton;

    @FXML
    private Button updateButton;

    private String role; // Assume this will be set from login/session logic

    @FXML
    public void initialize() {
        // Simulate receiving role from logged-in user
        this.role = getLoggedInUserRole();

        if (role.equals("buyer")) {
            bankInfoVBox.setVisible(false);
            bankInfoVBox.setManaged(false);
        } else {
            bankInfoVBox.setVisible(true);
            bankInfoVBox.setManaged(true);
        }
    }

    private String getLoggedInUserRole() {
        // TODO: Replace with actual role from login context
        return "courier"; // or "seller" or "buyer"
    }

    @FXML
    private void handleUpdateProfile() {
        // Gather data (you can add validation here too)
        String fullName = fullNameField.getText();
        String phone = phoneNumberField.getText();
        String email = emailField.getText();
        String address = addressField.getText();

        String bankName = bankNameField.getText();
        String accountNumber = accountNumberField.getText();

        System.out.println("Updating profile...");

        // TODO: Send this data to backend API via HttpClient

        // After success, navigate to appropriate dashboard
        try {
            String fxmlToLoad = switch (role.toLowerCase()) {
                case "buyer" -> "/fxml/BuyerDashboard.fxml";
                case "courier" -> "/fxml/CourierDashboard.fxml";
                case "seller" -> "/fxml/SellerDashboard.fxml";
                default -> throw new IllegalStateException("Unknown role: " + role);
            };

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlToLoad));
            Parent dashboardRoot = loader.load();
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // You could show an alert dialog here
        }
    }


    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Image");

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            System.out.println("Selected image: " + file.getAbsolutePath());
            // TODO: Display image or send to backend
        }
    }

    public void setUserData(Seller seller) {

        fullNameField.setText(seller.getRestaurantName());
    }

}
