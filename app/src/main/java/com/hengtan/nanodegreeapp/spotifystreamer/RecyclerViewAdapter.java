package com.hengtan.nanodegreeapp.spotifystreamer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    public enum AdapterType
    {
        ARTIST_SEARCH,
        TOP_TEN_TRACKS
    }

    //declaring our List of artists
    private List<ParcelableArtist> mArtists;
    private List<ParcelableTrack> mTracks;
    private AdapterType mType;
    OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        public void onArtistClick(String artistId);
        public void onTrackClick(String trackId);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public RecyclerViewAdapter(AdapterType type, List objects) {
        mType = type;

        switch (mType)
        {
            case ARTIST_SEARCH: mArtists = (List<ParcelableArtist>) objects; break;
            case TOP_TEN_TRACKS: mTracks = (List<ParcelableTrack>) objects; break;
            default: break;
        }
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_layout, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        switch (mType)
        {
            case ARTIST_SEARCH:  DisplayArtistList(viewHolder, position); break;
            case TOP_TEN_TRACKS: DisplayTopTenList(viewHolder, position); break;
            default: break;
        }
    }

    private void DisplayTopTenList(ViewHolder vh, int position) {

        ParcelableTrack track = mTracks.get(position);

        if(track != null)
        {
            // Get element from your dataset at this position and replace the contents of the view
            // with that element
            vh.getTopTextView().setText(track.name);

            String albumName = track.getalbumName();

            if(albumName != null)
            {
                vh.getBottomTextView().setVisibility(View.VISIBLE);
                vh.getBottomTextView().setText(track.name);
            }

            if(track.getThumbnailImage() != null && (!track.getThumbnailImage().equals(""))) {
                Glide.with(vh.getImageView().getContext()).load(track.getThumbnailImage()).into(vh.getImageView());
            }
            else
            {
                Glide.with(vh.getImageView().getContext()).load(R.mipmap.ic_default_art).into(vh.getImageView());
            }
        }
    }

    private void DisplayArtistList(ViewHolder vh, int position) {
        ParcelableArtist artist = mArtists.get(position);

        if(artist != null)
        {
            // Get element from your dataset at this position and replace the contents of the view
            // with that element
            vh.getTopTextView().setText(artist.name);


            if(artist.getThumbnailImage() != null && (!artist.getThumbnailImage().equals(""))) {
                Glide.with(vh.getImageView().getContext()).load(artist.getThumbnailImage()).into(vh.getImageView());
            }
            else
            {
                Glide.with(vh.getImageView().getContext()).load(R.mipmap.ic_default_art).into(vh.getImageView());
            }
        }

/*
        if(artist != null)
        {
            InitViewControlFromView(v);

            if(mImgView != null)
            {
                if(artist.getThumbnailImage() != null && (!artist.getThumbnailImage().equals(""))) {
                    Glide.with(getContext()).load(artist.getThumbnailImage()).into(mImgView);
                }
                else
                {
                    Glide.with(getContext()).load(R.mipmap.ic_default_art).into(mImgView);
                }
            }

            if(mTopTextView != null) {
                mTopTextView.setText(artist.name);
            }
        }*/
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mArtists != null)
        {
            return mArtists.size();
        }
        else if (mTracks != null)
        {
            return mTracks.size();
        }
        else
        {
            return 0;
        }
    }

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mImgView;
        private final TextView mTopTextView;
        private final TextView mBottomTextView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.

            mImgView = (ImageView) v.findViewById(R.id.listImageView);
            mTopTextView = (TextView) v.findViewById(R.id.listTextViewTop);
            mBottomTextView = (TextView) v.findViewById(R.id.listTextViewBottom);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                switch (mType)
                {
                    case ARTIST_SEARCH: mItemClickListener.onArtistClick(mArtists.get(getPosition()).id); break;
                    case TOP_TEN_TRACKS: mItemClickListener.onTrackClick(mTracks.get(getPosition()).id);; break;
                    default: break;
                }
            }
        }

        public ImageView getImageView() {
            return mImgView;
        }
        public TextView getTopTextView() {
            return mTopTextView;
        }
        public TextView getBottomTextView() {

            return mBottomTextView;
        }

    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

}

