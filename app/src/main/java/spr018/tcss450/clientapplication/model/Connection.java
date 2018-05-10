package spr018.tcss450.clientapplication.model;

import android.support.annotation.NonNull;

/**
 * This custom connection class hold is used to hold all the information
 * a connection has.
 */
public class Connection implements Comparable<Connection> {

    private String mUserName;
    private String mName;
    private String mRecentMessage;
    private String mEmail;
    private int mMemberID;

    public Connection(String username, String name, int memberID, String email) {
        mUserName = username;
        mName = name;
        mMemberID = memberID;
        mEmail = email;
    }

    public String getUsername() {
        return mUserName;
    }

    public String getName() {
        return mName;
    }

    public void setRecentMessage(String recentMessage) {
        mRecentMessage = recentMessage;
    }

    public String getRecentMessage() {
        return mRecentMessage;
    }

    public int getMemberID() {
        return mMemberID;
    }

    public String getEmail() {
        return mEmail;
    }

    @Override
    public int compareTo(@NonNull Connection connection) {
        return mUserName.compareTo(connection.getUsername());
    }
}
