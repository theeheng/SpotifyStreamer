package com.hengtan.nanodegreeapp.spotifystreamer;

import android.support.v7.widget.RecyclerView;

/**
 * Created by htan on 18/06/2015.
 */
public abstract class TitleBarScrollListerner extends RecyclerView.OnScrollListener {

    private static final int UPDATETITLE_THRESHOLD = 200;
    private int scrolledDistance = 0;
    private boolean showActivityTitle = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (scrolledDistance > UPDATETITLE_THRESHOLD && showActivityTitle) {
            onUpdateTitleWithArtistName();
            showActivityTitle = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -UPDATETITLE_THRESHOLD && !showActivityTitle) {
            onRestoreActivityTitle();
            showActivityTitle = true;
            scrolledDistance = 0;
        }

        if((showActivityTitle && dy>0) || (!showActivityTitle && dy<0)) {
            scrolledDistance += dy;
        }
    }

    public abstract void onUpdateTitleWithArtistName();
    public abstract void onRestoreActivityTitle();

}

