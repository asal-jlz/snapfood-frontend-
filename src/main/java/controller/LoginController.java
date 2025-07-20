package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.net.http.*;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class LoginController {

    @FXML
    private TextField phoneNumberField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink signUpLink;

    @FXML
    void onLoginClick(ActionEvent event) {
        String phone = phoneNumberField.getText();
        String password = passwordField.getText();

        if (phone.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill all fields.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"phone\":\"%s\",\"password\":\"%s\"}", phone, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:4567/api/login"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(request, BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            System.out.println("Login successful: " + response.body());
                            // load dashboard scene here
                        } else {
                            System.out.println("Login failed: " + response.body());
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onSignUpClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxmls/SignUp.fxml"));
            Stage stage = (Stage) signUpLink.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


