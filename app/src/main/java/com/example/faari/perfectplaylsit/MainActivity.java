package com.example.faari.perfectplaylsit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
    final static int REQUEST_CODE = 100;
    private SpotifyAppRemote mSpotifyAppRemote;
    private VoiceSearch voiceSearch;

    private void buildVoiceSearch() {
        //  new listener class created to handle status of recording, getting json string and handling aborting
        Listener voiceListener = new Listener();

        voiceSearch = new VoiceSearch.Builder()
                .setRequestInfo(buildRequestInfo())
                .setClientId("n06WnSgzJbML7AuGNJou3Q==")
                .setClientKey("ZzWH-lZ41uFCHq75opj9T5Zykux3aAWdDWLCCL8mPPzGR51Erds4gvnLT5v-TBzDs-qH9CoHNpdEG-oyDwVbmw==")
                .setListener(voiceListener)
                .setAudioSource(new SimpleAudioByteStreamSource())
                .build();
    }

    private HoundRequestInfo buildRequestInfo() {
        final HoundRequestInfo requestInfo = HoundRequestInfoFactory.getDefault(this);
        requestInfo.setUserId(UserIdFactory.get(this));
        requestInfo.setRequestId(UUID.randomUUID().toString());
        return requestInfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            final HoundSearchResult result = Houndify.get(this).fromActivityResult(resultCode, data);
            final HoundResponse houndResponse = result.getResponse();
        }
    }


    @Override   //  question over needing to explicitly create an overriden function
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Houndify houndify = Houndify.get(this);
        houndify.setClientId("n06WnSgzJbML7AuGNJou3Q==");
        houndify.setClientKey("ZzWH-lZ41uFCHq75opj9T5Zykux3aAWdDWLCCL8mPPzGR51Erds4gvnLT5v-TBzDs-qH9CoHNpdEG-oyDwVbmw==");
        houndify.setRequestInfoFactory(new DefaultRequestInfoFactory(this));



        Houndify.get(this).voiceSearch(this, REQUEST_CODE);
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
        buildVoiceSearch();
    }

    @Override
    protected void onStop(){
        super.onStop();

        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
    }

    //------------------------------------------------------------------------------------------------------------
    private class Listener implements VoiceSearch.RawResponseListener {

        @Override
        public void onTranscriptionUpdate(final PartialTranscript transcript) {
            TextView statusTextView = (TextView) findViewById(R.id.statusTextView);
            TextView contentTextView = (TextView) findViewById(R.id.contentTextView);
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

            contentTextView.setText("Transcription:\n" + transcript.getPartialTranscript());
        }

        @Override
        public void onResponse(String rawResponse, VoiceSearchInfo voiceSearchInfo) {
            TextView statusTextView = (TextView) findViewById(R.id.statusTextView);
            TextView contentTextView = (TextView) findViewById(R.id.contentTextView);
            TextView btnSearch = (TextView) findViewById(R.id.btnSearch);
            voiceSearch = null;

            statusTextView.setText("Received Response");

            String jsonString;
            try {
                jsonString = new JSONObject(rawResponse).toString(4);
            } catch (final JSONException ex) {
                jsonString = "Failed to parse content:\n" + rawResponse;
            }

            contentTextView.setText(jsonString);
            btnSearch.setText("Search");
        }

        @Override
        public void onError(final Exception ex, final VoiceSearchInfo info) {
            TextView statusTextView = (TextView) findViewById(R.id.statusTextView);
            TextView contentTextView = (TextView) findViewById(R.id.contentTextView);
            voiceSearch = null;

            statusTextView.setText("Something went wrong");
            contentTextView.setText(ex.toString());
        }

        @Override
        public void onRecordingStopped() {
            TextView statusTextView = (TextView) findViewById(R.id.statusTextView);
            statusTextView.setText("Receiving...");
        }

        @Override
        public void onAbort(final VoiceSearchInfo info) {
            TextView statusTextView = (TextView) findViewById(R.id.statusTextView);
            voiceSearch = null;
            statusTextView.setText("Aborted");
        }
    };
}
