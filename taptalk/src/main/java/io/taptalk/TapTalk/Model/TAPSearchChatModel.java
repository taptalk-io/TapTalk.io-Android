package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPSearchChatModel {
    public enum Type {
        RECENT_TITLE, SECTION_TITLE, MESSAGE_ITEM, ROOM_ITEM, EMPTY_STATE
    }

    private Type type;
    private String sectionTitle;
    private TAPRoomModel room;
    private TAPMessageEntity message;
    private TAPUserModel contact;
    private boolean isLastInSection;
    private int roomMentionCount;

    public static TAPSearchChatModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPSearchChatModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public TAPSearchChatModel(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public TAPRoomModel getRoom() {
        return room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
    }

    public TAPMessageEntity getMessage() {
        return message;
    }

    public void setMessage(TAPMessageEntity message) {
        this.message = message;
    }

    public TAPUserModel getContact() {
        return contact;
    }

    public void setContact(TAPUserModel contact) {
        this.contact = contact;
    }

    public boolean isLastInSection() {
        return isLastInSection;
    }

    public void setLastInSection(boolean lastInSection) {
        isLastInSection = lastInSection;
    }

    public int getRoomMentionCount() {
        return roomMentionCount;
    }

    public void setRoomMentionCount(int roomMentionCount) {
        this.roomMentionCount = roomMentionCount;
    }
}
