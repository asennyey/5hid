/**
 * @author Aramis Sennyey
 * This class is a record of an API User.
 */

package com.asennyey.a5hid.api.objects.read;

public class User {
    public String name;
    public boolean isFriend;
    public String email;
    public int id;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                "email='" + email + '\'' +
                "is_friend='" + isFriend + '\'' +
                '}';
    }
}
