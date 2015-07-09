package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by htan on 08/07/2015.
 */
public class Application extends android.app.Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

                final ActivityManager activityManager = (ActivityManager) com.hengtan.nanodegreeapp.spotifystreamer.Application.getContext().getSystemService(Context.ACTIVITY_SERVICE);
                final List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                final String packageName = context.getApplicationContext().getPackageName();

                boolean appStillRunning =  false;

                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                    if (runningAppProcessInfo.processName.equals(packageName)) {
                        appStillRunning = true;
                        }
                }

                if (!appStillRunning) {
                    // Create Notification Manager
                    NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    // Build Notification with Notification Manager
                    notificationmanager.cancel(MusicService.NOTIFY_ID);

                }
            }
        });


    }


    public static Context getContext(){
        return context;
    }
}