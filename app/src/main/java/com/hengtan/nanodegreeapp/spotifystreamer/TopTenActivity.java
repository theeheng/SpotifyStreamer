package com.hengtan.nanodegreeapp.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class TopTenActivity extends AppCompatActivity implements TopTenTracksFragment.TopTenTracksFragmentCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);

        if(savedInstanceState == null)
        {
            Bundle arguments = new Bundle();
            arguments.putString(TopTenTracksFragment.ARTIST_ID, getIntent().getStringExtra(TopTenTracksFragment.ARTIST_ID));

            TopTenTracksFragment fragment = new TopTenTracksFragment();
            fragment.setArguments(arguments);

            getFragmentManager().beginTransaction().add(R.id.artist_top_ten_container, fragment).commit();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrackSelected(String trackId) {
        Toast.makeText(getApplicationContext(), "Track Id : " + trackId,
                Toast.LENGTH_SHORT).show();
    }
}
