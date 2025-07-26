package controller;

import DTO.FoodItemDTO;
import Session.Session;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Food;
import model.Seller;
import model.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

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

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // Inject user data (expect Seller)
    public void setUserData(User user) {
        if (user instanceof Seller seller) {
            System.out.println("SellerDashboardController.setUserData: seller=" + seller.getBrandName() + ", restaurantId=" + seller.getRestaurantId());
            this.currentSeller = seller;
            Session.setCurrentSeller(seller);

            updateSellerUI();

            setupTable();
            setupPhotoUploadColumn();

            if (seller.getRestaurantId() > 0) {
                loadFoodData();
            } else {
                System.err.println("Invalid restaurantId on seller: " + seller.getRestaurantId());
                // You can trigger a reload restaurantId here if needed
            }
        } else {
            showError("Invalid user type for Seller Dashboard.");
        }
    }

    @FXML
    public void initialize() {
        if (currentSeller == null && Session.getCurrentSeller() != null) {
            setUserData(Session.getCurrentSeller());
        }
    }

    private void updateSellerUI() {
        if (welcomeLabel != null && currentSeller != null) {
            welcomeLabel.setText("Welcome, " + currentSeller.getBrandName());

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
                    showEditFoodDialog(food);
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
                        // TODO: implement actual upload logic to backend here
                        // After success, update food imageUrl and refresh table
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
        if (currentSeller == null) return;

        int restaurantId = currentSeller.getRestaurantId();

        if (restaurantId <= 0) {
            System.err.println("Cannot load foods: invalid restaurantId " + restaurantId);
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/foods/restaurant/" + restaurantId))
                .header("Authorization", "Bearer " + Session.getToken())
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        Type listType = new TypeToken<List<FoodItemDTO>>(){}.getType();
                        List<FoodItemDTO> foodItems = gson.fromJson(json, listType);

                        List<Food> foods = foodItems.stream().map(dto ->
                                new Food(dto.id, dto.name, String.format("%.2f", dto.price), dto.category, dto.description, dto.imageUrl)
                        ).toList();

                        Platform.runLater(() -> foodList.setAll(foods));
                    } catch (Exception e) {
                        System.err.println("Failed to parse foods JSON: " + e.getMessage());
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Failed to load foods: " + ex.getMessage());
                    return null;
                });
    }

    private void showEditFoodDialog(Food food) {
        Dialog<Food> dialog = new Dialog<>();
        dialog.setTitle("Edit Food Item");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField(food.getName());
        TextField priceField = new TextField(food.getPrice());
        TextField categoryField = new TextField(food.getCategory());
        TextField descriptionField = new TextField(food.getDescription());
        TextField imageUrlField = new TextField(food.getImageUrl());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionField, 1, 3);
        grid.add(new Label("Image URL:"), 0, 4);
        grid.add(imageUrlField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Food(
                        food.getId(),
                        nameField.getText().trim(),
                        priceField.getText().trim(),
                        categoryField.getText().trim(),
                        descriptionField.getText().trim(),
                        imageUrlField.getText().trim()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::sendUpdateFoodRequest);
    }

    private void sendUpdateFoodRequest(Food updatedFood) {
        try {
            double price = Double.parseDouble(updatedFood.getPrice());

            FoodItemDTO dto = new FoodItemDTO(
                    updatedFood.getId(),
                    currentSeller.getRestaurantId(),
                    updatedFood.getName(),
                    updatedFood.getImageUrl(),
                    updatedFood.getDescription(),
                    price,
                    0, // stock (example)
                    updatedFood.getCategory(),
                    "" // keywords (example)
            );

            String json = gson.toJson(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/foods/" + updatedFood.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Session.getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(responseBody -> {
                        System.out.println("Update response: " + responseBody);

                        Platform.runLater(() -> {
                            int idx = foodList.indexOf(updatedFood);
                            if (idx >= 0) {
                                foodList.set(idx, updatedFood);
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> showError("Failed to update food item: " + ex.getMessage()));
                        return null;
                    });

        } catch (NumberFormatException e) {
            showError("Price must be a valid number.");
        } catch (Exception e) {
            showError("Error sending update request: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddFood(ActionEvent event) {
        if (currentSeller == null) {
            showError("Seller data not loaded. Please re-login.");
            return;
        }
        Dialog<Food> dialog = new Dialog<>();
        dialog.setTitle("Add New Food Item");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

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

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        nameField.textProperty().addListener((obs, oldVal, newVal) ->
                addButton.setDisable(newVal.trim().isEmpty())
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Food(
                        nameField.getText().trim(),
                        priceField.getText().trim(),
                        categoryField.getText().trim(),
                        descriptionField.getText().trim(),
                        "" // empty imageUrl
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(food -> {
            addFoodToBackend(food);
        });
    }

    private void addFoodToBackend(Food food) {
        try {
            double price = Double.parseDouble(food.getPrice());

            FoodItemDTO dto = new FoodItemDTO(
                    0, // new food, id = 0 or omit if backend auto generates
                    currentSeller.getRestaurantId(),
                    food.getName(),
                    food.getImageUrl(),
                    food.getDescription(),
                    price,
                    0, // stock
                    food.getCategory(),
                    "" // keywords
            );

            String json = gson.toJson(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/foods"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Session.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 201 || response.statusCode() == 200) {
                            System.out.println("Food added successfully.");
                            // Reload food list after adding
                            loadFoodData();
                        } else {
                            Platform.runLater(() -> showError("Failed to add food: " + response.body()));
                        }
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> showError("Error adding food: " + ex.getMessage()));
                        return null;
                    });

        } catch (NumberFormatException e) {
            showError("Invalid price format.");
        } catch (Exception e) {
            showError("Error sending add food request: " + e.getMessage());
        }
    }


    @FXML
    private void handleEditMenu(ActionEvent event) {
        if (currentSeller == null) {
            showError("Seller data not loaded. Please re-login.");
            return;
        }

        if (foodList.isEmpty()) {
            showError("No food items available to edit.");
            return;
        }

        Dialog<Food> dialog = new Dialog<>();
        dialog.setTitle("Remove Food Item");

        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, ButtonType.CANCEL);

        // Dropdown with food names
        ComboBox<Food> foodComboBox = new ComboBox<>(foodList);
        foodComboBox.setPromptText("Select item to delete");
        foodComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Food item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        foodComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Food item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Food to Delete:"), 0, 0);
        grid.add(foodComboBox, 1, 0);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteButtonType) {
                return foodComboBox.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::deleteFoodFromBackend);
    }
    private void deleteFoodFromBackend(Food food) {
        if (food == null) {
            showError("No food selected for deletion.");
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/foods/" + food.getId()))
                    .header("Authorization", "Bearer " + Session.getToken())
                    .DELETE()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200 || response.statusCode() == 204) {
                            Platform.runLater(() -> {
                                foodList.remove(food);
                                System.out.println("Food deleted: " + food.getName());
                            });
                        } else {
                            Platform.runLater(() -> showError("Failed to delete food: " + response.body()));
                        }
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> showError("Error deleting food: " + ex.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            showError("Failed to send delete request: " + e.getMessage());
        }
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
            // TODO: send the new logo image/file to backend and update Seller.logoUrl
            // After successful upload, update currentSeller.logoUrl and refresh UI if needed
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.setCurrentSeller(null);
        Session.setUser(null);
        Session.setToken(null);

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
