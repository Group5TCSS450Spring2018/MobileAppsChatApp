package spr018.tcss450.clientapplication.model;

import android.support.annotation.NonNull;

import java.sql.Timestamp;

/**
 * This custom connection class hold is used to hold all the information
 * a connection has.
 */
public class Chat implements Comparable<Chat> {

    private String mName;
    private String mRecentMessage;
    private String mTimestamp;
    private int mChatId;
    private String mUsername;

    public Chat(String chatName, String chatRecentMessage, String timestamp, int chatID, String username) {
        mName = chatName;
        mRecentMessage = chatRecentMessage;
        mTimestamp = timestamp;
        mChatId = chatID;
        mUsername = username;
    }

    public String getName() {
        return mName;
    }

    public String getRecentMessage() {
        return mRecentMessage;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public int getChatID() {
        return mChatId;
    }

    public String getUsername(){ return mUsername;}

    public void setName(String name) {
        mName = name;
    }

    public void setRecentMessage(String recentMessage) {
        mRecentMessage = mRecentMessage;
    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }

    public void setChatID(int chatID) {
        mChatId = chatID;
    }

    @Override
    public int compareTo(@NonNull Chat connection) {
        return Timestamp.valueOf(mTimestamp).compareTo(Timestamp.valueOf(connection.mTimestamp));
    }
}
