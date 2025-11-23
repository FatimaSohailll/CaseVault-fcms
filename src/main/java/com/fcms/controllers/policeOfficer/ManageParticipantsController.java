package com.fcms.controllers.policeOfficer;

import com.fcms.services.ParticipantService;
import com.fcms.services.BusinessException;
import com.fcms.controllers.policeOfficer.AddParticipantController;
import com.fcms.controllers.policeOfficer.EditParticipantController;
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
    private String currentCaseId = "CS00001"; // This should come from your authentication system

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pass the officer ID to the service
        this.participantService = new ParticipantService(currentCaseId);
        setupTable();
        loadParticipants(); // This will now load only participants from this officer's cases

        // Apply header styling after table is initialized
        applyHeaderStyling();
    }

    private void setupTable() {
        // Configure cell value factories
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        idTypeColumn.setCellValueFactory(cellData -> cellData.getValue().idTypeProperty());
        idNumberColumn.setCellValueFactory(cellData -> cellData.getValue().idNumberProperty());

        // Remove all extra spacing and styling
        participantsTable.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
        participantsTable.setPrefHeight(300);

        // Remove grid lines
        participantsTable.setStyle("-fx-table-cell-border-color: transparent; -fx-background-color: white;");

        // Style columns with proper alignment
        styleColumn(idColumn);
        styleColumn(nameColumn);
        styleColumn(contactColumn);
        styleColumn(idTypeColumn);
        styleColumn(idNumberColumn);
        styleColumn(actionsColumn);

        // Special styling for role column
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
                    } else if ("Witness".equalsIgnoreCase(role)) {
                        setStyle("-fx-background-color: #f0fdf4; -fx-text-fill: #166534; -fx-background-radius: 12; -fx-padding: 4 8; -fx-font-size: 12px; -fx-alignment: center; -fx-font-weight: bold; -fx-border-color: #bbf7d0; -fx-border-width: 1;");
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

        // Clean row factory - no alternating colors, just clean lines
        participantsTable.setRowFactory(tv -> new TableRow<Participant>() {
            @Override
            protected void updateItem(Participant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
                } else {
                    setText(null);
                    setStyle("-fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
                }
            }
        });
    }

    private void applyHeaderStyling() {
        // Apply header styling after a short delay to ensure table is rendered
        javafx.application.Platform.runLater(() -> {
            try {
                // Style header if it exists
                javafx.scene.Node header = participantsTable.lookup(".column-header-background");
                if (header != null) {
                    header.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");
                }

                // Style individual column headers
                for (TableColumn<?, ?> col : participantsTable.getColumns()) {
                    javafx.scene.Node columnHeader = col.getStyleableNode();
                    if (columnHeader != null) {
                        columnHeader.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-text-fill: #374151; -fx-font-weight: bold; -fx-font-size: 14px;");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error applying header styling: " + e.getMessage());
            }
        });
    }

    private void styleColumn(TableColumn<Participant, String> column) {
        column.setStyle("-fx-alignment: CENTER_LEFT; -fx-font-size: 14px; -fx-text-fill: #374151; -fx-border-color: transparent;");
        column.setCellFactory(tc -> new TableCell<Participant, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 12 8; -fx-border-color: transparent;");
                } else {
                    setText(item);
                    if (column == idColumn) {
                        setStyle("-fx-background-color: transparent; -fx-padding: 12 8; -fx-font-weight: bold; -fx-text-fill: #001440; -fx-border-color: transparent;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-padding: 12 8; -fx-border-color: transparent;");
                    }
                }
            }
        });
    }

    private void loadParticipants() {
        try {
            java.util.List<Participant> participantList = participantService.getParticipantsByCase(currentCaseId);
            participants = FXCollections.observableArrayList(participantList);
            participantsTable.setItems(participants);

            System.out.println("Loaded " + participants.size() + " participants for case: " + currentCaseId);

        } catch (Exception e) {
            showErrorAlert("Failed to load participants: " + e.getMessage());
            e.printStackTrace(); // Add this to see detailed error
        }
    }

    @FXML
    private void handleAddParticipant() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/policeOfficer/addParticipant.fxml"));
            Parent root = loader.load();

            AddParticipantController controller = loader.getController();
            controller.setMainController(this);
            controller.setCurrentCaseId(currentCaseId); // Pass case ID to add controller

            Stage stage = new Stage();
            Scene scene = new Scene(root, 500, 550);

            // Apply stylesheet to the Scene, not the Stage
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("Add New Participant");
            stage.setScene(scene);
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

            Stage stage = new Stage();
            Scene scene = new Scene(root); // Set same dimensions as add form
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("Edit Participant");
            stage.setScene(scene); // Use the scene we created with stylesheet
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

    public String getCurrentCaseId() {
        return currentCaseId;
    }

    public void setCurrentCaseId(String currentCaseId) {
        this.currentCaseId = currentCaseId;
        // Re-initialize service with new officer ID
        this.participantService = new ParticipantService(currentCaseId);
        loadParticipants(); // Reload data for the new officer
    }
}