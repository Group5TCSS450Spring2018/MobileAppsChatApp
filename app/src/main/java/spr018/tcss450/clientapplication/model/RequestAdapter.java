package spr018.tcss450.clientapplication.model;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import spr018.tcss450.clientapplication.R;
/**
 * Adapter class that displays the requests that the user has.
 * @author  Tuan Dinh Tenma Rollins Daryan Hanshew Deepjot Kaur
 */
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<Connection> mConnections;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onAccept(Connection connection);
        void onDeny(Connection connection);
        void onExpand(Connection connection);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mUsernameTextView;
        private View mView;

        ViewHolder(View v) {
            super(v);
            mUsernameTextView = v.findViewById(R.id.requestUsername);
            mView = v;
        }

        void bind(Connection connection, OnItemClickListener listener) {
            ImageButton accept = mView.findViewById(R.id.requestAcceptButton);
            ImageButton deny = mView.findViewById(R.id.requestDeclineButton);

            //Modifies the view when the connection is null. Connection should be null when
            //there is not any connections.
            if (connection == null) {
                mUsernameTextView.setText("No New Requests");
                mUsernameTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mUsernameTextView.setTypeface(null, Typeface.NORMAL);
                mUsernameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                accept.setVisibility(View.GONE);
                deny.setVisibility(View.GONE);
            } else {
                mUsernameTextView.setTypeface(null, Typeface.BOLD);
                mUsernameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                mUsernameTextView.setOnClickListener(view -> listener.onExpand(connection));
                accept.setVisibility(View.VISIBLE);
                deny.setVisibility(View.VISIBLE);
                accept.setOnClickListener(view -> listener.onAccept(connection));
                deny.setOnClickListener(view -> listener.onDeny(connection));
            }
        }
    }

    // Parameter could be any type of collection. I'm using list for now. - Tuan
    public RequestAdapter(List<Connection> connections) {
        mConnections = connections;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View connectionView = inflater.inflate(R.layout.fragment_request, parent, false);
        return new ViewHolder(connectionView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Connection connection = mConnections.get(position);
        if (connection != null) {
            holder.mUsernameTextView.setText(connection.getUsername());
        }
        holder.bind(connection, mListener);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mConnections.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}

