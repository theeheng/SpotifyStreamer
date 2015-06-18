package com.hengtan.nanodegreeapp.spotifystreamer;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class TopTenTracksFragment extends Fragment {

    static final String ARTIST_ID = "ARTISTID";
    private String TRACK_KEY = "track_list";
    private String mArtistId;
    private String mCountryCode;
    private ListView mTopTenListView;
    private FrameLayout progressBarHolder;
    private ArrayList<ParcelableTrack> mTrackList;
    private Toast toastMessage;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface TopTenTracksFragmentCallback {
        /**
         * SearchArtistFragmentCallback for when an item has been selected.
         */
        void onTrackSelected(String trackId);
    }

    public TopTenTracksFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        progressBarHolder = (FrameLayout) view.findViewById(R.id.progressBarHolder);

        mTopTenListView = (ListView) view.findViewById(R.id.topTenListView);

        mTopTenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Track track = (Track) adapterView.getItemAtPosition(position);
                if (track != null) {
                    ((TopTenTracksFragmentCallback) getActivity()).onTrackSelected(track.id);
                }
            }
        });



            // If there's instance state, mine it for useful information.
            // The end-goal here is that the user never knows that turning their device sideways
            // does crazy lifecycle related things.  It should feel like some stuff stretched out,
            // or magically appeared to take advantage of room, but data or place in the app was never
            // actually *lost*.
            if (savedInstanceState != null && savedInstanceState.containsKey(TRACK_KEY)) {
                // The listview probably hasn't even been populated yet.  Actually perform the
                // swapout in onLoadFinished.
                mTrackList = savedInstanceState.getParcelableArrayList(TRACK_KEY);
                ShowTracks();
            }
            else {
                mTrackList = new ArrayList<ParcelableTrack>();

                Bundle arguments = getArguments();

                if(arguments != null)
                {
                    ProgressBarHelper.ShowProgressBar(progressBarHolder);
                    mArtistId = arguments.getString(TopTenTracksFragment.ARTIST_ID);
                    GetTracks(GetCountryCodeFromPreference(), false);
                }

            }


        return view;
    }

    private void GetTracks(String countryCode, final boolean twoPane)
    {
        SpotifyApi api = new SpotifyApi();

        // Most (but not all) of the Spotify Web API endpoints require authorisation.
        // If you know you'll only use the ones that don't require authorisation you can skip this step
        // api.setAccessToken("myAccessToken");

        SpotifyService spotify = api.getService();

        Map<String, Object> fieldMap = new HashMap<String, Object>();
        mCountryCode = countryCode;
        fieldMap.put("country", mCountryCode);

        spotify.getArtistTopTrack(mArtistId, fieldMap, new retrofit.Callback<Tracks>() {
            @Override
            public void success(final Tracks result, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ProgressBarHelper.HideProgressBar(progressBarHolder);
                        mTrackList.clear();
                        if(result.tracks.size() > 0)
                        {
                            for (Track track : result.tracks) {
                                ParcelableTrack pt = new ParcelableTrack(track);
                                mTrackList.add(pt);
                            }

                            ShowTracks();

                        }
                        else
                        {
                            if(twoPane)
                            {
                                ShowTracks();
                                DisplayToastMessage(getActivity().getResources().getString(R.string.no_track_found));
                            }
                            else
                            {
                                getActivity().finish();
                                DisplayToastMessage(getActivity().getResources().getString(R.string.no_track_found));

                            }
                        }

                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ProgressBarHelper.HideProgressBar(progressBarHolder);
                        ShowErrorMessage(error.getMessage());
                    }
                });
            }
        });
    }

    private void ShowTracks()
    {
        ListViewAdapter adapter = new ListViewAdapter(getActivity(), R.layout.list_layout, mTrackList);
        adapter.InitAdapterType(ListViewAdapter.AdapterType.TOP_TEN_TRACKS);
        mTopTenListView.setAdapter(adapter);
    }

    public void ShowErrorMessage(String errorMessage)
    {
        String tmpMessage  = getActivity().getResources().getString(R.string.unable_to_connect_to_spotify)+ " : " + errorMessage;
        DisplayToastMessage(tmpMessage);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mTrackList != null && mTrackList.size() > 0) {
            outState.putParcelableArrayList(TRACK_KEY, mTrackList);
        }
        super.onSaveInstanceState(outState);
    }

    public void UpdateTopTenTracksOnPreferenceUpdate()
    {
        String selectecCountryCode = GetCountryCodeFromPreference();

        if(!selectecCountryCode.equals(mCountryCode))
        {
            GetTracks(selectecCountryCode, false);
            ShowTracks();
        }
    }

    public void UpdateTopTenTracks(String artistId)
    {
        this.mArtistId = artistId;

        GetTracks(GetCountryCodeFromPreference(), true);
        ShowTracks();
    }

    public String GetCountryCodeFromPreference()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String tempCountryCode = sharedPrefs.getString("country_code_list", "");

        if(tempCountryCode.length() == 0)
        {
            return getActivity().getResources().getConfiguration().locale.getCountry();
        } else
        {
            return tempCountryCode;
        }
    }

    public void  DisplayToastMessage(String toastMessageText)
    {
        //Stop any previous toasts
        if(toastMessage !=null){ toastMessage.cancel(); }

        // Display toast message
        toastMessage = Toast.makeText(getActivity(),
                toastMessageText,
                Toast.LENGTH_LONG);
        toastMessage.show();
    }
}
