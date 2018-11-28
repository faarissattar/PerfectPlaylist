package com.example.faari.perfectplaylsit;

import android.app.IntentService;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

public class GetListsDatabaseService extends IntentService {
    private static final String TAG = "GetListsDatabase";
    public GetListsDatabaseService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        CurrentState state = (CurrentState)getApplication();
        state.setCommandList((ArrayList<Command>) AppDatabase.getInstance(getApplicationContext()).appDAO().getAllCommands());
        state.setSongList((ArrayList<Song>) AppDatabase.getInstance(getApplicationContext()).appDAO().getAllSongs());
        requestBackup();
    }

    public void requestBackup() {
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }
}
