package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.http.*;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Base64;

public class FoodViewingController {

    @FXML private ImageView foodImageView;
    @FXML private ImageView restaurantLogo;
    @FXML private Label restaurantNameLabel;
    @FXML private Label foodDescriptionLabel;
    @FXML private Label foodPriceLabel;
    @FXML private Label foodCategoryLabel;
    @FXML private TextArea opinionTextArea;
    @FXML private TextField ratingTextField;
    @FXML private Button submitButton;
    @FXML private Button photoButton;

    private File selectedPhotoFile;

    // Called when "photo" button is clicked
    @FXML
    private void onPhotoButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Food Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedPhotoFile = file;
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Photo selected: " + file.getName());
            alert.show();
        }
    }

    // Called when "submit" button is clicked
    @FXML
    private void onSubmitButtonClick() {
        String opinion = opinionTextArea.getText();
        String ratingStr = ratingTextField.getText();

        if (opinion.isEmpty() || ratingStr.isEmpty()) {
            showAlert("Please fill in both opinion and rating.");
            return;
        }

        double rating;
        try {
            rating = Double.parseDouble(ratingStr);
            if (rating < 0 || rating > 5) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert("Rating must be a number between 0 and 5.");
            return;
        }

        String encodedImage = "";
        if (selectedPhotoFile != null) {
            try (FileInputStream fis = new FileInputStream(selectedPhotoFile)) {
                byte[] imageBytes = fis.readAllBytes();
                encodedImage = Base64.getEncoder().encodeToString(imageBytes);
            } catch (IOException e) {
                showAlert("Error reading selected image.");
                return;
            }
        }

        // Send review to backend
        sendReviewToServer(opinion, rating, encodedImage);
    }

    private void sendReviewToServer(String opinion, double rating, String base64Photo) {
        try {
            String jsonBody = String.format(
                    "{\"opinion\":\"%s\", \"rating\": %.1f, \"photo\": \"%s\"}",
                    opinion, rating, base64Photo
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:4567/api/review")) // Replace with your actual endpoint
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            showAlert("Review submitted successfully!");
                        } else {
                            showAlert("Failed to submit review. Server returned: " + response.statusCode());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error submitting review.");
        }
    }

    public void setFoodDetails(String foodImageUrl, String foodName, String description,
                               double price, String category, String restaurantName, String restaurantLogoUrl) {
        foodImageView.setImage(new Image(foodImageUrl));
        foodDescriptionLabel.setText(description);
        foodPriceLabel.setText("Price: $" + String.format("%.2f", price));
        foodCategoryLabel.setText("Category: " + category);
        restaurantNameLabel.setText(restaurantName);
        restaurantLogo.setImage(new Image(restaurantLogoUrl));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.show();
    }
}
