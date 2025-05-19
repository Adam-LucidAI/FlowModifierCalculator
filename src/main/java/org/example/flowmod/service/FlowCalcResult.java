package org.example.flowmod.service;

import org.example.flowmod.engine.DesignResult;
import org.example.flowmod.engine.OptimizationResult;

/** Summary of a flow calculation. */
public record FlowCalcResult(
        DesignResult design,
        OptimizationResult optimisation,
        double reynolds,
        double sheetWidthMm,
        double minHoleMm,
        double maxHoleMm,
        double pitchMm,
        double minWebMm,
        boolean spacingOk) {}
