package org.example.flowmod.engine;

import org.example.flowmod.HoleLayout;

/** Aggregates the results of a modifier design. */
public record DesignResult(PipeSpecs pipe,
                           HoleLayout holeLayout,
                           VaneLayout vaneLayout,
                           double worstCaseErrorPct) {}
