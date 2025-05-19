package org.example.flowmod.engine;

import org.example.flowmod.HoleLayout;

/** Summary of an optimisation run. */
public record OptimizationResult(HoleLayout layout,
                                 double meanV,
                                 double maxV,
                                 double minV,
                                 double uniformityErrorPct) {
    /**
     * Check whether the result meets a uniformity specification.
     *
     * @param pct allowable percentage variation
     * @return true if within the limit
     */
    public boolean meetsSpec(double pct) {
        return uniformityErrorPct <= pct;
    }
}
