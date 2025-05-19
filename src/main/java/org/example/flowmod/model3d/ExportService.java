package org.example.flowmod.model3d;

import javafx.scene.control.Alert;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.FileChooser;
import org.example.flowmod.engine.DesignResult;
import org.example.flowmod.utils.CsvWriter;
import org.example.flowmod.utils.SvgWriter;
import java.nio.file.Path;
import java.util.Optional;

import java.io.File;
import java.io.PrintWriter;

public final class ExportService {
    private ExportService() {}

    /**
     * Temporary stub while STL export is disabled.
     */
    public static Optional<Path> saveAsStl(File file, TriangleMesh mesh) {
        // TODO implement STL export
        return Optional.empty();
    }

    /** Save the current layout to a CSV file. */
    public static void saveCsv(DesignResult result) {
        if (result == null || result.holeLayout() == null) return;
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("layout.csv");
        File file = fc.showSaveDialog(null);
        if (file == null) return;
        try {
            CsvWriter.write(file.toPath(), result.holeLayout());
            Alert a = new Alert(Alert.AlertType.INFORMATION,
                    "CSV saved to " + file.getAbsolutePath());
            a.setHeaderText(null);
            a.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Save a simple SVG blueprint of the layout. */
    public static void saveSvg(DesignResult result) {
        if (result == null || result.holeLayout() == null || result.pipe() == null) return;
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("layout.svg");
        File file = fc.showSaveDialog(null);
        if (file == null) return;
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.print(SvgWriter.toSvg(result.holeLayout(), result.pipe()));
            Alert a = new Alert(Alert.AlertType.INFORMATION,
                    "SVG saved to " + file.getAbsolutePath() + " (import into CAD or convert to DXF)");
            a.setHeaderText(null);
            a.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
