package org.example.flowmod.engine;

import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;

import java.util.ArrayList;
import java.util.List;

/** Optimiser for perforated inlet cores. */
public final class PerforatedCoreOptimizer {
    private static final double CD = 0.62;
    private static final double DENSITY = 1000.0;
    /** Maximum number of rows allowed when auto designing. */
    private static final int MAX_ROWS = 60;
    /** Desired jet/pipe velocity ratio when estimating open area. */
    private static final double JET_VELOCITY_RATIO = 3.0;

    private PerforatedCoreOptimizer() {}

    /**
     * Designs a perforated core using automatic rules.
     * The strip length defaults to five times the pipe diameter and the
     * maximum drill size is 25% of the pipe diameter rounded to the nearest
     * 0.5 mm.
     *
     * @param pipeDiameterMm pipe inner diameter
     * @param flowLpm        target total flow rate
     * @param drillMinMm     minimum drill diameter
     * @return optimised hole layout
     */
    public static HoleLayout autoDesign(double pipeDiameterMm,
                                        double flowLpm,
                                        double drillMinMm) {
        double stripLength = pipeDiameterMm * 5.0;
        double maxDia = Math.round(pipeDiameterMm * 0.25 * 2) / 2.0;

        double flowM3s = flowLpm / 60000.0;
        double areaPipe = Math.PI * Math.pow(pipeDiameterMm / 1000.0, 2) / 4.0;
        double vPipe = flowM3s / areaPipe;
        double areaReq = flowM3s / (JET_VELOCITY_RATIO * vPipe);
        double holeArea = Math.PI * Math.pow(maxDia / 1000.0 / 2.0, 2);
        int rows = (int) Math.ceil(areaReq / holeArea);
        if (rows < 8) rows = 8;
        if (rows > MAX_ROWS) rows = MAX_ROWS;

        HoleLayout layout = null;
        while (true) {
            PipeSpecs pipe = new PipeSpecs(pipeDiameterMm, flowLpm, stripLength);
            layout = layoutForCount(pipe, stripLength, flowLpm,
                                    4.0, maxDia, 0.5, rows);
            double err = layout.worstCaseErrorPct();
            boolean maxUsed = layout.holes().stream()
                    .anyMatch(h -> Math.abs(h.diameterMm() - maxDia) < 0.0001);
            if (err <= 5.0) {
                break;
            }

            if (rows < MAX_ROWS) {
                rows += 4;
                if (rows > MAX_ROWS) rows = MAX_ROWS;
                continue;
            }

            // At maximum row count: extend the strip if holes have not
            // reached the allowed maximum diameter or accuracy is poor.
            if (!maxUsed || rows >= MAX_ROWS) {
                stripLength += 0.5 * pipeDiameterMm;
                if (stripLength > 30.0 * pipeDiameterMm) {
                    break;
                }
                // keep rows at MAX_ROWS
            }
        }
        return layout;
    }

    /**
     * Designs a perforated inlet core.
     *
     * @param pipe           upstream pipe specifications
     * @param stripLengthMm  length of the perforated strip
     * @param flowLpm        desired total inlet flow
     * @param drillMinMm     minimum drill diameter
     * @param drillMaxMm     maximum drill diameter
     * @param errorLimitPct  acceptable error percentage
     * @param drillStep      drill size increment
     * @return hole layout meeting the constraints
     */
    public static HoleLayout design(PipeSpecs pipe,
                                    double stripLengthMm,
                                    double flowLpm,
                                    double drillMinMm,
                                    double drillMaxMm,
                                    double errorLimitPct,
                                    double drillStep) {
        HoleLayout best = null;
        double bestErr = Double.MAX_VALUE;
        for (int n = 3; n <= 50; n++) {
            HoleLayout layout = layoutForCount(pipe, stripLengthMm, flowLpm,
                                               drillMinMm, drillMaxMm,
                                               drillStep, n);
            double err = layout.worstCaseErrorPct();
            if (err < bestErr) {
                bestErr = err;
                best = layout;
            }
            if (err <= errorLimitPct) {
                break;
            }
        }
        return new HoleLayout(best.holes(), bestErr);
    }

    private static HoleLayout layoutForCount(PipeSpecs pipe,
                                             double stripLengthMm,
                                             double flowLpm,
                                             double drillMinMm,
                                             double drillMaxMm,
                                             double drillStep,
                                             int holes) {
        double dpStart = FlowPhysics.compute(pipe).pressureDropPaPerM() * (stripLengthMm / 1000.0);
        if (dpStart <= 0) {
            dpStart = 1000.0;
        }
        double dpEnd = dpStart * 0.2;

        List<HoleSpec> specs = new ArrayList<>();
        double total = 0.0;
        for (int i = 0; i < holes; i++) {
            double pos = (i + 1) * stripLengthMm / (holes + 1);
            double dp = dpStart - (dpStart - dpEnd) * (pos / stripLengthMm);
            double target = flowLpm / holes;
            double area = (target / 60000.0) / (CD * Math.sqrt(2 * dp / DENSITY));
            double dia = 2.0 * Math.sqrt(area / Math.PI) * 1000.0;
            if (Double.isNaN(dia) || dia < drillMinMm) dia = drillMinMm;
            if (dia > drillMaxMm) dia = drillMaxMm;
            double snapped = Math.round(dia / drillStep) * drillStep;
            double actualArea = Math.PI * Math.pow(snapped / 1000.0 / 2.0, 2);
            double actualFlow = CD * actualArea * Math.sqrt(2 * dp / DENSITY) * 60000.0;
            total += actualFlow;
            specs.add(new HoleSpec(i + 1, pos, snapped, actualFlow));
        }
        double err = Math.abs(total - flowLpm) / flowLpm * 100.0;
        return new HoleLayout(specs, err);
    }
}
