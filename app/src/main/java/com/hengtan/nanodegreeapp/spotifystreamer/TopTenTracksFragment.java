package com.hengtan.nanodegreeapp.spotifystreamer;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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

    private String mArtistId;
    private ListView mTopTenListView;

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

                            ListViewAdapter adapter = new ListViewAdapter(getActivity(), R.layout.list_layout, result.tracks);
                            adapter.InitAdapterType(ListViewAdapter.AdapterType.TOP_TEN_TRACKS);
                            mTopTenListView.setAdapter(adapter);

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

        return view;
    }

}
