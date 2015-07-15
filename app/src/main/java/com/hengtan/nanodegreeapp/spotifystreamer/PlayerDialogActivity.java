package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class PlayerDialogActivity extends Activity {

    private PlayerFragment mPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_player);


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -20;
        params.height = 1500;
        params.width = 1000;
        params.y = -10;

        this.getWindow().setAttributes(params);


        if(savedInstanceState == null)
        {
            this.mPlayerFragment = new PlayerFragment();
            this.mPlayerFragment.setTwoPane(true);

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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
