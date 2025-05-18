package org.example.flowmod;

import org.example.flowmod.engine.FilterSpecs;
import org.example.flowmod.engine.PipeSpecs;

public record Settings(
        PipeSpecs pipe,
        FilterSpecs filter,
        double drillMinMm,
        double drillMaxMm,
        int rows,
        double Cd) {}
