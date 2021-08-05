package com.example.moodify;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

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

    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private TextView tvStatus;

        public static final String TAG = "MainActivity";
        private final String CLIENT_ID = "1124ddefbcad484993f7f56399a98ffb";
        private static final int REQUEST_CODE = 1337;
        private static final String REDIRECT_URI = "com.example.moodify://callback";
        public SpotifyAppRemote mSpotifyAppRemote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            ConnectionParams connectionParams =
                    new ConnectionParams.Builder(CLIENT_ID)
                            .setRedirectUri(REDIRECT_URI)
                            .showAuthView(true)
                            .build();

            SpotifyAppRemote.connect(context.getApplicationContext(), connectionParams,
                    new Connector.ConnectionListener() {

                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;
                            Log.d("MainActivity", "Connected! Yay!");
                            // Now you can start interacting with App Remote
                            //connected();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e("MainActivity", throwable.getMessage(), throwable);
                            // Something went wrong when attempting to connect! Handle errors here
                        }
                    });
        }

        public void bind(ParseUser user) {
            tvUsername.setText(user.getUsername());
            tvStatus.setText(user.getString("status"));

            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);

            int backgroundResource = typedArray.getResourceId(0, 0);
            tvUsername.setBackgroundResource(backgroundResource);

            tvStatus.setBackgroundResource(backgroundResource);

            tvUsername.setOnClickListener(new View.OnClickListener() {
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
                        intent.putExtra("user", Parcels.wrap(user));
                        // show the activity
                        context.startActivity(intent);
                    }
                }
            });

            tvStatus.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mSpotifyAppRemote.getPlayerApi().play(user.getString("lastSongURI"));
                    return false;
                }
            });
        }
    }
}
