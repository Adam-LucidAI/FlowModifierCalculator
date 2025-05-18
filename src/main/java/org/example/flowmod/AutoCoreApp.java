package org.example.flowmod;

import org.example.flowmod.engine.PerforatedCoreOptimizer;
import org.example.flowmod.utils.UnitConv;

public final class AutoCoreApp {
    private AutoCoreApp() {}

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: AutoCoreApp ID_mm Flow_Gpm [dMin=4] [step=0.5]");
            System.exit(1);
        }
        double id = Double.parseDouble(args[0]);
        double flowGpm = Double.parseDouble(args[1]);
        double flowLpm = UnitConv.gpmToLpm(flowGpm);
        double dMin = args.length > 2 ? Double.parseDouble(args[2]) : 4.0;
        double step = args.length > 3 ? Double.parseDouble(args[3]) : 0.5;
        var layout = PerforatedCoreOptimizer.autoDesign(id, flowLpm, dMin, step);

        System.out.println("index,position_mm,diameter_mm,predicted_lpm");
        for (var h : layout.holes()) {
            System.out.printf("%d,%.2f,%.2f,%.2f%n", h.index(), h.positionMm(), h.diameterMm(), h.predictedLpm());
        }
        System.out.printf("error_pct,%.2f%n", layout.worstCaseErrorPct());
    }
}
