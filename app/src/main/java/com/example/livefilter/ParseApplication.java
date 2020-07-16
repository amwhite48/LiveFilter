package com.example.livefilter;

import android.app.Application;

import com.example.livefilter.models.FilterPost;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models
        ParseObject.registerSubclass(FilterPost.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("livefilter") // should correspond to APP_ID env variable
                .clientKey("AlanaListensToDrillSometimes")  // set explicitly unless clientKey is explicitly configured on Parse server
                 .server("https://livefilter.herokuapp.com/parse/").build());
    }
}
