package com.hengtan.nanodegreeapp.spotifystreamer;

import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class PlayerActivity extends ActionBarActivity {

    private PlayerFragment mPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

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
            this.mPlayerFragment = new PlayerFragment();
            this.mPlayerFragment.setTwoPane(false);

            if(getIntent().getExtras() != null) {

                Bundle arguments = new Bundle();

                arguments.putInt(PlayerFragment.TRACKINDEX, getIntent().getExtras().getInt(PlayerFragment.TRACKINDEX));
                arguments.putParcelableArrayList(PlayerFragment.TOPTENTRACKS_PARCELABLE, getIntent().getExtras().getParcelableArrayList(PlayerFragment.TOPTENTRACKS_PARCELABLE));
                this.mPlayerFragment.setArguments(arguments);
            }

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
