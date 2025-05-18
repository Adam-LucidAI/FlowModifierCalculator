package org.example.flowmod;

public record Settings(
        PipeSpecs pipe,
        FilterSpecs filter,
        double drillMinMm,
        double drillMaxMm,
        int rows,
        double Cd) {}
