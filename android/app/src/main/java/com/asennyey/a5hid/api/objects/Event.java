package com.asennyey.a5hid.api.objects;

public class Event {
    public Point location;
    public String description;
    public User user;

    @Override
    public String toString() {
        return "Event{" +
                "location=" + location +
                ", description='" + description + '\'' +
                ", user=" + user +
                '}';
    }
}
