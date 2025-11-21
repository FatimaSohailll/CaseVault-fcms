package com.fcms.controllers.policeOfficer;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;

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

    @FXML
    public void initialize() {
        locationCombo.getItems().addAll("All locations", "Downtown", "West District", "East District", "Suburbs");
        crimeTypeCombo.getItems().addAll("All types", "Robbery", "Fraud", "Cybercrime", "Assault");

        loadDummyData();
    }

    private void loadDummyData() {
        // Fill charts with dummy values
        XYChart.Series<String, Number> total = new XYChart.Series<>();
        total.setName("Total Cases");
        total.getData().add(new XYChart.Data<>("Jan", 30));
        total.getData().add(new XYChart.Data<>("Feb", 35));
        total.getData().add(new XYChart.Data<>("Mar", 28));
        total.getData().add(new XYChart.Data<>("Apr", 40));

        trendChart.getData().add(total);

        pieChart.getData().add(new PieChart.Data("Theft", 27));
        pieChart.getData().add(new PieChart.Data("Burglary", 24));
        pieChart.getData().add(new PieChart.Data("Robbery", 17));

        XYChart.Series<String, Number> locSeries = new XYChart.Series<>();
        locSeries.getData().add(new XYChart.Data<>("Downtown", 87));
        locSeries.getData().add(new XYChart.Data<>("West", 65));
        locationChart.getData().add(locSeries);
    }

    @FXML
    public void requestAnalysis() {
        System.out.println("Generating analytics...");
    }

    @FXML
    public void exportPDF() {
        System.out.println("Exporting PDF...");
    }

    @FXML
    public void exportCSV() {
        System.out.println("Exporting CSV...");
    }
}