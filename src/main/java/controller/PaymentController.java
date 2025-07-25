package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class PaymentController {

    @FXML private Label totalPriceLabel;
    @FXML private TextField cardNumberField, cvvField, expMonthField, expYearField;
    @FXML private PasswordField paymentPasswordField;

    // This should be set before loading payment.fxml
    private double totalPrice = 0.0;
    private double userWalletBalance = 0.0;

    public void setTotalPrice(double price) {
        this.totalPrice = price;
        totalPriceLabel.setText("Total Price : $ " + String.format("%.2f", totalPrice));
    }

    public void setUserWalletBalance(double balance) {
        this.userWalletBalance = balance;
    }

    @FXML
    private void onCheckout() {
        // Add real validation here
        System.out.println("Processing card payment...");
        goToBuyerDashboard();
    }

    @FXML
    private void onPayWithWallet() {
        if (userWalletBalance >= totalPrice) {
            userWalletBalance -= totalPrice;
            System.out.println("Paid with wallet. Remaining: $" + userWalletBalance);
            goToBuyerDashboard();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wallet Balance");
            alert.setHeaderText("Not Enough Balance");
            alert.setContentText("Please charge your wallet.");
            alert.showAndWait();
        }
    }

    private void goToBuyerDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/buyer-dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) totalPriceLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
