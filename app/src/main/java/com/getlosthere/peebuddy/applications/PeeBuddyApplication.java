package com.getlosthere.peebuddy.applications;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by violetaria on 7/16/17.
 */

public class PeeBuddyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}