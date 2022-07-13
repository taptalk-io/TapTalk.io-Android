package io.taptalk.TapTalk.Manager;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Data.Contact.TAPMyContactRepository;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Data.Message.TAPMessageRepository;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchRepository;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.MESSAGE_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.MY_CONTACT_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.SEARCH_DB;

public class TAPDatabaseManager {

    private static HashMap<String, TAPDatabaseManager> instances;

    private String instanceKey = "";

    private TAPMessageRepository messageRepository;
    private TAPMyContactRepository myContactRepository;
    private TAPRecentSearchRepository searchRepository;

    public TAPDatabaseManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TAPDatabaseManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPDatabaseManager instance = new TAPDatabaseManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPDatabaseManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    // TODO: 023, 23 Mar 2020
    public void setRepository(String databaseType, Application application) {
        switch (databaseType) {
            case MESSAGE_DB:
                messageRepository = new TAPMessageRepository(instanceKey, application);
                break;
            case SEARCH_DB:
                searchRepository = new TAPRecentSearchRepository(instanceKey, application);
                break;
            case MY_CONTACT_DB:
                myContactRepository = new TAPMyContactRepository(instanceKey, application);
        }
    }

    /**
     * ==============================================================
     * Message Table
     * ==============================================================
     */

    public void deleteMessage(List<TAPMessageEntity> messageEntities, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.delete(messageEntities, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void deleteRoomMessageBeforeTimestamp(String roomID, long minimumTimestamp, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.deleteRoomMessageBeforeTimestamp(roomID, minimumTimestamp, listener);
        else throw new IllegalStateException("Message Repository was not initialized");
    }

    public void insert(TAPMessageEntity messageEntity) {
        if (null != messageRepository)
            messageRepository.insert(messageEntity);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(List<TAPMessageEntity> messageEntities, boolean isClearSaveMessages) {
        if (null != messageRepository)
            messageRepository.insert(messageEntities, isClearSaveMessages);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(List<TAPMessageEntity> messageEntities, boolean isClearSaveMessages, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.insert(messageEntities, isClearSaveMessages, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void delete(String localID) {
        if (null != messageRepository)
            messageRepository.delete(localID);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void deleteAllMessage() {
        if (null != messageRepository)
            messageRepository.deleteAllMessage();
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void updatePendingStatus(String localID) {
        if (null != messageRepository)
            messageRepository.updatePendingStatus(localID);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void updateFailedStatusToSending(String localID) {
        if (null != messageRepository)
            messageRepository.updateFailedStatusToSending(localID);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void updateMessageAsRead(String messageID) {
        if (null != messageRepository)
            messageRepository.updateMessageAsRead(messageID);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void updateMessagesAsRead(List<String> messageIDs) {
        if (null != messageRepository)
            messageRepository.updateMessagesAsRead(messageIDs);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public LiveData<List<TAPMessageEntity>> getMessagesLiveData() {
        if (null != messageRepository)
            return messageRepository.getAllMessagesLiveData();
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getAllMessagesInRoom(String roomID, boolean excludeHidden, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getAllMessagesInRoom(roomID, excludeHidden, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessagesDesc(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getMessageListDesc(roomID, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessagesDesc(String roomID, TAPDatabaseListener<TAPMessageEntity> listener, long lastTimestamp) {
        if (null != messageRepository)
            messageRepository.getMessageListDesc(roomID, listener, lastTimestamp);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getRoomMessagesBeforeTimestampDesc(String roomID, TAPDatabaseListener<TAPMessageEntity> listener, long lastTimestamp, int itemLimit, boolean excludeHidden) {
        if (null != messageRepository)
            messageRepository.getRoomMessagesBeforeTimestampDesc(roomID, listener, lastTimestamp, itemLimit, excludeHidden);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessagesAsc(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getMessageListAsc(roomID, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void searchAllMessages(String keyword, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.searchAllMessages(keyword, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void searchAllRoomMessages(String keyword, String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.searchAllRoomMessages(keyword, roomID, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessageByLocalID(String localID, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getMessageByLocalID(localID, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessageByLocalIDs(List<String> localIDs, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getMessageByLocalIDs(localIDs, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessageBySenderUserID(String userID, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getMessageBySenderUserID(userID, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getRoomList(String myID, String username, List<TAPMessageEntity> saveMessages, boolean isCheckUnreadFirst, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getRoomList(myID, username, saveMessages, isCheckUnreadFirst, listener);
        else throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getAllUnreadMessagesFromRoom(String myID, String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getAllUnreadMessagesFromRoom(myID, roomID, listener);
        else throw new IllegalStateException("Message Repository was not initialized");
    }

    public void getAllUnreadMentionsFromRoom(String myID, String username, String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getAllUnreadMentionsFromRoom(myID, username, roomID, listener);
        else throw new IllegalStateException("Message Repository was not initialized");
    }

    public void getRoomList(String myID, String username, boolean isCheckUnreadFirst, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getRoomList(myID, username, isCheckUnreadFirst, listener);
        else throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void searchAllRooms(String myID, String username, String keyword, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.searchAllChatRooms(myID, username, keyword, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getRoomMedias(Long lastTimestamp, String roomID, int numberOfItems, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getRoomMedias(lastTimestamp, roomID, numberOfItems, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getRoomMediaMessageBeforeTimestamp(String roomID, long minimumTimestamp, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getRoomMediaMessageBeforeTimestamp(roomID, minimumTimestamp, listener);
        else throw new IllegalStateException("Message Repository was not initialized");
    }

    public void getRoomMediaMessage(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getRoomMediaMessage(roomID, listener);
        else throw new IllegalStateException("Message Repository was not initialized");
    }

    public void getRoom(String myID, TAPUserModel otherUserModel, TAPDatabaseListener<TAPRoomModel> listener) {
        if (null != messageRepository)
            messageRepository.getRoom(myID, otherUserModel, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getUnreadCountPerRoom(String myID, String username, String roomID, final TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getUnreadCountPerRoom(myID, username, roomID, listener);
        else throw new IllegalStateException("Message Repository was not initialized");
    }

    public void getUnreadCount(String myID, final TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.getUnreadCount(myID, listener);
        else throw new IllegalStateException("Message Repository was not initialized");
    }

    public void getMinCreatedOfUnreadMessage(String myID, String roomID, final TAPDatabaseListener<Long> listener) {
        if (null != messageRepository) {
            messageRepository.getMinCreatedOfUnreadMessage(myID, roomID, listener);
        } else throw new IllegalStateException("Message Repository was not initialized");
    }

    public void getOldestCreatedTimeFromRoom( String roomID, final TAPDatabaseListener<Long> listener) {
        if (null != messageRepository) {
            messageRepository.getOldestCreatedTimeFromRoom(roomID, listener);
        } else throw new IllegalStateException("Message Repository was not initialized");
    }

    public void updatePendingStatus() {
        if (null != messageRepository)
            messageRepository.updatePendingStatus();
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    /**
     * ==============================================================
     * Recent Search Table
     * ==============================================================
     */

    public void insert(TAPRecentSearchEntity recentSearchEntity) {
        if (null != recentSearchEntity)
            searchRepository.insert(recentSearchEntity);
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public void delete(TAPRecentSearchEntity recentSearchEntity) {
        if (null != recentSearchEntity)
            searchRepository.delete(recentSearchEntity);
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public void delete(List<TAPRecentSearchEntity> recentSearchEntities) {
        if (null != searchRepository)
            searchRepository.delete(recentSearchEntities);
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public void deleteAllRecentSearch() {
        if (null != searchRepository)
            searchRepository.deleteAllRecentSearch();
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public LiveData<List<TAPRecentSearchEntity>> getRecentSearchLive() {
        if (null != searchRepository)
            return searchRepository.getAllRecentSearch();
        else throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    /**
     * ==============================================================
     * My Contact Table
     * ==============================================================
     */

    public void getMyContactList(TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.getAllMyContactList(listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void getNonContactUsers(TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.getNonContactUsers(listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public LiveData<List<TAPUserModel>> getMyContactList() {
        if (null != myContactRepository)
            return myContactRepository.getMyContactListLive();
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void searchContactsByName(String keyword, TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.searchContactsByName(keyword, listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void searchContactsByNameAndUsername(String keyword, TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.searchContactsByNameAndUsername(keyword, listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void searchNonContactUsers(String keyword, TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.searchNonContactUsers(keyword, listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void insertMyContact(TAPUserModel... userModels) {
        if (null != myContactRepository)
            myContactRepository.insert(userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void insertMyContact(TAPDatabaseListener<TAPUserModel> listener, TAPUserModel... userModels) {
        if (null != myContactRepository)
            myContactRepository.insert(listener, userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void insertMyContact(List<TAPUserModel> userModels) {
        if (null != myContactRepository)
            myContactRepository.insert(userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void checkContactAndInsert(TAPUserModel userModel) {
        if (null != myContactRepository)
            myContactRepository.checkContactAndInsert(userModel);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void getUserWithXcUserID(String xcUserID, TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.getUserWithXcUserID(xcUserID, listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void insertAndGetMyContact(List<TAPUserModel> userModels, TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.insertAndGetContact(userModels, listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void deleteMyContact(TAPUserModel... userModels) {
        if (null != myContactRepository)
            myContactRepository.delete(userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void deleteMyContact(List<TAPUserModel> userModels) {
        if (null != myContactRepository)
            myContactRepository.delete(userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void deleteAllContact() {
        if (null != myContactRepository)
            myContactRepository.deleteAllContact();
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void updateMyContact(TAPUserModel userModels) {
        if (null != myContactRepository)
            myContactRepository.update(userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void checkUserInMyContacts(String userID, TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.checkUserInMyContacts(userID, listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void getAllUserData(TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.getAllUserData(listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void deleteMessageByRoomId(String roomId, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null != messageRepository)
            messageRepository.deleteMessageByRoomId(roomId, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }
}
