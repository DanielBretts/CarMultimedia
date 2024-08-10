package com.example.carmultimedia;

public class RadioStation {
    private String imageUrl;
    private String streamLink;
    private String stationName;

    public RadioStation(String imageUrl, String streamLink, String stationName) {
        this.imageUrl = imageUrl;
        this.streamLink = streamLink;
        this.stationName = stationName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStreamLink() {
        return streamLink;
    }

    public String getStationName() {
        return stationName;
    }
}
