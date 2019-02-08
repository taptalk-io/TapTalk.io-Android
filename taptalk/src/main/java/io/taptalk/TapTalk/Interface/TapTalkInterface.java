package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapTalkInterface {
    void onRefreshTokenExpiredOrInvalid();
    void onUserProfileClicked(Activity activity, TAPUserModel userModel);
    void onCustomKeyboardItemClicked(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser);
    List<TAPCustomKeyboardItemModel> onRequestCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser);
    void onProductLeftButtonClicked(Activity activity, TAPProductModel productModel, String recipientXcUserID, TAPRoomModel room);
    void onProductRightButtonClicked(Activity activity, TAPProductModel productModel, String recipientXcUserID, TAPRoomModel room);
    void onMessageQuoteClicked(Activity activity, TAPMessageModel messageModel, HashMap<String, Object> userInfo);
    void onUpdateUnreadCount(int unreadCount);
}
