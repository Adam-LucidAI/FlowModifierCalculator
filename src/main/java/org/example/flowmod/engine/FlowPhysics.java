package org.example.flowmod.engine;

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
}
