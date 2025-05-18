package org.example.flowmod;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.example.flowmod.engine.FilterSpecs;
import org.example.flowmod.engine.PipeSpecs;

public class HoleOptimizerTest {
    @Test
    void optimisationErrorWithinLimits() {
        PipeSpecs pipe = new PipeSpecs(20, 5, 100);
        FilterSpecs filter = new FilterSpecs(30, 1.0);
        HoleLayout layout = HoleOptimizer.optimise(pipe, filter, 1.0, 5.0, 10, 0.62);
        assertTrue(layout.worstCaseErrorPct() <= 5.0);
        assertEquals(10, layout.holes().size());
    }
}
