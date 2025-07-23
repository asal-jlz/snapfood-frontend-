package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CompleteProfileController {

    @FXML
    private Button uploadPhotoButton;

    @FXML
    private VBox bankInfoSection;

    @FXML
    private TextField bankNameField;

    @FXML
    private TextField accountNumberField;

    @FXML
    private VBox brandInfoSection;

    @FXML
    private TextField brandNameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private Button continueButton;

    private String role;

    public void setRole(String role) {
        this.role = role.toLowerCase();

        uploadPhotoButton.setVisible(true);

        switch (this.role) {
            case "buyer":
                bankInfoSection.setVisible(false);
                bankInfoSection.setDisable(true);

                brandInfoSection.setVisible(false);
                brandInfoSection.setDisable(true);
                break;

            case "courier":
                bankInfoSection.setVisible(true);
                bankInfoSection.setDisable(false);

                brandInfoSection.setVisible(false);
                brandInfoSection.setDisable(true);
                break;

            case "seller":
                bankInfoSection.setVisible(true);
                bankInfoSection.setDisable(false);

                brandInfoSection.setVisible(true);
                brandInfoSection.setDisable(false);
                break;

            default:
                // If role unknown, hide optional sections
                bankInfoSection.setVisible(false);
                bankInfoSection.setDisable(true);

                brandInfoSection.setVisible(false);
                brandInfoSection.setDisable(true);
                break;
        }
    }

    @FXML
    public void onUploadPhoto() {
        System.out.println("Upload photo clicked");
    }

    @FXML
    public void onContinue() {
        System.out.println("Continue clicked");
        System.out.println("Role: " + role);
        System.out.println("Bank Name: " + bankNameField.getText());
        System.out.println("Account Number: " + accountNumberField.getText());
        System.out.println("Brand Name: " + brandNameField.getText());
        System.out.println("Description: " + descriptionField.getText());
    }
}
