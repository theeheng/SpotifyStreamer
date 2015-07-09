package com.hengtan.nanodegreeapp.spotifystreamer;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;



import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.widget.MediaController.MediaPlayerControl;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends DialogFragment implements MediaPlayerControl {

    private boolean mTwoPane = false;
    static final String TRACKINDEX = "TRACKINDEX";
    static final String TOPTENTRACKS_PARCELABLE = "TOPTENTRACKSPARCELABLE";

    @InjectView(R.id.line1)
    protected TextView mLine1;

    @InjectView(R.id.line2)
    protected TextView mLine2;

    @InjectView(R.id.line3)
    protected TextView mLine3;

    protected Drawable mPauseDrawable;
    protected Drawable mPlayDrawable;

    @InjectView(R.id.background_image)
    protected ImageView mBackgroundImage;

    private ArrayList<ParcelableTrack> mTrackList;
    private int mTrackIndex;
    private String TRACK_KEY = "track_list";
    private View fragmentView;

    public void setTwoPane(boolean twoPane) {
        this.mTwoPane = twoPane;
    }

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    private MusicController controller;

    //activity and playback pause flags
    private boolean paused=false, playbackPaused=false;


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list

            if(mBackgroundImage != null)
            {
                musicSrv.setPlayerFragmentBackgroundImage(mBackgroundImage);
            }

            if(controller != null)
            {
                musicSrv.setMusicController(controller);
            }

            if(mLine1 != null && mLine2 != null && mLine3 != null)
            {
                musicSrv.setTrackDescriptionView(mLine1, mLine2, mLine3);
            }

            if(mTrackList != null) {
                musicSrv.setSongs(mTrackList);
                songPicked();
            }
            else
            {
                musicSrv.UpdatePlayerFragmentView();
                fragmentView.post(new Runnable() {

                    @Override
                    public void run() {
                        controller.show(5000);
                    }
                });
            }
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(playIntent==null){
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }

    }

    @Override
    public void onStart()
    {
        super.onStart();

        if(mTwoPane) {
            // safety check
            if (getDialog() == null)
                return;

            int dialogWidth = getResources().getDimensionPixelSize(R.dimen.popup_width); // specify a value here
            int dialogHeight = getResources().getDimensionPixelSize(R.dimen.popup_width); // specify a value here

            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        }
        // ... other stuff you want to do in your onStart() method
    }

    @Override
    public void onDestroy() {
        //getActivity().stopService(playIntent);
        //musicSrv=null;
        //getActivity().unbindService(musicConnection);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, fragmentView);

        mPauseDrawable = getActivity().getResources().getDrawable(R.mipmap.uamp_ic_pause_white_48dp);
        mPlayDrawable = getActivity().getResources().getDrawable(R.mipmap.uamp_ic_play_arrow_white_48dp);


        if (savedInstanceState != null && savedInstanceState.containsKey(TRACK_KEY)) {
            mTrackList = savedInstanceState.getParcelableArrayList(TRACK_KEY);
        }
        else {

            Bundle arguments = getArguments();

            if(arguments != null)
            {
                mTrackList = arguments.getParcelableArrayList(PlayerFragment.TOPTENTRACKS_PARCELABLE);
                mTrackIndex = arguments.getInt(PlayerFragment.TRACKINDEX);
            }

        }

        //setup controller
        setController();

        return fragmentView;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    /*@Override
    public void onPause(){
        super.onPause();
        paused=true;
    }


    @Override
    public void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }
    }
*/

    /*@Override
    public void onStop() {
        controller.hide();
        super.onStop();
    }*/


    //set the controller up
    private void setController(){
        controller = new MusicController(contex, mTwoPane);
        //set previous and next button listeners
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        //set and show
        controller.setMediaPlayer(this);
        controller.setAnchorView(mBackgroundImage);
        controller.setEnabled(true);

    }

    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }

    }

    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }

    }

    //user song select
    public void songPicked(){
        musicSrv.setSong(mTrackIndex);
        musicSrv.playSong();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
    }

}
