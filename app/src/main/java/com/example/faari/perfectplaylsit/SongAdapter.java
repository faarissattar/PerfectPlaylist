package com.example.faari.perfectplaylsit;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<Song> {
    private Context mContext;
    private ArrayList<Song> mSongList;
    private int mSelectedIndex;

    public SongAdapter(@NonNull Context context, ArrayList<Song> songList) {
        super(context, 0, songList);
        mContext = context;
        mSongList = songList;
        mSelectedIndex = -1;
    }

    public void setSelectedIndex(int selectedIndex) {
        mSelectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = LayoutInflater.from(mContext).inflate(R.layout.playlist_item, parent, false);

        TextView songName = listItem.findViewById(R.id.tv_song_name);
        TextView artistAndAlbum = listItem.findViewById(R.id.tv_artist_album);
        TextView songLength = listItem.findViewById(R.id.tv_length);

        Song currentSong = mSongList.get(position);
        songName.setText(currentSong.getTitle());
        artistAndAlbum.setText(currentSong.getArtist() + " \u2022 " + currentSong.getAlbum());
        songLength.setText(formatLength(currentSong.getLength()));

        if (mSelectedIndex != -1 &&  position == mSelectedIndex) {
            songName.setTextColor(Color.parseColor("#2196F3"));
            artistAndAlbum.setTextColor(Color.parseColor("#2196F3"));
            songLength.setTextColor(Color.parseColor("#2196F3"));
        }

        return listItem;
    }

    public void removeAll(){
        for(int i = 0; i < mSongList.size(); i++){
            mSongList.remove(i);
        }
    }

    private String formatLength(int millis) {
        String sec;
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        sec = ""+seconds;
        if(seconds<10){
            sec = ("0"+seconds);
        }
        return minutes + ":" + sec;
    }
}

