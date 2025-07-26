package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.User;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class UpdateProfileController {

    @FXML private TextField fullNameField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField bankNameField;
    @FXML private TextField accountNumberField;
    @FXML private VBox bankInfoVBox;
    @FXML private Button updateButton;

    // Add this ImageView to your FXML to show preview (optional)
    @FXML private ImageView profileImageView;

    private User currentUser;

    // Store image Base64 string for sending to backend
    private String profileImageBase64;

    public void setUserData(User user) {
        this.currentUser = user;
        if(user==null){
            throw new IllegalArgumentException("UpdateProfileController: passed user is null");
        }

        // Fill initial user info
        fullNameField.setText(user.getFullName());
        phoneNumberField.setText(user.getPhone());
        emailField.setText(user.getEmail());
        addressField.setText(user.getAddress());

        // Show/hide bank info based on role
        boolean isCourierOrSeller = user.getRole().equalsIgnoreCase("courier") || user.getRole().equalsIgnoreCase("seller");

        bankInfoVBox.setVisible(isCourierOrSeller);
        bankInfoVBox.setManaged(isCourierOrSeller);

        if (isCourierOrSeller && user.getBankInfo() != null) {
            bankNameField.setText(user.getBankInfo().getBankName());
            accountNumberField.setText(user.getBankInfo().getAccountNumber());
        }

        // If user already has profile image (Base64), decode and show it
        if (user.getProfileImageBase64() != null && !user.getProfileImageBase64().isEmpty()) {
            byte[] imageBytes = Base64.getDecoder().decode(user.getProfileImageBase64());
            Image image = new Image(new ByteArrayInputStream(imageBytes));
            profileImageView.setImage(image);
            profileImageBase64 = user.getProfileImageBase64();
        }
    }

    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(updateButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Show preview in ImageView
                Image image = new Image(new FileInputStream(selectedFile));
                profileImageView.setImage(image);

                // Convert file to Base64 string for backend
                byte[] fileContent = java.nio.file.Files.readAllBytes(selectedFile.toPath());
                profileImageBase64 = Base64.getEncoder().encodeToString(fileContent);

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load image: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        if (currentUser == null) {
            showAlert("Error", "User data not initialized properly!", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Gather updated data
            JSONObject userJson = new JSONObject();
            userJson.put("fullName", fullNameField.getText());
            userJson.put("phone", phoneNumberField.getText());
            userJson.put("email", emailField.getText());
            userJson.put("address", addressField.getText());
            userJson.put("role", currentUser.getRole());

            if (currentUser.getRole().equalsIgnoreCase("courier") || currentUser.getRole().equalsIgnoreCase("seller")) {
                userJson.put("bankName", bankNameField.getText());
                userJson.put("accountNumber", accountNumberField.getText());
            }

            // Include profile image Base64 if selected
            if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                userJson.put("profileImageBase64", profileImageBase64);
            }

            // Send PUT request
            URL url = new URL("http://localhost:8080/api/users/" + currentUser.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = userJson.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();
            if (status == 200) {
                showAlert("Success", "Profile updated successfully!", Alert.AlertType.INFORMATION);
                redirectToDashboard();
            } else {
                showAlert("Error", "Update failed with status: " + status, Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Exception", "Failed to update: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void redirectToDashboard() {
        try {
            String fxmlPath;
            switch (currentUser.getRole().toLowerCase()) {
                case "buyer" -> fxmlPath = "/fxmls/buyer-dashboard.fxml";
                case "courier" -> fxmlPath = "/fxmls/Courier-dashboard.fxml";
                case "seller" -> fxmlPath = "/fxmls/seller-dashboard.fxml";
                default -> throw new IllegalStateException("Unknown role: " + currentUser.getRole());
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dashboardRoot = loader.load();

            // Optional: set user in the dashboard controller
            Object controller = loader.getController();
            if (controller instanceof BuyerDashboardController buyerController) {
                buyerController.setUserData(currentUser);
            } else if (controller instanceof CourierDashboardController courierController) {
                courierController.setUserData(currentUser);
            } else if (controller instanceof SellerDashboardController sellerController) {
                sellerController.setUserData(currentUser);
            }

            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
