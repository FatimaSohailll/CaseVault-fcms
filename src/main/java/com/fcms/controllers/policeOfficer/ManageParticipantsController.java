package com.fcms.controllers.policeOfficer;

import com.fcms.services.ParticipantService;
import com.fcms.services.BusinessException;
import com.fcms.models.Participant;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;

public class ManageParticipantsController implements Initializable {

    @FXML private VBox successAlert;
    @FXML private Label successMessageLabel;

    @FXML private TableView<Participant> participantsTable;
    @FXML private TableColumn<Participant, String> idColumn;
    @FXML private TableColumn<Participant, String> nameColumn;
    @FXML private TableColumn<Participant, String> roleColumn;
    @FXML private TableColumn<Participant, String> contactColumn;
    @FXML private TableColumn<Participant, String> idTypeColumn;
    @FXML private TableColumn<Participant, String> idNumberColumn;
    @FXML private TableColumn<Participant, String> actionsColumn;

    @FXML private Button addParticipantButton;

    private ObservableList<Participant> participants;
    private ParticipantService participantService; // Business layer

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.participantService = new ParticipantService(); // Initialize service
        setupTable();
        loadParticipants(); // Load data through service
    }

    private void setupTable() {
        // UI configuration only
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        idTypeColumn.setCellValueFactory(cellData -> cellData.getValue().idTypeProperty());
        idNumberColumn.setCellValueFactory(cellData -> cellData.getValue().idNumberProperty());

        participantsTable.setStyle("-fx-font-size: 14px;");

        // Actions column with edit button (UI only)
        actionsColumn.setCellFactory(param -> new TableCell<Participant, String>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #001440; -fx-border-color: #001440; -fx-border-radius: 4; -fx-padding: 4 8; -fx-font-size: 12px;");
                editButton.setOnAction(event -> {
                    Participant participant = getTableView().getItems().get(getIndex());
                    handleEditParticipant(participant);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        // Style role column cells with badges (UI only)
        roleColumn.setCellFactory(column -> new TableCell<Participant, String>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(role);
                    // UI styling logic
                    switch (role) {
                        case "Suspect":
                            setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #991b1b; -fx-background-radius: 12; -fx-padding: 4 8; -fx-font-size: 12px; -fx-alignment: center;");
                            break;
                        case "Victim":
                            setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1e40af; -fx-background-radius: 12; -fx-padding: 4 8; -fx-font-size: 12px; -fx-alignment: center;");
                            break;
                        case "Witness":
                            setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #374151; -fx-background-radius: 12; -fx-padding: 4 8; -fx-font-size: 12px; -fx-alignment: center;");
                            break;
                    }
                }
            }
        });
    }

    private void loadParticipants() {
        try {
            // Delegate to business layer
            java.util.List<Participant> participantList = participantService.getAllParticipants();
            participants = FXCollections.observableArrayList(participantList);
            participantsTable.setItems(participants);
        } catch (Exception e) {
            showErrorAlert("Failed to load participants: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddParticipant() {
        try {
            // UI navigation only
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/police/addParticipant.fxml"));
            Parent root = loader.load();

            AddParticipantController controller = loader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("Add New Participant");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            showErrorAlert("Cannot open participant form: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditParticipant(Participant participant) {
        try {
            // UI navigation only
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/police/editParticipant.fxml"));
            Parent root = loader.load();

            EditParticipantController controller = loader.getController();
            controller.setMainController(this);
            controller.setParticipantData(participant);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("Edit Participant");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            showErrorAlert("Cannot open edit form: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToDashboard() {
        // UI navigation only
        // Main.getSceneManager().showDashboard();
    }

    // UI methods only
    public void showSuccessAlert(String message) {
        successMessageLabel.setText(message);
        successAlert.setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    successAlert.setVisible(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Called by child dialogs
    public void addNewParticipant(Participant participant) {
        try {
            // Delegate to business layer
            participantService.addParticipant(participant);
            loadParticipants(); // Refresh UI
            showSuccessAlert("Participant added successfully!");
        } catch (BusinessException e) {
            showErrorAlert(e.getMessage());
        }
    }

    public void updateParticipant(Participant updatedParticipant) {
        try {
            // Delegate to business layer
            participantService.updateParticipant(updatedParticipant);
            loadParticipants(); // Refresh UI
            showSuccessAlert("Participant updated successfully!");
        } catch (BusinessException e) {
            showErrorAlert(e.getMessage());
        }
    }

    public ObservableList<Participant> getParticipants() {
        return participants;
    }
}