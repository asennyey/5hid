/**
 * @author Aramis Sennyey
 * This class is a record of an API LeaderBoard User, which is different from a regular User.
 */

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
