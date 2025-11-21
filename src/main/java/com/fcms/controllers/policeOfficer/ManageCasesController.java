package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.models.Evidence;
import com.fcms.models.Participant;
import com.fcms.services.CaseService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ManageCasesController {

    @FXML private VBox caseListContainer;
    @FXML private VBox caseDetailsRoot, evidenceContainer, participantsContainer;
    @FXML private Label caseTitle, statusLabel, priorityLabel, typeLabel, dateLabel, locationLabel, officerLabel, descriptionLabel;

    private final CaseService manageCasesService = new CaseService();

    @FXML
    public void initialize() {
        showPlaceholder();

        for (Case c : manageCasesService.getAllCases()) {
            List<Evidence> evidence = manageCasesService.getEvidenceForCase(c.getId());
            List<Participant> participants = manageCasesService.getParticipantsForCase(c.getId());

            addCaseTile(
                    c.getId(), c.getTitle(), c.getStatus(), "High", c.getDate().toString(),
                    c.getType(), c.getLocation(), c.getOfficer(),
                    "Description not stored in model",
                    evidence, participants
            );
        }
    }

    private void showPlaceholder() {
        caseTitle.setText("Select a case from the list to view details");
        statusLabel.setText("");
        priorityLabel.setText("");
        typeLabel.setText("");
        dateLabel.setText("");
        locationLabel.setText("");
        officerLabel.setText("");
        descriptionLabel.setText("");

        evidenceContainer.getChildren().clear();
        participantsContainer.getChildren().clear();

        Label placeholder = new Label("No case selected");
        placeholder.getStyleClass().add("placeholder-text");
        evidenceContainer.getChildren().add(placeholder);
        participantsContainer.getChildren().add(new Label(""));
    }

    private void addCaseTile(String id, String title, String status, String priority, String date,
                             String type, String location, String officer, String description,
                             List<Evidence> evidence, List<Participant> participants) {

        VBox tile = new VBox(6);
        tile.getStyleClass().add("case-tile");
        tile.setMaxWidth(Double.MAX_VALUE);

        Label idLabel = new Label(id);
        idLabel.getStyleClass().add("case-id");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("case-title");

        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().add("status-pill");
        if (status.toLowerCase().contains("investigation")) statusLabel.getStyleClass().add("status-investigation");
        else if (status.toLowerCase().contains("analysis")) statusLabel.getStyleClass().add("status-analysis");

        Label priorityLabel = new Label(priority);
        priorityLabel.getStyleClass().add("priority-pill");
        if (priority.equalsIgnoreCase("High")) priorityLabel.getStyleClass().add("priority-high");
        else if (priority.equalsIgnoreCase("Medium")) priorityLabel.getStyleClass().add("priority-medium");

        Label dateLabel = new Label(date);
        dateLabel.getStyleClass().add("case-date");

        HBox meta = new HBox(8, statusLabel, priorityLabel, dateLabel);
        meta.getStyleClass().add("case-meta");

        tile.getChildren().addAll(idLabel, titleLabel, meta);

        tile.setOnMouseEntered(e -> tile.getStyleClass().add("hover"));
        tile.setOnMouseExited(e -> tile.getStyleClass().remove("hover"));

        tile.setOnMouseClicked(e -> {
            for (Node n : caseListContainer.getChildren()) {
                n.getStyleClass().remove("active");
            }
            tile.getStyleClass().add("active");

            caseTitle.setText(id + " — " + title);
            this.statusLabel.setText(status);
            this.priorityLabel.setText(priority);
            this.typeLabel.setText(type);
            this.dateLabel.setText(date);
            this.locationLabel.setText(location);
            this.officerLabel.setText(officer);
            this.descriptionLabel.setText(description);

            evidenceContainer.getChildren().clear();
            for (Evidence ev : evidence) {
                VBox item = new VBox(2);
                item.getStyleClass().add("list-item");
                item.getChildren().addAll(
                        new Label(ev.getId() + " — " + ev.getDescription()),
                        new Label("Collected: " + ev.getCollectionDateTime()),
                        new Label("Location: " + ev.getLocation())
                );
                evidenceContainer.getChildren().add(item);
            }

            participantsContainer.getChildren().clear();
            for (Participant p : participants) {
                VBox item = new VBox(2);
                item.getStyleClass().add("list-item");
                item.getChildren().addAll(
                        new Label(p.getName() + " — " + p.getRole()),
                        new Label("Contact: " + p.getContact())
                );
                participantsContainer.getChildren().add(item);
            }
        });

        caseListContainer.getChildren().add(tile);
    }
}
