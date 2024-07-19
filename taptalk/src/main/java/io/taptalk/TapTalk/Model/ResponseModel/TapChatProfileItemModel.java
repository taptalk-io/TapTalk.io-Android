package io.taptalk.TapTalk.Model.ResponseModel;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMessageModel;

public class TapChatProfileItemModel implements Parcelable {

    public static final int TYPE_USER_GROUP_DETAIL = 0;
    public static final int TYPE_SECTION_TITLE = 1;
    public static final int TYPE_MENU_BUTTON = 2;
    public static final int TYPE_MEDIA_THUMBNAIL = 3;
    public static final int TYPE_LOADING_LAYOUT = 4;

    private int type;
    private int menuId;
    private int iconResource;
    private int iconColorResource;
    private int textColorResource;
    private boolean isChecked;
    @Nullable private TAPMessageModel mediaMessage;
    @Nullable private TAPImageURL imageURL;
    @Nullable private String itemLabel;
    @Nullable private String itemSubLabel;

    // Constructor for user/group detail
    public TapChatProfileItemModel(@Nullable TAPImageURL imageURL, @Nullable String itemLabel, @Nullable String itemSubLabel, @ColorRes int textColorResource) {
        this.type = TYPE_USER_GROUP_DETAIL;
        this.imageURL = imageURL;
        this.itemLabel = itemLabel;
        this.itemSubLabel = itemSubLabel;
        this.textColorResource = textColorResource;
    }

    // Constructor for section title
    public TapChatProfileItemModel(@Nullable String sectionTitle) {
        this.type = TYPE_SECTION_TITLE;
        this.itemLabel = sectionTitle;
    }

    // Constructor for menu button
    public TapChatProfileItemModel(int menuId, @Nullable String itemLabel, int iconResource, @ColorRes int iconColorResource, @ColorRes int textColorResource) {
        this.type = TYPE_MENU_BUTTON;
        this.menuId = menuId;
        this.itemLabel = itemLabel;
        this.iconResource = iconResource;
        this.iconColorResource = iconColorResource;
        this.textColorResource = textColorResource;
    }

    // Constructor for media thumbnail
    public TapChatProfileItemModel(@Nullable TAPMessageModel mediaMessage) {
        this.type = TYPE_MEDIA_THUMBNAIL;
        this.mediaMessage = mediaMessage;
    }

    // Constructor for loading layout
    public TapChatProfileItemModel(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public int getIconColorResource() {
        return iconColorResource;
    }

    public void setIconColorResource(int iconColorResource) {
        this.iconColorResource = iconColorResource;
    }

    public int getTextColorResource() {
        return textColorResource;
    }

    public void setTextColorResource(int textColorResource) {
        this.textColorResource = textColorResource;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Nullable
    public TAPMessageModel getMediaMessage() {
        return mediaMessage;
    }

    public void setMediaMessage(@Nullable TAPMessageModel mediaMessage) {
        this.mediaMessage = mediaMessage;
    }

    @Nullable
    public TAPImageURL getImageURL() {
        return imageURL;
    }

    public void setImageURL(@Nullable TAPImageURL imageURL) {
        this.imageURL = imageURL;
    }

    @Nullable
    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(@Nullable String itemLabel) {
        this.itemLabel = itemLabel;
    }

    @Nullable
    public String getItemSubLabel() {
        return itemSubLabel;
    }

    public void setItemSubLabel(@Nullable String itemSubLabel) {
        this.itemSubLabel = itemSubLabel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.menuId);
        dest.writeInt(this.iconResource);
        dest.writeInt(this.iconColorResource);
        dest.writeInt(this.textColorResource);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mediaMessage, flags);
        dest.writeParcelable(this.imageURL, flags);
        dest.writeString(this.itemLabel);
        dest.writeString(this.itemSubLabel);
    }

    protected TapChatProfileItemModel(Parcel in) {
        this.type = in.readInt();
        this.menuId = in.readInt();
        this.iconResource = in.readInt();
        this.iconColorResource = in.readInt();
        this.textColorResource = in.readInt();
        this.isChecked = in.readByte() != 0;
        this.mediaMessage = in.readParcelable(TAPMessageModel.class.getClassLoader());
        this.imageURL = in.readParcelable(TAPImageURL.class.getClassLoader());
        this.itemLabel = in.readString();
        this.itemSubLabel = in.readString();
    }

    public static final Creator<TapChatProfileItemModel> CREATOR = new Creator<TapChatProfileItemModel>() {
        @Override
        public TapChatProfileItemModel createFromParcel(Parcel source) {
            return new TapChatProfileItemModel(source);
        }

        @Override
        public TapChatProfileItemModel[] newArray(int size) {
            return new TapChatProfileItemModel[size];
        }
    };
}
