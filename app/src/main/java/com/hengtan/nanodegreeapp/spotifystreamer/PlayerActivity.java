package com.hengtan.nanodegreeapp.spotifystreamer;

import android.os.Build;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;


public class PlayerActivity extends ActionBarActivity {

    private PlayerFragment mPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if(Build.VERSION.SDK_INT >= 19) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getSupportActionBar().hide();


        if(savedInstanceState == null)
        {
            Bundle arguments = new Bundle();

//            int test = getIntent().getExtras().getInt(PlayerFragment.TRACKINDEX);
                    arguments.putInt(PlayerFragment.TRACKINDEX, 0);

            Bundle extras =getIntent().getExtras();
            Bundle extra2 = extras.getBundle("test123");
ArrayList<ParcelableTrack> test = extra2.getParcelable(PlayerFragment.TOPTENTRACKS_PARCELABLE);

            arguments.putParcelableArrayList(PlayerFragment.TOPTENTRACKS_PARCELABLE, extras.getBundle("test123").getParcelableArrayList(PlayerFragment.TOPTENTRACKS_PARCELABLE));

            this.mPlayerFragment = new PlayerFragment();
            this.mPlayerFragment.setTwoPane(false);
            this.mPlayerFragment.setArguments(arguments);

            getFragmentManager().beginTransaction().add(R.id.player_container, mPlayerFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
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
}
