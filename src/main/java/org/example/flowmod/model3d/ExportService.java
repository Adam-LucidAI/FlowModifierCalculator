package org.example.flowmod.model3d;

import de.javagl.stl.STLExporter;
import javafx.scene.shape.TriangleMesh;

import java.io.File;
import java.io.FileOutputStream;

public final class ExportService {
    private ExportService() {}

    public static void saveAsStl(File file, TriangleMesh mesh) throws Exception {
        STLExporter exporter = new STLExporter();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            exporter.export(mesh, fos);
        }
    }
}
