package org.example.flowmod.ui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.stage.Stage;
import org.example.flowmod.*;
import org.example.flowmod.engine.*;

import java.util.ArrayList;
import java.util.List;

public class FlowModifierStudio extends Application {
    private static final int DEFAULT_ROWS = 10;
    private static final double DEFAULT_CD = 0.62;

    private TableView<HoleSpec> table;
    private Label summaryLabel;
    private ComboBox<String> algoBox;
    private final List<TextField> fields = new ArrayList<>();
    private final List<Label> errors = new ArrayList<>();
    private Group previewGroup;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

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
                "drillMaxMm"};
        for (int i = 0; i < keys.length; i++) {
            Label l = new Label(keys[i]);
            TextField tf = new TextField();
            tf.setId(keys[i]);
            Label err = new Label();
            err.setTextFill(Color.RED);
            int row = i;
            grid.addRow(row, l, tf);
            grid.add(err, 1, row+1);
            fields.add(tf);
            errors.add(err);
        }
        algoBox = new ComboBox<>();
        algoBox.getItems().addAll("Holes", "Vanes");
        algoBox.getSelectionModel().selectFirst();
        Button calc = new Button("Calculate");
        calc.setOnAction(e -> calculate());
        VBox inputBox = new VBox(5, grid, algoBox, calc);
        root.setLeft(inputBox);

        table = new TableView<>();
        TableColumn<HoleSpec, Integer> c1 = new TableColumn<>("Row");
        c1.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().index()));
        TableColumn<HoleSpec, Double> c2 = new TableColumn<>("Pos (mm)");
        c2.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().positionMm()));
        TableColumn<HoleSpec, Double> c3 = new TableColumn<>("Ø (mm)");
        c3.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().diameterMm()));
        table.getColumns().addAll(c1, c2, c3);
        summaryLabel = new Label();
        VBox resultsBox = new VBox(5, table, summaryLabel);
        root.setCenter(resultsBox);

        previewGroup = new Group();
        SubScene sub = new SubScene(previewGroup, 300, 300, true, null);
        sub.setCamera(new PerspectiveCamera());
        root.setRight(sub);

        Scene scene = new Scene(root, 900, 400);
        stage.setScene(scene);
        stage.setTitle("Flow Modifier Studio");
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

        PipeSpecs pipe = new PipeSpecs(vals[0], vals[1], vals[2]);
        FilterSpecs filter = new FilterSpecs(vals[3], vals[4]);

        ModifierDesignStrategy strategy = algoBox.getValue().equals("Vanes") ?
                new VaneDiffuserOptimizer() : new HoleOptimizer();
        DesignResult result = strategy.optimise(pipe, filter, vals[5], vals[6], DEFAULT_ROWS, DEFAULT_CD);
        if (result.holeLayout() != null) {
            table.getItems().setAll(result.holeLayout().holes());
        } else {
            table.getItems().clear();
        }

        FlowPhysics.Result phys = FlowPhysics.compute(pipe);
        summaryLabel.setText("Regime: " + phys.regime());

        previewGroup.getChildren().setAll(new Box(50, 10, 10));
    }
}
