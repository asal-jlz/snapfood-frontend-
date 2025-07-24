package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Food;
import model.Seller;
import Session.Session;

import java.io.File;
import java.io.IOException;

public class SellerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ImageView restaurantLogo;
    @FXML private TableView<Food> foodTable;
    @FXML private TableColumn<Food, String> nameCol;
    @FXML private TableColumn<Food, String> priceCol;
    @FXML private TableColumn<Food, String> categoryCol;
    @FXML private TableColumn<Food, String> descriptionCol;
    @FXML private TableColumn<Food, Void> actionsCol;
    @FXML private TableColumn<Food, Void> photoCol;
    @FXML private Button addFoodBtn;
    @FXML private Button editMenuBtn;
    @FXML private Button viewOrdersBtn;
    @FXML private Button editProfileBtn;
    @FXML private Button uploadPhotoBtn;


    private ObservableList<Food> foodList = FXCollections.observableArrayList();

    public void initialize() {
        Seller currentSeller = Session.getCurrentSeller(); // You need a Session singleton or similar

        welcomeLabel.setText("Welcome, " + currentSeller.getRestaurantName());

        if (currentSeller.getLogoUrl() != null) {
            restaurantLogo.setImage(new Image(currentSeller.getLogoUrl()));
        }

        setupTable();
        loadFoodData();
        photoCol.setCellFactory(col -> new TableCell<>() {
            private final Button uploadBtn = new Button("Upload");

            {
                uploadBtn.setOnAction(e -> {
                    Food selectedFood = getTableView().getItems().get(getIndex());
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Select Food Photo");
                    fileChooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
                    );

                    File file = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());
                    if (file != null) {
                        // Do something with the image file:
                        // selectedFood.setImageUrl(file.toURI().toString());
                        System.out.println("Uploaded image: " + file.getAbsolutePath());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : uploadBtn);
            }
        });

    }

    private void setupTable() {
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty());
        categoryCol.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        descriptionCol.setCellValueFactory(cell -> cell.getValue().descriptionProperty());

        // Optional: Add action buttons (e.g., Edit/Delete) inside "Actions" column
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");

            {
                editBtn.setOnAction(e -> {
                    Food food = getTableView().getItems().get(getIndex());
                    // open food edit form
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editBtn);
            }
        });

        foodTable.setItems(foodList);
    }

    private void loadFoodData() {
        // Mock data example
        foodList.addAll(
                new Food("Burger", "8.99", "Fast Food", "Delicious burger", "url"),
                new Food("Pizza", "10.00", "Italian", "Cheesy pizza", "url")
        );
    }

    @FXML
    private void handleAddFood(ActionEvent event) {
        // Open AddFood page (you can make it a modal)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-food.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Add New Food");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditMenu(ActionEvent event) {
        // This could toggle editing mode or navigate to a dedicated menu editor
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Edit Menu Clicked");
        alert.show();
    }

    @FXML
    private void handleViewOrders(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view-orders.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) viewOrdersBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/updateyourprofile.fxml"));
            Scene scene = new Scene(loader.load());

            UpdateProfileController controller = loader.getController();
            controller.setUserData(Session.getCurrentSeller()); // Prefill the form

            Stage stage = (Stage) editProfileBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(uploadPhotoBtn.getScene().getWindow());

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            restaurantLogo.setImage(image);
        }
    }

}
