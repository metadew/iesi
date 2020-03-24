package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

public class SlackEmoji extends GenerationComponentExecution {

    public SlackEmoji(GenerationDataExecution execution) {
        super(execution);
    }

    public String people() {
        return fetch("slack_emoji.people");
    }

    public String nature() {
        return fetch("slack_emoji.nature");
    }

    public String foodAndDrink() {
        return fetch("slack_emoji.food_and_drink");
    }

    public String celebration() {
        return fetch("slack_emoji.celebration");
    }

    public String activity() {
        return fetch("slack_emoji.activity");
    }

    public String travelAndPlaces() {
        return fetch("slack_emoji.travel_and_places");
    }

    public String objectsAndSymbols() {
        return fetch("slack_emoji.objects_and_symbols");
    }

    public String custom() {
        return fetch("slack_emoji.custom");
    }

    public String emoji() {
        return parse(fetch("slack_emoji.emoji"));
    }
}
