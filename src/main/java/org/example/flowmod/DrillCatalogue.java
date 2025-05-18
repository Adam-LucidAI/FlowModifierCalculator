package org.example.flowmod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/** Loads available drill diameters from drills.json. */
public final class DrillCatalogue {
    /** Immutable list of available drill diameters in mm. */
    public static final List<Double> DRILLS;

    static {
        List<Double> drills = new ArrayList<>();
        try (var reader = new InputStreamReader(
                DrillCatalogue.class.getResourceAsStream("/drills.json"))) {
            if (reader != null) {
                drills = new Gson().fromJson(reader,
                        new TypeToken<List<Double>>(){}.getType());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        DRILLS = List.copyOf(drills);
    }

    private DrillCatalogue() { }
}
