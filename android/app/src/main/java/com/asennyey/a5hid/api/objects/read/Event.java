/**
 * @author Aramis Sennyey
 * This class is a record of an API Event.
 */
package com.asennyey.a5hid.api.objects.read;

import com.google.android.gms.maps.model.LatLng;

public class Event {
    public LatLng location;
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
