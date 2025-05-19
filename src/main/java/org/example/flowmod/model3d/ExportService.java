package org.example.flowmod.model3d;

import javafx.scene.shape.TriangleMesh;
import java.nio.file.Path;
import java.util.Optional;

import java.io.File;

public final class ExportService {
    private ExportService() {}

    /**
     * Temporary stub while STL export is disabled.
     */
    public static Optional<Path> saveAsStl(File file, TriangleMesh mesh) {
        // TODO implement STL export
        return Optional.empty();
    }
}
