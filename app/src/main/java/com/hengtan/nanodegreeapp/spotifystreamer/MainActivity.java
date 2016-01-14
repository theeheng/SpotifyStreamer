package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
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
    private MenuItem mPlayingNow;
    private MenuItem mShareTrack;
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
        mShareTrack = menu.findItem(R.id.menu_item_share);
        mPlayNowAnimationDrawable = (AnimationDrawable) mPlayingNow.getIcon();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                return true;
            case R.id.action_playing_now :
                if(!mTwoPane) {
                    Intent intent = new Intent(this, PlayerActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(this, PlayerDialogActivity.class);
                    startActivity(intent);
                }

                return true;

            case R.id.menu_item_share :

                String trackUrl = Application.getCurrentTraclUrl();

                if(trackUrl != null && (!trackUrl.isEmpty()))
                {
                    ShareTrack(trackUrl);
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

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && (!mTwoPane)) {
                imgView.setTransitionName("photo_thumbnail");
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
            Intent intent = new Intent(this, PlayerDialogActivity.class);
            intent.putExtra(PlayerFragment.TRACKINDEX, trackIndex);
            intent.putParcelableArrayListExtra(PlayerFragment.TOPTENTRACKS_PARCELABLE, tracks);

            startActivity(intent);
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
            mShareTrack.setVisible(true);
            mPlayNowAnimationDrawable.start();
        }
        else
        {
            mPlayingNow.setVisible(false);
            mShareTrack.setVisible(false);
            mPlayNowAnimationDrawable.stop();
        }
    }

    // Call to update the share intent
    private void ShareTrack(String currentTrackUrl) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, currentTrackUrl);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

}
