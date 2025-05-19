package org.example.flowmod;

import org.example.flowmod.engine.PerforatedCoreOptimizer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

public class OptimiserInfiniteLoopTest {
    @Test
    void loopAbortsWithinTime() {
        assertTimeoutPreemptively(Duration.ofSeconds(1), () ->
                assertThrows(IllegalStateException.class, () ->
                        PerforatedCoreOptimizer.autoDesign(0, 100, 1.0, 0.5, 1.0)
                ));
    }
}
