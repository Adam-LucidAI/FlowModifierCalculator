package org.example.flowmod;

import org.example.flowmod.utils.CsvWriter;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvWriterTest {
    @Test
    void writesHeader() throws Exception {
        HoleLayout layout = new HoleLayout(List.of(new HoleSpec(1, 0.0, 4.0, 0.0)), 0.0);
        Path tmp = Files.createTempFile("layout", ".csv");
        CsvWriter.write(tmp, layout);
        List<String> lines = Files.readAllLines(tmp);
        assertFalse(lines.isEmpty());
        assertEquals("Row,Pos_mm,Dia_mm", lines.get(0));
    }
}
