package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by htan on 09/07/2015.
 */
public class NotificationStopButtonListener extends BroadcastReceiver {

    private MusicService mMusicService;

    public NotificationStopButtonListener()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("notification", "stop button clicked");
        Toast toastMessage = Toast.makeText(context,
                "stop button clicked",
                Toast.LENGTH_LONG);
        toastMessage.show();

        IBinder binder = peekService(context, new Intent(context, MusicService.class));

        if (binder != null) {
            this.mMusicService = ((MusicService.MusicBinder) binder).getService();
        }

        if(this.mMusicService != null) {

            this.mMusicService.stopPlayer();

            context.stopService(new Intent(context, MusicService.class));
            
            // Create Notification Manager
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(MusicService.NOTIFICATION_SERVICE);
            // Build Notification with Notification Manager
            notificationmanager.cancel(MusicService.NOTIFY_ID);

        }

    }
}