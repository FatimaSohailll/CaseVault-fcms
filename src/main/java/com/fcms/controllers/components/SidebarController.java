package com.fcms.controllers.components;

import com.fcms.app.SceneManager;
import com.fcms.models.Icons;
import com.fcms.models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public class SidebarController {

    @FXML private VBox sidebarContainer;

    // POLICE BUTTONS
    @FXML private Button dashboardBtn, registerBtn, manageCasesBtn, forensicAnalysisBtn,
            manageParticipantsBtn, crimeAnalyticsBtn, closeCaseBtn, submitToCourtBtn, searchCasesBtn;

    // ADMIN BUTTONS
    @FXML private Button adminDashboardBtn, adminManageUsersBtn, adminWaitingListBtn;

    // EXPERT BUTTONS
    @FXML private Button expertUploadReportBtn;

    // COURT BUTTONS
    @FXML private Button courtRecordVerdictBtn;

    // ROWS (all users)
    @FXML private HBox dashboardRow, registerRow, manageCasesRow, forensicAnalysisRow,
            manageParticipantsRow, crimeAnalyticsRow, closeCaseRow, submitToCourtRow,
            searchCasesRow,

    // ADMIN
    adminDashboardRow, adminManageUsersRow, adminWaitingListRow,

    // EXPERT
    expertDashboardRow, expertUploadReportRow, expertAddEvidenceRow,

    // COURT
    courtDashboardRow, courtRecordVerdictRow, courtSearchCasesRow;

    // ICON HOLDERS
    @FXML private Pane dashboardIcon, registerIcon, manageCasesIcon, forensicAnalysisIcon,
            manageParticipantsIcon, crimeAnalyticsIcon, closeCaseIcon, submitToCourtIcon, searchCasesIcon,

    adminDashboardIcon, adminManageUsersIcon, adminWaitingListIcon,

    expertUploadReportIcon, courtRecordVerdictIcon;

    private boolean collapsed = false;
    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        injectIcons();
        applyRoleVisibility();
    }
    // -------------------- ICONS --------------------

    private void injectIcons() {
        // Police
        dashboardIcon.getChildren().add(icon("home"));
        registerIcon.getChildren().add(icon("filetext"));
        manageCasesIcon.getChildren().add(icon("folderopen"));
        forensicAnalysisIcon.getChildren().add(icon("microscope"));
        manageParticipantsIcon.getChildren().add(icon("users"));
        crimeAnalyticsIcon.getChildren().add(icon("barchart3"));
        closeCaseIcon.getChildren().add(icon("foldercheck"));
        submitToCourtIcon.getChildren().add(icon("scale"));
        searchCasesIcon.getChildren().add(icon("search"));

        // Admin
        adminDashboardIcon.getChildren().add(icon("home"));
        adminManageUsersIcon.getChildren().add(icon("users"));
        adminWaitingListIcon.getChildren().add(icon("filetext"));

        // Expert
        expertUploadReportIcon.getChildren().add(icon("filetext"));

        // Court
        courtRecordVerdictIcon.getChildren().add(icon("folderopen"));
    }

    private Icons icon(String name) {
        Icons i = new Icons(name);
        i.setSize(16);
        return i;
    }

    // -------------------- VISIBILITY --------------------

    private void applyRoleVisibility() {

        String role = UserSession.getInstance().getRole();
        System.out.println("Sidebar loaded for role: " + role);

        // Hide all rows first
        List<HBox> all = List.of(
                // Police
                dashboardRow, registerRow, manageCasesRow, forensicAnalysisRow,
                manageParticipantsRow, crimeAnalyticsRow, closeCaseRow, submitToCourtRow,
                searchCasesRow,

                // Admin
                adminDashboardRow, adminManageUsersRow, adminWaitingListRow,

                // Expert
                expertDashboardRow, expertUploadReportRow, expertAddEvidenceRow,

                // Court
                courtDashboardRow, courtRecordVerdictRow, courtSearchCasesRow
        );

        all.forEach(r -> {
            r.setVisible(false);
            r.setManaged(false);
        });

        // Show based on role
        switch (role) {

            case "Police Officer" -> show(
                    dashboardRow, registerRow, manageCasesRow, forensicAnalysisRow,
                    manageParticipantsRow, crimeAnalyticsRow, closeCaseRow,
                    submitToCourtRow, searchCasesRow
            );

            case "Forensic Expert" -> show(
                    expertDashboardRow, expertUploadReportRow, expertAddEvidenceRow
            );

            case "Court Official" -> show(
                    courtDashboardRow, courtRecordVerdictRow, courtSearchCasesRow
            );

            case "System Admin" -> show(
                    adminDashboardRow, adminManageUsersRow, adminWaitingListRow
            );
        }
    }

    private void show(HBox... rows) {
        for (HBox r : rows) {
            r.setManaged(true);
            r.setVisible(true);
        }
    }

    // -------------------- SIDEBAR COLLAPSE --------------------

    @FXML
    private void toggleSidebar() {
        collapsed = !collapsed;
        sidebarContainer.setPrefWidth(collapsed ? 70 : 200);
    }

    // -------------------- NAVIGATION --------------------

    // POLICE
    @FXML public void handlePoliceDashboard() { sceneManager.switchContent("/fxml/policeOfficer/policeDashboard.fxml"); }
    @FXML public void handleRegisterCase() { sceneManager.switchContent("/fxml/policeOfficer/registerCase.fxml"); }
    @FXML public void handleManageCases() { sceneManager.switchContent("/fxml/policeOfficer/manageCases.fxml"); }
    @FXML public void handleForensicAnalysis() { sceneManager.switchContent("/fxml/policeOfficer/requestAnalysis.fxml"); }
    @FXML public void handleManageParticipants() { sceneManager.switchContent("/fxml/policeOfficer/manageCaseParticipants.fxml"); }
    @FXML public void handleCrimeAnalytics() { sceneManager.switchContent("/fxml/policeOfficer/crimeAnalytics.fxml"); }
    @FXML public void handleCloseCase() { sceneManager.switchContent("/fxml/policeOfficer/closeCase.fxml"); }
    @FXML public void handlePoliceSearchCases() { sceneManager.switchContent("/fxml/policeOfficer/searchCases.fxml"); }
    @FXML public void handleSubmitToCourt() { sceneManager.switchContent("/fxml/policeOfficer/submitToCourt.fxml"); }

    // FORENSIC EXPERT
    @FXML public void handleExpertDashboard() { sceneManager.switchContent("/fxml/forensicExpert/expertDashboard.fxml"); }
    @FXML public void handleExpertUploadReport() { sceneManager.switchContent("/fxml/forensicExpert/uploadReport.fxml"); }
    @FXML public void handleAddEvidence() { sceneManager.switchContent("/fxml/forensicExpert/addEvidence.fxml"); }

    // COURT OFFICIAL
    @FXML public void handleCourtDashboard() { sceneManager.switchContent("/fxml/courtOfficial/courtDashboard.fxml"); }
    @FXML public void handleCourtRecordVerdict() { sceneManager.switchContent("/fxml/courtOfficial/RecordVerdict.fxml"); }
    @FXML public void handleCourtSearchCases() { sceneManager.switchContent("/fxml/courtOfficial/SearchCases.fxml"); }

    // ADMIN
    @FXML public void handleAdminDashboard() { sceneManager.switchContent("/fxml/systemAdmin/adminDashboard.fxml"); }
    @FXML public void handleAdminManageUsers() { sceneManager.switchContent("/fxml/systemAdmin/manageUsers.fxml"); }
    @FXML public void handleAdminWaitingList() { sceneManager.switchContent("/fxml/systemAdmin/waitingList.fxml"); }
}
