/**
 * @author Aramis Sennyey
 * This class is the specific format that the API expects when writing to JSON.
 */
package com.asennyey.a5hid.api.objects.write;

import com.google.android.gms.maps.model.LatLng;

public class Event {
    public String description;
    public LatLng location;

    @Override
    public String toString() {
        return "{" +
                "\"description\":\"" + description + '\"' +
                ",\"location\":\"POINT(" + location.longitude + " " + location.latitude + ")\"" +
                '}';
    }
}
