

package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<ParcelableTrack> songs;
    //current position
    private int songPosn;

    private final IBinder musicBind = new MusicBinder();

    //notification id
    public static final int NOTIFY_ID=1;

    //title of current song
    private String songTitle="";

    //shuffle flag and random
    private boolean shuffle=false;
    private Random rand;

    private ImageView mPlayerFragmentBackgroundImage;

    private MusicController mMusicController;

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        //player.stop();
        //player.release();
        return false;
    }

    public void onCreate(){
        //create the service
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();

        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setSongs(ArrayList<ParcelableTrack> theSongs){
        songs=theSongs;
    }

    public ArrayList<ParcelableTrack> getSongs()
    {
        return this.songs;
    }

    public void setPlayerFragmentBackgroundImage(ImageView backgroundImageView)
    {
        this.mPlayerFragmentBackgroundImage = backgroundImageView;
    }

    public void setMusicController(MusicController mc)
    {
        this.mMusicController = mc;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong(){
        //play a song
        player.reset();

        //get song
        ParcelableTrack playSong = songs.get(songPosn);
        //get id
        //long currSong = playSong.getID();
        //set uri
        Uri trackUri = Uri.parse(playSong.preview_url);

        try{
            player.setDataSource(getApplicationContext(), trackUri);

            if(playSong.getPlaybackImage() != null && this.mPlayerFragmentBackgroundImage != null)
            {
                Glide.with(this).load(playSong.getPlaybackImage()).fitCenter().into(this.mPlayerFragmentBackgroundImage);
            }
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        if(player.getCurrentPosition()>0){

            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        player.start();

        if(mMusicController != null) {
            mMusicController.show(0);
        }

        //notification
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        ParcelableTrack song = songs.get(songPosn);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(song.name)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(song.getArtistName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // build a complex notification, with buttons and such
            //
            builder = builder.setContent(getPlayerNotificationView(song.getThumbnailImage(), song.getalbumName(), song.name));
        }

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(NOTIFY_ID, builder.build());
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    //playback methods
    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    //skip to previous track
    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn=songs.size()-1;
        playSong();
    }

    //skip to next
    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn>=songs.size()) songPosn=0;
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    //toggle shuffle
    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    public void UpdatePlayerFragmentBackgoundImage()
    {
        if(this.songs != null && this.songs.size() > 0 && this.songs.size() > this.songPosn)
        {
            ParcelableTrack playSong = songs.get(songPosn);

            if(playSong.getPlaybackImage() != null && this.mPlayerFragmentBackgroundImage != null)
            {
                Glide.with(this).load(playSong.getPlaybackImage()).fitCenter().into(this.mPlayerFragmentBackgroundImage);
            }

            mMusicController.setVisibility(View.VISIBLE);

        }
    }

    private RemoteViews getPlayerNotificationView(String trackThumbnail, String albumTitle, String trackTitle) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.service_player_notification);

        // Locate and set the Image into customnotificationtext.xml ImageViews
        //Glide.with(this).load(trackThumbnail).fitCenter().into();

        try
        {
            Bitmap thumbnailBitmap = Glide.with(this).load(trackThumbnail).asBitmap().into(-1, -1).get();
            notificationView.setImageViewBitmap(R.id.notification_track_thumbnail, thumbnailBitmap);
        }
        catch(Exception ex)
        {
            notificationView.setImageViewResource(R.id.notification_track_thumbnail, R.mipmap.ic_launcher);
        }

        // Locate and set the Text into customnotificationtext.xml TextViews
        notificationView.setTextViewText(R.id.title, albumTitle);
        notificationView.setTextViewText(R.id.text, trackTitle);

        //this is the intent that is supposed to be called when the button is clicked
        Intent notificationPlayIntent = new Intent(this, NotificationPlayPauseButtonListener.class);
        PendingIntent pendingNotificationPlayIntent = PendingIntent.getBroadcast(this, 0, notificationPlayIntent, 0);

        Intent notificationStopIntent = new Intent(this, NotificationStopButtonListener.class);
        PendingIntent pendingNotificationStopIntent = PendingIntent.getBroadcast(this, 0, notificationStopIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_play_button, pendingNotificationPlayIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_stop_button, pendingNotificationStopIntent);

        return notificationView;
    }

    public static class NotificationPlayPauseButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("notification", "play button clicked");
            Toast toastMessage = Toast.makeText(context,
                    "play pause button clicked",
                    Toast.LENGTH_LONG);
            toastMessage.show();
        }
    }

    public static class NotificationStopButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("notification", "stop button clicked");
            Toast toastMessage = Toast.makeText(context,
                    "stop button clicked",
                    Toast.LENGTH_LONG);
            toastMessage.show();

            // Create Notification Manager
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            // Build Notification with Notification Manager
            notificationmanager.cancel(NOTIFY_ID);

            //context.stopService(new Intent(context, MusicService.class));

        }
    }
}
