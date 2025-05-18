package org.example.flowmod;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.flowmod.engine.PipeSpecs;
import org.example.flowmod.engine.PerforatedCoreOptimizer;
import org.example.flowmod.engine.PhysicsUtil;
import org.example.flowmod.utils.UnitConv;
import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;

import java.util.ArrayList;
import java.util.List;

/** Simplified JavaFX UI for interactive optimisation. */
public class FlowModifierUI extends Application {
    private final List<TextField> fields = new ArrayList<>();
    private final List<Label> errors = new ArrayList<>();

    private ComboBox<Double> holeStepBox;

    private TableView<HoleSpec> table;
    private Label summaryLabel;
    private PipeSpecs lastPipe;
    private HoleLayout lastLayout;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        String[] keys = {"innerDiameterMm", "flowRateLpm", "drillMinMm"};
        for (int i = 0; i < keys.length; i++) {
            String text = keys[i];
            if ("flowRateLpm".equals(text)) text = "Flow rate (GPM)";
            Label l = new Label(text);
            TextField tf = new TextField();
            tf.setId(keys[i]);
            Label err = new Label();
            err.setTextFill(Color.RED);
            grid.addRow(i * 2, l, tf);
            grid.add(err, 1, i * 2 + 1);
            fields.add(tf);
            errors.add(err);
        }
        Label stepLabel = new Label("Hole step (mm)");
        holeStepBox = new ComboBox<>();
        holeStepBox.getItems().addAll(0.5, 1.0, 2.0, 5.0);
        holeStepBox.setValue(0.5);
        grid.addRow(keys.length * 2, stepLabel, holeStepBox);
        Label drillSet = new Label("Drill set: drills.json");
        Button calc = new Button("Calculate");
        calc.setId("calculateButton");
        calc.setOnAction(e -> calculate());
        VBox left = new VBox(10, grid, drillSet, calc);
        root.setLeft(left);

        summaryLabel = new Label();
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setId("resultTable");
        TableColumn<HoleSpec, Integer> c1 = new TableColumn<>("Row");
        c1.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().index()));
        TableColumn<HoleSpec, Double> c2 = new TableColumn<>("Pos (mm)");
        c2.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().positionMm()));
        c2.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("%.2f", v));
            }
        });
        TableColumn<HoleSpec, Double> c3 = new TableColumn<>("\u00D8 (mm)");
        c3.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().diameterMm()));
        c3.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("%.2f", v));
            }
        });
        table.getColumns().addAll(c1, c2, c3);
        VBox centre = new VBox(10, summaryLabel, table);
        root.setCenter(centre);

        Button confirm = new Button("Confirm hydraulics");
        confirm.setOnAction(e -> validateFlow());
        Button exportCsv = new Button("Export CSV");
        exportCsv.setOnAction(e -> exportCsv());
        Button exportCad = new Button("Export CAD 2-D");
        exportCad.setOnAction(e -> todoAlert());
        Button gen3d = new Button("Generate 3-D");
        gen3d.setOnAction(e -> todoAlert());
        HBox bottom = new HBox(10, confirm, exportCsv, exportCad, gen3d);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Flow Modifier Calculator");
        stage.show();
    }

    private void calculate() {
        for (Label err : errors) err.setText("");
        Double[] vals = new Double[fields.size()];
        boolean valid = true;
        for (int i = 0; i < fields.size(); i++) {
            try { vals[i] = Double.parseDouble(fields.get(i).getText()); }
            catch (Exception ex) { errors.get(i).setText("Number required"); valid = false; }
        }
        if (!valid) return;

        double id = vals[0];
        double flowGpm = vals[1];
        double flowLpm = UnitConv.gpmToLpm(flowGpm);
        double dMin = vals[2];

        double stripLength = id * 5.0;
        double dMax = Math.round(id * 0.25 * 2) / 2.0;
        double step = holeStepBox.getValue() == null ? 0.5 : holeStepBox.getValue();

        lastPipe = new PipeSpecs(id, flowLpm, stripLength);
        lastLayout = PerforatedCoreOptimizer.autoDesign(id, flowLpm, dMin, step);
        table.getItems().setAll(lastLayout.holes());

        double re = PhysicsUtil.reynolds(id, flowLpm);
        double sheetW = Math.PI * id;
        double minD = lastLayout.holes().stream().mapToDouble(HoleSpec::diameterMm).min().orElse(dMin);
        double maxD = lastLayout.holes().stream().mapToDouble(HoleSpec::diameterMm).max().orElse(dMax);
        summaryLabel.setText(String.format(
                "Len=%.0f mm   Rows=%d   \u00D8 %.1f-%.1f mm   \u00D8 step = %.1f mm   Error=%.1f%%\nRe=%.0f   Sheet=%.0f\u00D7%.0f mm",
                stripLength, lastLayout.holes().size(), minD, maxD, step, lastLayout.worstCaseErrorPct(),
                re, sheetW, stripLength));
        summaryLabel.setTextFill(lastLayout.worstCaseErrorPct() > 5.0 ? Color.RED : Color.BLACK);
    }

    private void validateFlow() {
        if (lastPipe == null || lastLayout == null) return;
        boolean ok = lastLayout.worstCaseErrorPct() <= 5.0;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(ok ? "Balanced" : "Recalc");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill:" + (ok ? "green" : "red"));
        alert.showAndWait();
    }

    private void exportCsv() {
        if (lastLayout == null) return;
        try (java.io.PrintWriter pw = new java.io.PrintWriter("hole_layout.csv")) {
            pw.println("index,position_mm,diameter_mm,predicted_lpm");
            for (HoleSpec h : lastLayout.holes()) {
                pw.printf("%d,%.2f,%.2f,%.2f%n", h.index(), h.positionMm(), h.diameterMm(), h.predictedLpm());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void todoAlert() {
        Alert a = new Alert(Alert.AlertType.INFORMATION, "TODO");
        a.showAndWait();
    }
}
