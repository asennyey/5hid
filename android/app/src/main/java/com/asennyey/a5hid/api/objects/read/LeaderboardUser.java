package com.asennyey.a5hid.api.objects.read;

public class LeaderboardUser {
    public String name;
    public int score;

    @Override
    public String toString() {
        return "LeaderboardUser{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
