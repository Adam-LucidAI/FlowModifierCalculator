package org.example.flowmod.engine;

public final class PhysicsUtil {
    private PhysicsUtil() {}

    public static FlowPhysics.Result compute(PipeSpecs pipe, double kinematicViscosity) {
        double diameterM = pipe.innerDiameterMm() / 1000.0;
        double area = Math.PI * Math.pow(diameterM / 2.0, 2);
        double flowRateM3s = pipe.flowRateLpm() / 1000.0 / 60.0;
        double velocity = flowRateM3s / area;
        double reynolds = velocity * diameterM / kinematicViscosity;
        double frictionFactor = reynolds < 2300 ? 64.0 / reynolds
                : 0.3164 / Math.pow(reynolds, 0.25);
        double density = 1000.0; // water
        double pressureDrop = frictionFactor * (density * velocity * velocity / 2.0) / diameterM;
        FlowPhysics.Regime regime = reynolds < 2300 ? FlowPhysics.Regime.LAMINAR
                : (reynolds < 4000 ? FlowPhysics.Regime.TRANSITIONAL : FlowPhysics.Regime.TURBULENT);
        return new FlowPhysics.Result(velocity, reynolds, pressureDrop, regime);
    }

    /**
     * Compute the Reynolds number for water at 20&nbsp;°C.
     *
     * @param diameterMm pipe inner diameter in millimetres
     * @param flowLpm    flow rate in litres per minute
     * @return Reynolds number
     */
    public static double reynolds(double diameterMm, double flowLpm) {
        double d = diameterMm / 1000.0;
        double q = flowLpm / 60000.0;
        double area = Math.PI * Math.pow(d / 2.0, 2);
        double v = q / area;
        return v * d / 1.004e-6;
    }
}
