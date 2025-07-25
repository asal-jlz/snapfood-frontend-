package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Food;
import model.Seller;
import model.User;
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
    @FXML private Button logoutBtn;

    private ObservableList<Food> foodList = FXCollections.observableArrayList();
    private Seller currentSeller;

    // For external use: inject a Seller (or User)
    public void setUserData(User user) {
        if (user instanceof Seller seller) {
            this.currentSeller = seller;
            Session.setCurrentSeller(seller);  // Optional: global access
            updateSellerUI();
        } else {
            showError("Invalid user type for Seller Dashboard.");
        }
    }

    @FXML
    public void initialize() {
        // Fallback to session
        if (currentSeller == null) {
            currentSeller = Session.getCurrentSeller();
        }

        if (currentSeller == null) {
            System.err.println("⚠️ Seller not initialized.");
            welcomeLabel.setText("Welcome, Seller");
            return;
        }

        updateSellerUI();

        setupTable();
        loadFoodData();
        setupPhotoUploadColumn();
    }

    private void updateSellerUI() {
        if (welcomeLabel != null && currentSeller != null) {
            welcomeLabel.setText("Welcome, " + currentSeller.getRestaurantName());
            if (currentSeller.getLogoUrl() != null && !currentSeller.getLogoUrl().isEmpty()) {
                try {
                    restaurantLogo.setImage(new Image(currentSeller.getLogoUrl()));
                } catch (Exception e) {
                    System.err.println("Invalid logo URL: " + e.getMessage());
                    restaurantLogo.setImage(null);
                }
            }
        }
    }

    private void setupTable() {
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty());
        categoryCol.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        descriptionCol.setCellValueFactory(cell -> cell.getValue().descriptionProperty());

        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");

            {
                editBtn.setOnAction(e -> {
                    Food food = getTableView().getItems().get(getIndex());
                    System.out.println("Editing food: " + food.getName());
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

    private void setupPhotoUploadColumn() {
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
                        System.out.println("Uploaded image for food: " + selectedFood.getName());
                        // Implement photo save logic here
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

    private void loadFoodData() {
        // Example data
        foodList.setAll(
                new Food("Burger", "8.99", "Fast Food", "Delicious burger", "url"),
                new Food("Pizza", "10.00", "Italian", "Cheesy pizza", "url")
        );
    }

    // === UPDATED handleAddFood: OPEN MODAL DIALOG TO ADD FOOD INLINE ===
    @FXML
    private void handleAddFood(ActionEvent event) {
        Dialog<Food> dialog = new Dialog<>();
        dialog.setTitle("Add New Food Item");

        // Buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Input fields
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Disable Add button until name entered
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        nameField.textProperty().addListener((obs, oldVal, newVal) ->
                addButton.setDisable(newVal.trim().isEmpty())
        );

        // Convert result to Food object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Food(
                        nameField.getText().trim(),
                        priceField.getText().trim(),
                        categoryField.getText().trim(),
                        descriptionField.getText().trim(),
                        ""  // photo URL empty for now
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(food -> foodList.add(food));
    }

    @FXML
    private void handleEditMenu(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Edit Menu feature coming soon.");
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
            showError("Failed to load View Orders page.");
        }
    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/UpdateYourProfile.fxml"));
            Scene scene = new Scene(loader.load());

            UpdateProfileController controller = loader.getController();
            controller.setUserData(currentSeller);

            Stage stage = (Stage) editProfileBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Failed to load Edit Profile page.");
        }
    }

    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Restaurant Logo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(uploadPhotoBtn.getScene().getWindow());

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            restaurantLogo.setImage(image);
            // Optionally update backend or Seller object
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.setCurrentSeller(null);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(loginScene);
        } catch (IOException e) {
            showError("Failed to load Login page.");
        }
    }

    private void showError(String message) {
        System.err.println(message);
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
