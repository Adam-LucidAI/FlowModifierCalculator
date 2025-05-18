package org.example.flowmod;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class AppLauncher {
    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "settings.json";
        Gson gson = new Gson();
        Settings settings;
        try (FileReader reader = new FileReader(file)) {
            settings = gson.fromJson(reader, Settings.class);
        }

        FlowPhysics.Result phys = FlowPhysics.compute(settings.pipe());
        HoleLayout layout = HoleOptimizer.optimise(
                settings.pipe(),
                settings.filter(),
                settings.drillMinMm(),
                settings.drillMaxMm(),
                settings.rows(),
                settings.Cd());

        System.out.println("Row | Pos mm | \u00D8 mm | L/min");
        for (HoleSpec h : layout.holes()) {
            System.out.printf("%3d | %6.1f | %5.1f | %5.2f\n",
                    h.index(), h.positionMm(), h.diameterMm(), h.predictedLpm());
        }

        double area = Math.PI * Math.pow(settings.pipe().innerDiameterMm() / 1000.0 / 2.0, 2);
        double openArea = area * settings.filter().openAreaPercent() / 100.0;
        double flowRate = settings.pipe().flowRateLpm() / 1000.0 / 60.0;
        double faceVelocity = flowRate / openArea;
        double screenDp = 1000 * faceVelocity * faceVelocity / 2.0;

        System.out.printf("Re=%.0f, pipe \u0394P=%.1f Pa/m, screen \u0394P=%.1f Pa, worst-case=%.1f%%\n",
                phys.reynolds(), phys.pressureDropPaPerM(), screenDp, layout.worstCaseErrorPct());

        try (PrintWriter pw = new PrintWriter("hole_layout.csv")) {
            pw.println("index,position_mm,diameter_mm,predicted_lpm");
            for (HoleSpec h : layout.holes()) {
                pw.printf("%d,%.2f,%.2f,%.2f\n", h.index(), h.positionMm(), h.diameterMm(), h.predictedLpm());
            }
        }
    }
}
