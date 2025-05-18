package org.example.flowmod.utils;

/** Utility conversion methods. */
public final class UnitConv {
    private UnitConv() {}

    /** Convert US gallons per minute to litres per minute. */
    public static double gpmToLpm(double gpm) {
        return gpm * 3.785411784;
    }
}
