package org.example.flowmod.engine;

import org.example.flowmod.HoleLayout;

public record DesignResult(HoleLayout holeLayout, VaneLayout vaneLayout, double worstCaseErrorPct) {}
