package com.hengtan.nanodegreeapp.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String TOPTENFRAGMENT_TAG = "TTFTAG";
    public static final int RESULT_SETTINGS = 1;

    private boolean mTwoPane;

    private TopTenTracksFragment mTopTenFragment;
    private SearchArtistFragment mSearchArtistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.artist_top_ten_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {

                mTopTenFragment = new TopTenTracksFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.artist_top_ten_container, mTopTenFragment , TOPTENFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        mSearchArtistFragment = ((SearchArtistFragment) getFragmentManager().findFragmentById(R.id.fragment_search_artist));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void onArtistClick(ParcelableArtist artist) {
        if (mTwoPane) {
            this.mTopTenFragment.UpdateTopTenTracks(artist);
        } else {
            Intent intent = new Intent(this, TopTenActivity.class)
                    .putExtra(TopTenTracksFragment.ARTIST_PARCELABLE, artist);
            startActivity(intent);
        }
    }

    public void onTrackClick(ParcelableTrack track) {
        Toast.makeText(getApplicationContext(), "Track Id : " + track.id,
                Toast.LENGTH_SHORT).show();
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

}
