package spr018.tcss450.clientapplication.model;

import android.support.annotation.NonNull;

import java.sql.Timestamp;
import java.util.List;

/**
 * This custom connection class hold is used to hold all the information
 * a connection has.
 */
public class Chat implements Comparable<Chat> {

    private String mName;
    private List<String> mMembers;
    private String mTimestamp;
    private int mChatId;

    private String mRecentMessage;

    public Chat(String chatName, List<String> members, String timestamp, int chatID) {
        mName = chatName;
        mMembers = members;
        mTimestamp = timestamp;
        mChatId = chatID;
    }

    public Chat(String chatName, String recentMessage, String timestamp, int chatID) {
        mName = chatName;
        mRecentMessage = recentMessage;
        mTimestamp = timestamp;
        mChatId = chatID;
    }

    public String getName() {
        return mName;
    }

    public List<String> getMembers() {
        return mMembers;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public int getChatID() {
        return mChatId;
    }

    public String getRecentMessage() { return mRecentMessage; }

    public void setName(String name) {
        mName = name;
    }

    public void addMember(String member) {
        mMembers.add(member);
    }

    public void removeMember(String member) {
        mMembers.remove(member);
    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }

    public void setChatID(int chatID) {
        mChatId = chatID;
    }

    public  void setRecentMessage(String recentMessage) {
        mRecentMessage = recentMessage;
    }

    @Override
    public int compareTo(@NonNull Chat connection) {
        return Timestamp.valueOf(mTimestamp).compareTo(Timestamp.valueOf(connection.mTimestamp));
    }
}
