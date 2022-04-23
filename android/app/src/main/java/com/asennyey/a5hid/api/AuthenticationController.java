package com.asennyey.a5hid.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.asennyey.a5hid.api.objects.read.Jwt;

public class AuthenticationController {
    private static AuthenticationController instance;
    private Jwt cache;
    private final Context context;

    private AuthenticationController(Context context){
        this.context = context;
    }

    public Jwt getJwt(){
        if(cache != null) return cache;
        SharedPreferences prefs = getSharedPreferences();
        Jwt retrievedJwt = new Jwt();
        retrievedJwt.accessToken = prefs.getString("accessToken", null);
        retrievedJwt.refreshToken = prefs.getString("refreshToken", null);
        this.cache = retrievedJwt;
        return retrievedJwt;
    }

    public void setJwt(Jwt jwt){
        this.cache = jwt;
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString("accessToken", jwt.accessToken);
        editor.putString("refreshToken", jwt.refreshToken);
        editor.apply();
    }

    public void logout(){
        this.setJwt(new Jwt());
    }

    private SharedPreferences getSharedPreferences(){
        return this.context.getSharedPreferences("auth-prefs", 0);
    }

    public static AuthenticationController getInstance(Context context){
        if(AuthenticationController.instance == null){
            AuthenticationController.instance = new AuthenticationController(context);
        }
        return AuthenticationController.instance;
    }
}
