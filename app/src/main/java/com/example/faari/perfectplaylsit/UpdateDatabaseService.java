package com.example.faari.perfectplaylsit;

import android.app.IntentService;
import android.app.backup.BackupManager;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.support.annotation.Nullable;

public class UpdateDatabaseService extends IntentService {
    private static final String TAG = "UpdateSongDatabase";
    public UpdateDatabaseService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        CurrentState state = (CurrentState)getApplication();
        AppDatabase.getInstance(getApplicationContext()).appDAO().deleteAllCommands();
        for(int i = 0; i<state.getCommandList().size(); i++) {
            AppDatabase.getInstance(getApplicationContext()).appDAO().insertAll(state.getCommandList().get(i));
        }
        AppDatabase.getInstance(getApplicationContext()).appDAO().deleteAllSongs();
        for(int i = 0; i<state.getSongList().size(); i++) {
            AppDatabase.getInstance(getApplicationContext()).appDAO().insertAll(state.getSongList().get(i));
        }
        requestBackup();
    }

    public void requestBackup() {
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }
}
