package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by htan on 09/07/2015.
 */
public class NotificationNextButtonListener extends BroadcastReceiver {

    private MusicService mMusicService;

    public NotificationNextButtonListener()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        IBinder binder = peekService(context, new Intent(context, MusicService.class));

        if (binder != null) {
            this.mMusicService = ((MusicService.MusicBinder) binder).getService();
        }

        if(this.mMusicService != null) {

            this.mMusicService.playNext();

        }

    }
}
