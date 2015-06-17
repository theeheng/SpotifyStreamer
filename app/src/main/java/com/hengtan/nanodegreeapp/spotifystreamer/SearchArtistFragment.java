package com.hengtan.nanodegreeapp.spotifystreamer;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;
import retrofit.client.Response;



public class SearchArtistFragment extends Fragment {

    private SearchView artistSearchView;
    private ListView artistListView;
    private ArrayList<ParcelableArtist> mArtistList;
    private String ARTIST_KEY = "artist_list";

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface SearchArtistFragmentCallback {
        /**
         * SearchArtistFragmentCallback for when an item has been selected.
         */
        void onArtistSelected(String artistId);
    }

    public SearchArtistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_search_artist, container, false);

        artistListView = (ListView) rootView.findViewById(R.id.artistListView);
        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                ParcelableArtist artist = (ParcelableArtist) adapterView.getItemAtPosition(position);
                if (artist != null) {
                    ((SearchArtistFragmentCallback) getActivity()).onArtistSelected(artist.id);
                }
            }
        });
        //Get a reference to artist search view
        artistSearchView = (SearchView) rootView.findViewById(R.id.searchArtistView);
        artistSearchView.setIconifiedByDefault(false);
        artistSearchView.setSubmitButtonEnabled(true);

        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) artistSearchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));


        artistSearchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.d("Search click ", "Search click");
                //SearchArtistFragment.this.onSearchArtistBtnClick((View) view);
            }
        });

        artistSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Search text submit", query);
                SearchArtistFragment.this.onSearchArtistBtnClick(null);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                Log.d("Search text change", newText);
                return false;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(ARTIST_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mArtistList = savedInstanceState.getParcelableArrayList(ARTIST_KEY);
            showArtists();
        }
        else {
            mArtistList = new ArrayList<ParcelableArtist>();
        }

        return rootView;
    }

    private void onSearchArtistBtnClick(View view) {


        SpotifyApi api = new SpotifyApi();

// Most (but not all) of the Spotify Web API endpoints require authorisation.
// If you know you'll only use the ones that don't require authorisation you can skip this step
       // api.setAccessToken("myAccessToken");

        SpotifyService spotify = api.getService();

        spotify.searchArtists(artistSearchView.getQuery().toString(), new retrofit.Callback<ArtistsPager>() {
            @Override
            public void success(final ArtistsPager artistsPager, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mArtistList.clear();

                        for(Artist artist : artistsPager.artists.items) {
                            ParcelableArtist pa = new ParcelableArtist(artist);
                            mArtistList.add(pa);
                        }
                        showArtists();
                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showErrorMessage(error.getMessage());
                    }
                });
            }
        });
    }

    public void showArtists()
    {
        ListViewAdapter adapter = new ListViewAdapter(getActivity(), R.layout.list_layout, mArtistList);
        adapter.InitAdapterType(ListViewAdapter.AdapterType.ARTIST_SEARCH);
        artistListView.setAdapter(adapter);
    }

    public void showErrorMessage(String errorMessage)
    {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mArtistList != null && mArtistList.size() > 0) {
            outState.putParcelableArrayList(ARTIST_KEY, mArtistList);
        }
        super.onSaveInstanceState(outState);
    }

}
