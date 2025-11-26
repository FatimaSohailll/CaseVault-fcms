package com.fcms.controllers.courtOfficial;

import com.fcms.models.Case;
import com.fcms.models.UserSession;
import com.fcms.services.CaseService;
import com.fcms.services.RecordVerdictService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;

public class SearchCasesController {

    @FXML private VBox resultsContainer;
    @FXML private Label resultsCount;

    // This is the container inside CourtOfficial main layout (same as SystemAdmin contentArea)
    @FXML private AnchorPane rootContainer;

    private final CaseService caseService = new CaseService();
    private final RecordVerdictService verdictService = new RecordVerdictService();


    @FXML
    public void initialize() {
        loadCases();
    }

    private void loadCases() {
        String officialId = UserSession.getInstance().getUserID();
        List<Case> cases = caseService.getSubmittedCasesForOfficial(officialId);

        resultsContainer.getChildren().clear();
        resultsCount.setText(cases.size() + " cases found");

        for (Case c : cases) {
            resultsContainer.getChildren().add(createCaseCard(c));
        }
    }

    private VBox createCaseCard(Case c) {

        VBox card = new VBox(8);
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

        // STATUS BADGE — override to "closed" if verdict exists
        Label status;
        boolean hasVerdict = verdictService.hasVerdict(c.getId());
        if (hasVerdict) {
            status = new Label("closed");
            status.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 5;");
        } else {
            status = new Label(c.getStatus());  // likely "open"
            status.setStyle("-fx-background-color: #ff9800; -fx-padding: 2 8; -fx-background-radius: 5;");
        }

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button recordBtn = new Button("Record Verdict");
        recordBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-padding: 6 14;");

        // ------------------------------------------------------
        // ⭐️ CHECK IF THIS CASE ALREADY HAS A RECORDED VERDICT
        // ------------------------------------------------------
        if (hasVerdict) {
            recordBtn.setVisible(false);
            recordBtn.setManaged(false); // removes empty space
        } else {
            recordBtn.setOnAction(e -> openRecordVerdictPage(c));
        }

        header.getChildren().addAll(caseId, type, status, spacer, recordBtn);

        // Info section
        Label officer = new Label("Officer: " + c.getAssignedOfficer());
        Label location = new Label("Location: " + c.getLocation());
        Label filed = new Label("Filed: " + c.getDateRegistered());
        filed.setStyle("-fx-text-fill: #666;");

        card.getChildren().addAll(header, officer, location, filed);
        return card;
    }

    // =====================================================================
    // OPEN THE RECORD VERDICT PAGE
    // =====================================================================
    private void openRecordVerdictPage(Case selectedCase) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/courtOfficial/RecordVerdict.fxml")
            );

            Node page = loader.load();

            // Get its controller
            RecordVerdictController controller = loader.getController();
            controller.setCaseData(selectedCase);

            // Replace content area
            AnchorPane parent = (AnchorPane) resultsContainer.getScene().lookup("#contentArea");
            parent.getChildren().setAll(page);

            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
