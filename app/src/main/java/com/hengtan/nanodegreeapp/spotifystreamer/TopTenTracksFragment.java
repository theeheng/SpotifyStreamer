package com.hengtan.nanodegreeapp.spotifystreamer;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class TopTenTracksFragment extends Fragment implements ObservableScrollViewCallbacks {

    public static final String ARTIST_PARCELABLE = "ARTISTPARCELABLE";

    private final String LOG_TAG = TopTenTracksFragment.class.getSimpleName();
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    private String TRACK_KEY = "track_list";
    private ParcelableArtist mArtist;
    private String mCountryCode;
    private int mScrollPosition = 0;
    private boolean mTwoPane = false;
    private int mArtistTitleMeasuredWidth = 0;
    private int mArtistTitleMeasuredHeight = 0;
    private int mPlayingNowAlignmentMargin = 10;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerViewAdapter mAdapter;
    private ArrayList<ParcelableTrack> mTrackList;
    private Toast toastMessage;
    private AnimationDrawable mPlayingNowAnimationDrawable;

    @InjectView(R.id.topTenRecyclerView)
    protected ObservableRecyclerView mTopTenRecyclerView;

    @InjectView(R.id.image)
    protected ImageView mImageView;

    @InjectView(R.id.overlay)
    protected View mOverlayView;

    @InjectView(R.id.list_background)
    protected View mRecyclerViewBackground;

    @InjectView(R.id.top_ten_artist_title)
    protected TextView mArtistTitleView;

    @InjectView(R.id.top_ten_playing_now)
    protected ImageView mPlayingNow;

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


        if (savedInstanceState != null && savedInstanceState.containsKey(ARTIST_PARCELABLE)) {
            mArtist = savedInstanceState.getParcelable(ARTIST_PARCELABLE);
        }
        else
        {
            Bundle arguments = getArguments();

            if(arguments != null)
            {
                mArtist = arguments.getParcelable(ARTIST_PARCELABLE);
            }
        }

        UpdateArtistImageViewAndTitle();

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize();

        mTopTenRecyclerView.setScrollViewCallbacks(this);
        mTopTenRecyclerView.setHasFixedSize(false);

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

        final float scale = 1 + MAX_TEXT_SCALE_DELTA;

        mRecyclerViewBackground.post(new Runnable() {
            @Override
            public void run() {
                ViewHelper.setTranslationY(mRecyclerViewBackground, mFlexibleSpaceImageHeight);
            }
        });

        ViewHelper.setTranslationY(mOverlayView, mFlexibleSpaceImageHeight);

        mPlayingNowAnimationDrawable = (AnimationDrawable) mPlayingNow.getDrawable();

        /*mArtistTitleView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i("MyTag","Image button is pressed, visible in LogCat");;
                } catch (Exception e) {
                    Log.e("MyTag", e.toString());
                }
            }
        });*/

        mPlayingNow.post(new Runnable() {
            @Override
            public void run() {

                ViewHelper.setTranslationY(mPlayingNow, (int) (mFlexibleSpaceImageHeight - mPlayingNow.getHeight() * scale) - (mArtistTitleMeasuredHeight + mPlayingNowAlignmentMargin));
                ViewHelper.setTranslationX(mPlayingNow, mArtistTitleMeasuredWidth * scale);
                ViewHelper.setPivotX(mPlayingNow, 0);
                ViewHelper.setPivotY(mPlayingNow, 0);
                ViewHelper.setScaleX(mPlayingNow, scale);
                ViewHelper.setScaleY(mPlayingNow, scale);
            }
        });

        mArtistTitleView.post(new Runnable() {
            @Override
            public void run() {
                ViewHelper.setTranslationY(mArtistTitleView, (int) (mFlexibleSpaceImageHeight - mArtistTitleView.getHeight() * scale));
                ViewHelper.setPivotX(mArtistTitleView, 0);
                ViewHelper.setPivotY(mArtistTitleView, 0);
                ViewHelper.setScaleX(mArtistTitleView, scale);
                ViewHelper.setScaleY(mArtistTitleView, scale);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(TRACK_KEY)) {
            mTrackList = savedInstanceState.getParcelableArrayList(TRACK_KEY);
            ShowTracks();
        }
        else {
            mTrackList = new ArrayList<ParcelableTrack>();

            if(mArtist != null)
            {
                GetTracks(GetCountryCodeFromPreference());
            }

        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mTrackList != null && mTrackList.size() > 0) {
            outState.putParcelableArrayList(TRACK_KEY, mTrackList);
        }

        if(mArtist != null)
        {
            outState.putParcelable(ARTIST_PARCELABLE, mArtist);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        UpdatePlayingNowImageView();
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
        ViewHelper.setPivotY(mArtistTitleView, 0);
        ViewHelper.setScaleX(mArtistTitleView, scale);
        ViewHelper.setScaleY(mArtistTitleView, scale);

        ViewHelper.setPivotY(mPlayingNow, 0);
        ViewHelper.setScaleX(mPlayingNow, scale);
        ViewHelper.setScaleY(mPlayingNow, scale);


        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mArtistTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;

        ViewHelper.setTranslationY(mArtistTitleView, titleTranslationY);

        ViewHelper.setTranslationY(mPlayingNow, titleTranslationY - mArtistTitleMeasuredHeight - mPlayingNowAlignmentMargin);

        mArtistTitleView.measure(0, 0);

        ViewHelper.setTranslationX(mPlayingNow, (mArtistTitleView.getMeasuredWidth() * scale));
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
            ViewHelper.setPivotX(mArtistTitleView, getActivity().findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mArtistTitleView, 0);
        }
    }


    @OnClick({R.id.top_ten_playing_now})
    public void onPlayingNowImageViewClick(View view) {

        if(!mTwoPane) {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            startActivity(intent);
        }
        else
        {
            PlayerFragment playerFragment = new PlayerFragment();
            playerFragment.setTwoPane(true);
            playerFragment.show(getFragmentManager(), MainActivity.PLAYERFRAGMENT_TAG);
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


    private void UpdatePlayingNowImageView() {
        if(Application.getIsPlayingNow()) {

            mPlayingNowAnimationDrawable.start();
            mPlayingNow.setVisibility(View.VISIBLE);
        }
        else
        {
            mPlayingNowAnimationDrawable.stop();
            mPlayingNow.setVisibility(View.INVISIBLE);
        }
    }

    private void UpdateArtistImageViewAndTitle() {

        if(mArtist != null) {
            Glide.with(getActivity()).load(mArtist.getThumbnailImage()).fitCenter().into(mImageView);
            mArtistTitleView.setText(mArtist.name);
            mArtistTitleView.measure(0, 0);
            mArtistTitleMeasuredWidth = mArtistTitleView.getMeasuredWidth();
            mArtistTitleMeasuredHeight = mArtistTitleView.getMeasuredHeight();
        }
    }

    private void GetTracks(String countryCode)
    {
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
        String tmpMessage  = getActivity().getResources().getString(R.string.unable_to_connect_to_spotify);
        Log.v(LOG_TAG, errorMessage);
        DisplayToastMessage(tmpMessage);
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
        UpdateArtistImageViewAndTitle();
        GetTracks(GetCountryCodeFromPreference());
        ShowTracks();
    }

    public String GetCountryCodeFromPreference()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String tempCountryCode = sharedPrefs.getString(SettingsActivity.COUNTRY_PREFERENCE_ID, "");

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
        toastMessage = Toast.makeText(getActivity(), toastMessageText, Toast.LENGTH_LONG);
        toastMessage.show();
    }

}
