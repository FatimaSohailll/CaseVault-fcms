package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.models.Evidence;
import com.fcms.models.Participant;
import com.fcms.models.UserSession;
import com.fcms.services.CaseService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageCasesController {

    @FXML private VBox caseListContainer;
    @FXML private VBox caseDetailsRoot;
    @FXML private VBox evidenceContainer;
    @FXML private VBox participantsContainer;

    @FXML private Label caseTitle;
    @FXML private Label statusLabel;
    @FXML private Label priorityLabel;
    @FXML private Label typeLabel;
    @FXML private Label dateLabel;
    @FXML private Label locationLabel;
    @FXML private Label officerLabel;
    @FXML private Label descriptionLabel;

    @FXML private Button editCaseBtn;

    private final CaseService caseService = new CaseService();
    private Case activeCase;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        System.out.println("ManageCasesController.initialize()");

        showPlaceholder();
        updateEditButtonState();
        loadCases();

        if (editCaseBtn != null) {
            editCaseBtn.setOnAction(e -> {
                UserSession s = UserSession.getInstance();
                if (s == null || !s.isLoggedIn()) {
                    showAlert("Not signed in", "Please sign in to edit cases.");
                    return;
                }
                if (!s.isPoliceOfficer()) {
                    showAlert("Permission denied", "Only Police Officers can edit cases.");
                    return;
                }
                if (activeCase == null) {
                    showAlert("No case selected", "Please select a case first.");
                    return;
                }
                if (isClosedStatus(activeCase.getStatus())) {
                    showAlert("Cannot edit", "This case is closed and cannot be edited.");
                    return;
                }

                try {
                    Stage stage = (Stage) caseDetailsRoot.getScene().getWindow();
                    BorderPane root = (BorderPane) stage.getScene().getRoot();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/policeOfficer/editCase.fxml"));
                    Parent editRoot = loader.load();

                    Object controller = loader.getController();
                    if (controller instanceof EditCaseController editController) {
                        editController.setCase(activeCase);

                        // After SAVE: reload manageCases.fxml
                        editController.setOnSaved(savedCase -> {
                            System.out.println("onSaved callback: " + savedCase.getId());
                            try {
                                FXMLLoader manageLoader = new FXMLLoader(getClass().getResource("/fxml/policeOfficer/manageCases.fxml"));
                                Parent manageRoot = manageLoader.load();
                                root.setCenter(manageRoot);
                            } catch (IOException ioEx) {
                                ioEx.printStackTrace();
                                Platform.runLater(() -> root.setCenter(caseDetailsRoot));
                            }
                        });

                        // After CANCEL: reload manageCases.fxml
                        editController.setOnCancel(() -> {
                            System.out.println("onCancel callback");
                            try {
                                FXMLLoader manageLoader = new FXMLLoader(getClass().getResource("/fxml/policeOfficer/manageCases.fxml"));
                                Parent manageRoot = manageLoader.load();
                                root.setCenter(manageRoot);
                            } catch (IOException ioEx) {
                                ioEx.printStackTrace();
                                Platform.runLater(() -> root.setCenter(caseDetailsRoot));
                            }
                        });
                    }

                    root.setCenter(editRoot);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("Load error", "Failed to open edit screen.");
                }
            });
        }
    }

    private void loadCases() {
        caseListContainer.getChildren().clear();

        UserSession s = UserSession.getInstance();
        List<Case> cases = null;

        if (s != null && s.isLoggedIn() && s.isPoliceOfficer()) {
            // Only fetch cases assigned to this officer
            cases = caseService.getCasesByOfficer(s.getUserID());
        } else {
            // If no session or not officer, show nothing
            Label none = new Label("No cases available");
            none.getStyleClass().add("placeholder-text");
            caseListContainer.getChildren().add(none);
            return;
        }

        if (cases == null || cases.isEmpty()) {
            Label none = new Label("No cases found");
            none.getStyleClass().add("placeholder-text");
            caseListContainer.getChildren().add(none);
            return;
        }

        for (Case c : cases) {
            List<Evidence> evidence = caseService.getEvidenceForCase(c.getId());
            List<Participant> participants = caseService.getParticipantsForCase(c.getId());
            addCaseTile(c, evidence, participants);
        }
    }

    private void showPlaceholder() {
        // Title
        if (caseTitle != null) {
            caseTitle.setText("Select a case from the list to view details");
            caseTitle.getStyleClass().removeIf(s -> s.equals("muted-title"));
            caseTitle.getStyleClass().add("section-title");
        }

        // Clear stat values
        if (statusLabel != null) statusLabel.setText("");
        if (priorityLabel != null) priorityLabel.setText("");
        if (typeLabel != null) typeLabel.setText("");
        if (dateLabel != null) dateLabel.setText("");
        if (locationLabel != null) locationLabel.setText("");
        if (officerLabel != null) officerLabel.setText("");
        if (descriptionLabel != null) descriptionLabel.setText("");

        // Disable edit button until a case is selected or session allows
        updateEditButtonState();

        // Evidence placeholder
        if (evidenceContainer != null) {
            evidenceContainer.getChildren().clear();
            Label evPlaceholder = new Label("No case selected");
            evPlaceholder.getStyleClass().add("placeholder-text");
            evidenceContainer.getChildren().add(evPlaceholder);
        }

        // Participants placeholder
        if (participantsContainer != null) {
            participantsContainer.getChildren().clear();
            Label pPlaceholder = new Label("No case selected");
            pPlaceholder.getStyleClass().add("placeholder-text");
            participantsContainer.getChildren().add(pPlaceholder);
        }

        // Remove any previous placeholder card and add a friendly card
        if (caseDetailsRoot != null) {
            caseDetailsRoot.getChildren().removeIf(node -> "empty-placeholder-card".equals(node.getId()));

            VBox placeholderCard = new VBox(8);
            placeholderCard.setId("empty-placeholder-card");
            placeholderCard.getStyleClass().addAll("manage-card", "placeholder-card");
            Label phTitle = new Label("No case selected");
            phTitle.getStyleClass().add("manage-card-title");
            Label phText = new Label("Choose a case from the list on the left to view or edit details.");
            phText.getStyleClass().add("placeholder-text");
            placeholderCard.getChildren().addAll(phTitle, phText);

            int insertIndex = 1;
            if (caseDetailsRoot.getChildren().size() > insertIndex) {
                caseDetailsRoot.getChildren().add(insertIndex, placeholderCard);
            } else {
                caseDetailsRoot.getChildren().add(placeholderCard);
            }
        }
    }

    private void addCaseTile(Case c, List<Evidence> evidence, List<Participant> participants) {
        String id = safe(c.getId());
        String title = safe(c.getTitle());
        String status = normalizeStatus(c.getStatus());
        String priority = normalizePriority(c.getPriority());
        String type = safe(c.getType());
        String location = safe(c.getLocation());
        String officer = safe(c.getAssignedOfficer());
        String description = safeOrDefault(c.getDescription(), "No description available");
        String date = c.getDateRegistered() != null ? c.getDateRegistered().format(DATE_FMT) : "";

        VBox tile = new VBox(6);
        tile.getStyleClass().add("case-tile");
        tile.setMaxWidth(Double.MAX_VALUE);

        Label idLabel = new Label(id);
        idLabel.getStyleClass().add("case-id");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("case-title");

        Label statusPill = new Label(status);
        statusPill.getStyleClass().add("status-pill");
        switch (status.toLowerCase()) {
            case "under investigation", "investigation" -> statusPill.getStyleClass().add("status-investigation");
            case "evidence analysis", "analysis" -> statusPill.getStyleClass().add("status-analysis");
            case "open" -> statusPill.getStyleClass().add("status-open");
            case "closed" -> statusPill.getStyleClass().add("status-closed");
            default -> statusPill.getStyleClass().add("status-open");
        }

        Label priorityPill = new Label(capitalize(priority));
        priorityPill.getStyleClass().add("priority-pill");
        switch (priority.toLowerCase()) {
            case "high" -> priorityPill.getStyleClass().add("priority-high");
            case "medium" -> priorityPill.getStyleClass().add("priority-medium");
            default -> priorityPill.getStyleClass().add("priority-low");
        }

        Label dateLabelTile = new Label(date);
        dateLabelTile.getStyleClass().add("case-date");

        HBox meta = new HBox(8, statusPill, priorityPill, dateLabelTile);
        meta.getStyleClass().add("case-meta");

        tile.getChildren().addAll(idLabel, titleLabel, meta);

        tile.setOnMouseEntered(e -> tile.getStyleClass().add("hover"));
        tile.setOnMouseExited(e -> tile.getStyleClass().remove("hover"));

        tile.setOnMouseClicked(e -> {
            // clear previous active
            for (Node n : caseListContainer.getChildren()) {
                n.getStyleClass().remove("active");
            }
            tile.getStyleClass().add("active");

            activeCase = c;

            // Remove placeholder card if present
            if (caseDetailsRoot != null) {
                caseDetailsRoot.getChildren().removeIf(node -> "empty-placeholder-card".equals(node.getId()));
            }

            // Populate right-side details
            if (caseTitle != null) caseTitle.setText(id + " — " + title);
            if (statusLabel != null) statusLabel.setText(status);
            if (priorityLabel != null) priorityLabel.setText(capitalize(priority));
            if (typeLabel != null) typeLabel.setText(type);
            if (dateLabel != null) dateLabel.setText(date);
            if (locationLabel != null) locationLabel.setText(location);
            if (officerLabel != null) officerLabel.setText(officer);
            if (descriptionLabel != null) descriptionLabel.setText(description);

            // Enable/disable edit button based on session and case status
            UserSession s = UserSession.getInstance();
            boolean closed = isClosedStatus(activeCase.getStatus());
            boolean canEdit = s != null && s.isLoggedIn() && s.isPoliceOfficer() && !closed;
            if (editCaseBtn != null) editCaseBtn.setDisable(!canEdit);

            // Evidence
            if (evidenceContainer != null) {
                evidenceContainer.getChildren().clear();
                if (evidence == null || evidence.isEmpty()) {
                    Label l = new Label("No evidence linked");
                    l.getStyleClass().add("placeholder-text");
                    evidenceContainer.getChildren().add(l);
                } else {
                    for (Evidence ev : evidence) {
                        VBox item = new VBox(2);
                        item.getStyleClass().add("list-item");

                        Label evTitle = new Label(safe(ev.getId()) + " — " + safe(ev.getDescription()));
                        evTitle.getStyleClass().add("item-title");

                        Label evCollected = new Label("Collected: " + safe(String.valueOf(ev.getCollectionDateTime())));
                        evCollected.getStyleClass().add("item-meta");

                        Label evLocation = new Label("Location: " + safe(ev.getLocation()));
                        evLocation.getStyleClass().add("item-meta");

                        item.getChildren().addAll(evTitle, evCollected, evLocation);
                        evidenceContainer.getChildren().add(item);
                    }
                }
            }

            // Participants
            if (participantsContainer != null) {
                participantsContainer.getChildren().clear();
                if (participants == null || participants.isEmpty()) {
                    Label l = new Label("No participants linked");
                    l.getStyleClass().add("placeholder-text");
                    participantsContainer.getChildren().add(l);
                } else {
                    for (Participant p : participants) {
                        VBox item = new VBox(2);
                        item.getStyleClass().add("list-item");

                        Label pTitle = new Label(safe(p.getName()) + " — " + safe(p.getRole()));
                        pTitle.getStyleClass().add("item-title");

                        Label pContact = new Label("Contact: " + safe(p.getContact()));
                        pContact.getStyleClass().add("item-meta");

                        item.getChildren().addAll(pTitle, pContact);
                        participantsContainer.getChildren().add(item);
                    }
                }
            }
        });

        caseListContainer.getChildren().add(tile);
    }

    private void updateEditButtonState() {
        UserSession s = UserSession.getInstance();
        boolean canEdit = s != null && s.isLoggedIn() && s.isPoliceOfficer();
        if (editCaseBtn != null) editCaseBtn.setDisable(!canEdit);
    }

    private boolean isClosedStatus(String status) {
        if (status == null) return false;
        return status.trim().equalsIgnoreCase("closed");
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            a.setTitle(title);
            a.setHeaderText(null);
            a.setContentText(content);
            a.showAndWait();
        });
    }

    private String safe(String s) { return s == null ? "" : s; }
    private String safeOrDefault(String s, String def) { return (s == null || s.isBlank()) ? def : s; }

    private String capitalize(String s) {
        String v = safe(s).trim().toLowerCase();
        return v.isEmpty() ? "" : Character.toUpperCase(v.charAt(0)) + v.substring(1);
    }

    private String normalizeStatus(String s) {
        String v = safe(s).trim().toLowerCase();
        if (v.isBlank()) return "Open";
        if (v.contains("investigation")) return "Under Investigation";
        if (v.contains("analysis")) return "Evidence Analysis";
        if (v.contains("closed")) return "Closed";
        if (v.contains("open")) return "Open";
        return capitalize(v);
    }

    private String normalizePriority(String s) {
        String v = safe(s).trim().toLowerCase();
        if (v.isBlank()) return "Medium";
        if (v.startsWith("h")) return "High";
        if (v.startsWith("m")) return "Medium";
        if (v.startsWith("l")) return "Low";
        return capitalize(v);
    }
}