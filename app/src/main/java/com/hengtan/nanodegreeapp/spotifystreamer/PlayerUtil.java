package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.util.List;

/**
 * Created by htan on 08/07/2015.
 */
public class PlayerUtil {
    private static String LOG_TAG = PlayerUtil.class.getName();

    public static boolean isMusicServiceRunning(){
        final ActivityManager activityManager = (ActivityManager) com.hengtan.nanodegreeapp.spotifystreamer.Application.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        final String musicServiceName = MusicService.class.getName();

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(musicServiceName)){
                return true;
            }
        }
        return false;
    }

}
