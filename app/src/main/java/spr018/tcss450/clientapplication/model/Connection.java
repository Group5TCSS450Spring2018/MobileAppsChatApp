package spr018.tcss450.clientapplication.model;

import android.support.annotation.NonNull;

/**
 * This custom connection class hold is used to hold all the information
 * a connection has.
 * Username first and last name email, and most recent messages.
 * @author  Tuan Dinh Tenma Rollins Daryan Hanshew Deepjot Kaur
 */
public class Connection implements Comparable<Connection> {

    private String mUserName;
    private String mName;
    private String mRecentMessage;
    private String mEmail;

    public Connection(String username, String name, String email) {
        mUserName = username;
        mName = name;
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

    public String getEmail() {
        return mEmail;
    }

    @Override
    public int compareTo(@NonNull Connection connection) {
        return mUserName.compareTo(connection.getUsername());
    }
}
