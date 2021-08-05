package com.example.moodify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodify.models.Song;
import com.parse.ParseUser;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    private Context context;
    private List<String> songs;

    public SongsAdapter(Context context, List<String> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String song = songs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvSong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSong = itemView.findViewById(R.id.tvSong);
        }

        public void bind(String song) {
            tvSong.setText(song);
        }
    }
}
