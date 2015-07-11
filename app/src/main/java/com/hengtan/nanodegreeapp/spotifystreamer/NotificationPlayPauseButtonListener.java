package com.hengtan.nanodegreeapp.spotifystreamer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

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