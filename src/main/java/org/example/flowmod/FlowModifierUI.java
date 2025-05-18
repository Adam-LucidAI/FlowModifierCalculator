package org.example.flowmod;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.flowmod.engine.FlowPhysics;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal JavaFX UI for interactive optimisation.
 */
public class FlowModifierUI extends Application {

    private static final int DEFAULT_ROWS = 10;
    private static final double DEFAULT_CD = 0.62;

    private TableView<HoleSpec> table;
    private Label summaryLabel;

    private final List<TextField> fields = new ArrayList<>();
    private final List<Label> errors = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane();
        Tab inputsTab = new Tab("Inputs");
        inputsTab.setClosable(false);
        Tab resultsTab = new Tab("Results");
        resultsTab.setClosable(false);
        tabs.getTabs().addAll(inputsTab, resultsTab);

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        String[] keys = new String[]{
                "innerDiameterMm",
                "flowRateLpm",
                "modifierLengthMm",
                "openAreaPercent",
                "faceVelocityMaxMs",
                "drillMinMm",
                "drillMaxMm"
        };

        for (int i = 0; i < keys.length; i++) {
            Label l = new Label(keys[i]);
            TextField tf = new TextField();
            tf.setId(keys[i]);
            Label err = new Label();
            err.setTextFill(Color.RED);
            int row = i * 2;
            grid.add(l, 0, row);
            grid.add(tf, 1, row);
            grid.add(err, 1, row + 1);
            fields.add(tf);
            errors.add(err);
        }

        Button calc = new Button("Calculate");
        calc.setId("calculateButton");
        calc.setOnAction(e -> calculate());
        VBox inputBox = new VBox(10, grid, calc);
        inputsTab.setContent(inputBox);

        table = new TableView<>();
        table.setId("resultTable");
        TableColumn<HoleSpec, Integer> c1 = new TableColumn<>("Row");
        c1.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().index()));
        TableColumn<HoleSpec, Double> c2 = new TableColumn<>("Pos (mm)");
        c2.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().positionMm()));
        TableColumn<HoleSpec, Double> c3 = new TableColumn<>("Ø (mm)");
        c3.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().diameterMm()));
        TableColumn<HoleSpec, Double> c4 = new TableColumn<>("L/min");
        c4.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().predictedLpm()));
        table.getColumns().addAll(c1, c2, c3, c4);

        summaryLabel = new Label();
        VBox resultsBox = new VBox(10, table, summaryLabel);
        resultsTab.setContent(resultsBox);

        Scene scene = new Scene(tabs, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Flow Modifier Calculator");
        stage.show();
    }

    private void calculate() {
        for (Label err : errors) {
            err.setText("");
        }

        Double[] vals = new Double[fields.size()];
        boolean valid = true;
        for (int i = 0; i < fields.size(); i++) {
            try {
                vals[i] = Double.parseDouble(fields.get(i).getText());
            } catch (Exception ex) {
                errors.get(i).setText("Number required");
                valid = false;
            }
        }
        if (!valid) return;

        double innerDia = vals[0];
        double flowRate = vals[1];
        double modifierLen = vals[2];
        double openAreaPct = vals[3];
        double faceVelMax = vals[4];
        double drillMin = vals[5];
        double drillMax = vals[6];

        PipeSpecs pipe = new PipeSpecs(innerDia, flowRate, modifierLen);
        FilterSpecs filter = new FilterSpecs(openAreaPct, faceVelMax);

        HoleLayout layout = HoleOptimizer.optimise(pipe, filter, drillMin, drillMax, DEFAULT_ROWS, DEFAULT_CD);
        table.getItems().setAll(layout.holes());

        FlowPhysics.Result phys = FlowPhysics.compute(pipe);
        double area = Math.PI * Math.pow(pipe.innerDiameterMm() / 1000.0 / 2.0, 2);
        double openArea = area * filter.openAreaPercent() / 100.0;
        double flowRateM3s = pipe.flowRateLpm() / 1000.0 / 60.0;
        double faceVelocity = flowRateM3s / openArea;
        double screenDp = 1000 * faceVelocity * faceVelocity / 2.0;

        String txt = String.format(
                "Re=%.0f   Pipe \u0394P=%.1f kPa/m   Screen \u0394P=%.1f kPa   Error=%.1f%%",
                phys.reynolds(), phys.pressureDropPaPerM() / 1000.0, screenDp / 1000.0, layout.worstCaseErrorPct());
        summaryLabel.setText(txt);

        boolean warn = layout.worstCaseErrorPct() > 5.0 || faceVelocity > filter.faceVelocityMaxMs();
        summaryLabel.setTextFill(warn ? Color.RED : Color.BLACK);
    }
}

