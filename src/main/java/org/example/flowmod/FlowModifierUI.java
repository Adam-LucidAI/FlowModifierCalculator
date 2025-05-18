package org.example.flowmod;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.flowmod.engine.FlowPhysics;
import org.example.flowmod.engine.FilterSpecs;
import org.example.flowmod.engine.PipeSpecs;
import org.example.flowmod.engine.DesignResult;

import java.util.ArrayList;
import java.util.List;

/** Simplified JavaFX UI for interactive optimisation. */
public class FlowModifierUI extends Application {
    private static final int DEFAULT_ROWS = 10;
    private static final double DEFAULT_CD = 0.62;

    private final List<TextField> fields = new ArrayList<>();
    private final List<Label> errors = new ArrayList<>();
    private TableView<HoleSpec> table;
    private Label summaryLabel;
    private PipeSpecs lastPipe;
    private DesignResult lastResult;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        String[] keys = {"innerDiameterMm", "modifierLengthMm", "flowRateLpm", "drillMinMm", "drillMaxMm"};
        for (int i = 0; i < keys.length; i++) {
            Label l = new Label(keys[i]);
            TextField tf = new TextField();
            tf.setId(keys[i]);
            Label err = new Label();
            err.setTextFill(Color.RED);
            grid.addRow(i * 2, l, tf);
            grid.add(err, 1, i * 2 + 1);
            fields.add(tf);
            errors.add(err);
        }
        Button calc = new Button("Calculate");
        calc.setId("calculateButton");
        calc.setOnAction(e -> calculate());
        VBox left = new VBox(10, grid, calc);
        root.setLeft(left);

        summaryLabel = new Label();
        table = new TableView<>();
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

        Button gen3d = new Button("Generate 3-D");
        gen3d.setDisable(true);
        gen3d.setOnAction(e -> showStlTodo());
        Button validate = new Button("Validate flow");
        validate.setOnAction(e -> validateFlow());
        HBox bottom = new HBox(10, gen3d, validate);
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
        double len = vals[1];
        double flow = vals[2];
        double dMin = vals[3];
        double dMax = vals[4];

        lastPipe = new PipeSpecs(id, flow, len);
        FilterSpecs filter = new FilterSpecs(0, 0); // defaults ignored
        lastResult = HoleOptimizer.optimise(lastPipe, filter, dMin, dMax, DEFAULT_ROWS, DEFAULT_CD);
        HoleLayout layout = lastResult.holeLayout();
        table.getItems().setAll(layout.holes());

        FlowPhysics.Result phys = FlowPhysics.compute(lastPipe);
        summaryLabel.setText(String.format("Re=%.0f   \u0394P=%.1f kPa/m   Error=%.1f%%",
                phys.reynolds(), phys.pressureDropPaPerM() / 1000.0, lastResult.worstCaseErrorPct()));
        summaryLabel.setTextFill(lastResult.worstCaseErrorPct() > 5.0 ? Color.RED : Color.BLACK);
    }

    private void validateFlow() {
        if (lastPipe == null || lastResult == null) return;
        boolean ok = lastResult.worstCaseErrorPct() <= 5.0;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(ok ? "Balanced" : "Recalc");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill:" + (ok ? "green" : "red"));
        alert.showAndWait();
    }

    private void showStlTodo() {
        Alert a = new Alert(Alert.AlertType.INFORMATION, "STL export coming soon");
        a.showAndWait();
    }
}
