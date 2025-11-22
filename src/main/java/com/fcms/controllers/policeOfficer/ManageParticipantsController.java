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
    private ParticipantService participantService;
    private String currentOfficerId = "PO00001"; // This should come from your authentication system

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pass the officer ID to the service
        this.participantService = new ParticipantService(currentOfficerId);
        setupTable();
        loadParticipants(); // This will now load only participants from this officer's cases
    }

    private void setupTable() {
        // Configure cell value factories
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        idTypeColumn.setCellValueFactory(cellData -> cellData.getValue().idTypeProperty());
        idNumberColumn.setCellValueFactory(cellData -> cellData.getValue().idNumberProperty());

        // Style the entire table
        participantsTable.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 6;");
        participantsTable.setPrefHeight(400);

        // Remove grid lines
        participantsTable.setStyle("-fx-table-cell-border-color: transparent; -fx-background-color: white;");

        // Style columns
        styleColumn(idColumn);
        styleColumn(nameColumn);
        styleColumn(contactColumn);
        styleColumn(idTypeColumn);
        styleColumn(idNumberColumn);
        styleColumn(actionsColumn);

        // Special styling for role column - updated for database roles
        roleColumn.setCellFactory(column -> new TableCell<Participant, String>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-alignment: center; -fx-padding: 8;");
                } else {
                    setText(role);
                    if ("Suspect".equalsIgnoreCase(role)) {
                        setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #991b1b; -fx-background-radius: 12; -fx-padding: 4 8; -fx-font-size: 12px; -fx-alignment: center; -fx-font-weight: bold; -fx-border-color: #fecaca; -fx-border-width: 1;");
                    } else if ("Victim".equalsIgnoreCase(role)) {
                        setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1e40af; -fx-background-radius: 12; -fx-padding: 4 8; -fx-font-size: 12px; -fx-alignment: center; -fx-font-weight: bold; -fx-border-color: #93c5fd; -fx-border-width: 1;");
                    } else {
                        setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #374151; -fx-background-radius: 12; -fx-padding: 4 8; -fx-font-size: 12px; -fx-alignment: center; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Actions column
        actionsColumn.setCellFactory(param -> new TableCell<Participant, String>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #001440; -fx-border-color: #001440; -fx-border-radius: 4; -fx-padding: 4 12; -fx-font-size: 12px; -fx-cursor: hand;");
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
                    setStyle("-fx-alignment: center; -fx-padding: 8;");
                } else {
                    setGraphic(editButton);
                    setStyle("-fx-alignment: center; -fx-padding: 8;");
                }
            }
        });

        // Add row factory for alternating colors
        participantsTable.setRowFactory(tv -> new TableRow<Participant>() {
            @Override
            protected void updateItem(Participant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("-fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
                } else {
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: #fafafa; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
                    } else {
                        setStyle("-fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
                    }
                }
            }
        });
    }

    private void styleColumn(TableColumn<Participant, String> column) {
        column.setStyle("-fx-alignment: CENTER_LEFT; -fx-font-size: 14px; -fx-text-fill: #374151;");
        column.setCellFactory(tc -> new TableCell<Participant, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 8;");
                } else {
                    setText(item);
                    if (column == idColumn) {
                        setStyle("-fx-background-color: transparent; -fx-padding: 8; -fx-font-weight: bold; -fx-text-fill: #001440;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-padding: 8;");
                    }
                }
            }
        });
    }

    private void loadParticipants() {
        try {
            java.util.List<Participant> participantList = participantService.getAllParticipants();
            participants = FXCollections.observableArrayList(participantList);
            participantsTable.setItems(participants);

            System.out.println("Loaded " + participants.size() + " participants for officer: " + currentOfficerId);

        } catch (Exception e) {
            showErrorAlert("Failed to load participants: " + e.getMessage());
            e.printStackTrace(); // Add this to see detailed error
        }
    }

    // REMOVED addSampleData() method - no more dummy data

    @FXML
    private void handleAddParticipant() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/policeOfficer/addParticipant.fxml"));
            Parent root = loader.load();

            AddParticipantController controller = loader.getController();
            controller.setMainController(this);
            controller.setCurrentOfficerId(currentOfficerId); // Pass officer ID to add controller

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("Add New Participant");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            showErrorAlert("Cannot open participant form: " + e.getMessage());
            e.printStackTrace(); // Add this to see detailed error
        }
    }

    @FXML
    private void handleEditParticipant(Participant participant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/policeOfficer/editParticipant.fxml"));
            Parent root = loader.load();

            EditParticipantController controller = loader.getController();
            controller.setMainController(this);
            controller.setParticipantData(participant);
            //controller.setCurrentOfficerId(currentOfficerId); // Pass officer ID to edit controller

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("Edit Participant");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            showErrorAlert("Cannot open edit form: " + e.getMessage());
            e.printStackTrace(); // Add this to see detailed error
        }
    }

    @FXML
    private void handleBackToDashboard() {
        // UI navigation only
    }

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

    public void addNewParticipant(Participant participant) {
        try {
            participantService.addParticipant(participant);
            loadParticipants();
            showSuccessAlert("Participant added successfully!");
        } catch (BusinessException e) {
            showErrorAlert(e.getMessage());
        }
    }

    public void updateParticipant(Participant updatedParticipant) {
        try {
            participantService.updateParticipant(updatedParticipant);
            loadParticipants();
            showSuccessAlert("Participant updated successfully!");
        } catch (BusinessException e) {
            showErrorAlert(e.getMessage());
        }
    }

    public ObservableList<Participant> getParticipants() {
        return participants;
    }

    // Getter for officer ID
    public String getCurrentOfficerId() {
        return currentOfficerId;
    }

    // Setter for officer ID (if you need to change it dynamically)
    public void setCurrentOfficerId(String currentOfficerId) {
        this.currentOfficerId = currentOfficerId;
        // Re-initialize service with new officer ID
        this.participantService = new ParticipantService(currentOfficerId);
        loadParticipants(); // Reload data for the new officer
    }
}