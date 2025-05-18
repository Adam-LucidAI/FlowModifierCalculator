package org.example.flowmod;

import org.example.flowmod.engine.PerforatedCoreOptimizer;
import org.example.flowmod.engine.PipeSpecs;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;

public class PerforatedCoreOptimizerTest {
    @Test
    void basicDesignWithinError() {
        PipeSpecs pipe = new PipeSpecs(100, 200, 500);
        HoleLayout layout = PerforatedCoreOptimizer.design(pipe, 500, 200, 2.0, 6.0, 5.0, 0.5);
        assertTrue(layout.worstCaseErrorPct() <= 5.0);
        for (HoleSpec h : layout.holes()) {
            assertTrue(h.diameterMm() >= 2.0 && h.diameterMm() <= 6.0);
        }
    }
}
