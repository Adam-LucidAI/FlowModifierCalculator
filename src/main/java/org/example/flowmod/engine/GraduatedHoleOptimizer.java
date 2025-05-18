package org.example.flowmod.engine;

import java.util.ArrayList;
import java.util.List;
import org.example.flowmod.FilterSpecs;
import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;
import org.example.flowmod.PipeSpecs;
import org.example.flowmod.engine.DesignResult;
import org.example.flowmod.engine.ModifierDesignStrategy;

public final class GraduatedHoleOptimizer implements ModifierDesignStrategy {
    private GraduatedHoleOptimizer() {}

    @Override
    public DesignResult optimise(PipeSpecs pipe,
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
        HoleLayout layout = new HoleLayout(specs, 0.0);
        return new DesignResult(layout, null, 0.0);
    }
}
