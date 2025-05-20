package org.example.flowmod;

import org.example.flowmod.engine.PerforatedCoreOptimizer;
import org.example.flowmod.engine.PipeSpecs;
import org.example.flowmod.engine.OptimizationResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;

public class PerforatedCoreOptimizerTest {
    @Test
    void basicDesignWithinError() {
        PipeSpecs pipe = new PipeSpecs(100, 200, 500);
        HoleLayout layout = PerforatedCoreOptimizer.design(pipe, 500, 200, 2.0, 6.0, 5.0);
        assertTrue(layout.worstCaseErrorPct() <= 5.0);
        for (HoleSpec h : layout.holes()) {
            assertTrue(h.diameterMm() >= 2.0 && h.diameterMm() <= 6.0);
        }
    }

    @Test
    void autoDesignRespectsCap() {
        OptimizationResult result = PerforatedCoreOptimizer.autoDesign(300, 100, 4.0, 0.5, null);
        HoleLayout layout = result.layout();
        double max = layout.holes().stream().mapToDouble(HoleSpec::diameterMm).max().orElse(0);
        assertTrue(max <= 75.0);
        assertTrue(layout.worstCaseErrorPct() <= 5.0);
    }

    @Test
    void highFlowAddsRows() {
        OptimizationResult result = PerforatedCoreOptimizer.autoDesign(300, 5000, 4.0, 0.5, null);
        HoleLayout layout = result.layout();
        assertTrue(layout.holes().size() >= 20);
        assertTrue(layout.holes().size() <= 60);
        assertTrue(layout.worstCaseErrorPct() <= 5.0);
    }

    @Test
    void lowFlowDoesNotExceedMaxRows() {
        OptimizationResult result = PerforatedCoreOptimizer.autoDesign(300, 40, 4.0, 0.5, null);
        HoleLayout layout = result.layout();
        assertTrue(layout.holes().size() <= 60);
        assertTrue(layout.worstCaseErrorPct() <= 5.0);
    }

    @Test
    void stepTwoMmProducesMultiples() {
        OptimizationResult result = PerforatedCoreOptimizer.autoDesign(150, 100, 4.0, 2.0, null);
        HoleLayout layout = result.layout();
        for (HoleSpec h : layout.holes()) {
            double mod = Math.round(h.diameterMm() / 2.0);
            assertEquals(mod * 2.0, h.diameterMm(), 0.0001);
        }
    }

    @Test
    void halfMillimetreGranularityPresent() {
        OptimizationResult result = PerforatedCoreOptimizer.autoDesign(150, 100, 4.0, 0.5, null);
        HoleLayout layout = result.layout();
        boolean hasHalf = layout.holes().stream()
                .anyMatch(h -> Math.abs(h.diameterMm() - Math.round(h.diameterMm())) > 0.0001);
        assertTrue(hasHalf);
    }

    @Test
    void ligamentRule150mmWall6() {
        OptimizationResult result = PerforatedCoreOptimizer.autoDesign(150, 100, 4.0, 0.5, 6.0);
        HoleLayout layout = result.layout();
        double pitch = 150 * 5.0 / (layout.holes().size() + 1);
        for (HoleSpec h : layout.holes()) {
            assertTrue(pitch >= h.diameterMm() + 1.8);
        }
    }

    @Test
    void ligamentRule100mmWall2() {
        OptimizationResult result = PerforatedCoreOptimizer.autoDesign(100, 100, 4.0, 0.5, 2.0);
        HoleLayout layout = result.layout();
        double pitch = 100 * 5.0 / (layout.holes().size() + 1);
        for (HoleSpec h : layout.holes()) {
            assertTrue(pitch >= h.diameterMm() + 0.6);
        }
    }

    @Test
    void zeroDiameterProducesFiniteLayout() {
        PipeSpecs pipe = new PipeSpecs(0, 50, 100);
        HoleLayout layout = PerforatedCoreOptimizer.design(pipe, 100, 50, 1.0, 5.0, 5.0);
        assertFalse(Double.isNaN(layout.worstCaseErrorPct()));
        for (HoleSpec h : layout.holes()) {
            assertFalse(Double.isNaN(h.diameterMm()));
            assertFalse(Double.isNaN(h.positionMm()));
            assertFalse(Double.isNaN(h.predictedLpm()));
        }
    }
}
