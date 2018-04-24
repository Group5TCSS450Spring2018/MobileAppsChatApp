package spr018.tcss450.clientapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import spr018.tcss450.clientapplication.model.ChatViewItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<ChatViewItem> mChatList;

    public HomeFragment() {
        // Required empty public constructor
    }

    public interface OnSuccessFragmentInteractionListener { void onLogout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView connections = v.findViewById(R.id.chatListContainer);

        mChatList = ChatViewItem.populateChats(20    );

        ChatViewItemAdapter adapter = new ChatViewItemAdapter(mChatList);

        connections.setAdapter(adapter);
        connections.setLayoutManager(new LinearLayoutManager(getActivity()));

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onHomeInteraction(uri);
        }
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
        void onHomeInteraction(Uri uri);
    }

    private class ChatViewItemAdapter
            extends RecyclerView.Adapter<ChatViewItemAdapter.ViewHolder> {

        private ArrayList<ChatViewItem> mChats;

        public ChatViewItemAdapter(ArrayList<ChatViewItem> list) {
            this.mChats = list;
        }

        @NonNull
        @Override
        public ChatViewItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View chatViewItem = inflater.inflate(R.layout.fragment_home_chat_list_item,
                    parent, false);


            ChatViewItemAdapter.ViewHolder viewHolder = new ChatViewItemAdapter.ViewHolder(chatViewItem);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewItemAdapter.ViewHolder holder, int position) {
            ChatViewItem chat = mChats.get(position);

            // Set item views based on your views and data model
            TextView textView = holder.mChatTitle;
            textView.setText(chat.getTitle());
        }

        @Override
        public int getItemCount() {
            return mChats.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mChatIcon;
            public TextView mChatTitle;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View view) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(view);

                mChatIcon = view.findViewById(R.id.chatIcon);
                mChatTitle = view.findViewById(R.id.chatTitle);
            }
        }
    }
}
