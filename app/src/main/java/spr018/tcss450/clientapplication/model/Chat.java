package spr018.tcss450.clientapplication.model;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * This custom chat class holds all information about the chat.
 * @author  Tuan Dinh Tenma Rollins Daryan Hanshew Deepjot Kaur
 */
public class Chat implements Comparable<Chat> {

    private String mName;
    private List<String> mMembers;
    private Calendar mTimestamp;
    private int mChatId;
    private String mUsername;
    private String mRecentMessage;

    /*
    * Constructor
    * @param chatName; name of the chat.
    * @param members: list of members in given chat
    * @param timestamp: time that the chat was sent
    * @param chatID: chats id.
    */
    public Chat(String chatName, List<String> members, String timestamp, int chatID) {
        mName = chatName;
        mMembers = members;
        mTimestamp = parseTime(timestamp);
        mChatId = chatID;
    }
    /*
     * Constructor
     * @param chatName; name of the chat.
     * @param recent messages: most recent message
     * @param timestamp: time that the chat was sent
     * @param chatID: chats id.
     */
    public Chat(String chatName, String recentMessage, String timestamp, int chatID) {
        mName = chatName;
        mRecentMessage = recentMessage;
        mTimestamp = parseTime(timestamp);
        mChatId = chatID;
    }
    /*
     * Constructor
     * @param chatName; name of the chat.
     * @param recent messages: most recent message
     * @param timestamp: time that the chat was sent
     * @param chatID: chats id.
     * @param username: name of the sender.
     */
    public Chat(String chatName, String chatRecentMessage, String timestamp, int chatID, String username) {
        mName = chatName;
        mRecentMessage = chatRecentMessage;
        mTimestamp = parseTime(timestamp);
        mChatId = chatID;
        mUsername = username;
    }

    /**
     * Returns the date and time
     * @param timestamp; time stamp of the chat message.
     * @return the date and time.
     */
    private Calendar parseTime(String timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.US);
        Calendar calendar = Calendar.getInstance(Locale.US);
        try {
            calendar.setTime(format.parse(timestamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public String getName() {
        return mName;
    }

    public List<String> getMembers() {
        return mMembers;
    }

    public Calendar getTimestamp() {
        return mTimestamp;
    }

    public int getChatID() {
        return mChatId;
    }

    public String getUsername(){ return mUsername;}

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
        mTimestamp = parseTime(timestamp);
    }

    public void setChatID(int chatID) {
        mChatId = chatID;
    }

    public void setRecentMessage(String recentMessage) {
        mRecentMessage = recentMessage;
    }

    @Override
    public int compareTo(@NonNull Chat connection) {
        return connection.getTimestamp().compareTo(mTimestamp);
    }
}