package org.example.flowmod;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FlowPhysicsTest {
    @Test
    void laminarCase() {
        PipeSpecs pipe = new PipeSpecs(10, 0.5, 100);
        FlowPhysics.Result r = FlowPhysics.compute(pipe);
        assertTrue(r.reynolds() < 2000);
    }

    @Test
    void turbulentCase() {
        PipeSpecs pipe = new PipeSpecs(25, 10, 100);
        FlowPhysics.Result r = FlowPhysics.compute(pipe);
        assertEquals(10000, r.reynolds(), 3000);
    }
}
