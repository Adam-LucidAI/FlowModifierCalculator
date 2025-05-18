package org.example.flowmod.model3d;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import org.example.flowmod.engine.PipeSpecs;
import org.example.flowmod.engine.DesignResult;

public final class ModelBuilder {
    private ModelBuilder() {}

    public static TriangleMesh buildFlowModifierMesh(DesignResult design, PipeSpecs pipe) {
        // Placeholder simple box mesh
        TriangleMesh mesh = new TriangleMesh();
        // TODO real mesh generation
        return mesh;
    }
}
