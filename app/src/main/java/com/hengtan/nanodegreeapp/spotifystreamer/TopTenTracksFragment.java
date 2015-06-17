package com.hengtan.nanodegreeapp.spotifystreamer;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
    private ListView mTopTenListView;
    private ArrayList<ParcelableTrack> mTrackList;

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
                    mArtistId = arguments.getString(TopTenTracksFragment.ARTIST_ID);

                    SpotifyApi api = new SpotifyApi();

                    // Most (but not all) of the Spotify Web API endpoints require authorisation.
                    // If you know you'll only use the ones that don't require authorisation you can skip this step
                    // api.setAccessToken("myAccessToken");

                    SpotifyService spotify = api.getService();

                    Map<String, Object> fieldMap = new HashMap<String, Object>();

                    fieldMap.put("country","GB");

                    spotify.getArtistTopTrack(mArtistId, fieldMap, new retrofit.Callback<Tracks>() {
                        @Override
                        public void success(final Tracks result, Response response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mTrackList.clear();

                                    for(Track track : result.tracks) {
                                        ParcelableTrack pt = new ParcelableTrack(track);
                                        mTrackList.add(pt);
                                    }
                                    ShowTracks();
                                }
                            });
                        }

                        @Override
                        public void failure(final RetrofitError error) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String err = error.getMessage();
                                }
                            });
                        }
                    });
                }

            }


        return view;
    }

    private void ShowTracks()
    {
        ListViewAdapter adapter = new ListViewAdapter(getActivity(), R.layout.list_layout, mTrackList);
        adapter.InitAdapterType(ListViewAdapter.AdapterType.TOP_TEN_TRACKS);
        mTopTenListView.setAdapter(adapter);
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
}
