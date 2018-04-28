package spr018.tcss450.clientapplication.model;

import java.util.ArrayList;

/**
 * Created by Tenma Rollins on 20/04/2018.
 */
public class ChatViewItem {
    private String mChatName;

    public ChatViewItem(String title) {
        this.mChatName = title;
    }

    public String getTitle() { return mChatName; }

    public static ArrayList<ChatViewItem> populateChats(int amount) {
        ArrayList<ChatViewItem> list = new ArrayList<ChatViewItem>();

        for (int i = 0; i < amount; i++) {
            list.add(new ChatViewItem("Chat " + (i+1) ));
        }

        return list;
    }

}
