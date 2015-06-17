package com.hengtan.nanodegreeapp.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements SearchArtistFragment.SearchArtistFragmentCallback, TopTenTracksFragment.TopTenTracksFragmentCallback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String TOPTENFRAGMENT_TAG = "TTFTAG";
    private static final int RESULT_SETTINGS = 1;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.artist_top_ten_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.artist_top_ten_container, new TopTenTracksFragment(), TOPTENFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        SearchArtistFragment searchArtistFragment = ((SearchArtistFragment) getFragmentManager().findFragmentById(R.id.fragment_search_artist));


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

    @Override
    public void onArtistSelected(String artistId) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString(TopTenTracksFragment.ARTIST_ID, artistId);

            TopTenTracksFragment fragment = new TopTenTracksFragment();
            fragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .replace(R.id.artist_top_ten_container, fragment, TOPTENFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, TopTenActivity.class)
                    .putExtra(TopTenTracksFragment.ARTIST_ID, artistId);
            startActivity(intent);
        }
    }

    @Override
    public void onTrackSelected(String artistId) {
    }
}
