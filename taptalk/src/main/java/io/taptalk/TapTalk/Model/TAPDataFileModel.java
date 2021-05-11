package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

import javax.annotation.Nullable;

import io.taptalk.TapTalk.Helper.TAPUtils;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;

public class TAPDataFileModel implements Parcelable {
    @Nullable
    @JsonProperty("fileID")
    private String fileID;
    @Nullable
    @JsonProperty("url")
    private String fileURL;
    @Nullable
    @JsonProperty("fileName")
    private String fileName;
    @Nullable
    @JsonProperty("mediaType")
    private String mediaType;
    @Nullable
    @JsonProperty("size")
    private Number size;
    @Nullable
    @JsonProperty("fileUri")
    private String fileUri;

    public TAPDataFileModel(@Nullable String fileID, @Nullable String fileURL, @Nullable String fileName, @Nullable String mediaType, @Nullable Number size) {
        this.fileID = fileID;
        this.fileURL = fileURL;
        this.fileName = fileName;
        this.mediaType = mediaType;
        this.size = size;
    }

    public TAPDataFileModel(@Nullable String fileName, @Nullable String mediaType, @Nullable Number size) {
        this.fileName = fileName;
        this.mediaType = mediaType;
        this.size = size;
    }

    public TAPDataFileModel() {
    }

    public static TAPDataFileModel Builder(String fileID, String fileURL, String fileName,
                                           String mediaType, Number size) {
        return new TAPDataFileModel(fileID, fileURL, fileName, mediaType, size);
    }

    public TAPDataFileModel(HashMap<String, Object> imageDataMap) {
        this.fileID = (String) imageDataMap.get(FILE_ID);
        this.fileURL = (String) imageDataMap.get(FILE_URL);
        this.fileName = (String) imageDataMap.get(FILE_NAME);
        this.mediaType = (String) imageDataMap.get(MEDIA_TYPE);
        this.fileUri = (String) imageDataMap.get(FILE_URI);
        this.size = (Number) imageDataMap.get(SIZE);
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    @Nullable
    public String getFileID() {
        return fileID;
    }

    public void setFileID(@Nullable String fileID) {
        this.fileID = fileID;
    }

    @Nullable
    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(@Nullable String fileURL) {
        this.fileURL = fileURL;
    }

    @Nullable
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@Nullable String fileName) {
        this.fileName = fileName;
    }

    @Nullable
    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(@Nullable String mediaType) {
        this.mediaType = mediaType;
    }

    @Nullable
    public Number getSize() {
        return size;
    }

    public void setSize(@Nullable Number size) {
        this.size = size;
    }

    @Nullable
    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(@Nullable String fileUri) {
        this.fileUri = fileUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileID);
        dest.writeString(this.fileURL);
        dest.writeString(this.fileName);
        dest.writeString(this.mediaType);
        dest.writeSerializable(this.size);
        dest.writeString(this.fileUri);
    }

    protected TAPDataFileModel(Parcel in) {
        this.fileID = in.readString();
        this.fileURL = in.readString();
        this.fileName = in.readString();
        this.mediaType = in.readString();
        this.size = (Number) in.readSerializable();
        this.fileUri = in.readString();
    }

    public static final Creator<TAPDataFileModel> CREATOR = new Creator<TAPDataFileModel>() {
        @Override
        public TAPDataFileModel createFromParcel(Parcel source) {
            return new TAPDataFileModel(source);
        }

        @Override
        public TAPDataFileModel[] newArray(int size) {
            return new TAPDataFileModel[size];
        }
    };
}
