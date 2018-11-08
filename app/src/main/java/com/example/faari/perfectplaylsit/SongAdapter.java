package com.example.faari.perfectplaylsit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<Song> {
    private Context mContext;
    private ArrayList<Song> mSongList;

    public SongAdapter(@NonNull Context context, ArrayList<Song> songList) {
        super(context, 0, songList);
        mContext = context;
        mSongList = songList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.playlist_item, parent, false);
            holder = new ViewHolder();
            holder.albumCover = convertView.findViewById(R.id.iv_album_cover);
            holder.songName = convertView.findViewById(R.id.tv_song_name);
            holder.artistName = convertView.findViewById(R.id.tv_artist_name);
            holder.songLength = convertView.findViewById(R.id.tv_song_length);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Song currentSong = mSongList.get(position);
        // holder.albumCover.setImageResource();
        holder.songName.setText(currentSong.getTitle());
        holder.artistName.setText(currentSong.getArtist());
        holder.songLength.setText(currentSong.getLength());

        return convertView;
    }

    private static class ViewHolder {
        ImageView albumCover;
        TextView songName;
        TextView artistName;
        TextView songLength;
    }
}

