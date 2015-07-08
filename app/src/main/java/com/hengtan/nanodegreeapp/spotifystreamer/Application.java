package com.hengtan.nanodegreeapp.spotifystreamer;

import android.content.Context;

/**
 * Created by htan on 08/07/2015.
 */
public class Application extends android.app.Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext(){
        return context;
    }
}