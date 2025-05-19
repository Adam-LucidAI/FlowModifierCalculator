package org.example.flowmod.utils;

/** Utility for pipe wall thickness defaults. */
public final class PipeSchedule {
    private PipeSchedule() {}

    /**
     * Approximate schedule 40 wall thickness for a given pipe ID.
     * Values are simplified for this demo.
     * @param dPipeMm pipe inner diameter in millimetres
     * @return wall thickness in millimetres
     */
    public static double defaultWall(double dPipeMm) {
        if (dPipeMm <= 25) return 2.5;
        if (dPipeMm <= 50) return 3.5;
        if (dPipeMm <= 80) return 4.0;
        if (dPipeMm <= 150) return 6.0;
        if (dPipeMm <= 250) return 8.0;
        return 10.0;
    }
}
