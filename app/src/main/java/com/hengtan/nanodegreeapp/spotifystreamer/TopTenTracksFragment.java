package com.hengtan.nanodegreeapp.spotifystreamer;


import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.view.ViewHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class TopTenTracksFragment extends Fragment implements ObservableScrollViewCallbacks {

    static final String ARTIST_PARCELABLE = "ARTISTPARCELABLE";
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    private String TRACK_KEY = "track_list";
    private ParcelableArtist mArtist;
    private String mCountryCode;
    private int mScrollPosition = 0;
    private boolean mTwoPane = false;

    //@InjectView(R.id.progressBarHolder)
    //protected FrameLayout progressBarHolder;

    @InjectView(R.id.topTenRecyclerView)
    protected ObservableRecyclerView mTopTenRecyclerView;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerViewAdapter mAdapter;
    private ArrayList<ParcelableTrack> mTrackList;
    private Toast toastMessage;

    @InjectView(R.id.image)
    protected ImageView mImageView;

    @InjectView(R.id.overlay)
    protected View mOverlayView;

    @InjectView(R.id.list_background)
    protected View mRecyclerViewBackground;

    @InjectView(R.id.title)
    protected TextView mTitleView;

    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;

    private View headerView;

    public TopTenTracksFragment() {
    }

    public void setTwoPane(boolean twoPane) {
        this.mTwoPane = twoPane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);
        ButterKnife.inject(this, view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mTopTenRecyclerView.setLayoutManager(mLayoutManager);

        // If a layout manager has already been set, get current scroll position.
        if (mTopTenRecyclerView.getLayoutManager() != null) {
            mScrollPosition = ((LinearLayoutManager) mTopTenRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mTopTenRecyclerView.scrollToPosition(mScrollPosition);

        if (savedInstanceState != null && savedInstanceState.containsKey(TRACK_KEY)) {
            mTrackList = savedInstanceState.getParcelableArrayList(TRACK_KEY);
            ShowTracks();
        }
        else {
            mTrackList = new ArrayList<ParcelableTrack>();

            Bundle arguments = getArguments();

            if(arguments != null)
            {
                //ProgressBarHelper.ShowProgressBar(progressBarHolder);
                mArtist = arguments.getParcelable(TopTenTracksFragment.ARTIST_PARCELABLE);
                GetTracks(GetCountryCodeFromPreference());

            }

        }

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize();

        mTopTenRecyclerView.setScrollViewCallbacks(this);
        mTopTenRecyclerView.setHasFixedSize(false);


        if(!mTwoPane) {
            mTopTenRecyclerView.setOnScrollListener(new TitleBarScrollListerner() {
                @Override
                public void onUpdateTitleWithArtistName() {

                        final WindowManager.LayoutParams attrs = getActivity().getWindow()
                                .getAttributes();
                        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        getActivity().getWindow().setAttributes(attrs);
                        getActivity().getWindow().clearFlags(
                                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                   //    ((ActionBarActivity) getActivity()).getSupportActionBar().show();

                }

                @Override
                public void onRestoreActivityTitle() {


                        Window w = getActivity().getWindow();
                        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                        ((ActionBarActivity) getActivity()).getSupportActionBar().hide();

                }
            });
        }

        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, null);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mFlexibleSpaceImageHeight);
        headerView.setLayoutParams(params1);

        headerView.post(new Runnable() {
            @Override
            public void run() {
                if (headerView.getLayoutParams() != null)
                    headerView.getLayoutParams().height = mFlexibleSpaceImageHeight;
            }
        });


        //getActivity().setTitle(null);

        // mRecyclerViewBackground makes RecyclerView's background except header view.
        //mRecyclerViewBackground = findViewById(R.id.list_background);

        //since you cannot programmatically add a header view to a RecyclerView we added an empty view as the header
        // in the adapter and then are shifting the views OnCreateView to compensate
        final float scale = 1 + MAX_TEXT_SCALE_DELTA;
        mRecyclerViewBackground.post(new Runnable() {
            @Override
            public void run() {
                ViewHelper.setTranslationY(mRecyclerViewBackground, mFlexibleSpaceImageHeight);
            }
        });

        if(!mTwoPane) {
            ViewHelper.setTranslationY(mOverlayView, mFlexibleSpaceImageHeight);
        }
        else
        {
            mOverlayView.setVisibility(View.GONE);
        }
        mTitleView.post(new Runnable() {
            @Override
            public void run() {
                ViewHelper.setTranslationY(mTitleView, (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale));
                ViewHelper.setPivotX(mTitleView, 0);
                ViewHelper.setPivotY(mTitleView, 0);
                ViewHelper.setScaleX(mTitleView, scale);
                ViewHelper.setScaleY(mTitleView, scale);
            }
        });

        return view;
    }

    private void GetTracks(String countryCode)
    {
        Glide.with(getActivity()).load(mArtist.getThumbnailImage()).fitCenter().into(mImageView);
        mTitleView.setText(mArtist.name);

        if(!mTwoPane)
            getActivity().setTitle(mArtist.name);

        SpotifyApi api = new SpotifyApi();

        // Most (but not all) of the Spotify Web API endpoints require authorisation.
        // If you know you'll only use the ones that don't require authorisation you can skip this step
        // api.setAccessToken("myAccessToken");

        SpotifyService spotify = api.getService();

        Map<String, Object> fieldMap = new HashMap<String, Object>();
        mCountryCode = countryCode;
        fieldMap.put("country", mCountryCode);

        spotify.getArtistTopTrack(mArtist.id, fieldMap, new retrofit.Callback<Tracks>() {
            @Override
            public void success(final Tracks result, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //ProgressBarHelper.HideProgressBar(progressBarHolder);
                        mTrackList.clear();
                        if (result.tracks.size() > 0) {
                            for (Track track : result.tracks) {
                                ParcelableTrack pt = new ParcelableTrack(track);
                                pt.setArtistName(mArtist.name);
                                mTrackList.add(pt);
                            }

                            ShowTracks();

                        } else {
                            if (mTwoPane) {
                                ShowTracks();
                                DisplayToastMessage(getActivity().getResources().getString(R.string.no_track_found));
                            } else {
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

                        //ProgressBarHelper.HideProgressBar(progressBarHolder);
                        ShowErrorMessage(error.getMessage());
                    }
                });
            }
        });
    }

    private void ShowTracks()
    {
        if(mAdapter == null) {
            mAdapter = new RecyclerViewAdapter(getActivity(), headerView, RecyclerViewAdapter.AdapterType.TOP_TEN_TRACKS);
            mAdapter.setTracks(mTrackList);
            mAdapter.SetOnItemClickListener((RecyclerViewAdapter.OnItemClickListener) this.getActivity());
            mTopTenRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter.setTracks(mTrackList);
            mAdapter.notifyDataSetChanged();
            mTopTenRecyclerView.scrollToPosition(0);
            onScrollChanged(0, false, true);
        }
    }

    public void ShowErrorMessage(String errorMessage)
    {
        String tmpMessage  = getActivity().getResources().getString(R.string.unable_to_connect_to_spotify)+ " : " + errorMessage;
        DisplayToastMessage(tmpMessage);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
            GetTracks(selectecCountryCode);
            ShowTracks();
        }
    }

    public void UpdateTopTenTracks(ParcelableArtist artist)
    {
        this.mArtist = artist;
        GetTracks(GetCountryCodeFromPreference());
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

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();

        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Translate list background
        ViewHelper.setTranslationY(mRecyclerViewBackground, Math.max(0, -scrollY + mFlexibleSpaceImageHeight));

        // Change alpha of overlay
        if(!mTwoPane) {
            ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
            ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));
        }

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, getActivity().findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = getActivity().obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }
}
