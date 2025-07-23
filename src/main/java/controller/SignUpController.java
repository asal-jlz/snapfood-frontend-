package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

public class SignUpController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField addressField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button nextButton;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("Buyer", "Seller", "Courier");
    }

    @FXML
    public void handleNextButton(ActionEvent event) {
        String name = fullNameField.getText();
        String phone = phoneNumberField.getText();
        String address = addressField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Please fill in all fields.");
        } else {
            System.out.println("Registered:");
            System.out.println("Name: " + name);
            System.out.println("Phone: " + phone);
            System.out.println("Address: " + address);
            System.out.println("Role: " + role);
            System.out.println("Password: " + password);

        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
