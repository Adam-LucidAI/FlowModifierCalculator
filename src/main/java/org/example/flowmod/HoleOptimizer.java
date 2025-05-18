package org.example.flowmod;

import org.example.flowmod.engine.DesignResult;
import org.example.flowmod.engine.FilterSpecs;
import org.example.flowmod.engine.GraduatedHoleOptimizer;
import org.example.flowmod.engine.PipeSpecs;

/** Utility wrapper delegating to {@link GraduatedHoleOptimizer}. */
public final class HoleOptimizer {

    private HoleOptimizer() {
    }

    public static DesignResult optimise(PipeSpecs pipe,
                                        FilterSpecs filter,
                                        double drillMinMm,
                                        double drillMaxMm,
                                        int rows,
                                        double Cd) {
        return new GraduatedHoleOptimizer()
                .optimise(pipe, filter, drillMinMm, drillMaxMm, rows, Cd);
    }
}
