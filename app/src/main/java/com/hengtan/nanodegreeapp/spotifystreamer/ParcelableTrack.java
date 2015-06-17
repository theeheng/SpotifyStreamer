package com.hengtan.nanodegreeapp.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by htan on 17/06/2015.
 */
public class ParcelableTrack extends Track implements Parcelable {

    private String thumbnailImage;
    private String albumName;

    private List<String> mParcelableString;

    private enum ParcelableTrackIndex
    {
        TRACK_ID(0), TRACK_NAME(1), TRACK_ALBUM_NAME(2), TRACK_THUMBNAIL(3);

        private int value;

        private ParcelableTrackIndex(int value)
        {
            this.value = value;
        }
    };

    ParcelableTrack(Track track)
    {
        this.id = track.id;
        this.name = track.name;
        this.album = track.album;
    }

    ParcelableTrack(Parcel in) {
        in.readStringList(this.mParcelableString);
        this.id = mParcelableString.get(ParcelableTrackIndex.TRACK_ID.ordinal());
        this.name = mParcelableString.get(ParcelableTrackIndex.TRACK_NAME.ordinal());
        this.albumName = mParcelableString.get(ParcelableTrackIndex.TRACK_ALBUM_NAME.ordinal());
        this.thumbnailImage = mParcelableString.get(ParcelableTrackIndex.TRACK_THUMBNAIL.ordinal());
    }

    public void setAlbumName(String value) {
        this.albumName = value;
    }

    public String getalbumName() {

        if(this.albumName == null)
        {
            return GetAlbumNameFromTrack();
        }
        else
        {
            return this.albumName;
        }
    }

    private String GetAlbumNameFromTrack() {

        if(this.album == null)
        {
            return null;
        }
        else
        {
            return this.album.name;
        }
    }


    public void setThumbnailImage(String value) {
        this.thumbnailImage = value;
    }

    public String getThumbnailImage() {

        if(this.thumbnailImage == null)
        {
            return GetThumbnailImageFromTrack();
        }
        else
        {
            return this.thumbnailImage;
        }
    }

    private String GetThumbnailImageFromTrack()
    {
        if(this.album != null && this.album.images.size() > 0)
        {
            if(this.album.images.size() == 1)
            {
                return this.album.images.get(0).url;
            }
            else if(this.album.images.size() >= 3)
            {
                return this.album.images.get(2).url;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        ArrayList<String> values = new ArrayList<String>();
        values.add(ParcelableTrackIndex.TRACK_ID.ordinal(),this.id);
        values.add(ParcelableTrackIndex.TRACK_NAME.ordinal(),this.name);
        values.add(ParcelableTrackIndex.TRACK_ALBUM_NAME.ordinal(),getalbumName());
        values.add(ParcelableTrackIndex.TRACK_THUMBNAIL.ordinal(),getThumbnailImage());
        dest.writeStringList(values);
    }

    public static final Parcelable.Creator<ParcelableTrack> CREATOR
            = new Parcelable.Creator<ParcelableTrack>() {

        public ParcelableTrack createFromParcel(Parcel in) {
            return new ParcelableTrack(in);
        }

        public ParcelableTrack[] newArray(int size) {
            return new ParcelableTrack[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
