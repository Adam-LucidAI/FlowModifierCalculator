package org.example.flowmod;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.example.flowmod.engine.PipeSpecs;
import org.example.flowmod.engine.PerforatedCoreOptimizer;
import org.example.flowmod.engine.PhysicsUtil;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import org.example.flowmod.utils.UnitConv;
import org.example.flowmod.utils.PipeSchedule;
import org.example.flowmod.utils.SvgWriter;
import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;
import org.example.flowmod.engine.DesignResult;
import org.example.flowmod.engine.OptimizationResult;
import org.example.flowmod.model3d.ExportService;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Simplified JavaFX UI for interactive optimisation. */
public class FlowModifierUI extends Application {
    private final List<TextField> fields = new ArrayList<>();
    private final List<Label> errors = new ArrayList<>();

    private ComboBox<Double> holeStepBox;
    private TextField wallThkField;

    private TableView<HoleSpec> table;
    private Label summaryLabel;
    private PipeSpecs lastPipe;
    private HoleLayout lastLayout;
    private OptimizationResult lastOpt;
    private String lastSvg;
    private final StringProperty statusProp = new SimpleStringProperty("PASS");
    private final javafx.beans.property.ObjectProperty<DesignResult> resultProp =
            new javafx.beans.property.SimpleObjectProperty<>();

    private Button confirmBtn;
    private Button exportCsvBtn;
    private Button exportSvgBtn;
    private Button gen3dBtn;

    private WebView blueprintView;
    private TabPane rightTabs;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        String[] keys = {"innerDiameterMm", "flowRateLpm", "headerLengthMm", "drillMinMm"};
        for (int i = 0; i < keys.length; i++) {
            String text = keys[i];
            if ("flowRateLpm".equals(text)) text = "Flow rate (GPM)";
            if ("headerLengthMm".equals(text)) text = "Header length (mm)";
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

        Label wallLabel = new Label("Pipe wall (mm) \u2013 blank = Sch-40");
        wallThkField = new TextField();
        wallThkField.setId("wallThkMm");
        grid.addRow(keys.length * 2 + 2, wallLabel, wallThkField);
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
        TableColumn<HoleSpec, String> c4 = new TableColumn<>("Status");
        c4.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(statusProp.get()));
        statusProp.addListener((obs, o, n) -> table.refresh());
        table.getColumns().addAll(c1, c2, c3, c4);
        table.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(HoleSpec item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setStyle("");
                } else if ("FAIL".equals(statusProp.get())) {
                    setStyle("-fx-background-color:#ffdddd;");
                } else {
                    setStyle("");
                }
            }
        });
        root.setTop(summaryLabel);
        BorderPane tablePane = new BorderPane(table);
        root.setCenter(tablePane);

        blueprintView = new WebView();
        Tab blueprintTab = new Tab("Blueprint", blueprintView);
        blueprintTab.setClosable(false);
        rightTabs = new TabPane(blueprintTab);
        rightTabs.setVisible(false);
        root.setRight(rightTabs);

        confirmBtn = new Button("Confirm hydraulics");
        confirmBtn.setOnAction(e -> validateFlow());
        exportCsvBtn = new Button("Export CSV");
        exportSvgBtn = new Button("Export 2-D (SVG)");
        gen3dBtn = new Button("Generate 3-D");
        gen3dBtn.setOnAction(e -> todoAlert());
        HBox bottom = new HBox(10, confirmBtn, exportCsvBtn, exportSvgBtn, gen3dBtn);
        root.setBottom(bottom);

        confirmBtn.setDisable(true);
        exportCsvBtn.setDisable(true);
        exportSvgBtn.setDisable(true);
        gen3dBtn.setDisable(true);


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

        final Stage ownerStage = (Stage) table.getScene().getWindow();
        final double holeStep = Optional.ofNullable(holeStepBox.getValue())
                                     .orElse(0.5);

        exportCsvBtn.disableProperty().bind(resultProp.isNull());
        exportCsvBtn.setOnAction(e ->
                ExportService.saveCsv(resultProp.get(), ownerStage));
        exportSvgBtn.disableProperty().bind(resultProp.isNull());
        exportSvgBtn.setOnAction(e ->
                ExportService.saveSvg(resultProp.get(), ownerStage));
        double id = vals[0];
        double flowGpm = vals[1];
        double flowLpm = UnitConv.gpmToLpm(flowGpm);
        double stripLength = vals[2];
        double dMin = vals[3];
        double dMax = Math.round(id * 0.25 * 2) / 2.0;
        final double wall = wallThkField.getText().isBlank()
                ? PipeSchedule.defaultWall(id)
                : Double.parseDouble(wallThkField.getText());

        lastPipe = new PipeSpecs(id, flowLpm, stripLength);
        Scene scene = table.getScene();
        scene.setCursor(Cursor.WAIT);

        Task<DesignResult> task = new Task<>() {
            @Override
            protected DesignResult call() {
                lastOpt = PerforatedCoreOptimizer.autoDesign(id, flowLpm, dMin, holeStep, wall);
                HoleLayout layout = lastOpt.layout();
                return new DesignResult(lastPipe, layout, null, layout.worstCaseErrorPct());
            }
        };

        task.setOnSucceeded(ev -> {
            scene.setCursor(Cursor.DEFAULT);
            resultProp.set(task.getValue());
            DesignResult result = task.getValue();
            lastLayout = result.holeLayout();
            statusProp.set(lastOpt.meetsSpec(5.0) ? "PASS" : "FAIL");
            table.getItems().setAll(lastLayout.holes());
            lastSvg = SvgWriter.toSvg(lastLayout, lastPipe);
            blueprintView.getEngine().loadContent(lastSvg, "image/svg+xml");
            rightTabs.setVisible(true);
            confirmBtn.setDisable(false);
            gen3dBtn.setDisable(false);

            double re = PhysicsUtil.reynolds(id, flowLpm);
            double sheetW = Math.PI * id;
            double minD = lastLayout.holes().stream().mapToDouble(HoleSpec::diameterMm).min().orElse(dMin);
            double maxD = lastLayout.holes().stream().mapToDouble(HoleSpec::diameterMm).max().orElse(dMax);
            double usedWall = wall;
            double pitch = stripLength / (lastLayout.holes().size() + 1);
            double minWeb = 0.30 * usedWall;
            boolean okSpacing = pitch >= maxD + minWeb;
            summaryLabel.setText(String.format(
                    "Len=%.0f mm   Rows=%d   \u00D8 %.1f-%.1f mm   \u00D8 step = %.1f mm   Error=%.1f%%   Uniformity=%.1f%% (%s)\nRe=%.0f   Sheet=%.0f\u00D7%.0f mm\nPitch = %.1f mm  (min web = %.1f mm)",
                    stripLength, lastLayout.holes().size(), minD, maxD, holeStep, lastLayout.worstCaseErrorPct(),
                    lastOpt.uniformityErrorPct(), statusProp.get(),
                    re, sheetW, stripLength, pitch, minWeb));
            summaryLabel.setTextFill(lastLayout.worstCaseErrorPct() > 5.0 || !okSpacing || !"PASS".equals(statusProp.get()) ? Color.RED : Color.BLACK);
        });

        task.setOnFailed(ev -> {
            scene.setCursor(Cursor.DEFAULT);
            Throwable ex = task.getException();
            Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            a.setHeaderText(null);
            a.showAndWait();
        });

        new Thread(task, "calc-thread").start();
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

    private void todoAlert() {
        Alert a = new Alert(Alert.AlertType.INFORMATION, "TODO");
        a.showAndWait();
    }
}
