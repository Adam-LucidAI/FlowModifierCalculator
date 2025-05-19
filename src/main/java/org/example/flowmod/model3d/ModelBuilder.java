package org.example.flowmod.model3d;

import javafx.scene.shape.Box;
import javafx.scene.Node;
import org.example.flowmod.engine.PipeSpecs;
import org.example.flowmod.engine.DesignResult;

public final class ModelBuilder {
    private ModelBuilder() {}

    public static Node buildFlowModifierMesh(DesignResult design, PipeSpecs pipe) {
        if (design == null || design.holeLayout() == null ||
                design.holeLayout().holes().isEmpty()) {
            return new Box(1, 1, 1);
        }
        // TODO replace with TriangleMesh generation
        return new Box(1, 1, 1);
    }
}
