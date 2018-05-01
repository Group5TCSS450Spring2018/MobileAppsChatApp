package spr018.tcss450.clientapplication.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import spr018.tcss450.clientapplication.R;

public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {
    private List<Connection> mConnections;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mNameTextView;
        public ImageView mIconView;
        public ViewHolder(View v) {
            super(v);
            mNameTextView = v.findViewById(R.id.connectionName);
            mIconView = v.findViewById(R.id.connectionIcon);
        }
    }

    // Parameter could be any type of collection. I'm using list for now. - Tuan
    public ConnectionAdapter(List<Connection> connections) {
        mConnections = connections;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View connectionView = inflater.inflate(R.layout.fragment_connections_list_item, parent, false);
        ViewHolder vh = new ViewHolder(connectionView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Connection connection = mConnections.get(position);
        holder.mNameTextView.setText(connection.getUsername());
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mConnections.size();
    }
}

