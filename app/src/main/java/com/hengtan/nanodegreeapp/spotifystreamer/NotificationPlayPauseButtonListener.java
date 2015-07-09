package com.hengtan.nanodegreeapp.spotifystreamer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by htan on 09/07/2015.
 */
public class NotificationPlayPauseButtonListener extends BroadcastReceiver {

    private MusicService mMusicService;

    public NotificationPlayPauseButtonListener()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("notification", "play button clicked");
        Toast toastMessage = Toast.makeText(context,
                "play pause button clicked",
                Toast.LENGTH_LONG);
        toastMessage.show();

        IBinder binder = peekService(context, new Intent(context, MusicService.class));

        if (binder != null) {
            this.mMusicService = ((MusicService.MusicBinder) binder).getService();
        }

        if(this.mMusicService != null)
        {
            if (mMusicService.isPng())
                mMusicService.pausePlayer();
            else
                mMusicService.go();
        }
    }
}