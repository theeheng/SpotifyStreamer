package com.hengtan.nanodegreeapp.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by heng on 6/17/15.
 */
public class ParcelableArtist extends Artist implements Parcelable {

    private String thumbnailImage;
    private List<String> mParcelableString;

    private enum ParcelableArtistIndex
    {
        ARTIST_ID(0), ARTIST_NAME(1), ARTIST_THUMBNAIL(2);

        private int value;

        private ParcelableArtistIndex(int value)
        {
            this.value = value;
        }
    };

    ParcelableArtist(Artist artist)
    {
        this.id = artist.id;
        this.name = artist.name;
        this.images = artist.images;
    }

    ParcelableArtist(Parcel in) {
        in.readStringList(this.mParcelableString);
        this.id = mParcelableString.get(ParcelableArtistIndex.ARTIST_ID.ordinal());
        this.name = mParcelableString.get(ParcelableArtistIndex.ARTIST_NAME.ordinal());
        this.thumbnailImage = mParcelableString.get(ParcelableArtistIndex.ARTIST_THUMBNAIL.ordinal());
    }

    public void setThumbnailImage(String value) {
        this.thumbnailImage = value;
    }

    public String getThumbnailImage() {

        if(this.thumbnailImage == null)
        {
            return GetThumbnailImageFromArtist();
        }
        else
        {
            return this.thumbnailImage;
        }
    }

    private String GetThumbnailImageFromArtist()
    {
        if(this.images != null && this.images.size() > 0)
        {
            if(this.images.size() == 1)
            {
                return this.images.get(0).url;
            }
            else if(this.images.size() >= 3)
            {
                return this.images.get(2).url;
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
        values.add(ParcelableArtistIndex.ARTIST_ID.ordinal(),this.id);
        values.add(ParcelableArtistIndex.ARTIST_NAME.ordinal(),this.name);
        values.add(ParcelableArtistIndex.ARTIST_THUMBNAIL.ordinal(),getThumbnailImage());
        dest.writeStringList(values);
    }

    public static final Parcelable.Creator<ParcelableArtist> CREATOR
            = new Parcelable.Creator<ParcelableArtist>() {

        public ParcelableArtist createFromParcel(Parcel in) {
            return new ParcelableArtist(in);
        }

        public ParcelableArtist[] newArray(int size) {
            return new ParcelableArtist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
