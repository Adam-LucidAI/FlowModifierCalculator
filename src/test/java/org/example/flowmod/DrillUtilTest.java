package org.example.flowmod;

import org.example.flowmod.engine.PerforatedCoreOptimizer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DrillUtilTest {
    @Test
    void emptyList_throws() throws Exception {
        Method m = PerforatedCoreOptimizer.class
                .getDeclaredMethod("nearest", List.class, double.class);
        m.setAccessible(true);
        assertThrows(IllegalArgumentException.class,
                () -> m.invoke(null, List.of(), 5.0));
    }
}
