package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPImageURL implements Parcelable {
    @JsonProperty("fullsize") private String fullsize;
    @JsonProperty("thumbnail") private String thumbnail;

    public TAPImageURL() {
        this.fullsize = "";
        this.thumbnail = "";
    }

    @Ignore
    public TAPImageURL(String fullsize, String thumbnail) {
        this.fullsize = fullsize;
        this.thumbnail = thumbnail;
    }

    public static TAPImageURL fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPImageURL>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public String getFullsize() {
        return fullsize;
    }

    public void setFullsize(String fullsize) {
        this.fullsize = fullsize;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullsize);
        dest.writeString(this.thumbnail);
    }

    protected TAPImageURL(Parcel in) {
        this.fullsize = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Parcelable.Creator<TAPImageURL> CREATOR = new Parcelable.Creator<TAPImageURL>() {
        @Override
        public TAPImageURL createFromParcel(Parcel source) {
            return new TAPImageURL(source);
        }

        @Override
        public TAPImageURL[] newArray(int size) {
            return new TAPImageURL[size];
        }
    };
}
