package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String TOPTENFRAGMENT_TAG = "TTFTAG";
    public static final String PLAYERFRAGMENT_TAG = "PFTAG";
    public static final int RESULT_SETTINGS = 1;

    private boolean mTwoPane;
    private TopTenTracksFragment mTopTenFragment;
    private PlayerFragment mPlayerFragment;
    private MenuItem mPlayingNow;
    private AnimationDrawable mPlayNowAnimationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();

        if (findViewById(R.id.artist_top_ten_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {

                mTopTenFragment = new TopTenTracksFragment();
                mTopTenFragment.setTwoPane(mTwoPane);
                fragmentManager.beginTransaction()
                        .replace(R.id.artist_top_ten_container, mTopTenFragment , TOPTENFRAGMENT_TAG)
                        .commit();
            }else
            {
                mTopTenFragment = (TopTenTracksFragment) fragmentManager.findFragmentByTag(TOPTENFRAGMENT_TAG);
                mTopTenFragment.setTwoPane(mTwoPane);
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mPlayingNow = menu.findItem(R.id.action_playing_now);

        mPlayNowAnimationDrawable = (AnimationDrawable) mPlayingNow.getIcon();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
            return true;
        }

        if (id == R.id.action_playing_now) {

            if(!mTwoPane) {
                Intent intent = new Intent(this, PlayerActivity.class);
                startActivity(intent);
            }
            else
            {
                mPlayerFragment.show(getFragmentManager(), PLAYERFRAGMENT_TAG);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArtistClick(ParcelableArtist artist, ImageView imgView) {
        if (mTwoPane) {
            this.mTopTenFragment.UpdateTopTenTracks(artist);
        } else {
            Intent intent = new Intent(this, TopTenActivity.class).putExtra(TopTenTracksFragment.ARTIST_PARCELABLE, artist);

            if(Build.VERSION.SDK_INT >= 21) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, imgView, "photo_thumbnail");
                startActivity(intent, options.toBundle());
            }
            else
            {
                startActivity(intent);
            }

        }
    }

    public void onTrackClick(ArrayList<ParcelableTrack> tracks, int trackIndex) {

        if(mTwoPane)
        {
            Bundle arguments = new Bundle();

            arguments.putInt(PlayerFragment.TRACKINDEX, trackIndex);
            arguments.putParcelableArrayList(PlayerFragment.TOPTENTRACKS_PARCELABLE, tracks);

            this.mPlayerFragment = new PlayerFragment();
            this.mPlayerFragment.setTwoPane(true);
            this.mPlayerFragment.setArguments(arguments);

            mPlayerFragment.show(getFragmentManager(), PLAYERFRAGMENT_TAG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (mTwoPane) {
            switch (requestCode) {
                case MainActivity.RESULT_SETTINGS:
                    mTopTenFragment.UpdateTopTenTracksOnPreferenceUpdate();
                    break;

            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        boolean isPlayingNow = Application.getIsPlayingNow();

        if(hasFocus && isPlayingNow && mPlayingNow != null)
        {
            mPlayingNow.setVisible(true);
            mPlayNowAnimationDrawable.start();
        }
        else
        {
            mPlayingNow.setVisible(false);
            mPlayNowAnimationDrawable.stop();
        }
    }


}
