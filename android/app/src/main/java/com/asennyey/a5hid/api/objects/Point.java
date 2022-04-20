package com.asennyey.a5hid.api.objects;

public class Point {
    public long latitude;
    public long longitude;
    public Point(long latitude, long longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Point{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
