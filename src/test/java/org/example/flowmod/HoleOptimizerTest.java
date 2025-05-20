package org.example.flowmod;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.example.flowmod.engine.FilterSpecs;
import org.example.flowmod.engine.PipeSpecs;
import org.example.flowmod.engine.DesignResult;

public class HoleOptimizerTest {
    @Test
    void optimisationErrorWithinLimits() {
        PipeSpecs pipe = new PipeSpecs(20, 5, 100);
        FilterSpecs filter = new FilterSpecs(30, 1.0);
        DesignResult result = HoleOptimizer.optimise(pipe, filter, 1.0, 5.0, 10, 0.62);
        HoleLayout layout = result.holeLayout();
        assertTrue(result.worstCaseErrorPct() <= 5.0);
        assertEquals(10, layout.holes().size());
    }

    @Test
    void singleRowUsesMinimumDrillSize() {
        PipeSpecs pipe = new PipeSpecs(20, 5, 100);
        FilterSpecs filter = new FilterSpecs(30, 1.0);
        DesignResult result = HoleOptimizer.optimise(pipe, filter, 1.0, 5.0, 1, 0.62);
        HoleLayout layout = result.holeLayout();
        assertEquals(1, layout.holes().size());
        HoleSpec hole = layout.holes().get(0);
        assertEquals(pipe.modifierLengthMm() / 2.0, hole.positionMm(), 0.0001);
        assertEquals(1.0, hole.diameterMm(), 0.0001);
    }
}
