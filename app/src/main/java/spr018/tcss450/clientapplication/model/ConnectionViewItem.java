package spr018.tcss450.clientapplication.model;

import java.util.ArrayList;

/**
 * Created by Tenma Rollins on 20/04/2018.
 */
public class ConnectionViewItem {
    private String mConnectionName;

    public ConnectionViewItem(String name) {
        this.mConnectionName = name;
    }

    public String getName() { return mConnectionName; }

    public static ArrayList<ConnectionViewItem> populateConnections(int amount) {
        ArrayList<ConnectionViewItem> list = new ArrayList<ConnectionViewItem>();

        for (int i = 0; i < amount; i++) {
            list.add(new ConnectionViewItem("Connection " + (i+1) ));
        }

        return list;
    }

}
