package org.example.flowmod;

import java.util.List;

public record HoleLayout(List<HoleSpec> holes, double worstCaseErrorPct) {}
