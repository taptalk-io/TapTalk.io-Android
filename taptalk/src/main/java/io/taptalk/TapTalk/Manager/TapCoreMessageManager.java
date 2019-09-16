package io.taptalk.TapTalk.Manager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TAPSendMessageWithIDListener;
import io.taptalk.TapTalk.Interface.TapFileDownloadInterface;
import io.taptalk.TapTalk.Interface.TapGetMessageInterface;
import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCoreFileDownloadListener;
import io.taptalk.TapTalk.Listener.TapCoreGetMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetOlderMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreReceiveMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreSendMessageListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_DOWNLOAD_INVALID_MESSAGE_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_DOWNLOAD_INVALID_MESSAGE_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadErrorCode;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadErrorMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadedFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ITEMS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

public class TapCoreMessageManager {

    private static TapCoreMessageManager instance;

    private List<TapCoreReceiveMessageListener> receiveMessageListeners;
    private TAPChatListener chatListener;

    public static TapCoreMessageManager getInstance() {
        return null == instance ? instance = new TapCoreMessageManager() : instance;
    }

    private List<TapCoreReceiveMessageListener> getReceiveMessageListeners() {
        return null == receiveMessageListeners ? receiveMessageListeners = new ArrayList<>() : receiveMessageListeners;
    }

    public void addMessageListener(TapCoreReceiveMessageListener listener) {
        if (getReceiveMessageListeners().isEmpty()) {
            if (null == chatListener) {
                chatListener = new TAPChatListener() {
                    @Override
                    public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
                        for (TapCoreReceiveMessageListener listener : getReceiveMessageListeners()) {
                            listener.onReceiveMessageInActiveRoom(message);
                        }
                    }

                    @Override
                    public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
                        for (TapCoreReceiveMessageListener listener : getReceiveMessageListeners()) {
                            listener.onReceiveMessageInOtherRoom(message);
                        }
                    }

                    @Override
                    public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
                        for (TapCoreReceiveMessageListener listener : getReceiveMessageListeners()) {
                            listener.onUpdateMessageInActiveRoom(message);
                        }
                    }

                    @Override
                    public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
                        for (TapCoreReceiveMessageListener listener : getReceiveMessageListeners()) {
                            listener.onUpdateMessageInOtherRoom(message);
                        }
                    }
                };
            }
            TAPChatManager.getInstance().addChatListener(chatListener);
        }
        getReceiveMessageListeners().add(listener);
    }

    public void removeMessageListener(TapCoreReceiveMessageListener listener) {
        getReceiveMessageListeners().remove(listener);
        if (getReceiveMessageListeners().isEmpty()) {
            TAPChatManager.getInstance().removeChatListener(chatListener);
        }
    }

    public void sendTextMessage(String messageBody, TAPRoomModel room, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().sendTextMessageWithRoomModel(messageBody, room, listener);
    }

    public void sendTextMessage(String messageBody, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendTextMessageWithRoomModel(messageBody, room, listener);
    }

    public void sendLocationMessage(Double latitude, Double longitude, String address, TAPRoomModel room, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().sendLocationMessage(address, latitude, longitude, room, listener);
    }

    public void sendLocationMessage(Double latitude, Double longitude, String address, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendLocationMessage(address, latitude, longitude, room, listener);
    }

    public void sendImageMessage(Uri imageUri, String caption, TAPRoomModel room, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().sendImageMessage(TapTalk.appContext, room, imageUri, caption, listener);
    }

    public void sendImageMessage(Uri imageUri, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendImageMessage(TapTalk.appContext, room, imageUri, caption, listener);
    }

    public void sendImageMessage(Bitmap image, String caption, TAPRoomModel room, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().sendImageMessage(TapTalk.appContext, room, image, caption, listener);
    }

    public void sendImageMessage(Bitmap image, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendImageMessage(TapTalk.appContext, room, image, caption, listener);
    }

    public void sendVideoMessage(Uri videoUri, String caption, TAPRoomModel room, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().sendVideoMessage(TapTalk.appContext, room, videoUri, caption, listener);
    }

    public void sendVideoMessage(Uri videoUri, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendVideoMessage(TapTalk.appContext, room, videoUri, caption, listener);
    }

    public void sendFileMessage(File file, TAPRoomModel room, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().sendFileMessage(TapTalk.appContext, room, file, listener);
    }

    public void sendFileMessage(File file, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendFileMessage(TapTalk.appContext, room, file, listener);
    }

    public void sendForwardedMessage(TAPMessageModel messageToForward, TAPRoomModel room, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), messageToForward, TAPDefaultConstant.QuoteAction.FORWARD);
        TAPChatManager.getInstance().checkAndSendForwardedMessage(room, listener);
    }

    public TAPMessageModel constructTapTalkMessageModel(String messageBody, TAPRoomModel room, Integer type, @Nullable HashMap<String, Object> messageData) {
        return TAPMessageModel.Builder(messageBody, room, type, System.currentTimeMillis(), TAPChatManager.getInstance().getActiveUser(), TYPE_PERSONAL == room.getRoomType() ? TAPChatManager.getInstance().getOtherUserIdFromRoom(room.getRoomID()) : "0", messageData);
    }

    public TAPMessageModel constructTapTalkMessageModelWithQuote(String messageBody, TAPRoomModel room, Integer type, @Nullable HashMap<String, Object> messageData, TAPMessageModel quotedMessage) {
        return TAPMessageModel.BuilderWithQuotedMessage(messageBody, room, type, System.currentTimeMillis(), TAPChatManager.getInstance().getActiveUser(), TYPE_PERSONAL == room.getRoomType() ? TAPChatManager.getInstance().getOtherUserIdFromRoom(room.getRoomID()) : "0", messageData, quotedMessage);
    }

    public void sendCustomMessage(TAPMessageModel customMessage, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance().sendMessage(customMessage, listener);
    }

    public void deleteLocalMessage(String localID) {
        TAPDataManager.getInstance().deleteFromDatabase(localID);
    }

    public void cancelMessageFileUpload(TAPMessageModel message) {
        TAPFileUploadManager.getInstance().cancelUpload(TapTalk.appContext, message, message.getRoom().getRoomID());
        TAPDataManager.getInstance().deleteFromDatabase(message.getLocalID());
    }

    public void downloadMessageFile(TAPMessageModel message, TapCoreFileDownloadListener listener) {
        if (!TAPUtils.getInstance().hasPermissions(TapTalk.appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            listener.onError(ERROR_CODE_DOWNLOAD_INVALID_MESSAGE_TYPE, ERROR_MESSAGE_DOWNLOAD_INVALID_MESSAGE_TYPE);
        } else {
            TAPFileDownloadManager.getInstance().downloadFile(TapTalk.appContext, message);

            BroadcastReceiver downloadProgressReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    String localID = intent.getStringExtra(DownloadLocalID);
                    Integer downloadProgressPercent = TAPFileDownloadManager.getInstance().getDownloadProgressPercent(localID);
                    Long downloadProgressBytes = TAPFileDownloadManager.getInstance().getDownloadProgressBytes(localID);
                    if (null == action) {
                        return;
                    }
                    switch (action) {
                        case DownloadProgressLoading:
                            if (null != downloadProgressPercent && null != downloadProgressBytes) {
                                listener.onProgress(message, downloadProgressPercent, downloadProgressBytes);
                            }
                            break;
                        case DownloadFinish:
                            listener.onSuccess(intent.getParcelableExtra(DownloadedFile));
                            TAPBroadcastManager.unregister(TapTalk.appContext, this);
                            break;
                        case DownloadFailed:
                            listener.onError(intent.getStringExtra(DownloadErrorCode), intent.getStringExtra(DownloadErrorMessage));
                            TAPBroadcastManager.unregister(TapTalk.appContext, this);
                            break;
                    }
                }
            };

            TAPBroadcastManager.register(TapTalk.appContext, downloadProgressReceiver,
                    DownloadProgressLoading, DownloadFinish, DownloadFailed);
        }
    }

    public void cancelMessageFileDownload(TAPMessageModel message) {
        TAPFileDownloadManager.getInstance().cancelFileDownload(message.getLocalID());
    }

    public void markMessageAsRead(TAPMessageModel message) {
        TAPMessageStatusManager.getInstance().addReadMessageQueue(message);
    }

    public void getOlderMessagesBeforeTimestamp(String roomID, long maxCreatedTimestamp, int numberOfItems, TapCoreGetOlderMessageListener listener) {
        TAPDataManager.getInstance().getMessageListByRoomBefore(roomID, maxCreatedTimestamp, numberOfItems,
                new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
                    @Override
                    public void onSuccess(TAPGetMessageListByRoomResponse response) {
                        List<TAPMessageModel> messageAfterModels = new ArrayList<>();
                        for (HashMap<String, Object> messageMap : response.getMessages()) {
                            try {
                                messageAfterModels.add(TAPEncryptorManager.getInstance().decryptMessage(messageMap));
                            } catch (Exception e) {
                                listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                            }
                        }
                        listener.onSuccess(messageAfterModels, response.getHasMore());
                    }

                    @Override
                    public void onError(TAPErrorModel error) {
                        listener.onError(error.getCode(), error.getMessage());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        listener.onError(ERROR_CODE_OTHERS, errorMessage);
                    }
                });
    }

    public void getNewerMessagesAfterTimestamp(String roomID, long minCreatedTimestamp, long lastUpdateTimestamp, TapCoreGetMessageListener listener) {
        TAPDataManager.getInstance().getMessageListByRoomAfter(roomID, minCreatedTimestamp, lastUpdateTimestamp,
                new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
                    @Override
                    public void onSuccess(TAPGetMessageListByRoomResponse response) {
                        List<TAPMessageModel> messageAfterModels = new ArrayList<>();
                        for (HashMap<String, Object> messageMap : response.getMessages()) {
                            try {
                                messageAfterModels.add(TAPEncryptorManager.getInstance().decryptMessage(messageMap));
                            } catch (Exception e) {
                                listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                            }
                        }
                        listener.onSuccess(messageAfterModels);
                    }

                    @Override
                    public void onError(TAPErrorModel error) {
                        listener.onError(error.getCode(), error.getMessage());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        listener.onError(ERROR_CODE_OTHERS, errorMessage);
                    }
                });
    }

    public void getNewerMessages(String roomID, TapCoreGetMessageListener listener) {
        TAPDataManager.getInstance().getMessagesFromDatabaseAsc(roomID, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                long lastUpdateTimestamp = TAPDataManager.getInstance().getLastUpdatedMessageTimestamp(roomID);
                long minCreatedTimestamp = 0L;
                if (entities.size() > 0) {
                    minCreatedTimestamp = entities.get(0).getCreated();
                }
                TAPDataManager.getInstance().getMessageListByRoomAfter(roomID, minCreatedTimestamp, lastUpdateTimestamp,
                        new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
                            @Override
                            public void onSuccess(TAPGetMessageListByRoomResponse response) {
                                List<TAPMessageModel> messageAfterModels = new ArrayList<>();
                                for (HashMap<String, Object> messageMap : response.getMessages()) {
                                    try {
                                        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                                        messageAfterModels.add(message);
                                        if (null != message.getUpdated() &&
                                                TAPDataManager.getInstance().getLastUpdatedMessageTimestamp(roomID) < message.getUpdated()) {
                                            TAPDataManager.getInstance().saveLastUpdatedMessageTimestamp(roomID, message.getUpdated());
                                        }
                                    } catch (Exception e) {
                                        listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                                    }
                                }
                                listener.onSuccess(messageAfterModels);
                            }

                            @Override
                            public void onError(TAPErrorModel error) {
                                listener.onError(error.getCode(), error.getMessage());
                            }

                            @Override
                            public void onError(String errorMessage) {
                                listener.onError(ERROR_CODE_OTHERS, errorMessage);
                            }
                        });
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                listener.onError(ERROR_CODE_OTHERS, errorMessage);
            }
        });
    }

    /**
     * =============================================================================================
     * TEMP
     * =============================================================================================
     */
    private static void sendProductMessage(List<TAPProductModel> productModels, TAPUserModel recipientUserModel) {
        int productSize = productModels.size();
        List<TAPProductModel> tempProductModel = new ArrayList<>();
        for (int index = 1; index <= productSize; index++) {
            tempProductModel.add(productModels.get(index - 1));
            if (index == productSize || index % 20 == 0) {
                HashMap<String, Object> productHashMap = new LinkedHashMap<>();
                productHashMap.put(ITEMS, new ArrayList<>(tempProductModel));
                TAPChatManager.getInstance().sendProductMessageToServer(productHashMap, recipientUserModel);
                tempProductModel.clear();
            }
        }
    }

    private void getUserFromRecipientUserAndSendProductRequestMessage(String message, @NonNull TAPUserModel recipientUser, TAPSendMessageWithIDListener listener) {
        new Thread(() -> {
            try {
                final TAPUserModel myUserModel = TAPChatManager.getInstance().getActiveUser();
                createAndSendProductRequestMessage(message, myUserModel, recipientUser, listener);
            } catch (Exception e) {
                e.printStackTrace();
                listener.sendFailed(new TAPErrorModel("", e.getMessage(), ""));
            }
        }).start();
    }

    private void createAndSendProductRequestMessage(String message, TAPUserModel myUserModel, TAPUserModel otherUserModel, TAPSendMessageWithIDListener listener) {
        TAPRoomModel roomModel = TAPRoomModel.Builder(TAPChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), otherUserModel.getUserID()),
                otherUserModel.getName(), 1, otherUserModel.getAvatarURL(), "#FFFFFF");
        TAPChatManager.getInstance().sendTextMessageWithRoomModel(message, roomModel);
        listener.sendSuccess();
    }
}
