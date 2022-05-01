package com.asennyey.a5hid.api.objects.read;

public class User {
    public String name;
    public String firstName;
    public String lastName;
    public String email;
    public String id;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                "email='" + email + '\'' +
                "firstName='" + firstName + '\'' +
                "lastName='" + lastName + '\'' +
                '}';
    }
}
