package org.example.flowmod;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.example.flowmod.engine.FlowPhysics;

public class FlowPhysicsTest {
    @Test
    void laminarCase() {
        PipeSpecs pipe = new PipeSpecs(10, 0.5, 100);
        FlowPhysics.Result r = FlowPhysics.compute(pipe);
        assertEquals(FlowPhysics.Regime.LAMINAR, r.regime());
    }

    @Test
    void transitionalCase() {
        PipeSpecs pipe = new PipeSpecs(10, 1.2, 100);
        FlowPhysics.Result r = FlowPhysics.compute(pipe);
        assertEquals(FlowPhysics.Regime.TRANSITIONAL, r.regime());
    }

    @Test
    void turbulentCase() {
        PipeSpecs pipe = new PipeSpecs(25, 10, 100);
        FlowPhysics.Result r = FlowPhysics.compute(pipe);
        assertEquals(FlowPhysics.Regime.TURBULENT, r.regime());
    }
}
