package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public enum AdapterType
    {
        ARTIST_SEARCH,
        TOP_TEN_TRACKS
    }

    //declaring our List of artists
    private List<ParcelableArtist> mArtists;
    private List<ParcelableTrack> mTracks;
    private AdapterType mType;
    private LayoutInflater mInflater;
    private View mHeaderView;

    OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onArtistClick(ParcelableArtist artist, ImageView imgView);
        void onTrackClick(ParcelableTrack track);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param type AdapterType to indicate type of object passed in .
     */
    public RecyclerViewAdapter(Context context, View headerView, AdapterType type) {
        mInflater = LayoutInflater.from(context);
        mHeaderView = headerView;
        mType = type;
    }

    public void setArtists(List<ParcelableArtist> artists)
    {
        this.mArtists = artists;
    }

    public void setTracks(List<ParcelableTrack> tracks)
    {
        this.mTracks = tracks;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        if (viewType == VIEW_TYPE_HEADER && mHeaderView != null) {
            return new HeaderViewHolder(mHeaderView);
        } else {
            return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_layout, viewGroup, false));
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {

            if(mHeaderView != null)
            {
                position = position - 1;
            }

            switch (mType)
            {
                case ARTIST_SEARCH:  DisplayArtistList((ItemViewHolder)viewHolder, position); break;
                case TOP_TEN_TRACKS: DisplayTopTenList((ItemViewHolder)viewHolder, position); break;
                default: break;
            }
        }
    }

    private void DisplayTopTenList(ItemViewHolder vh, int position) {

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

    private void DisplayArtistList(ItemViewHolder vh, int position) {
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        int itemCountSize = 0;

        if(mArtists != null)
        {
            itemCountSize = mArtists.size();
        }
        else if (mTracks != null)
        {
            itemCountSize = mTracks.size();
        }

        if (mHeaderView == null) {
            return itemCountSize;
        } else {
            return itemCountSize + 1;
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mImgView;
        private final TextView mTopTextView;
        private final TextView mBottomTextView;

        public ItemViewHolder(View v) {
            super(v);

            mImgView = (ImageView) v.findViewById(R.id.image);
            mTopTextView = (TextView) v.findViewById(R.id.listTextViewTop);
            mBottomTextView = (TextView) v.findViewById(R.id.listTextViewBottom);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {


                int clickPosition = getPosition();

                Log.d("Log position for list : "+ clickPosition,Integer.toString(clickPosition));


                if(mHeaderView != null)
                {
                    clickPosition = clickPosition - 1;
                }

                switch (mType)
                {
                    case ARTIST_SEARCH:
                        Log.d("selected artist : "+ mArtists.get(clickPosition).id,mArtists.get(clickPosition).id);
                        mItemClickListener.onArtistClick(mArtists.get(clickPosition), mImgView); break;
                    case TOP_TEN_TRACKS:
                        Log.d("selected track : "+ mTracks.get(clickPosition).id,mTracks.get(clickPosition).id);
                        mItemClickListener.onTrackClick(mTracks.get(clickPosition)); break;
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
}

