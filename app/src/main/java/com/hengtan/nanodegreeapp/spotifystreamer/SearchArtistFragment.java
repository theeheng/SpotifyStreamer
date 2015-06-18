package com.hengtan.nanodegreeapp.spotifystreamer;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;
import retrofit.client.Response;



public class SearchArtistFragment extends Fragment {

    private SearchView artistSearchView;
    private RecyclerView artistRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerViewAdapter mAdapter;
    private FrameLayout progressBarHolder;
    private ArrayList<ParcelableArtist> mArtistList;
    private String ARTIST_KEY = "artist_list";
    private Toast toastMessage;

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

        progressBarHolder = (FrameLayout) rootView.findViewById(R.id.progressBarHolder);

        artistRecyclerView = (RecyclerView) rootView.findViewById(R.id.artistRecyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        artistRecyclerView.setLayoutManager(mLayoutManager);

        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (artistRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) artistRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        artistRecyclerView.scrollToPosition(scrollPosition);
       /* artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                ParcelableArtist artist = (ParcelableArtist) adapterView.getItemAtPosition(position);
                if (artist != null) {
                    ((SearchArtistFragmentCallback) getActivity()).onArtistSelected(artist.id);
                }
            }
        }); */
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
                //Log.d("Search text change", newText);
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
            ShowArtists();
        }
        else {
            mArtistList = new ArrayList<ParcelableArtist>();
        }

        return rootView;
    }

    private void onSearchArtistBtnClick(View view) {

        ProgressBarHelper.ShowProgressBar(progressBarHolder);

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

                        ProgressBarHelper.HideProgressBar(progressBarHolder);

                        if(artistsPager.artists.items.size() > 0) {
                            mArtistList.clear();

                            for (Artist artist : artistsPager.artists.items) {
                                ParcelableArtist pa = new ParcelableArtist(artist);
                                mArtistList.add(pa);
                            }
                            ShowArtists();
                        }
                        else
                        {
                            DisplayToastMessage(getActivity().getResources().getString(R.string.no_artist_found));
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

    public void ShowArtists()
    {
        /*ListViewAdapter adapter = new ListViewAdapter(getActivity(), R.layout.list_layout, mArtistList);
        adapter.InitAdapterType(ListViewAdapter.AdapterType.ARTIST_SEARCH);
        artistListView.setAdapter(adapter);*/
        mAdapter = new RecyclerViewAdapter(RecyclerViewAdapter.AdapterType.ARTIST_SEARCH, mArtistList);
        mAdapter.SetOnItemClickListener((RecyclerViewAdapter.OnItemClickListener)this.getActivity());
        // Set CustomAdapter as the adapter for RecyclerView.
        artistRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)

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
        if (mArtistList != null && mArtistList.size() > 0) {
            outState.putParcelableArrayList(ARTIST_KEY, mArtistList);
        }
        super.onSaveInstanceState(outState);
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
