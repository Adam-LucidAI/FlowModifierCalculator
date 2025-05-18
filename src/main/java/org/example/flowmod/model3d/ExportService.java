package org.example.flowmod.model3d;

import javafx.scene.shape.TriangleMesh;

import java.io.File;

public final class ExportService {
    private ExportService() {}

    /**
     * Temporary stub while STL export is disabled.
     */
    public static void saveAsStl(File file, TriangleMesh mesh) {
        throw new UnsupportedOperationException("STL export temporarily disabled");
    }
}
