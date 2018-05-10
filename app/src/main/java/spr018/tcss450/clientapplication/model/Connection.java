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

    public Connection(String username, String name) {
        mUserName = username;
        mName = name;
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

    @Override
    public int compareTo(@NonNull Connection connection) {
        return mUserName.compareTo(connection.getUsername());
    }
}
