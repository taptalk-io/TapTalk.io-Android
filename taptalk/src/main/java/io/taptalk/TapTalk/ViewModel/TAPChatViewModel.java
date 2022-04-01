package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LOADING_INDICATOR_LOCAL_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_DATE_SEPARATOR;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER;

public class TAPChatViewModel extends AndroidViewModel {

    private static final String TAG = TAPChatViewModel.class.getSimpleName();
    private String instanceKey = "";
    private LiveData<List<TAPMessageEntity>> allMessages;
    private Map<String, TAPMessageModel> messagePointer, unreadMessages, unreadMentions;
    private Map<String, TAPUserModel> roomParticipantsByUsername;
    private Map<String, List<Integer>> messageMentionIndexes;
    private LinkedHashMap<String, TAPUserModel> groupTyping;
    private LinkedHashMap<String, TAPMessageModel> dateSeparators;
    private LinkedHashMap<String, Integer> dateSeparatorIndexes;
    private List<TAPMessageModel> messageModels, pendingRecyclerMessages;
    private List<TAPCustomKeyboardItemModel> customKeyboardItems;
    private ArrayList<String> starredMessageIds;
    private TAPUserModel myUserModel, otherUserModel;
    private TAPRoomModel room;
    private TAPMessageModel quotedMessage, pendingDownloadMessage, openedFileMessage, unreadIndicator, loadingIndicator;
    private TAPOnlineStatusModel onlineStatus;
    private Uri cameraImageUri;
    private Handler lastActivityHandler;
    private String tappedMessageLocalID;
    private String lastUnreadMessageLocalID;
    private Integer quoteAction;
    private TAPGetMessageListByRoomResponse pendingAfterResponse;
    private long lastTimestamp = 0;
    private int initialUnreadCount, numUsers, containerAnimationState, firstVisibleItemIndex;
    private boolean isOnBottom, isActiveUserTyping, isOtherUserTyping, isCustomKeyboardEnabled,
            isInitialAPICallFinished, isUnreadButtonShown, isNeedToShowLoading,
            isScrollFromKeyboard, isAllUnreadMessagesHidden, deleteGroup;
    private boolean isHasMoreData = true;
    public final int IDLE = 0;
    public final int ANIMATING = 1;
    public final int PROCESSING = 2;

    public static class TAPChatViewModelFactory implements ViewModelProvider.Factory {
        private Application application;
        private String instanceKey;

        public TAPChatViewModelFactory(Application application, String instanceKey) {
            this.application = application;
            this.instanceKey = instanceKey;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TAPChatViewModel(application, instanceKey);
        }
    }

    public TAPChatViewModel(Application application, String instanceKey) {
        super(application);
        this.instanceKey = instanceKey;
        allMessages = TAPDataManager.getInstance(instanceKey).getMessagesLiveData();
        setOnBottom(true);
    }

    public String getInstanceKey() {
        return instanceKey;
    }

    public void setInstanceKey(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public LiveData<List<TAPMessageEntity>> getAllMessages() {
        return allMessages;
    }

    public void delete(String messageLocalID) {
        TAPDataManager.getInstance(instanceKey).deleteFromDatabase(messageLocalID);
    }

    public void removeFromUploadingList(String messageLocalID) {
        TAPChatManager.getInstance(instanceKey).removeUploadingMessageFromHashMap(messageLocalID);
    }

    public Map<String, TAPMessageModel> getMessagePointer() {
        return messagePointer == null ? messagePointer = new LinkedHashMap<>() : messagePointer;
    }

    public void setMessagePointer(Map<String, TAPMessageModel> messagePointer) {
        this.messagePointer = messagePointer;
    }

    public void addMessagePointer(TAPMessageModel pendingMessage) {
        getMessagePointer().put(pendingMessage.getLocalID(), pendingMessage);
    }

    public void removeMessagePointer(String localID) {
        getMessagePointer().remove(localID);
    }

    public void updateMessagePointer(TAPMessageModel newMessage) {
        TAPMessageModel message = getMessagePointer().get(newMessage.getLocalID());
        if (null != message) {
            message.updateValue(newMessage);
        }
    }

    public Map<String, TAPMessageModel> getUnreadMessages() {
        return unreadMessages == null ? unreadMessages = new LinkedHashMap<>() : unreadMessages;
    }

    public int getUnreadCount() {
        return getUnreadMessages().size();
    }

    public void addUnreadMessage(TAPMessageModel unreadMessage) {
        getUnreadMessages().put(unreadMessage.getLocalID(), unreadMessage);
        if (TAPUtils.isActiveUserMentioned(unreadMessage, myUserModel)) {
            addUnreadMention(unreadMessage);
        }
    }

    public void removeUnreadMessage(String localID) {
        getUnreadMessages().remove(localID);
        if (TAPUtils.isActiveUserMentioned(getMessagePointer().get(localID), myUserModel)) {
            removeUnreadMention(localID);
        }
    }

    public void clearUnreadMessages() {
        getUnreadMessages().clear();
    }

    public Map<String, TAPUserModel> getRoomParticipantsByUsername() {
        return roomParticipantsByUsername == null ? roomParticipantsByUsername = new LinkedHashMap<>() : roomParticipantsByUsername;
    }

    public void addRoomParticipantByUsername(TAPUserModel user) {
        if (null == user.getUsername() || user.getUsername().isEmpty()) {
            return;
        }
        getRoomParticipantsByUsername().put(user.getUsername(), user);
    }

    public Map<String, List<Integer>> getMessageMentionIndexes() {
        return messageMentionIndexes == null ? messageMentionIndexes = new LinkedHashMap<>() : messageMentionIndexes;
    }

    public void addMessageMentionIndexes(String localID, List<Integer> indexes) {
        getMessageMentionIndexes().put(localID, indexes);
    }

    public Map<String, TAPMessageModel> getUnreadMentions() {
        return unreadMentions == null ? unreadMentions = new LinkedHashMap<>() : unreadMentions;
    }

    public int getUnreadMentionCount() {
        return getUnreadMentions().size();
    }

    public void addUnreadMention(TAPMessageModel unreadMessage) {
        getUnreadMentions().put(unreadMessage.getLocalID(), unreadMessage);
    }

    public void removeUnreadMention(String localID) {
        getUnreadMentions().remove(localID);
    }

    public void clearUnreadMentions() {
        getUnreadMentions().clear();
    }

    public void getMessageEntities(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDataManager.getInstance(instanceKey).getMessagesFromDatabaseDesc(roomID, listener);
    }

    public void getMessageByTimestamp(String roomID, TAPDatabaseListener<TAPMessageEntity> listener, long lastTimestamp) {
        TAPDataManager.getInstance(instanceKey).getMessagesFromDatabaseDesc(roomID, listener, lastTimestamp);
    }

    public List<TAPMessageModel> getMessageModels() {
        return messageModels == null ? messageModels = new ArrayList<>() : messageModels;
    }

    public void setMessageModels(List<TAPMessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    public void addMessageModels(List<TAPMessageModel> messageModels) {
        getMessageModels().addAll(messageModels);
    }

    public List<TAPMessageModel> getPendingRecyclerMessages() {
        return null == pendingRecyclerMessages ? pendingRecyclerMessages = new ArrayList<>() : pendingRecyclerMessages;
    }

    public void setPendingRecyclerMessages(List<TAPMessageModel> pendingRecyclerMessages) {
        this.pendingRecyclerMessages = pendingRecyclerMessages;
    }

    public void addPendingRecyclerMessage(TAPMessageModel message) {
        getPendingRecyclerMessages().add(message);
    }

    public void removePendingRecyclerMessage(TAPMessageModel message) {
        getPendingRecyclerMessages().remove(message);
    }

    public List<TAPCustomKeyboardItemModel> getCustomKeyboardItems() {
        return null == customKeyboardItems ? customKeyboardItems = new ArrayList<>() : customKeyboardItems;
    }

    public void setCustomKeyboardItems(List<TAPCustomKeyboardItemModel> customKeyboardItems) {
        this.customKeyboardItems = customKeyboardItems;
    }

    public TAPUserModel getMyUserModel() {
        return myUserModel;
    }

    public void setMyUserModel(TAPUserModel myUserModel) {
        this.myUserModel = myUserModel;
    }

    public TAPUserModel getOtherUserModel() {
        return otherUserModel;
    }

    public void setOtherUserModel(TAPUserModel otherUserModel) {
        this.otherUserModel = otherUserModel;
    }

    public TAPRoomModel getRoom() {
        return room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
        TAPChatManager.getInstance(instanceKey).setActiveRoom(room);
    }

    public TAPMessageModel getQuotedMessage() {
        return null == quotedMessage ? TAPChatManager.getInstance(instanceKey).getQuotedMessage(room.getRoomID()) : quotedMessage;
    }

    public int getInitialUnreadCount() {
        return initialUnreadCount;
    }

    public void setInitialUnreadCount(int initialUnreadCount) {
        this.initialUnreadCount = initialUnreadCount;
    }

    public Integer getQuoteAction() {
        return null == quoteAction ? null == TAPChatManager.getInstance(instanceKey).getQuoteAction(room.getRoomID()) ? -1 : TAPChatManager.getInstance(instanceKey).getQuoteAction(room.getRoomID()) : quoteAction;
    }

    public void setQuotedMessage(TAPMessageModel quotedMessage, int quoteAction) {
        this.quotedMessage = quotedMessage;
        this.quoteAction = quoteAction;
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, quoteAction);
    }

    public TAPGetMessageListByRoomResponse getPendingAfterResponse() {
        return pendingAfterResponse;
    }

    public void setPendingAfterResponse(TAPGetMessageListByRoomResponse pendingAfterResponse) {
        this.pendingAfterResponse = pendingAfterResponse;
    }

    public String getTappedMessageLocalID() {
        return tappedMessageLocalID;
    }

    public void setTappedMessageLocalID(String tappedMessageLocalID) {
        this.tappedMessageLocalID = tappedMessageLocalID;
    }

    /**
     * Used to save pending download message when requesting storage permission
     */
    public TAPMessageModel getPendingDownloadMessage() {
        return pendingDownloadMessage;
    }

    public void setPendingDownloadMessage(TAPMessageModel pendingDownloadMessage) {
        this.pendingDownloadMessage = pendingDownloadMessage;
    }

    /**
     * Used to save file-type message when user opens file from chat bubble
     */
    public TAPMessageModel getOpenedFileMessage() {
        return openedFileMessage;
    }

    public void setOpenedFileMessage(TAPMessageModel openedFileMessage) {
        this.openedFileMessage = openedFileMessage;
    }

    public TAPMessageModel getUnreadIndicator() {
        return unreadIndicator;
    }

    public void setUnreadIndicator(TAPMessageModel unreadIndicator) {
        this.unreadIndicator = unreadIndicator;
    }

    public TAPMessageModel getLoadingIndicator(boolean updateCreated) {
        if (null == loadingIndicator) {
            loadingIndicator = new TAPMessageModel();
            loadingIndicator.setType(TYPE_LOADING_MESSAGE_IDENTIFIER);
            loadingIndicator.setLocalID(LOADING_INDICATOR_LOCAL_ID);
            loadingIndicator.setUser(TAPChatManager.getInstance(instanceKey).getActiveUser());
        }
        if (updateCreated) {
            // Update created time for loading indicator to array's last message created time
            if (getMessageModels().isEmpty()) {
                loadingIndicator.setCreated(0L);
            } else {
                loadingIndicator.setCreated(getMessageModels().get(getMessageModels().size() - 1).getCreated() - 1L);
            }
        }
        return loadingIndicator;
    }

    public LinkedHashMap<String, TAPMessageModel> getDateSeparators() {
        return null == dateSeparators ? dateSeparators = new LinkedHashMap<>() : dateSeparators;
    }

    public LinkedHashMap<String, Integer> getDateSeparatorIndexes() {
        return null == dateSeparatorIndexes ? dateSeparatorIndexes = new LinkedHashMap<>() : dateSeparatorIndexes;
    }

    public TAPMessageModel generateDateSeparator(Context context, TAPMessageModel message) {
        return TAPMessageModel.Builder(
                TAPTimeFormatter.dateStampString(context, message.getCreated()),
                getRoom(),
                TYPE_DATE_SEPARATOR,
                message.getCreated() - 1,
                getMyUserModel(),
                "",
                null);
    }

    public TAPOnlineStatusModel getOnlineStatus() {
        return null == onlineStatus ? onlineStatus = new TAPOnlineStatusModel(false, 0L) : onlineStatus;
    }

    public void setOnlineStatus(TAPOnlineStatusModel onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Uri getCameraImageUri() {
        return cameraImageUri;
    }

    public void setCameraImageUri(Uri cameraImageUri) {
        this.cameraImageUri = cameraImageUri;
    }

    public Handler getLastActivityHandler() {
        return null == lastActivityHandler ? lastActivityHandler = new Handler() : lastActivityHandler;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public int getContainerAnimationState() {
        return containerAnimationState;
    }

    public void setContainerAnimationState(int containerAnimationState) {
        this.containerAnimationState = containerAnimationState;
    }

    public int getFirstVisibleItemIndex() {
        return firstVisibleItemIndex;
    }

    public void setFirstVisibleItemIndex(int firstVisibleItemIndex) {
        this.firstVisibleItemIndex = firstVisibleItemIndex;
    }

    public boolean isActiveUserTyping() {
        return isActiveUserTyping;
    }

    public void setActiveUserTyping(boolean activeUserTyping) {
        isActiveUserTyping = activeUserTyping;
    }

    public LinkedHashMap<String, TAPUserModel> getGroupTyping() {
        return null == groupTyping ? groupTyping = new LinkedHashMap<>() : groupTyping;
    }

    public void setGroupTyping(LinkedHashMap<String, TAPUserModel> groupTyping) {
        this.groupTyping = groupTyping;
    }

    public void removeGroupTyping(String userID) {
        getGroupTyping().remove(userID);
    }

    public boolean removeAndCheckIfNeedToDismissTyping(String userID) {
        if (null == userID || getGroupTyping().containsKey(userID)) return false;

        removeGroupTyping(userID);

        return 0 == getGroupTyping().size();
    }

    public void addGroupTyping(TAPUserModel typingUsers) {
        if (null != typingUsers) {
            getGroupTyping().put(typingUsers.getUserID(), typingUsers);
        }
    }

    public int getGroupTypingSize() {
        return getGroupTyping().size();
    }

    public String getFirstTypingUserName() {
        if (0 >= getGroupTypingSize()) return "";

        TAPUserModel firstTypingUser = getGroupTyping().entrySet().iterator().next().getValue();
        return firstTypingUser.getFullname().split(" ")[0];
    }

    public boolean isCustomKeyboardEnabled() {
        return isCustomKeyboardEnabled;
    }

    public void setCustomKeyboardEnabled(boolean customKeyboardEnabled) {
        isCustomKeyboardEnabled = customKeyboardEnabled;
    }

    public boolean isInitialAPICallFinished() {
        return isInitialAPICallFinished;
    }

    public void setInitialAPICallFinished(boolean initialAPICallFinished) {
        isInitialAPICallFinished = initialAPICallFinished;
    }

    public boolean isOnBottom() {
        return isOnBottom;
    }

    public void setOnBottom(boolean onBottom) {
        isOnBottom = onBottom;
    }

    /**
     * Unread button will only show once
     */
    public boolean isUnreadButtonShown() {
        return isUnreadButtonShown;
    }

    public void setUnreadButtonShown(boolean unreadButtonShown) {
        isUnreadButtonShown = unreadButtonShown;
    }

    /**
     * Show loading when fetching older messages
     */
    public boolean isNeedToShowLoading() {
        return isNeedToShowLoading;
    }

    public void setNeedToShowLoading(boolean needToShowLoading) {
        isNeedToShowLoading = needToShowLoading;
    }

    public int getMessageSize() {
        if (null != allMessages.getValue()) {
            return allMessages.getValue().size();
        }
        return 0;
    }

    public String getOtherUserID() {
        try {
            String[] tempUserID = room.getRoomID().split("-");
            return tempUserID[0].equals(myUserModel.getUserID()) ? tempUserID[1] : tempUserID[0];
        } catch (Exception e) {
//            Log.e(TAG, "getOtherUserID: ", e);
            return "0";
        }
    }

    public String getLastUnreadMessageLocalID() {
        return null == lastUnreadMessageLocalID ? "" : lastUnreadMessageLocalID;
    }

    public void setLastUnreadMessageLocalID(String lastUnreadMessageLocalID) {
        this.lastUnreadMessageLocalID = lastUnreadMessageLocalID;
    }

    public boolean isScrollFromKeyboard() {
        return isScrollFromKeyboard;
    }

    public void setScrollFromKeyboard(boolean scrollFromKeyboard) {
        isScrollFromKeyboard = scrollFromKeyboard;
    }

    public boolean isAllUnreadMessagesHidden() {
        return isAllUnreadMessagesHidden;
    }

    public void setAllUnreadMessagesHidden(boolean allUnreadMessagesHidden) {
//        Log.e(TAG, "setAllUnreadMessagesHidden: " + allUnreadMessagesHidden);
        isAllUnreadMessagesHidden = allUnreadMessagesHidden;
    }

    public boolean isDeleteGroup() {
        return deleteGroup;
    }

    public void setDeleteGroup(boolean deleteGroup) {
        this.deleteGroup = deleteGroup;
    }

    public ArrayList<String> getStarredMessageIds() {
        return null == starredMessageIds? new ArrayList<>() : starredMessageIds;
    }

    public void setStarredMessageIds(ArrayList<String> starredMessageIds) {
        this.starredMessageIds = starredMessageIds;
    }

    public void addStarredMessageId(String messageId) {
        if (starredMessageIds == null) {
            starredMessageIds = new ArrayList<>();
        }
        this.starredMessageIds.add(messageId);
    }

    public void removeStarredMessageId(String messageId) {
        if (starredMessageIds == null) {
            starredMessageIds = new ArrayList<>();
        }
        this.starredMessageIds.remove(messageId);
    }

    public boolean isHasMoreData() {
        return isHasMoreData;
    }

    public void setHasMoreData(boolean hasMoreData) {
        isHasMoreData = hasMoreData;
    }
}
