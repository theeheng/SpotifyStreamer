package com.hengtan.nanodegreeapp.spotifystreamer;

import android.content.Context;
import android.content.Intent;

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

    @Override
    public void onTerminate(){
        super.onTerminate();

        context.stopService(new Intent(context, MusicService.class));
    }
    public static Context getContext(){
        return context;
    }
}