package org.example.flowmod.engine;

import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;

public final class FlowPhysics {
    private FlowPhysics() {}

    public enum Regime {
        LAMINAR, TRANSITIONAL, TURBULENT
    }

    public record Result(double velocityMs, double reynolds,
                         double pressureDropPaPerM, Regime regime) {}

    public static Result compute(PipeSpecs specs) {
        return compute(specs, 1.004e-6);
    }

    public static Result compute(PipeSpecs specs, double kinematicViscosity) {
        return PhysicsUtil.compute(specs, kinematicViscosity);
    }

    /** Compute the mean jet velocity for the given layout. */
    public static double meanVelocity(HoleLayout layout) {
        return layout.holes().stream()
                .mapToDouble(FlowPhysics::velocity)
                .average().orElse(0.0);
    }

    /** Compute the maximum jet velocity. */
    public static double maxVelocity(HoleLayout layout) {
        return layout.holes().stream()
                .mapToDouble(FlowPhysics::velocity)
                .max().orElse(0.0);
    }

    /** Compute the minimum jet velocity. */
    public static double minVelocity(HoleLayout layout) {
        return layout.holes().stream()
                .mapToDouble(FlowPhysics::velocity)
                .min().orElse(0.0);
    }

    /**
     * Calculate the uniformity error of the given layout as
     * (max - min) / mean * 100.
     */
    public static double uniformityError(HoleLayout layout) {
        double mean = meanVelocity(layout);
        double max = maxVelocity(layout);
        double min = minVelocity(layout);
        return mean == 0.0 ? 0.0 : (max - min) / mean * 100.0;
    }

    private static double velocity(HoleSpec h) {
        double q = h.predictedLpm() / 60000.0;
        double area = Math.PI * Math.pow(h.diameterMm() / 1000.0 / 2.0, 2);
        return area == 0.0 ? 0.0 : q / area;
    }
}
