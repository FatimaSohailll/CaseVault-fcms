package com.fcms.controllers.components;

import com.fcms.app.SceneManager;
import com.fcms.models.Icons;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public class SidebarController {

    @FXML private VBox sidebarContainer;

    @FXML private Button dashboardBtn, registerBtn, manageCasesBtn, forensicAnalysisBtn,
            manageParticipantsBtn, crimeAnalyticsBtn, closeCaseBtn, submitToCourtBtn, searchCasesBtn;

    @FXML private HBox dashboardRow, registerRow, manageCasesRow, forensicAnalysisRow,
            manageParticipantsRow, crimeAnalyticsRow, closeCaseRow, submitToCourtRow, searchCasesRow;

    @FXML private Pane dashboardIcon, registerIcon, manageCasesIcon, forensicAnalysisIcon,
            manageParticipantsIcon, crimeAnalyticsIcon, closeCaseIcon, submitToCourtIcon, searchCasesIcon;

    private boolean collapsed = false;

    // Reference to SceneManager injected from Main
    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

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
        searchCasesIcon.getChildren().add(createIcon("search"));
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

            sidebarContainer.setPrefWidth(80);
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

    private void setActiveSidebar(String name) {
        List<HBox> allRows = List.of(
                dashboardRow, registerRow, manageCasesRow, forensicAnalysisRow,
                manageParticipantsRow, crimeAnalyticsRow, closeCaseRow, submitToCourtRow, searchCasesRow
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
            case "Search Cases" -> searchCasesRow.getStyleClass().add("active");
        }
    }

    // ---------------- Navigation Handlers ----------------
    @FXML
    public void handleDashboard() {
        setActiveSidebar("Dashboard");
        sceneManager.switchContent(
                "/fxml/policeOfficer/policeDashboard.fxml");
    }

    @FXML
    public void handleRegisterCase() {
        setActiveSidebar("Register New Case");
        sceneManager.switchContent(
                "/fxml/policeOfficer/registerCase.fxml");

    }

    @FXML
    public void handleManageCases() {
        setActiveSidebar("Manage Cases");
        sceneManager.switchContent("/fxml/policeOfficer/manageCases.fxml");
    }

    @FXML
    public void handleForensicAnalysis() {
        setActiveSidebar("Forensic Analysis");
        sceneManager.switchContent(
                "/fxml/policeOfficer/requestAnalysis.fxml");
    }

    @FXML
    public void handleManageParticipants() {
        setActiveSidebar("Manage Participants");
        sceneManager.switchContent(
                "/fxml/policeOfficer/manageCaseParticipants.fxml");
    }

    @FXML
    public void handleCrimeAnalytics() {
        setActiveSidebar("Crime Analytics");
        sceneManager.switchContent(
                "/fxml/policeOfficer/crimeAnalytics.fxml");
    }

    @FXML
    public void handleCloseCase() {
        setActiveSidebar("Close Case");
        sceneManager.switchContent(
                "/fxml/policeOfficer/closeCase.fxml");
    }
    @FXML
    public void handleSearchCases() {
        setActiveSidebar("Search Cases");
        sceneManager.switchContent(
                "/fxml/policeOfficer/searchCases.fxml");
    }
    @FXML
    public void handleSubmitToCourt() {
        setActiveSidebar("Submit to Court");
        sceneManager.switchContent(
                "/fxml/policeOfficer/submitToCourt.fxml");
    }
}
