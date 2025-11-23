package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.models.Evidence;
import com.fcms.models.Participant;
import com.fcms.models.UserSession;
import com.fcms.services.CaseService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Consumer;

public class EditCaseController {

    @FXML private TextField caseIdField;
    @FXML private TextField titleField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextField typeField;
    @FXML private TextField dateField;
    @FXML private TextField locationField;
    @FXML private TextField officerField;
    @FXML private TextArea descriptionArea;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @FXML private VBox evidenceContainer;
    @FXML private VBox participantsContainer;

    private final CaseService caseService = new CaseService();
    private Case caseToEdit;
    private Consumer<Case> onSaved; // optional callback
    private Runnable onCancel; // optional callback for cancel

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Tracks whether fields and item edit buttons should be enabled
    private boolean editable = true;

    @FXML
    public void initialize() {
        System.out.println("EditCaseController.initialize()");

        // Populate combo boxes with the allowed options
        if (priorityCombo != null) {
            priorityCombo.getItems().clear();
            priorityCombo.getItems().addAll("Low", "Medium", "High");
            priorityCombo.setEditable(false);
        }
        if (statusCombo != null) {
            statusCombo.getItems().clear();
            statusCombo.getItems().addAll("Open", "Closed", "Archived");
            statusCombo.setEditable(false);
        }

        if (saveBtn != null) saveBtn.setOnAction(e -> onSaveClicked());
        if (cancelBtn != null) cancelBtn.setOnAction(e -> onCancelClicked());

        // If session does not allow editing, disable editing controls immediately
        if (!sessionCanEdit()) {
            setEditable(false);
            System.out.println("Editing disabled: no session or insufficient role");
        }
    }

    /**
     * Called by loader to provide the Case to edit.
     * Populates fields and enables/disables editing based on status and session.
     */
    public void setCase(Case c) {
        System.out.println("EditCaseController.setCase() called: " + (c == null ? "null" : c.getId()));
        this.caseToEdit = c;

        if (c == null) {
            clearForm();
            return;
        }

        // Populate fields
        caseIdField.setText(safe(c.getId()));
        titleField.setText(safe(c.getTitle()));
        // set combo selections (capitalize for display)
        if (statusCombo != null) statusCombo.getSelectionModel().select(capitalize(safe(c.getStatus())));
        if (priorityCombo != null) priorityCombo.getSelectionModel().select(capitalize(safe(c.getPriority())));
        typeField.setText(safe(c.getType()));
        dateField.setText(c.getDateRegistered() != null ? c.getDateRegistered().format(DATE_FMT) : "");
        locationField.setText(safe(c.getLocation()));
        officerField.setText(safe(c.getAssignedOfficer()));
        descriptionArea.setText(safe(c.getDescription()));

        // Determine editability from status and session
        boolean closed = isClosedStatus(c.getStatus());
        boolean canEdit = sessionCanEdit() && !closed;
        setEditable(canEdit);

        // Populate lists (pass current editable state)
        populateEvidence(caseService.getEvidenceForCase(c.getId()));
        populateParticipants(caseService.getParticipantsForCase(c.getId()));

        // If case is closed and session would otherwise allow editing, inform user via console (no behavior change)
        if (closed) {
            System.out.println("Case is closed; editing disabled for this case.");
        }
    }

    public void setOnSaved(Consumer<Case> onSaved) {
        this.onSaved = onSaved;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    private void populateEvidence(List<Evidence> evidence) {
        System.out.println("populateEvidence called, evidence size: " + (evidence == null ? 0 : evidence.size()));
        if (evidenceContainer == null) {
            System.out.println("evidenceContainer is null!");
            return;
        }
        evidenceContainer.getChildren().clear();

        if (evidence == null || evidence.isEmpty()) {
            // Row showing "No evidence linked" plus an Edit button
            HBox row = new HBox(8);
            row.getStyleClass().add("list-item");

            Label none = new Label("No evidence linked");
            none.getStyleClass().add("placeholder-text");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button editBtn = new Button("Edit");
            // Force visible and clickable while debugging; remove inline style later if desired
            editBtn.setStyle("-fx-opacity:1; -fx-min-width:64; -fx-min-height:28;");
            editBtn.getStyleClass().add("primary-btn");
            editBtn.setDisable(!editable);

            editBtn.setOnAction(evt -> {
                System.out.println("Edit evidence (empty) clicked for case: " + (caseToEdit == null ? "null" : caseToEdit.getId()));
                showAlert(Alert.AlertType.INFORMATION, "Not implemented", "Can't open edit page for evidence yet.");
            });

            row.getChildren().addAll(none, spacer, editBtn);
            evidenceContainer.getChildren().add(row);
            System.out.println("Added empty-evidence row with Edit button");
            return;
        }

        for (Evidence ev : evidence) {
            HBox row = new HBox(8);
            row.getStyleClass().add("list-item");

            VBox left = new VBox(2);
            Label title = new Label(safe(ev.getId()) + " — " + safe(ev.getDescription()));
            title.getStyleClass().add("item-title");
            Label meta = new Label("Collected: " + safe(String.valueOf(ev.getCollectionDateTime())) + " • " + safe(ev.getLocation()));
            meta.getStyleClass().add("item-meta");
            left.getChildren().addAll(title, meta);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button editBtn = new Button("Edit");
            editBtn.setStyle("-fx-opacity:1; -fx-min-width:64; -fx-min-height:28;");
            editBtn.getStyleClass().add("primary-btn");
            editBtn.setDisable(!editable);

            editBtn.setOnAction(evt -> {
                System.out.println("Edit evidence clicked: " + safe(ev.getId()));
                showAlert(Alert.AlertType.INFORMATION, "Not implemented", "Can't open edit page for evidence yet.");
            });

            row.getChildren().addAll(left, spacer, editBtn);
            evidenceContainer.getChildren().add(row);
        }
        System.out.println("evidenceContainer children after populate: " + evidenceContainer.getChildren().size());
    }

    private void populateParticipants(List<Participant> participants) {
        System.out.println("populateParticipants called, participants size: " + (participants == null ? 0 : participants.size()));
        if (participantsContainer == null) {
            System.out.println("participantsContainer is null!");
            return;
        }
        participantsContainer.getChildren().clear();

        if (participants == null || participants.isEmpty()) {
            // Row showing "No participants linked" plus an Edit button
            HBox row = new HBox(8);
            row.getStyleClass().add("list-item");

            Label none = new Label("No participants linked");
            none.getStyleClass().add("placeholder-text");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button editBtn = new Button("Edit");
            editBtn.setStyle("-fx-opacity:1; -fx-min-width:64; -fx-min-height:28;");
            editBtn.getStyleClass().add("primary-btn");
            editBtn.setDisable(!editable);

            editBtn.setOnAction(evt -> {
                System.out.println("Edit participant (empty) clicked for case: " + (caseToEdit == null ? "null" : caseToEdit.getId()));
                showAlert(Alert.AlertType.INFORMATION, "Not implemented", "Can't open edit page for participant yet.");
            });

            row.getChildren().addAll(none, spacer, editBtn);
            participantsContainer.getChildren().add(row);
            System.out.println("Added empty-participant row with Edit button");
            return;
        }

        for (Participant p : participants) {
            HBox row = new HBox(8);
            row.getStyleClass().add("list-item");

            VBox left = new VBox(2);
            Label title = new Label(safe(p.getName()) + " — " + safe(p.getRole()));
            title.getStyleClass().add("item-title");
            Label meta = new Label("Contact: " + safe(p.getContact()));
            meta.getStyleClass().add("item-meta");
            left.getChildren().addAll(title, meta);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button editBtn = new Button("Edit");
            editBtn.setStyle("-fx-opacity:1; -fx-min-width:64; -fx-min-height:28;");
            editBtn.getStyleClass().add("primary-btn");
            editBtn.setDisable(!editable);

            editBtn.setOnAction(evt -> {
                System.out.println("Edit participant clicked: " + safe(p.getName()));
                showAlert(Alert.AlertType.INFORMATION, "Not implemented", "Can't open edit page for participant yet.");
            });

            row.getChildren().addAll(left, spacer, editBtn);
            participantsContainer.getChildren().add(row);
        }
        System.out.println("participantsContainer children after populate: " + participantsContainer.getChildren().size());
    }

    private void onSaveClicked() {
        System.out.println("onSaveClicked()");
        if (caseToEdit == null) return;

        // Session check: only allow save if session permits editing
        if (!sessionCanEdit()) {
            showAlert(Alert.AlertType.WARNING, "Permission denied", "You are not allowed to save changes. Please sign in as a Police Officer.");
            return;
        }

        if (isClosedStatus(caseToEdit.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Cannot edit", "The case can't be edited once closed.");
            return;
        }

        String newTitle = safe(titleField.getText());
        String newStatus = statusCombo != null && statusCombo.getValue() != null ? statusCombo.getValue() : safe(statusCombo == null ? null : statusCombo.getEditor().getText());
        String rawPriority = priorityCombo != null && priorityCombo.getValue() != null ? priorityCombo.getValue() : safe(priorityCombo == null ? null : priorityCombo.getEditor().getText());
        String newType = safe(typeField.getText());
        String newDateText = safe(dateField.getText());
        String newLocation = safe(locationField.getText());
        String newOfficer = safe(officerField.getText());
        String newDescription = safe(descriptionArea.getText());

        // Parse date
        LocalDate parsedDate = null;
        if (!newDateText.isBlank()) {
            try {
                parsedDate = LocalDate.parse(newDateText, DATE_FMT);
            } catch (DateTimeParseException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid date", "Please use yyyy-MM-dd format for Date Reported.");
                return;
            }
        }

        // Normalize and validate priority to match DB CHECK constraint
        String normalizedPriority = normalizePriority(rawPriority);
        if (normalizedPriority == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid priority", "Priority must be one of: high, medium, low.");
            return;
        }

        // Normalize status to canonical form (lowercase) but keep DB-friendly values
        String normalizedStatus = normalizeStatus(newStatus);
        if (normalizedStatus == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid status", "Status must be one of: Open, Closed, Archived.");
            return;
        }

        // Apply to model
        caseToEdit.setTitle(newTitle);
        caseToEdit.setStatus(normalizedStatus);
        caseToEdit.setPriority(normalizedPriority);
        caseToEdit.setType(newType);
        if (parsedDate != null) caseToEdit.setDateRegistered(parsedDate);
        caseToEdit.setLocation(newLocation);
        caseToEdit.setAssignedOfficer(newOfficer);
        caseToEdit.setDescription(newDescription);

        // Persist and handle errors
        try {
            caseService.updateCase(caseToEdit);
            System.out.println("caseService.updateCase succeeded for: " + caseToEdit.getId());
            showAlert(Alert.AlertType.INFORMATION, "Saved", "Changes saved successfully.");

            // Notify parent if present on the JavaFX thread to avoid detached-node NPEs
            if (onSaved != null) {
                Platform.runLater(() -> {
                    try {
                        onSaved.accept(caseToEdit);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save failed", "Failed to save changes. See console for details.");
        }
    }

    private void onCancelClicked() {
        System.out.println("onCancelClicked()");
        // If parent provided a cancel callback, invoke it so the parent can restore the manage screen
        if (onCancel != null) {
            try {
                Platform.runLater(onCancel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        // Fallback: reload original values or clear
        if (caseToEdit != null) {
            setCase(caseToEdit); // reload original values
        } else {
            clearForm();
        }
    }

    private void setEditable(boolean editable) {
        this.editable = editable;

        if (titleField != null) titleField.setEditable(editable);
        if (statusCombo != null) statusCombo.setDisable(!editable);
        if (priorityCombo != null) priorityCombo.setDisable(!editable);
        if (typeField != null) typeField.setEditable(editable);
        if (dateField != null) dateField.setEditable(editable);
        if (locationField != null) locationField.setEditable(editable);
        if (officerField != null) officerField.setEditable(editable);
        if (descriptionArea != null) descriptionArea.setEditable(editable);

        if (saveBtn != null) saveBtn.setDisable(!editable);
        if (cancelBtn != null) cancelBtn.setDisable(!editable);

        // If lists already populated, update their edit buttons' disabled state
        if (evidenceContainer != null) {
            evidenceContainer.getChildren().forEach(node -> {
                if (node instanceof HBox) {
                    HBox row = (HBox) node;
                    row.getChildren().forEach(child -> {
                        if (child instanceof Button) ((Button) child).setDisable(!editable);
                    });
                }
            });
        }
        if (participantsContainer != null) {
            participantsContainer.getChildren().forEach(node -> {
                if (node instanceof HBox) {
                    HBox row = (HBox) node;
                    row.getChildren().forEach(child -> {
                        if (child instanceof Button) ((Button) child).setDisable(!editable);
                    });
                }
            });
        }
    }

    private boolean isClosedStatus(String status) {
        if (status == null) return false;
        return status.trim().equalsIgnoreCase("closed");
    }

    private void clearForm() {
        if (caseIdField != null) caseIdField.clear();
        if (titleField != null) titleField.clear();
        if (statusCombo != null) statusCombo.getSelectionModel().clearSelection();
        if (priorityCombo != null) priorityCombo.getSelectionModel().clearSelection();
        if (typeField != null) typeField.clear();
        if (dateField != null) dateField.clear();
        if (locationField != null) locationField.clear();
        if (officerField != null) officerField.clear();
        if (descriptionArea != null) descriptionArea.clear();
        if (evidenceContainer != null) evidenceContainer.getChildren().clear();
        if (participantsContainer != null) participantsContainer.getChildren().clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert a = new Alert(type);
            a.setTitle(title);
            a.setHeaderText(null);
            a.setContentText(content);
            a.showAndWait();
        });
    }

    /**
     * Normalize user input for priority to one of the allowed DB values.
     * Returns "high", "medium", "low" or null if invalid.
     */
    private String normalizePriority(String raw) {
        if (raw == null) return "medium";
        String v = raw.trim().toLowerCase();
        if (v.isEmpty()) return "medium";
        if (v.startsWith("h")) return "high";
        if (v.startsWith("m")) return "medium";
        if (v.startsWith("l")) return "low";
        if (v.equals("high") || v.equals("medium") || v.equals("low")) return v;
        return null;
    }

    /**
     * Normalize status to canonical DB-friendly values (lowercase).
     * Accepts "open", "closed", "archived" (case-insensitive).
     */
    private String normalizeStatus(String raw) {
        if (raw == null) return "open";
        String v = raw.trim().toLowerCase();
        if (v.startsWith("o")) return "open";
        if (v.startsWith("c")) return "closed";
        if (v.startsWith("a")) return "archived";
        if (v.equals("open") || v.equals("closed") || v.equals("archived")) return v;
        return null;
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return "";
        String v = s.trim().toLowerCase();
        return Character.toUpperCase(v.charAt(0)) + v.substring(1);
    }

    /**
     * Session helper: only Police Officer role may edit cases in this app.
     */
    private boolean sessionCanEdit() {
        UserSession s = UserSession.getInstance();
        return s != null && s.isLoggedIn() && s.isPoliceOfficer();
    }

    private String safe(String s) { return s == null ? "" : s; }
}
