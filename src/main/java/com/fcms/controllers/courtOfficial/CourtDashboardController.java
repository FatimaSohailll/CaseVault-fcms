package com.fcms.controllers.courtOfficial;

import com.fcms.models.Case;
import com.fcms.models.CourtVerdict;
import com.fcms.services.CaseService;
import com.fcms.services.RecordVerdictService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CourtDashboardController {

    @FXML private Label totalCasesLabel;
    @FXML private Label awaitingVerdictLabel;
    @FXML private Label verdictsRecordedLabel;

    @FXML private VBox submittedCasesContainer;
    @FXML private VBox caseHistoryContainer;

    private final CaseService caseService = new CaseService();
    private final RecordVerdictService verdictService = new RecordVerdictService();

    private final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        refreshDashboard();
    }

    // =========================================================
    // MAIN REFRESH
    // =========================================================
    private void refreshDashboard() {
        List<Case> allCases = caseService.getAllCases();

        // ---- stats ----
        int total = allCases.size();
        int verdicts = 0;
        for (Case c : allCases) {
            if (verdictService.hasVerdict(c.getId())) {
                verdicts++;
            }
        }
        int awaiting = total - verdicts;

        totalCasesLabel.setText(String.valueOf(total));
        awaitingVerdictLabel.setText(String.valueOf(awaiting));
        verdictsRecordedLabel.setText(String.valueOf(verdicts));

        // ---- sections ----
        buildSubmittedCasesSection(allCases);
        buildCaseHistorySection(allCases);
    }

    // =========================================================
    // SUBMITTED CASES (awaiting verdict)
    // =========================================================
    private void buildSubmittedCasesSection(List<Case> allCases) {
        submittedCasesContainer.getChildren().clear();

        int added = 0;
        for (Case c : allCases) {
            if (!verdictService.hasVerdict(c.getId())) {
                submittedCasesContainer.getChildren().add(createSubmittedCaseCard(c));
                added++;
                if (added >= 3) break;   // show only top 3
            }
        }

        if (added == 0) {
            Label empty = new Label("No cases awaiting verdict.");
            empty.setStyle("-fx-text-fill: #777;");
            submittedCasesContainer.getChildren().add(empty);
        }
    }

    private VBox createSubmittedCaseCard(Case c) {
        VBox card = new VBox(5);
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 10;
            -fx-padding: 15;
        """);

        HBox header = new HBox(10);

        Label caseId = new Label(c.getId());
        caseId.setStyle("-fx-font-weight: bold;");

        Label type = new Label(c.getType());
        type.setStyle("-fx-background-color: #ffc107; -fx-padding: 2 8; -fx-background-radius: 5;");

        Label badge = new Label("Awaiting Verdict");
        badge.setStyle("-fx-background-color: #F6F6F6; -fx-padding: 2 8; -fx-background-radius: 5;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button recordBtn = new Button("Record Verdict");
        recordBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-padding: 6 14; -fx-background-radius: 5;");
        recordBtn.setOnAction(e -> openRecordVerdictPage(c));

        header.getChildren().addAll(caseId, type, badge, spacer, recordBtn);

        Label officer = new Label("Officer: " + c.getAssignedOfficer());
        officer.setStyle("-fx-font-weight: bold;");

        Label desc = new Label("Location: " + c.getLocation());
        Label filed = new Label("Filed: " + (c.getDateRegistered() != null ? c.getDateRegistered().format(DATE_FMT) : ""));
        filed.setStyle("-fx-text-fill: #666;");

        card.getChildren().addAll(header, officer, desc, filed);
        return card;
    }

    // =========================================================
    // CASE HISTORY (cases with verdicts)
    // =========================================================
    private void buildCaseHistorySection(List<Case> allCases) {
        caseHistoryContainer.getChildren().clear();

        int added = 0;
        for (Case c : allCases) {
            CourtVerdict v = verdictService.getVerdictForCase(c.getId());
            if (v != null) {
                caseHistoryContainer.getChildren().add(createHistoryCard(c, v));
                added++;
                if (added >= 5) break;   // cap list size a bit
            }
        }

        if (added == 0) {
            Label empty = new Label("No verdicts recorded yet.");
            empty.setStyle("-fx-text-fill: #777;");
            caseHistoryContainer.getChildren().add(empty);
        }
    }

    private VBox createHistoryCard(Case c, CourtVerdict v) {
        VBox card = new VBox(5);
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 10;
            -fx-padding: 15;
        """);

        // top row: case id + type + badge
        HBox header = new HBox(10);

        Label caseId = new Label(c.getId());
        caseId.setStyle("-fx-font-weight: bold;");

        Label type = new Label(c.getType());
        type.setStyle("-fx-background-color: #ffc107; -fx-padding: 2 8; -fx-background-radius: 5;");

        Label badge = new Label("Verdict Recorded");
        badge.setStyle("-fx-background-color: #1a2c60; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 5;");

        header.getChildren().addAll(caseId, type, badge);

        // body: two columns
        HBox body = new HBox();
        body.setSpacing(10);

        VBox left = new VBox(3);
        Label officer = new Label("Officer: " + c.getAssignedOfficer());
        officer.setStyle("-fx-font-weight: bold;");
        Label loc = new Label("Location: " + c.getLocation());
        Label filed = new Label("Filed: " + (c.getDateRegistered() != null ?c.getDateRegistered().format(DATE_FMT) : ""));
        filed.setStyle("-fx-text-fill: #666;");
        left.getChildren().addAll(officer, loc, filed);

        VBox right = new VBox(3);
        Label verdictLbl = new Label("Verdict: " + v.getOutcome());
        verdictLbl.setStyle("-fx-text-fill: #d63031; -fx-font-weight: bold;");
        Label sentence = new Label("Sentence: " + v.getSentence());
        Label vDate = new Label("Verdict Date: " + (v.getDateIssued() != null ? v.getDateIssued().format(DATE_FMT) : ""));
        Label notes = new Label("Notes: " + (v.getNotes() != null ? v.getNotes() : ""));
        right.getChildren().addAll(verdictLbl, sentence, vDate, notes);

        body.getChildren().addAll(left, right);

        card.getChildren().addAll(header, body);
        return card;
    }

    // =========================================================
    // NAVIGATION
    // =========================================================
    @FXML
    private void openSearchCases() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/courtOfficial/searchCases.fxml")
            );
            Node page = loader.load();

            // use same pattern as SearchCasesController: look up #contentArea
            AnchorPane parent = (AnchorPane) totalCasesLabel.getScene().lookup("#contentArea");
            if (parent != null) {
                parent.getChildren().setAll(page);
                AnchorPane.setTopAnchor(page, 0.0);
                AnchorPane.setBottomAnchor(page, 0.0);
                AnchorPane.setLeftAnchor(page, 0.0);
                AnchorPane.setRightAnchor(page, 0.0);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void openRecordVerdictPage(Case selectedCase) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/courtOfficial/RecordVerdict.fxml")
            );
            Node page = loader.load();

            RecordVerdictController controller = loader.getController();
            controller.setCaseData(selectedCase);

            AnchorPane parent = (AnchorPane) totalCasesLabel.getScene().lookup("#contentArea");
            if (parent != null) {
                parent.getChildren().setAll(page);
                AnchorPane.setTopAnchor(page, 0.0);
                AnchorPane.setBottomAnchor(page, 0.0);
                AnchorPane.setLeftAnchor(page, 0.0);
                AnchorPane.setRightAnchor(page, 0.0);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
