package com.fcms.controllers.policeOfficer;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ManageCasesController {

    @FXML private VBox caseListContainer;
    @FXML private VBox caseDetailsRoot, evidenceContainer, participantsContainer;
    @FXML private Label caseTitle, statusLabel, priorityLabel, typeLabel, dateLabel, locationLabel, officerLabel, descriptionLabel;

    @FXML
    public void initialize() {
        showPlaceholder();

        addCaseTile("CASE-2024-001", "Armed Robbery - Downtown Bank", "Under Investigation", "High", "2024-03-15",
                "Robbery", "123 Main Street, Downtown", "Officer J. Smith",
                "Armed robbery at First National Bank on Main Street. Two suspects with firearms took approximately $45,000. Security footage available.",
                new String[][] {
                        {"EV-001", "Video footage", "2024-03-15", "Bank main entrance"},
                        {"EV-002", "Fingerprints", "2024-03-15", "Teller counter"}
                },
                new String[][] {
                        {"John Anderson", "Witness", "555-9101"},
                        {"Sarah Miller", "Victim", "555-9102"}
                });

        addCaseTile("CASE-2024-015", "Residential Burglary - Oak Street", "Evidence Analysis", "Medium", "2024-03-18",
                "Burglary", "17 Oak Street", "Officer R. Khan",
                "Break-in reported at 17 Oak Street. Jewellery and electronics missing. Prints and shoe impressions collected.",
                new String[][] {
                        {"EV-021", "Door pry marks", "2024-03-18", "Front door"},
                        {"EV-022", "Shoe impression cast", "2024-03-18", "Garden soil"}
                },
                new String[][] {
                        {"Maria Lopez", "Resident", "555-9123"},
                        {"Tom Reed", "Neighbor", "555-9134"}
                });

        addCaseTile("CASE-2024-023", "Vehicle Theft - Shopping Mall", "Under Investigation", "Medium", "2024-03-18",
                "Theft", "Mall Parking Lot C", "Officer S. Hussain",
                "SUV stolen from mall parking lot. CCTV review pending. GPS tracker last ping at ring road.",
                new String[][] {
                        {"EV-030", "CCTV request submitted", "2024-03-18", "Mall security office"}
                },
                new String[][] {
                        {"Rashid Ali", "Owner", "555-9007"}
                });
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
                             String[][] evidence, String[][] participants) {

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
            for (String[] ev : evidence) {
                VBox item = new VBox(2);
                item.getStyleClass().add("list-item");
                item.getChildren().addAll(
                        new Label(ev[0] + " — " + ev[1]),
                        new Label("Collected: " + ev[2]),
                        new Label("Location: " + ev[3])
                );
                evidenceContainer.getChildren().add(item);
            }

            participantsContainer.getChildren().clear();
            for (String[] p : participants) {
                VBox item = new VBox(2);
                item.getStyleClass().add("list-item");
                item.getChildren().addAll(
                        new Label(p[0] + " — " + p[1]),
                        new Label("Contact: " + p[2])
                );
                participantsContainer.getChildren().add(item);
            }
        });

        caseListContainer.getChildren().add(tile);
    }
}
