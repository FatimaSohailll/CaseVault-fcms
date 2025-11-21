package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.services.CaseService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class PoliceDashboardController {

    @FXML private VBox recentCasesContainer;

    private final CaseService caseService = new CaseService();

    @FXML
    public void initialize() {
        loadRecentCases();
    }

    private void loadRecentCases() {
        List<Case> cases = caseService.getAllCases();
        cases.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(5)
                .forEach(this::createCaseCard);
    }

    private void createCaseCard(Case c) {
        VBox card = new VBox(6);
        card.getStyleClass().add("case-card");

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label id = new Label(c.getId());
        id.getStyleClass().add("case-id");

        Label status = new Label(c.getStatus());
        status.getStyleClass().add("case-status");

        switch (c.getStatus()) {
            case "Open" -> status.getStyleClass().add("status-open");
            case "Closed" -> status.getStyleClass().add("status-closed");
            default -> status.getStyleClass().add("status-pending");
        }

        topRow.getChildren().addAll(id, status);

        Label title = new Label(c.getTitle());
        title.getStyleClass().add("case-title");

        Label category = new Label("Category: " + c.getType());
        Label officer = new Label("Officer: " + c.getOfficer());
        Label date = new Label("Date: " + c.getDate().toString());

        category.getStyleClass().add("case-meta");
        officer.getStyleClass().add("case-meta");
        date.getStyleClass().add("case-meta");

        card.getChildren().addAll(topRow, title, category, officer, date);
        card.setOnMouseClicked(e -> openCaseDetails(c.getId()));

        recentCasesContainer.getChildren().add(card);
    }

    private void openCaseDetails(String caseId) {
        System.out.println("Opening details for case: " + caseId);
    }
}
