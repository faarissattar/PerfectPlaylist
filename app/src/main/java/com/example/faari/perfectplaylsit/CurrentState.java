package com.example.faari.perfectplaylsit;

import android.app.Application;

import java.util.ArrayList;

public class CurrentState extends Application {
    private ArrayList<Song> songList;
    private ArrayList<Command> commandList;

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
    }

    public ArrayList<Command> getCommandList() {
        return commandList;
    }

    public void setCommandList(ArrayList<Command> commandList) {
        this.commandList = commandList;
    }
}
