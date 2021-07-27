package com.example.moodify;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("5Yg5uKX8KM7magqYtcFgWeY234kIfzcJwi4Y6ais")
                .clientKey("fzNp6QKfNP362G9hLrG9uUdivdEx5F0lNOW5Uws3")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}