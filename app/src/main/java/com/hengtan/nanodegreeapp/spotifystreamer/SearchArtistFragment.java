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
import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchArtistFragment extends Fragment {

    private final String LOG_TAG = SearchArtistFragment.class.getSimpleName();

    private String ARTIST_KEY = "artist_list";
    private int mScrollPosition = 0;

    @InjectView(R.id.searchArtistView)
    protected SearchView artistSearchView;

    @InjectView(R.id.progressBarHolder)
    protected FrameLayout progressBarHolder;

    @InjectView(R.id.artistRecyclerView)
    protected RecyclerView artistRecyclerView;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerViewAdapter mAdapter;
    private ArrayList<ParcelableArtist> mArtistList;
    private Toast toastMessage;

    public SearchArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_search_artist, container, false);
        ButterKnife.inject(this, rootView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        artistRecyclerView.setLayoutManager(mLayoutManager);

        // If a layout manager has already been set, get current scroll position.
        if (artistRecyclerView.getLayoutManager() != null) {
            mScrollPosition = ((LinearLayoutManager) artistRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        artistRecyclerView.scrollToPosition(mScrollPosition);

        artistSearchView.setIconifiedByDefault(false);

        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) artistSearchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));


        artistSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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

        if (savedInstanceState != null && savedInstanceState.containsKey(ARTIST_KEY)) {
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

                        if (artistsPager.artists.items.size() > 0) {
                            mArtistList.clear();

                            for (Artist artist : artistsPager.artists.items) {
                                ParcelableArtist pa = new ParcelableArtist(artist);
                                mArtistList.add(pa);
                            }
                            ShowArtists();
                        } else {
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
        if(mAdapter == null) {
            mAdapter = new RecyclerViewAdapter(getActivity(), null, RecyclerViewAdapter.AdapterType.ARTIST_SEARCH);
            mAdapter.setArtists(mArtistList);
            mAdapter.SetOnItemClickListener((RecyclerViewAdapter.OnItemClickListener) this.getActivity());
            artistRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter.setArtists(mArtistList);
            mAdapter.notifyDataSetChanged();
            artistRecyclerView.scrollToPosition(0);
        }
    }

    public void ShowErrorMessage(String errorMessage)
    {
        String tmpMessage  = getActivity().getResources().getString(R.string.unable_to_connect_to_spotify);
        Log.v(LOG_TAG,errorMessage);
        DisplayToastMessage(tmpMessage);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

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
