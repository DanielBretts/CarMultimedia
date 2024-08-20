package com.example.carmultimedia;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("current_observation")
    private CurrentObservation currentObservation;

    public CurrentObservation getCurrentObservation() {
        return currentObservation;
    }

    public void setCurrentObservation(CurrentObservation currentObservation) {
        this.currentObservation = currentObservation;
    }

    public static class CurrentObservation {
        @SerializedName("condition")
        private Condition condition;

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }
    }

    public static class Condition {
        @SerializedName("temperature")
        private int temperature;

        public int getTemperature() {
            return temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }
    }
}
