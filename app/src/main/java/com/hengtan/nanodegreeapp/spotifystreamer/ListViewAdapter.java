package com.hengtan.nanodegreeapp.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by htan on 11/06/2015.
 */

public class ListViewAdapter<T> extends ArrayAdapter<T> {

    public enum AdapterType
    {
        ARTIST_SEARCH,
        TOP_TEN_TRACKS
    }

    //declaring our List of artists
    private List<T> mObjects;
    private List<Artist> mArtists;
    private List<Track> mTracks;
    private ImageView mImgView;
    private TextView mTopTextView;
    private TextView mBottomTextView;
    private AdapterType mType;

    public ListViewAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        mObjects = objects;
    }

    public void InitAdapterType(AdapterType type)
    {
        mType = type;

        InitAdapterListObject();
    }

    private void InitAdapterListObject()
    {
        switch (mType)
        {
            case ARTIST_SEARCH: mArtists = (List<Artist>) mObjects; break;
            case TOP_TEN_TRACKS: mTracks = (List<Track>) mObjects; break;
            default: break;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;

        if(v == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_layout, null);
        }

        switch (mType)
        {
            case ARTIST_SEARCH:  DisplayArtistList(v, position); break;
            case TOP_TEN_TRACKS: DisplayTopTenList(v, position); break;
            default: break;
        }

        return v;
    }

    private void DisplayTopTenList(View v, int position) {

        Track track = mTracks.get(position);

        if(track != null)
        {
            InitViewControlFromView(v);

            if(mImgView != null && track.album != null && track.album.images != null && track.album.images.size() > 0)
            {
                if(track.album.images.size() >= 3)
                    Glide.with(getContext()).load(track.album.images.get(2).url).into(mImgView);
                else
                    Glide.with(getContext()).load(track.album.images.get(0).url).into(mImgView);
            }

            if(mTopTextView != null) {
                mTopTextView.setText(track.name);
            }

            if(mBottomTextView != null && track.album != null) {
                mBottomTextView.setVisibility(View.VISIBLE);
                mBottomTextView.setText(track.album.name);
            }
        }
    }

    private void DisplayArtistList(View v, int position) {
        Artist artist = mArtists.get(position);

        if(artist != null)
        {
            InitViewControlFromView(v);

            if(mImgView != null && artist.images.size() > 0)
            {
                if( artist.images.size() >= 3)
                    Glide.with(getContext()).load(artist.images.get(2).url).into(mImgView);
                else
                    Glide.with(getContext()).load(artist.images.get(0).url).into(mImgView);
            }

            if(mTopTextView != null) {
                mTopTextView.setText(artist.name);
            }
        }
    }

    private void InitViewControlFromView(View v) {
        mImgView = (ImageView) v.findViewById(R.id.listImageView);
        mTopTextView = (TextView) v.findViewById(R.id.listTextViewTop);
        mBottomTextView = (TextView) v.findViewById(R.id.listTextViewBottom);
    }

}