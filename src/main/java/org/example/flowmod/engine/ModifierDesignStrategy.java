package org.example.flowmod.engine;

import org.example.flowmod.FilterSpecs;
import org.example.flowmod.PipeSpecs;

public interface ModifierDesignStrategy {
    DesignResult optimise(PipeSpecs pipe,
                          FilterSpecs filter,
                          double drillMinMm,
                          double drillMaxMm,
                          int rows,
                          double Cd);
}
