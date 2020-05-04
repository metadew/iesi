package io.metadew.iesi.data.generation.tools;

public class GenerationTools {

    private MapTools mapTools;
    private NumberTools numberTools;
    private RandomTools randomTools;
    private StringTools stringTools;

    public GenerationTools() {
        this.setMapTools(new MapTools());
        this.setNumberTools(new NumberTools());
        this.setRandomTools(new RandomTools());
        this.setStringTools(new StringTools());
    }

    // Getters and Setters
    public MapTools getMapTools() {
        return mapTools;
    }

    public void setMapTools(MapTools mapTools) {
        this.mapTools = mapTools;
    }

    public NumberTools getNumberTools() {
        return numberTools;
    }

    public void setNumberTools(NumberTools numberTools) {
        this.numberTools = numberTools;
    }

    public RandomTools getRandomTools() {
        return randomTools;
    }

    public void setRandomTools(RandomTools randomTools) {
        this.randomTools = randomTools;
    }

    public StringTools getStringTools() {
        return stringTools;
    }

    public void setStringTools(StringTools stringTools) {
        this.stringTools = stringTools;
    }


}