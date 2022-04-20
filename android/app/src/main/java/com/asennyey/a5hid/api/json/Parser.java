package com.asennyey.a5hid.api.json;

import android.util.JsonReader;

public interface Parser<T> {
    T parse(JsonReader reader);
}
