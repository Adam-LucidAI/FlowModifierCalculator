package org.example.flowmod;

import org.example.flowmod.engine.DesignResult;
import org.example.flowmod.engine.FilterSpecs;
import org.example.flowmod.engine.GraduatedHoleOptimizer;
import org.example.flowmod.engine.ModifierDesignStrategy;
import org.example.flowmod.engine.PipeSpecs;

/** Convenience wrapper delegating to {@link GraduatedHoleOptimizer}. */
public class HoleOptimizer implements ModifierDesignStrategy {

    private final GraduatedHoleOptimizer delegate;

    public HoleOptimizer() {
        this.delegate = new GraduatedHoleOptimizer();
    }

    @Override
    public DesignResult optimise(PipeSpecs pipe,
                                 FilterSpecs filter,
                                 double drillMinMm,
                                 double drillMaxMm,
                                 int rows,
                                 double Cd) {
        return delegate.optimise(pipe, filter, drillMinMm, drillMaxMm, rows, Cd);
    }

    /**
     * Optimise using a new instance of the underlying {@link GraduatedHoleOptimizer}.
     */
    public static HoleLayout optimise(PipeSpecs pipe,
                                      FilterSpecs filter,
                                      double drillMinMm,
                                      double drillMaxMm,
                                      int rows,
                                      double Cd) {
        return new HoleOptimizer()
                .optimise(pipe, filter, drillMinMm, drillMaxMm, rows, Cd)
                .holeLayout();
    }
}
