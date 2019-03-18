package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

public class Color extends GenerationComponentExecution {

    public static final int MAX_RGB = 256;
    public static final double MAX_HSL = 360d;

    public Color(GenerationDataExecution execution) {
        super(execution);
    }

    public String hexColor() {
        return String.format("#%06x", this.getGenerationTools().getRandomTools().number((int) Math.pow(MAX_RGB, 3)));
    }

    public String colorName() {
        return fetch("color.name");
    }

    public int singleRgbColor() {
        return this.getGenerationTools().getRandomTools().number(MAX_RGB);
    }

    public int[] rgbColor() {
        return new int[]{singleRgbColor(), singleRgbColor(), singleRgbColor()};
    }

    public double singleHslColor() {
        return this.getGenerationTools().getNumberTools().round(this.getGenerationTools().getRandomTools().range(0, MAX_HSL), 2);
    }

    public double alphaChannel() {
        return this.getGenerationTools().getRandomTools().randDouble();
    }

    public double[] hslColor() {
        return new double[]{singleHslColor(), singleHslColor(), singleHslColor()};
    }

    public double[] hslaColor() {
        return new double[]{singleHslColor(), singleHslColor(), singleHslColor(), alphaChannel()};
    }
}
