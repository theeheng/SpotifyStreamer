package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.ActivityManager;
import android.content.Context;
import java.util.List;

/**
 * Created by htan on 08/07/2015.
 */
public class Application extends android.app.Application {

    private static Context context;
    public static boolean isPlayingNow;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        isPlayingNow = false;
    }

    public static boolean getIsPlayingNow()
    {
        return isPlayingNow;
    }

    public static void setIsPlayingNow(boolean playingNow)
    {
        isPlayingNow = playingNow;
    }

    public static Context getContext(){
        return context;
    }

    public static boolean isLastActivity(String activityClassName){
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = activityManager.getRunningTasks(10);

        if(taskList.get(0).numActivities == 1 &&
                taskList.get(0).topActivity.getClassName().equals(activityClassName)) {

            return true;
        }
        else
        {
            return false;
        }
    }
}