package com.example.faari.perfectplaylsit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hound.android.fd.DefaultRequestInfoFactory;
import com.hound.android.fd.HoundSearchResult;
import com.hound.android.fd.Houndify;
import com.hound.android.fd.UserIdFactory;
import com.hound.android.sdk.VoiceSearch;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.android.sdk.audio.SimpleAudioByteStreamSource;
import com.hound.android.sdk.util.HoundRequestInfoFactory;
import com.hound.core.model.sdk.HoundRequestInfo;
import com.hound.core.model.sdk.HoundResponse;
import com.hound.core.model.sdk.PartialTranscript;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "ffe427ae0c784377ab9f5afc7bf47a15";
    private static final String REDIRECT_URI = "https://example.com/redirect/";
    final static int REQUEST_CODE = 5744;
    private TextView statusTextView;
    private TextView contentTextView;
    private ImageView btnSearch;
    private SpotifyAppRemote mSpotifyAppRemote;
    private VoiceSearch voiceSearch;

    @Override   //  question over needing to explicitly create an overriden function
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        statusTextView = findViewById(R.id.statusTextView);
        contentTextView = findViewById(R.id.contentTextView);
        btnSearch = findViewById(R.id.btnSearch);

        if (isFirstTime()) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("To run this app correctly, spotify must be installed on this device.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, 0);

        final Houndify houndify = Houndify.get(this);
        houndify.setClientId("n06WnSgzJbML7AuGNJou3Q==");
        houndify.setClientKey("ZzWH-lZ41uFCHq75opj9T5Zykux3aAWdDWLCCL8mPPzGR51Erds4gvnLT5v-TBzDs-qH9CoHNpdEG-oyDwVbmw==");
        houndify.setRequestInfoFactory(new DefaultRequestInfoFactory(this));
    }

    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }

    @Override
    protected void onStart(){
        //  super calls the onStart function of the superType for this class
        super.onStart();

        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("MainActivity", "Connected! Yay!");


                connected();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("MainActivity", throwable.getMessage(), throwable);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            final HoundSearchResult result = Houndify.get(this).fromActivityResult(resultCode, data);
            final HoundResponse houndResponse = result.getResponse();
        }
    }

    private void buildVoiceSearch() {
        if (voiceSearch != null) {
            return; // We are already searching
        }

        voiceSearch = new VoiceSearch.Builder()
                .setRequestInfo(buildRequestInfo())
                .setClientId("n06WnSgzJbML7AuGNJou3Q==")
                .setClientKey("ZzWH-lZ41uFCHq75opj9T5Zykux3aAWdDWLCCL8mPPzGR51Erds4gvnLT5v-TBzDs-qH9CoHNpdEG-oyDwVbmw==")
                .setListener(voiceListener)
                .setAudioSource(new SimpleAudioByteStreamSource())
                .build();

        //Houndify.get(this).voiceSearch(this, REQUEST_CODE);
        voiceSearch.start();
    }

    private HoundRequestInfo buildRequestInfo() {
        final HoundRequestInfo requestInfo = HoundRequestInfoFactory.getDefault(this);
        requestInfo.setUserId(UserIdFactory.get(this));
        requestInfo.setRequestId(UUID.randomUUID().toString());
        return requestInfo;
    }

    private void connected(){
        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
    }

    public void pauseMusic(View view){
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    public void skipMusic(View view){
        mSpotifyAppRemote.getPlayerApi().skipNext();
    }

    public void playbackMusic(View view){
        mSpotifyAppRemote.getPlayerApi().skipPrevious();
    }

    public void startVoiceSearch(View view){
        if (voiceSearch == null) {
            buildVoiceSearch();
        }
        else {
            voiceSearch.stopRecording();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();

        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
    }

    private final Listener voiceListener = new Listener();

    //------------------------------------------------------------------------------------------------------------
    private class Listener implements VoiceSearch.RawResponseListener {

        @Override
        public void onTranscriptionUpdate(final PartialTranscript transcript) {
            switch (voiceSearch.getState()) {
                case STATE_STARTED:
                    statusTextView.setText("Listening...");
                    break;

                case STATE_SEARCHING:
                    statusTextView.setText("Receiving...");
                    break;

                default:
                    statusTextView.setText("Unknown");
                    break;
            }

            contentTextView.setText("\t\tTranscription:\n" + transcript.getPartialTranscript());
        }

        @Override
        public void onResponse(String rawResponse, VoiceSearchInfo voiceSearchInfo) {
            btnSearch.setClickable(true);
            voiceSearch = null;

            statusTextView.setText("Received Response");


            //btnSearch.setText("Search");
        }

        @Override
        public void onError(final Exception ex, final VoiceSearchInfo info) {
            voiceSearch = null;

            statusTextView.setText("Something went wrong");
            contentTextView.setText(ex.toString());
        }

        @Override
        public void onRecordingStopped() {
            statusTextView.setText("Receiving...");
        }

        @Override
        public void onAbort(final VoiceSearchInfo info) {
            voiceSearch = null;
            statusTextView.setText("Aborted");
        }
    }
}
