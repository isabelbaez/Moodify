package com.example.moodify;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private Context context;
    private List<ParseUser> friends;

    public StatusAdapter(Context context, List<ParseUser> friends) {
        this.context = context;
        this.friends = friends;
    }


    @NonNull
    @Override
    public StatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.ViewHolder holder, int position) {
        ParseUser user = friends.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvUsername;
        private TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            itemView.setOnClickListener(this);
        }

        public void bind(ParseUser user) {
            tvUsername.setText(user.getUsername());
            tvStatus.setText(user.getString("status"));
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.i("yes", "yes");
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the tweet at the position, this won't work if the class is static
                ParseUser user = friends.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, FriendProfileActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra("post", Parcels.wrap(user));
                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
