package com.fcms.controllers.components;

import com.fcms.models.Icons;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import com.fcms.controllers.policeOfficer.PoliceDashboardController;
import com.fcms.controllers.policeOfficer.RequestAnalysisController;

import java.util.List;

public class SidebarController {

    @FXML private VBox sidebarContainer;

    @FXML private Button dashboardBtn, registerBtn, manageCasesBtn, forensicAnalysisBtn,
            manageParticipantsBtn, crimeAnalyticsBtn, closeCaseBtn, submitToCourtBtn;

    @FXML private HBox dashboardRow, registerRow, manageCasesRow, forensicAnalysisRow,
            manageParticipantsRow, crimeAnalyticsRow, closeCaseRow, submitToCourtRow;

    @FXML private Pane dashboardIcon, registerIcon, manageCasesIcon, forensicAnalysisIcon,
            manageParticipantsIcon, crimeAnalyticsIcon, closeCaseIcon, submitToCourtIcon;

    private boolean collapsed = false;

    @FXML
    public void initialize() {
        injectIcons();
    }

    private void injectIcons() {
        dashboardIcon.getChildren().add(createIcon("home"));
        registerIcon.getChildren().add(createIcon("filetext"));
        manageCasesIcon.getChildren().add(createIcon("folderopen"));
        forensicAnalysisIcon.getChildren().add(createIcon("microscope"));
        manageParticipantsIcon.getChildren().add(createIcon("users"));
        crimeAnalyticsIcon.getChildren().add(createIcon("barchart3"));
        closeCaseIcon.getChildren().add(createIcon("foldercheck"));
        submitToCourtIcon.getChildren().add(createIcon("scale"));
    }

    private Icons createIcon(String name) {
        Icons icon = new Icons(name);
        icon.setSize(16);
        return icon;
    }

    @FXML
    private void toggleSidebar() {
        collapsed = !collapsed;

        if (collapsed) {
            dashboardBtn.setText("");
            registerBtn.setText("");
            manageCasesBtn.setText("");
            forensicAnalysisBtn.setText("");
            manageParticipantsBtn.setText("");
            crimeAnalyticsBtn.setText("");
            closeCaseBtn.setText("");
            submitToCourtBtn.setText("");

            sidebarContainer.setPrefWidth(60);
        } else {
            dashboardBtn.setText("Dashboard");
            registerBtn.setText("Register New Case");
            manageCasesBtn.setText("Manage Cases");
            forensicAnalysisBtn.setText("Forensic Analysis");
            manageParticipantsBtn.setText("Manage Participants");
            crimeAnalyticsBtn.setText("Crime Analytics");
            closeCaseBtn.setText("Close Case");
            submitToCourtBtn.setText("Submit to Court");

            sidebarContainer.setPrefWidth(200);
        }
    }

    @FXML
    private void handleRequestAnalysis() {
        // Get reference to the main controller
        PoliceDashboardController mainController = (PoliceDashboardController)
                forensicAnalysisBtn.getScene().lookup("#mainBorderPane").getParent().getUserData();
        mainController.loadRequestAnalysis();
    }

    private void setActiveSidebar(String name) {
        List<HBox> allRows = List.of(
                dashboardRow, registerRow, manageCasesRow, forensicAnalysisRow,
                manageParticipantsRow, crimeAnalyticsRow, closeCaseRow, submitToCourtRow
        );
        for (HBox row : allRows) row.getStyleClass().remove("active");

        switch (name) {
            case "Dashboard" -> dashboardRow.getStyleClass().add("active");
            case "Register New Case" -> registerRow.getStyleClass().add("active");
            case "Manage Cases" -> manageCasesRow.getStyleClass().add("active");
            case "Forensic Analysis" -> forensicAnalysisRow.getStyleClass().add("active");
            case "Manage Participants" -> manageParticipantsRow.getStyleClass().add("active");
            case "Crime Analytics" -> crimeAnalyticsRow.getStyleClass().add("active");
            case "Close Case" -> closeCaseRow.getStyleClass().add("active");
            case "Submit to Court" -> submitToCourtRow.getStyleClass().add("active");
        }
    }

    public void handleDashboard() { setActiveSidebar("Dashboard"); }
    public void handleRegisterCase() { setActiveSidebar("Register New Case"); }
    public void handleManageCases() { setActiveSidebar("Manage Cases"); }
    public void handleForensicAnalysis() { setActiveSidebar("Forensic Analysis"); }
    public void handleManageParticipants() { setActiveSidebar("Manage Participants"); }
    public void handleCrimeAnalytics() { setActiveSidebar("Crime Analytics"); }
    public void handleCloseCase() { setActiveSidebar("Close Case"); }
    public void handleSubmitToCourt() { setActiveSidebar("Submit to Court"); }
}
