package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.IOException;

public class AdminDashboardController {

    @FXML private Button logoutButton;
    @FXML private Button userListButton;
    @FXML private Button orderListButton;
    @FXML private Button salesReportButton;
    @FXML private Button systemStatsButton;
    @FXML private VBox userListView;
    @FXML private VBox orderListView;
    @FXML private VBox salesReportText;
    @FXML private VBox systemStatsView;
    @FXML private TableView<?> userTable;
    @FXML private Button approveUserButton;
    @FXML private Button deleteUserButton;
    @FXML private TableView<?> orderTable;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalOrdersLabel;
    @FXML private ImageView profileImageView;

    @FXML
    public void initialize() {
        hideAllViews();
        userListView.setVisible(true);
    }

    @FXML
    public void onUserListClick(ActionEvent event) {
        hideAllViews();
        userListView.setVisible(true);
    }

    @FXML
    public void onOrderListClick(ActionEvent event) {
        hideAllViews();
        orderListView.setVisible(true);
    }

    @FXML
    public void onSalesReportClick(ActionEvent event) {
        hideAllViews();
        salesReportText.setVisible(true);
    }

    @FXML
    public void onSystemStatsClick(ActionEvent event) {
        hideAllViews();
        systemStatsView.setVisible(true);
    }

    @FXML
    public void onLogoutClick(ActionEvent event) {
        System.out.println("Logging out...");

        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hideAllViews() {
        userListView.setVisible(false);
        orderListView.setVisible(false);
        salesReportText.setVisible(false);
        systemStatsView.setVisible(false);
    }
}
