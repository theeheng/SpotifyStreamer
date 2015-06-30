package com.hengtan.nanodegreeapp.spotifystreamer;


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
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    private boolean mTwoPane = false;
    static final String TRACKINDEX = "TRACKINDEX";
    static final String TOPTENTRACKS_PARCELABLE = "TOPTENTRACKSPARCELABLE";

    @InjectView(R.id.prev)
    protected ImageView mSkipPrev;

    @InjectView(R.id.next)
    protected ImageView mSkipNext;

    @InjectView(R.id.imageView1)
    protected ImageView mPlayPause;

    @InjectView(R.id.startText)
    protected TextView mStart;

    @InjectView(R.id.endText)
    protected TextView mEnd;

    @InjectView(R.id.seekBar1)
    protected SeekBar mSeekbar;

    @InjectView(R.id.line1)
    protected TextView mLine1;

    @InjectView(R.id.line2)
    protected TextView mLine2;

    @InjectView(R.id.line3)
    protected TextView mLine3;

    @InjectView(R.id.progressBar1)
    protected ProgressBar mLoading;

    @InjectView(R.id.controllers)
    protected View mControllers;

    protected Drawable mPauseDrawable;
    protected Drawable mPlayDrawable;

    @InjectView(R.id.background_image)
    protected ImageView mBackgroundImage;

    private static final String TAG = PlayerFragment.class.getSimpleName();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private final Handler mHandler = new Handler();
    private MediaBrowser mMediaBrowser;
    private PlaybackState mLastPlaybackState;

    private ArrayList<ParcelableTrack> mTrackList;
    private int mTrackIndex;
    private String TRACK_KEY = "track_list";

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    /*
    private final MediaController.Callback mCallback = new MediaController.Callback() {

        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackState state) {
            Log.d(TAG, "onPlaybackstate changed : " + state);
            //updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            if (metadata != null) {
                //updateMediaDescription(metadata.getDescription());
                //updateDuration(metadata);
            }
        }
    };
   */

    public void setTwoPane(boolean twoPane) {
        this.mTwoPane = twoPane;
    }

    public PlayerFragment() {
        // Required empty public constructor
    }

    /*private final MediaBrowser.ConnectionCallback mConnectionCallback =
            new MediaBrowser.ConnectionCallback() {
                @Override
                public void onConnected() {
                    Log.d(TAG, "onConnected");
                    connectToSession(mMediaBrowser.getSessionToken());
                }
            };
*/

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list

            if(mTrackList != null) {
                musicSrv.setList(mTrackList);
                musicSrv.playSong(mTrackIndex);
            }
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, view);

        mPauseDrawable = getActivity().getResources().getDrawable(R.mipmap.uamp_ic_pause_white_48dp);
        mPlayDrawable = getActivity().getResources().getDrawable(R.mipmap.uamp_ic_play_arrow_white_48dp);

        mControllers.setVisibility(VISIBLE);
        mLoading.setVisibility(VISIBLE);
        mPlayPause.setVisibility(VISIBLE);
        mPlayPause.setImageDrawable(mPlayDrawable);


        if (savedInstanceState != null && savedInstanceState.containsKey(TRACK_KEY)) {
            mTrackList = savedInstanceState.getParcelableArrayList(TRACK_KEY);
        }
        else {

            Bundle arguments = getArguments();

            if(arguments != null)
            {
                //ProgressBarHelper.ShowProgressBar(progressBarHolder);
                mTrackList = arguments.getParcelable(PlayerFragment.TOPTENTRACKS_PARCELABLE);
                mTrackIndex = arguments.getInt(PlayerFragment.TRACKINDEX);

                Glide.with(this).load(mTrackList.get(mTrackIndex).getPlaybackImage()).into(mBackgroundImage);

            }

        }


        //mMediaBrowser = new MediaBrowser(this, new ComponentName(this, MusicService.class), mConnectionCallback, null);

        return view;
    }

    @OnClick(R.id.next)
    public void NextClicked(View view) {
        //MediaController.TransportControls controls = getActivity().getMediaController().getTransportControls();
        //controls.skipToNext();
    }

    @OnClick(R.id.prev)
    public void PreviousClicked(View view) {
        //MediaController.TransportControls controls = getActivity().getMediaController().getTransportControls();
        //controls.skipToPrevious();
    }

    @OnClick(R.id.imageView1)
    public void PlayPauseClicked(View view) {
        /*PlaybackState state = getActivity().getMediaController().getPlaybackState();
        if (state != null) {
            MediaController.TransportControls controls = getActivity().getMediaController().getTransportControls();
            switch (state.getState()) {
                case PlaybackState.STATE_PLAYING: // fall through
                case PlaybackState.STATE_BUFFERING:
                    controls.pause();
                    //stopSeekbarUpdate();
                    break;
                case PlaybackState.STATE_PAUSED:
                case PlaybackState.STATE_STOPPED:
                    controls.play();
                    //scheduleSeekbarUpdate();
                    break;
                default:
                    Log.d(TAG, "onClick with state : "+state.getState());
            }
        }*/
    }

    /*private void connectToSession(MediaSession.Token token) {
        MediaController mediaController = new MediaController(getActivity(), token);

        if (mediaController.getMetadata() == null) {
            finish();
            return;
        }

        getActivity().setMediaController(mediaController);
        mediaController.registerCallback(mCallback);
        PlaybackState state = mediaController.getPlaybackState();


        updatePlaybackState(state);

        MediaMetadata metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata.getDescription());
            updateDuration(metadata);
        }
        updateProgress();
        if (state != null && (state.getState() == PlaybackState.STATE_PLAYING ||
                state.getState() == PlaybackState.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }

    }*/

    private void updateProgress() {

    }
}
