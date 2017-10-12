package com.getlosthere.peebuddy.models;

/**
 * Created by violetaria on 7/22/17.
 */

public class UserRequest {
    public String email;
    public String password;

    public UserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
