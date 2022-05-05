/**
 * @author Aramis Sennyey
 * This class is an interface for methods that will parse data to implement.
 */
package com.asennyey.a5hid.api.json;

import android.util.JsonReader;

public interface Parser<T> {
    T parse(JsonReader reader);
}
