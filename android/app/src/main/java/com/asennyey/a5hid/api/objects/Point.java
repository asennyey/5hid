package com.asennyey.a5hid.api.objects;

public class Point {
    public double latitude;
    public double longitude;
    public Point(double latitude, double longitude){
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
