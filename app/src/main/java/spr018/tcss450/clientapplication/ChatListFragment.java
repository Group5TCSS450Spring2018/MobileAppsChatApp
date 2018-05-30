package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import spr018.tcss450.clientapplication.model.Chat;
import spr018.tcss450.clientapplication.model.ChatPreviewAdapter;
import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.model.ConnectionAdapter;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Displays the list of chats you are in.
 * @author  Tuan Dinh Tenma Rollins Daryan Hanshew Deepjot Kaur
 */
public class ChatListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private List<Chat> mChatList;
    private ChatPreviewAdapter mAdapter;
    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);


        mChatList = new ArrayList<>();

        Log.d("mChatList size", "" +  mChatList.size());
        RecyclerView chats = v.findViewById(R.id.chatListContainer);
        chats.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ChatPreviewAdapter(mChatList);
        mAdapter.setOnItemClickListener(this::onItemClicked);
        chats.setAdapter(mAdapter);
        checkChats();
        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        checkChats();
    }

    /**
     * Check the chats you are in. Call a method to display a list of chats.
     */
    private void checkChats() {
        //send get connections the username.
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String u = prefs.getString(getString(R.string.keys_prefs_user_name), "");

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_everyChatParticipant))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username", u);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleViewConnections)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

    }

    /**
     * Display the chats to the screen. Parse through the results and put in to a hashmap.
     * @param results; Results from the database.
     */
    private void handleViewConnections(String results) {
        try {
            JSONObject resultJSON = new JSONObject(results);
            if(!resultJSON.has("error")) {
                try {
                    JSONArray chatList = resultJSON.getJSONArray("message");
                    mChatList.clear();
                    if(chatList.length() == 0) {
                        mChatList.add(null);
                    } else {
                        HashMap<Integer, Chat> chats = new HashMap<Integer, Chat>();
                        for (int i = 0; i < chatList.length(); i++) {
                            JSONObject c = chatList.getJSONObject(i);
                            if (chats.get(c.getInt("chatid")) != null) {
                                chats.get(c.getInt("chatid")).addMember(c.getString("username"));
                            } else {
                                Chat chat = new Chat(c.getString("name"), new ArrayList<String>() , "", c.getInt("chatid"));
                                chat.addMember(c.getString("username"));
                                chats.put(c.getInt("chatid"), chat);
                            }
                        }

                        for (Integer key : chats.keySet()) {
                            mChatList.add(chats.get(key));
                        }

                        /* TODO: FIX THIS BY FORMATTING TIMESTAMP CORRECTLY */
                        //Collections.sort(mChatList);

                        mAdapter.setOnItemClickListener(this::onItemClicked);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //return;
                }
                Log.d("size of mChatsList", ""+ mChatList.size());
                //return;
            }

        } catch (JSONException e) {
            mChatList.clear();
            mAdapter.notifyDataSetChanged();
            e.printStackTrace();
            //return;
        }

    }
    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    private void onItemClicked(Chat chat) {
        mListener.onChatListSelection(chat);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChatListSelection(Chat chat);
    }
}
