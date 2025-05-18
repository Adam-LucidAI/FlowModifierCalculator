package org.example.flowmod;

import org.example.flowmod.engine.PerforatedCoreOptimizer;
import org.example.flowmod.engine.PipeSpecs;
import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;

public final class CoreApp {
    private CoreApp() {}

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: CoreApp ID_mm Length_mm Flow_Lpm [dMin] [dMax]");
            System.exit(1);
        }
        double id = Double.parseDouble(args[0]);
        double length = Double.parseDouble(args[1]);
        double flow = Double.parseDouble(args[2]);
        double dMin = args.length > 3 ? Double.parseDouble(args[3]) : 1.0;
        double dMax = args.length > 4 ? Double.parseDouble(args[4]) : 5.0;

        PipeSpecs pipe = new PipeSpecs(id, flow, length);
        HoleLayout layout = PerforatedCoreOptimizer.design(pipe, length, flow, dMin, dMax, 5.0);

        System.out.println("index,position_mm,diameter_mm,predicted_lpm");
        for (HoleSpec h : layout.holes()) {
            System.out.printf("%d,%.2f,%.2f,%.2f%n", h.index(), h.positionMm(), h.diameterMm(), h.predictedLpm());
        }
        System.out.printf("error_pct,%.2f%n", layout.worstCaseErrorPct());
    }
}
