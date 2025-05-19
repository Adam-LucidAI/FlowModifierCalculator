package org.example.flowmod.utils;

import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

/** Utility for exporting hole layouts to CSV. */
public final class CsvWriter {
    private CsvWriter() {}

    /**
     * Write the given layout to a CSV file.
     * @param path destination file
     * @param layout hole layout to export
     */
    public static void write(Path path, HoleLayout layout) throws IOException {
        try (PrintWriter pw = new PrintWriter(path.toFile())) {
            pw.println("Row,Pos_mm,Dia_mm");
            for (HoleSpec h : layout.holes()) {
                pw.printf("%d,%.1f,%.1f%n", h.index(), h.positionMm(), h.diameterMm());
            }
        }
    }
}
