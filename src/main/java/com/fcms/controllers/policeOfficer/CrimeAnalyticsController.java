package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.services.AnalyticsService;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.*;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrimeAnalyticsController {

    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;

    @FXML private ComboBox<String> locationCombo;
    @FXML private ComboBox<String> crimeTypeCombo;

    @FXML private Label insight1, insight2, insight3, insight4;
    @FXML private Label totalCases, avgMonth, closureRate, caseTypes;

    @FXML private LineChart<String, Number> trendChart;
    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> locationChart;

    private final AnalyticsService analyticsService = new AnalyticsService();

    @FXML
    public void initialize() {

        // Load dynamic filters
        locationCombo.getItems().addAll(analyticsService.getAllLocations());
        crimeTypeCombo.getItems().addAll(analyticsService.getAllCrimeTypes());

        locationCombo.getItems().add(0, "All");
        crimeTypeCombo.getItems().add(0, "All");

        locationCombo.getSelectionModel().selectFirst();
        crimeTypeCombo.getSelectionModel().selectFirst();

        loadAnalytics();
    }

    private void loadAnalytics() {
        List<Case> cases = analyticsService.getFilteredCases(
                dateFrom.getValue(),
                dateTo.getValue(),
                locationCombo.getValue(),
                crimeTypeCombo.getValue()
        );

        updateStats(cases);
        updateCharts(cases);
        updateInsights(cases);
    }

    @FXML
    public void requestAnalysis() { loadAnalytics(); }

    private void updateStats(List<Case> cases) {
        totalCases.setText(String.valueOf(cases.size()));
        caseTypes.setText(String.valueOf(analyticsService.countCaseTypes(cases)));
        avgMonth.setText(String.format("%.2f", analyticsService.calculateMonthlyAverage(cases)));
        closureRate.setText(String.format("%.1f%%", analyticsService.closureRate(cases)));
    }

    private void updateCharts(List<Case> cases) {

        trendChart.getData().clear();
        pieChart.getData().clear();
        locationChart.getData().clear();

        // Trend
        var series = new javafx.scene.chart.XYChart.Series<String, Number>();
        Map<String, Integer> trend = analyticsService.monthlyCounts(cases);
        trend.forEach((month, count) -> series.getData().add(new javafx.scene.chart.XYChart.Data<>(month, count)));
        trendChart.getData().add(series);

        // Pie
        analyticsService.typeDistribution(cases)
                .forEach((type, count) -> pieChart.getData().add(new PieChart.Data(type, count)));

        // Bar
        var locSeries = new javafx.scene.chart.XYChart.Series<String, Number>();
        analyticsService.locationDistribution(cases)
                .forEach((loc, count) -> locSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(loc, count)));

        locationChart.getData().add(locSeries);
    }

    private void updateInsights(List<Case> cases) {
        insight1.setText("• Highest crime type: " + analyticsService.getMostCommonType(cases));
        insight2.setText("• Most active location: " + analyticsService.getTopLocation(cases));
        insight3.setText("• Peak month: " + analyticsService.getPeakMonth(cases));
        insight4.setText("• Average monthly frequency: " +
                String.format("%.2f", analyticsService.calculateMonthlyAverage(cases)));
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.show();
    }

    private ImageView hiResChartSnapshot(Chart chart) {
        // wrap chart in a fake, off-screen scene so it fully renders
        Group g = new Group(chart);
        Scene s = new Scene(g);
        s.getRoot().applyCss();
        s.getRoot().layout();

        double w = chart.getBoundsInParent().getWidth();
        double h = chart.getBoundsInParent().getHeight();

        if (w <= 0 || h <= 0) {
            w = 800;
            h = 600;
        }

        WritableImage img = new WritableImage((int)(w * 2), (int)(h * 2));
        chart.snapshot(new SnapshotParameters(), img);

        ImageView view = new ImageView(img);
        view.setFitWidth(550);
        view.setPreserveRatio(true);
        return view;
    }

    private VBox buildReportNode(List<Case> cases) {

        VBox report = new VBox(20);
        report.setStyle("-fx-padding: 30; -fx-background-color: white;");

        // TITLE
        Label title = new Label("Crime Analytics Report");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        report.getChildren().add(title);

        Label filters = new Label(
                "Date Range: " + dateFrom.getValue() + " to " + dateTo.getValue() + "\n" +
                        "Location: " + locationCombo.getValue() + "\n" +
                        "Crime Type: " + crimeTypeCombo.getValue()
        );
        report.getChildren().add(filters);

        // --- TEXT STATS (NO SNAPSHOT!) ---
        VBox stats = new VBox(5);
        stats.getChildren().addAll(
                new Label("Total Cases: " + cases.size()),
                new Label("Closure Rate: " + closureRate.getText()),
                new Label("Avg Cases/Month: " + avgMonth.getText()),
                new Label("Case Types: " + caseTypes.getText())
        );
        report.getChildren().add(stats);

        // --- SNAPSHOT ONLY CHARTS ---
        report.getChildren().add(new Label("Charts"));
        report.getChildren().add(hiResChartSnapshot(trendChart));
        report.getChildren().add(hiResChartSnapshot(pieChart));
        report.getChildren().add(hiResChartSnapshot(locationChart));

        // --- CASE LIST TEXT (NO SNAPSHOT!) ---
        report.getChildren().add(new Label("Case Details"));
        for (Case c : cases) {
            VBox card = new VBox(3);
            card.setStyle("-fx-padding: 10; -fx-background-color: #f6f6f6; -fx-background-radius: 8;");
            card.getChildren().addAll(
                    new Label("ID: " + c.getId()),
                    new Label("Title: " + c.getTitle()),
                    new Label("Type: " + c.getType()),
                    new Label("Location: " + c.getLocation()),
                    new Label("Officer: " + c.getAssignedOfficer()),
                    new Label("Date: " + c.getDateRegistered()),
                    new Label("Status: " + c.getStatus())
            );
            report.getChildren().add(card);
        }

        return report;
    }

    private List<Node> splitIntoPages(VBox report, double maxHeight) {
        List<Node> pages = new ArrayList<>();
        VBox current = new VBox(20);
        double h = 0;

        for (Node child : new ArrayList<>(report.getChildren())) {

            child.applyCss();
            if (child instanceof Parent p) p.layout();

            double ch = child.prefHeight(-1);

            if (h + ch > maxHeight) {
                pages.add(current);
                current = new VBox(20);
                h = 0;
            }

            current.getChildren().add(child);
            h += ch;
        }

        if (!current.getChildren().isEmpty())
            pages.add(current);

        return pages;
    }

    @FXML
    public void exportCSV() {
        try {
            List<Case> cases = analyticsService.getFilteredCases(
                    dateFrom.getValue(), dateTo.getValue(),
                    locationCombo.getValue(), crimeTypeCombo.getValue()
            );

            if (cases.isEmpty()) {
                showAlert("No Data", "No cases found to export.");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export CSV");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
            File file = chooser.showSaveDialog(null);

            if (file == null) return;

            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("CaseID,Title,Type,Officer,Location,Date,Status");
                for (Case c : cases) {
                    writer.printf("%s,%s,%s,%s,%s,%s,%s%n",
                            c.getId(), c.getTitle(), c.getType(), c.getAssignedOfficer(),
                            c.getLocation(), c.getDateRegistered(), c.getStatus());
                }
            }

            showAlert("Success", "CSV exported successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to export CSV.");
        }
    }

    @FXML
    public void exportPDF() {

        List<Case> cases = analyticsService.getFilteredCases(
                dateFrom.getValue(), dateTo.getValue(),
                locationCombo.getValue(), crimeTypeCombo.getValue()
        );

        if (cases.isEmpty()) {
            showAlert("No Data", "No cases available to export.");
            return;
        }

        VBox report = buildReportNode(cases);

        report.applyCss();
        if (report instanceof Parent p) p.layout();

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null || !job.showPrintDialog(null)) return;

        PageLayout layout = job.getJobSettings().getPageLayout();
        double pageWidth = layout.getPrintableWidth();
        double pageHeight = layout.getPrintableHeight();

        double width = report.prefWidth(-1);
        double scale = pageWidth / width;

        List<Node> pages = splitIntoPages(report, pageHeight / scale);

        for (Node pg : pages) {
            Group wrapper = new Group(pg);
            wrapper.setScaleX(scale);
            wrapper.setScaleY(scale);

            if (!job.printPage(wrapper)) return;
        }

        job.endJob();
        showAlert("Success", "PDF exported successfully!");
    }

}
