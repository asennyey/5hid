package com.asennyey.a5hid.api.objects.read;

public class Jwt {
    public String accessToken;
    public String refreshToken;

    @Override
    public String toString() {
        return "Jwt{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
