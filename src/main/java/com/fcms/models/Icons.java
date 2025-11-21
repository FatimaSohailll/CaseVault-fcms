package com.fcms.models;

import javafx.scene.Group;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

public class Icons extends Group {
    public Icons(String iconName) {
        getStyleClass().add("lucide-icon"); // For CSS styling
        setPathsForIcon(iconName);
    }

    private void setPathsForIcon(String iconName) {
        switch (iconName.toLowerCase()) {
            case "home" -> addPaths("m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z", "9 22 9 12 15 12 15 22");
            case "filetext" -> {
                addPaths("M6 22a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h8a2.4 2.4 0 0 1 1.704.706l3.588 3.588A2.4 2.4 0 0 1 20 8v12a2 2 0 0 1-2 2z");
                addPaths("M14 2v5a1 1 0 0 0 1 1h5");
                addPaths("M10 9H8", "M16 13H8", "M16 17H8");
            }
            case "search" -> {
                addPaths("m21 21-4.34-4.34");
                addCircle(11, 11, 8);
            }
            case "barchart3" -> {
                addLine(12, 20, 12, 10);
                addLine(18, 20, 18, 4);
                addLine(6, 20, 6, 16);
            }
            case "folderopen" -> {
                addPaths("m6 14 1.5-2.9A2 2 0 0 1 9.24 10H20a2 2 0 0 1 1.94 2.5l-1.54 6a2 2 0 0 1-1.95 1.5H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h3.9a2 2 0 0 1 1.69.9l.81 1.2a2 2 0 0 0 1.67.9H18a2 2 0 0 1 2 2v2");
                addPaths("M12 13v6");
            }
            case "users" -> {
                addPaths("M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2");
                addCircle(9, 7, 4);
                addPaths("M22 21v-2a4 4 0 0 0-3-3.87");
                addPaths("M16 3.13a4 4 0 0 1 0 7.75");
            }
            case "microscope" -> {
                addPaths("M6 18h8", "M3 22h18");
                addArc(13, 15, 7, 270, 190);
                addPaths("M9 14h2", "M9 12a2 2 0 0 1-2-2V6h6v4a2 2 0 0 1-2 2Z", "M12 6V3a1 1 0 0 0-1-1H9a1 1 0 0 0-1 1v3");
            }
            case "foldercheck" -> {
                addPaths("M20 20a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.9a2 2 0 0 1-1.69-.9L9.6 3.9A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13a2 2 0 0 0 2 2Z");
                addPaths("M9 13 13 17 21 9");
            }
            case "scale" -> {
                addPaths("m16 16 3-8 3 8c-.87.65-1.92 1-3 1s-2.13-.35-3-1Z");
                addPaths("m2 16 3-8 3 8c-.87.65-1.92 1-3 1s-2.13-.35-3-1Z");
                addPaths("M7 21h10", "M12 3v18");
            }
            case "user" -> {
                addPaths("M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2");
                addCircle(12, 7, 4);
            }
            default -> throw new IllegalArgumentException("Unknown icon: " + iconName);
        }
    }

    private void addPaths(String... pathData) {
        for (String data : pathData) {
            SVGPath path = new SVGPath();
            path.setContent(data);
            path.setStrokeLineCap(StrokeLineCap.ROUND);
            path.setStrokeLineJoin(StrokeLineJoin.ROUND);
            path.getStyleClass().add("icon-path");
            getChildren().add(path);
        }
    }

    private void addLine(double x1, double y1, double x2, double y2) {
        // Use Line node for straight lines (SVGPath can approximate, but Line is precise)
        Line line = new Line(x1, y1, x2, y2);
        line.getStyleClass().add("icon-line");
        getChildren().add(line);
    }

    private void addCircle(double cx, double cy, double r) {
        // Use Circle for non-path shapes
        Circle circle = new Circle(cx, cy, r);
        circle.getStyleClass().add("icon-circle");
        getChildren().add(circle);
    }

    private void addArc(double cx, double cy, double r, double startAngle, double length) {
        // Use Arc for partial circle shapes
        Arc arc = new Arc(cx, cy, r, r, startAngle, length);
        arc.setType(ArcType.OPEN); // just the curved line, no fill
        arc.getStyleClass().add("icon-circle");
        getChildren().add(arc);
    }

    // Helper to scale the icon
    public void setSize(double size) {
        setScaleX(size / 24.0); // default is 24x24
        setScaleY(size / 24.0);
    }
}