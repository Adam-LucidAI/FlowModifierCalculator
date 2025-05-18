package org.example.flowmod.engine;

import org.example.flowmod.engine.FilterSpecs;
import org.example.flowmod.engine.PipeSpecs;

public interface ModifierDesignStrategy {
    DesignResult optimise(PipeSpecs pipe,
                          FilterSpecs filter,
                          double drillMinMm,
                          double drillMaxMm,
                          int rows,
                          double Cd);
}
