package org.example.flowmod.utils;

import org.example.flowmod.HoleLayout;
import org.example.flowmod.HoleSpec;
import org.example.flowmod.engine.PipeSpecs;

/** Utility for producing simple SVG blueprints. */
public final class SvgWriter {
    private SvgWriter() {}

    /**
     * Convert the layout to an SVG string.
     * @param layout hole layout
     * @param pipe associated pipe specs
     * @return SVG markup string
     */
    public static String toSvg(HoleLayout layout, PipeSpecs pipe) {
        double width = pipe.modifierLengthMm();
        double height = Math.PI * pipe.innerDiameterMm();
        double half = height / 2.0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "<svg xmlns='http://www.w3.org/2000/svg' width='%.1fmm' height='%.1fmm' viewBox='0 0 %.1f %.1f'>",
                width, height, width, height));
        sb.append(String.format("<rect x='0' y='0' width='%.1f' height='%.1f' fill='none' stroke='black'/>",
                width, height));
        for (HoleSpec h : layout.holes()) {
            sb.append(String.format("<circle cx='%.1f' cy='%.1f' r='%.1f'/>",
                    h.positionMm(), half, h.diameterMm() / 2.0));
        }
        sb.append("</svg>");
        return sb.toString();
    }
}
