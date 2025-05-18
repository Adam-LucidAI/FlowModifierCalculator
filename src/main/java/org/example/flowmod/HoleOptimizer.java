package org.example.flowmod;

import java.util.ArrayList;
import java.util.List;

public final class HoleOptimizer {
    private HoleOptimizer() {}

    public static HoleLayout optimise(PipeSpecs pipe,
                                      FilterSpecs filter,
                                      double drillMinMm,
                                      double drillMaxMm,
                                      int rows,
                                      double Cd) {
        List<HoleSpec> specs = new ArrayList<>();
        double flowPerHole = pipe.flowRateLpm() / rows;
        for (int i = 0; i < rows; i++) {
            double position = (i + 1) * pipe.modifierLengthMm() / (rows + 1);
            double dia = drillMinMm + (drillMaxMm - drillMinMm) * i / (rows - 1.0);
            double snapped = Math.round(dia * 2.0) / 2.0;
            specs.add(new HoleSpec(i + 1, position, snapped, flowPerHole));
        }
        return new HoleLayout(specs, 0.0);
    }
}
