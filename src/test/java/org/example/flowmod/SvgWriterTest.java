package org.example.flowmod;

import org.example.flowmod.engine.PipeSpecs;
import org.example.flowmod.utils.SvgWriter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class SvgWriterTest {
    @Test
    void svgContainsCircles() {
        HoleLayout layout = new HoleLayout(List.of(
                new HoleSpec(1, 10.0, 4.0, 0.0),
                new HoleSpec(2, 20.0, 5.0, 0.0)
        ), 0.0);
        PipeSpecs pipe = new PipeSpecs(20, 10, 40);
        String svg = SvgWriter.toSvg(layout, pipe);
        assertTrue(svg.startsWith("<svg"));
        int count = svg.split("<circle", -1).length - 1;
        assertEquals(2, count);
    }
}
