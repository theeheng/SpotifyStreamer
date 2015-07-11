package com.hengtan.nanodegreeapp.spotifystreamer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import java.util.ArrayList;


public class TopTenActivity extends ActionBarActivity implements RecyclerViewAdapter.OnItemClickListener {

    private TopTenTracksFragment mTopTenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);

        Window w = getWindow();

        if(Build.VERSION.SDK_INT >= 19) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else
        {
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        getSupportActionBar().hide();

        if(savedInstanceState == null)
        {
            Bundle arguments = new Bundle();
            arguments.putParcelable(TopTenTracksFragment.ARTIST_PARCELABLE, getIntent().getParcelableExtra(TopTenTracksFragment.ARTIST_PARCELABLE));

            this.mTopTenFragment = new TopTenTracksFragment();
            this.mTopTenFragment.setTwoPane(false);
            this.mTopTenFragment.setArguments(arguments);

            getFragmentManager().beginTransaction().add(R.id.artist_top_ten_container, mTopTenFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_ten, menu);
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
            startActivityForResult(i, MainActivity.RESULT_SETTINGS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArtistClick(ParcelableArtist artist, ImageView imgView) {
    }

    public void onTrackClick(ArrayList<ParcelableTrack> tracks, int trackIndex) {

        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerFragment.TRACKINDEX, trackIndex);
        intent.putParcelableArrayListExtra(PlayerFragment.TOPTENTRACKS_PARCELABLE, tracks);

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case MainActivity.RESULT_SETTINGS:
                mTopTenFragment.UpdateTopTenTracksOnPreferenceUpdate();
                break;

        }

    }
}
