package org.example.flowmod.service;

import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;
import org.example.flowmod.engine.*;
import org.example.flowmod.utils.PipeSchedule;
import org.example.flowmod.utils.UnitConv;

/** Business logic for calculating flow modifier layouts. */
public final class FlowModifierService {
    private FlowModifierService() {}

    public static FlowCalcResult calculate(double pipeIdMm,
                                           double flowGpm,
                                           double stripLengthMm,
                                           double drillMinMm,
                                           double holeStepMm,
                                           Double wallThkMm) {
        double flowLpm = UnitConv.gpmToLpm(flowGpm);
        double wall = wallThkMm != null ? wallThkMm
                : PipeSchedule.defaultWall(pipeIdMm);
        PipeSpecs pipe = new PipeSpecs(pipeIdMm, flowLpm, stripLengthMm);

        OptimizationResult opt = PerforatedCoreOptimizer.autoDesign(
                pipeIdMm, stripLengthMm, flowLpm, drillMinMm, holeStepMm, wall);
        HoleLayout layout = opt.layout();
        DesignResult design = new DesignResult(pipe, layout, null,
                layout.worstCaseErrorPct());

        double re = PhysicsUtil.reynolds(pipeIdMm, flowLpm);
        double sheetWidth = Math.PI * pipeIdMm;
        double minD = layout.holes().stream()
                .mapToDouble(HoleSpec::diameterMm).min().orElse(drillMinMm);
        double maxD = layout.holes().stream()
                .mapToDouble(HoleSpec::diameterMm).max().orElse(drillMinMm);
        double pitch = stripLengthMm / (layout.holes().size() + 1);
        double minWeb = 0.30 * wall;
        boolean okSpacing = pitch >= maxD + minWeb;

        return new FlowCalcResult(design, opt, re, sheetWidth,
                minD, maxD, pitch, minWeb, okSpacing);
    }
}
