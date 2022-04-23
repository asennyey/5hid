package com.asennyey.a5hid.api.objects.write;

public class User {
    public String username;
    public String password;

    @Override
    public String toString() {
        return "{" +
                "\"email\":\"" + username + '\"' +
                ",\"password\":\"" + password + '\"' +
                '}';
    }
}
