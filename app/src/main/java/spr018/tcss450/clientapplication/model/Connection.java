package spr018.tcss450.clientapplication.model;

import android.support.annotation.NonNull;

public class Connection implements Comparable<Connection> {

    private String mUserName;

    public Connection(String username) {
        mUserName = username;
    }

    public String getUsername() {
        return mUserName;
    }

    @Override
    public int compareTo(@NonNull Connection connection) {
        return mUserName.compareTo(connection.getUsername());
    }
}
