package org.example.flowmod;

public final class FlowPhysics {
    private FlowPhysics() {}

    public record Result(double velocityMs, double reynolds, double pressureDropPaPerM) {}

    public static Result compute(PipeSpecs specs) {
        return compute(specs, 1.004e-6);
    }

    public static Result compute(PipeSpecs specs, double kinematicViscosity) {
        double diameterM = specs.innerDiameterMm() / 1000.0;
        double area = Math.PI * Math.pow(diameterM / 2.0, 2);
        double flowRateM3s = specs.flowRateLpm() / 1000.0 / 60.0;
        double velocity = flowRateM3s / area;
        double reynolds = velocity * diameterM / kinematicViscosity;
        double frictionFactor = reynolds < 2300 ? 64.0 / reynolds
                : 0.3164 / Math.pow(reynolds, 0.25);
        double density = 1000.0; // water
        double pressureDrop = frictionFactor * (density * velocity * velocity / 2.0) / diameterM;
        return new Result(velocity, reynolds, pressureDrop);
    }
}
