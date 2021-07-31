package com.example.moodify;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private Context context;
    private List<ParseUser> users;

    private ParseUser currentUser = ParseUser.getCurrentUser();

    private ArrayList<String> friends = (ArrayList<String>) currentUser.get("friends");

    public UsersAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvUsername;
        private Button btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }

        public void bind(ParseUser user) {
            tvUsername.setText(user.getUsername());

            if (friends == null) {
                friends = new ArrayList<String>();
            }

            if (friends.contains(user.getUsername()) || user.getUsername().equals(currentUser.getUsername())) {
                btnAdd.setVisibility(View.GONE);
            }

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Add to current user's friends
                    btnAdd.setVisibility(View.GONE);

                    friends.add(user.getUsername());
                    //JSONArray finalFriends = new JSONArray(friends);

                    currentUser.put("friends", friends);
                    currentUser.saveInBackground();

                    Log.i("PLIS", "Friends: " + currentUser.get("friends"));
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
}
