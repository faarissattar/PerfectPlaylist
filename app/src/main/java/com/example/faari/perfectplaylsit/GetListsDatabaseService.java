package com.example.faari.perfectplaylsit;

import android.app.IntentService;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;


public class GetListsDatabaseService extends IntentService {
    private static final String TAG = "GetListsDatabase";
    public static final String BROADCAST_ACTION =
            "com.example.android.threadsample.BROADCAST";
    public static final String EXTENDED_DATA_STATUS =
            "com.example.android.threadsample.STATUS";
    public GetListsDatabaseService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        CurrentState state = (CurrentState)getApplication();
        state.setCommandList((ArrayList<Command>) AppDatabase.getInstance(getApplicationContext()).appDAO().getAllCommands());
        state.setSongList((ArrayList<Song>) AppDatabase.getInstance(getApplicationContext()).appDAO().getAllSongs());
        Intent localIntent =
                new Intent(BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(EXTENDED_DATA_STATUS, "done");
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        requestBackup();
    }

    public void requestBackup() {
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }
}
