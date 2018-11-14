package com.example.faari.perfectplaylsit;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hound.android.fd.DefaultRequestInfoFactory;
import com.hound.android.fd.Houndify;
import com.hound.android.fd.UserIdFactory;
import com.hound.android.sdk.AsyncTextSearch;
import com.hound.android.sdk.TextSearchListener;
import com.hound.android.sdk.VoiceSearch;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.android.sdk.audio.SimpleAudioByteStreamSource;
import com.hound.android.sdk.util.HoundRequestInfoFactory;
import com.hound.core.model.sdk.HoundRequestInfo;
import com.hound.core.model.sdk.HoundResponse;
import com.hound.core.model.sdk.PartialTranscript;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "ffe427ae0c784377ab9f5afc7bf47a15";
    private static final String CLIENT_SECRET_ID = "dbf89482e2974892bba6f183b6f05fb9";
    private static final String REDIRECT_URI = "https://example.com/redirect/";
    /*
    private static SpotifyApi spotifyApi;
    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
            .build();
            */
    private static final String IBM_USERNAME = "6ab5486c-37e8-4a6e-8281-0ade7278857b";
    private static final String IBM_PASSWORD = "Bkjugo5VkhOD";
    private static final String SPOTIFY_URL = "https://api.spotify.com/v1/recommendations?";
    private static String spotApiToken = "";
    private ArrayList<String> seeds = new ArrayList<>();
    private ArrayList<Song> songs = new ArrayList<>();
    private String voiceMessage = "";
    //	new instance of NLU
    private final NaturalLanguageUnderstanding botNlu = new NaturalLanguageUnderstanding("2017-02-27", IBM_USERNAME, IBM_PASSWORD);
    //	final to hold our NLU features object
    private final Features features = new Features.Builder()
            .emotion(new EmotionOptions.Builder()
                    .build())
            .entities(new EntitiesOptions.Builder()
                    .build())
            .build();
    final static int REQUEST_CODE = 5744;
    private SpotifyAppRemote mSpotifyAppRemote;
    private VoiceSearch mvoiceSearch;
    private FloatingActionButton mbuttonSearch;
    private AsyncTextSearch asyncTextSearch = null;
    private String jsonString = "";
    ViewPager mviewPager;
    SectionsPagerAdapter msectionsPagerAdapter;
    static SongAdapter songAdapter;
    ArrayAdapter<Command> commandAdapter;
    CurrentState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        state = (CurrentState) getApplication();
        if(state.getCommandList() == null) state.setCommandList(new ArrayList<Command>());
        if(state.getSongList() == null) state.setSongList(new ArrayList<Song>());
        mbuttonSearch = findViewById(R.id.fab_microphone);
        msectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mviewPager = findViewById(R.id.container);
        mviewPager.setAdapter(msectionsPagerAdapter);
        songAdapter = new SongAdapter(getApplicationContext(), state.getSongList());
        commandAdapter =
                new ArrayAdapter<Command>(getApplicationContext(), android.R.layout.simple_list_item_1, state.getCommandList()) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        TextView listItem = (TextView) super.getView(position, convertView, parent);
                        listItem.setTextColor(Color.WHITE);
                        return listItem;
                    }
                };

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        }, 0);

        final Houndify houndify = Houndify.get(this);
        houndify.setClientId("n06WnSgzJbML7AuGNJou3Q==");
        houndify.setClientKey("ZzWH-lZ41uFCHq75opj9T5Zykux3aAWdDWLCCL8mPPzGR51Erds4gvnLT5v-TBzDs-qH9CoHNpdEG-oyDwVbmw==");
        houndify.setRequestInfoFactory(new DefaultRequestInfoFactory(this));

        /***************
         * SPOTIFY AUTHENTICATION ACTIVITY
         ***************/
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        //builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onStart(){
        super.onStart();

        mbuttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Houndify.get(MainActivity.this).voiceSearch(MainActivity.this, REQUEST_CODE);
                if (mvoiceSearch == null) {
                    //startVoiceSearch(view);
                    mvoiceSearch = new VoiceSearch.Builder()
                            .setRequestInfo(buildRequestInfo())
                            .setClientId("n06WnSgzJbML7AuGNJou3Q==")
                            .setClientKey("ZzWH-lZ41uFCHq75opj9T5Zykux3aAWdDWLCCL8mPPzGR51Erds4gvnLT5v-TBzDs-qH9CoHNpdEG-oyDwVbmw==")
                            .setListener(voiceListener)
                            .setAudioSource(new SimpleAudioByteStreamSource())
                            .build();

                    //Houndify.get(MainActivity.this).voiceSearch(MainActivity.this, REQUEST_CODE);
                    mvoiceSearch.start();
                }
                else {
                    mvoiceSearch.stopRecording();
                }
                PlaceholderFragment.setAdapterCommands(commandAdapter);
                PlaceholderFragment.setAdapterPlaylist(songAdapter);
                //TODO: Put code here to get results from Houndify and Spotify
                //state.pushCommand(voiceMessage);
                state.setSongList(songs);
                commandAdapter.notifyDataSetChanged();
                songAdapter.notifyDataSetChanged();
                Intent intent1 = new Intent(getApplicationContext(), UpdateDatabaseService.class);
                startService(intent1);
                mviewPager.setCurrentItem(2);
                //TODO: Make first item in list start playing
                //TODO: Put info from first item in list to the Now Playing View
            }
        });

        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("MainActivity", "Connected! Yay!");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("MainActivity", throwable.getMessage(), throwable);

            }
        });
    }

    /*
     *   ON SPOTIFY AUTHENTICATION RESULT
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //if (requestCode == REQUEST_CODE) {
        //final HoundSearchResult result = Houndify.get(this).fromActivityResult(resultCode, data);
        //final HoundResponse houndResponse = result.getResponse();
        //}
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    spotApiToken = response.getAccessToken();
                    Log.d("TOKEN-SUCC", response.getExpiresIn()+ " " +spotApiToken/*response.getAccessToken()*/);
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d("TOKEN", "ERROR"+response.getCode());
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d("TOKEN", "ELSE"+response.toString());
                    // Handle other cases
            }
        }
    }

    /*
    private void connected(){
        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
    }
    */

    private HoundRequestInfo buildRequestInfo() {
        final HoundRequestInfo requestInfo = HoundRequestInfoFactory.getDefault(this);
        requestInfo.setUserId(UserIdFactory.get(this));
        requestInfo.setRequestId(UUID.randomUUID().toString());
        return requestInfo;
    }

    @Override
    protected void onStop(){
        super.onStop();

        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static View recentView, playlistView;
        private static ListView recentListView, playlistListView;

        public PlaceholderFragment() {}

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            recentView = inflater.inflate(R.layout.fragment_recent, container, false);
            playlistView = inflater.inflate(R.layout.fragment_playlist, container, false);
            getListViewCommands();
            getListViewPlaylist();

            final View songBar = playlistView.findViewById(R.id.inc_song_bar);
            final ImageView previousBtn = songBar.findViewById(R.id.iv_previous);
            previousBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // skip to previous
                }
            });
            final ImageView nextBtn = songBar.findViewById(R.id.iv_next);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // skip to next
                }
            });
            final ImageView playPauseBtn = songBar.findViewById(R.id.iv_play_pause);
            playPauseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (playPauseBtn.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_play_arrow).getConstantState()) {
                        // pause music
                        playPauseBtn.setImageResource(R.drawable.ic_pause);
                    } else {
                        // play music
                        playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
                    }
                }
            });

            playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Song song = (Song) playlistListView.getItemAtPosition(i);
                    songAdapter.setSelectedIndex(i);
                    TextView songPlaying = songBar.findViewById(R.id.tv_song_playing);
                    TextView artistPlaying = songBar.findViewById(R.id.tv_artist_playing);
                    songPlaying.setText(song.getTitle());
                    artistPlaying.setText(song.getArtist());

                    //TODO: Make song play
                    //TODO: Put song info in Now Playing bar
                }
            });

            recentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Command command = (Command)recentListView.getItemAtPosition(i);
                    //TODO: Make this command run
                    CurrentState state = (CurrentState)getContext();
                    ArrayList<Command> commands = state.getCommandList();
                    commands.add(0, command);
                    commands.remove(command);
                    state.setCommandList(commands);
                }
            });

            if(getArguments().getInt(ARG_SECTION_NUMBER)==1){
                return recentView;
            } else if(getArguments().getInt(ARG_SECTION_NUMBER)==2){
                return playlistView;
            }
            return recentView;
        }

        public static ListView getListViewCommands(){
            recentListView = recentView.findViewById(R.id.lv_commands);
            return recentListView;
        }

        public static void setAdapterCommands(ArrayAdapter<Command> commandArrayAdapter){
            if(recentListView == null){
                getListViewCommands();
            }
            recentListView.setAdapter(commandArrayAdapter);
        }

        public static ListView getListViewPlaylist(){
            playlistListView = playlistView.findViewById(R.id.lv_playlist);
            return playlistListView;
        }

        public static void setAdapterPlaylist(SongAdapter songAdapter){
            if(playlistListView == null){
                getListViewPlaylist();
            }
            playlistListView.setAdapter(songAdapter);
        }
    }

    /******************************
     Function to analyze the String passed in and return the NLU text analysis json
     @params
     String message, hold the message that will be analyzed by the nlu
     @return
     Returns a string holding the json response from the nlu
     error cases
     if the nlu cannot execute the analysis, ERROR will be returned
     *****************************/
    public String analyzeResponse(String message) {
        AnalysisResults results;

        //	builds our analysis options, using the features that we build in the constructor
        AnalyzeOptions parameter = new AnalyzeOptions.Builder()
                .text(message)
                .features(features)
                .build();

        //	gets the results from the text analysis
        try {
            results = botNlu.analyze(parameter).execute();
        }
        catch(Exception e) {
            return "ERROR";
        }

        //	returns a string Json of our text analysis
        return results.toString();
    }

    public String[] getEmotionsFromResponse(String jString){
        String[] emotions = new String[4];

        return emotions;
    }

    public String findGenre(String jString){
        String genre = "";

        return genre;
    }

    /*********************
     * Spotify web api request builder
     *********************/

    public String buildSpotifyApiRequest(String response, String songUriSeed, int tracksFound) {
        String jsonResponseAnalysis = analyzeResponse(response);
        String emotions[] = getEmotionsFromResponse(jsonResponseAnalysis);
        String genre = findGenre(jsonResponseAnalysis);

        return SPOTIFY_URL + "market=US&seed_tracks="+ songUriSeed + "min_energy=0.4&min_popularity=50";
    }

    public String spotifyApiRequest(String voiceMess, String trackURI, int tracks_found) {
        HttpURLConnection api_get;
        StringBuffer response = new StringBuffer();

        try {
            URL url = new URL(buildSpotifyApiRequest(voiceMess, trackURI, tracks_found));
            //	creates a URL connection, are the url that we built
            api_get = (HttpURLConnection) url.openConnection();

            //	sets the url connection to a GET request\
            api_get.setRequestMethod("GET");
            //api_get.setRequestProperty("User-Agent", "perfectPlaylist");
            api_get.setRequestProperty("Accept", "application/json");
            api_get.setRequestProperty("Content-Type", "application/json");
            api_get.setRequestProperty("Authorization", ("Bearer "+spotApiToken));


            int responseCode = api_get.getResponseCode();
            Log.d("sending","\nSending 'GET' request to URL : " + url);
            Log.d( "Response Code : ", ""+responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(api_get.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            Log.d("jsonResponse: ", response.toString());
        }
        catch(MalformedURLException e){
            Log.d("malformed", e.getMessage());
        }
        catch(IOException e){
            Log.d("ioEx", e.getMessage());
        }
        return response.toString();
    }


    private final Listener voiceListener = new Listener();

    //------------------------------------------------------------------------------------------------------------
    private class Listener implements VoiceSearch.RawResponseListener {

        @Override
        public void onTranscriptionUpdate(final PartialTranscript transcript) {
            switch (mvoiceSearch.getState()) {
                case STATE_STARTED:
                    //statusTextView.setText("Listening...");
                    break;

                case STATE_SEARCHING:
                    //statusTextView.setText("Receiving...");
                    break;

                default:
                    //statusTextView.setText("Unknown");
                    break;
            }

            //contentTextView.setText("Transcription:\n" + transcript.getPartialTranscript());
        }

        @Override
        public void onResponse(String rawResponse, VoiceSearchInfo voiceSearchInfo) {
            mbuttonSearch.setClickable(true);
            mvoiceSearch = null;

            try {
                JSONObject jsonObj = new JSONObject(rawResponse);
                jsonObj = jsonObj.getJSONObject("Disambiguation");
                JSONArray resultsArray = jsonObj.getJSONArray("ChoiceData");
                jsonObj = resultsArray.getJSONObject(0);
                String voiceMessag = jsonObj.getString("Transcription");

                if (asyncTextSearch == null) {
                    Log.d("text called", "coolio");
                    AsyncTextSearch.Builder builder = new AsyncTextSearch.Builder()
                            .setRequestInfo(buildRequestInfo())
                            .setClientId("n06WnSgzJbML7AuGNJou3Q==")
                            .setClientKey("ZzWH-lZ41uFCHq75opj9T5Zykux3aAWdDWLCCL8mPPzGR51Erds4gvnLT5v-TBzDs-qH9CoHNpdEG-oyDwVbmw==")
                            .setListener(textSearchListener)
                            .setQuery(("Songs " + voiceMessag));

                    asyncTextSearch = builder.build();

                    asyncTextSearch.start();
                }
                //statusTextView.setText("Received Response");
            }
            catch(Exception e){
                Log.d("jException: ", e.getMessage());
            }
        }

        @Override
        public void onError(final Exception ex, final VoiceSearchInfo info) {
            mvoiceSearch = null;

            //statusTextView.setText("Something went wrong");
            //contentTextView.setText(ex.toString());
        }

        @Override
        public void onRecordingStopped() {
            //statusTextView.setText("Receiving...");
        }

        @Override
        public void onAbort(final VoiceSearchInfo info) {
            mvoiceSearch = null;
            //statusTextView.setText("Aborted");
        }
    }

    /*****************
     * Text search listener
     **********************/

    private final TextSearchListener textSearchListener = new TextSearchListener() {

        @Override
        public void onResponse(final HoundResponse response, final VoiceSearchInfo info) {
            asyncTextSearch = null;

            // Make sure the request succeeded with OK
            if (response.getStatus().equals(HoundResponse.Status.OK)) {
                Log.d("Text search: ", response.toString());
                try {
                    String message = "Response\n\n" +
                            new JSONObject(info.getJsonResponse().toString()).toString();
                    Log.d("jsonStringy", message);

                    try {
                        /**************
                         * GETS THE TRANSCRIPTION OF THE VOICE MESSAGE
                         **************/
                        JSONObject jsonObj = new JSONObject(info.getJsonResponse().asText());
                        jsonObj = jsonObj.getJSONObject("Disambiguation");
                        JSONArray resultsArray = jsonObj.getJSONArray("ChoiceData");
                        jsonObj = resultsArray.getJSONObject(0);
                        voiceMessage = jsonObj.getString("Transcription");

                        /**********
                         * GETS SEEDS FROM THE JSON RESPONSE
                         **********/

                        //TODO The problem is here, the VoiceSearchInfo will not give use the entire JSONResponse

                        jsonObj = new JSONObject(info.getJsonResponse().asText());
                        JSONArray results = jsonObj.getJSONArray("AllResults");
                        for (int k = 0; k < results.length(); k++) {
                            JSONObject nativeData = results.getJSONObject(k).getJSONObject("NativeData");
                            ArrayList<Command> commands = state.getCommandList();
                            commands.add(0, new Command(nativeData.getString("FormattedTranscription")));
                            state.setCommandList(commands);
                            JSONObject track1 = nativeData.getJSONArray("Tracks").getJSONObject(0);
                            JSONArray thirdParty = track1.getJSONArray("MusicThirdPartyIds");

                            int index = -1;
                            for (int i = 0; i < thirdParty.length(); i++) {

                                String name = thirdParty.getJSONObject(i).getJSONObject("MusicThirdParty").getString("Name");
                                if (name.equals("Spotify")) {
                                    index = i;
                                }
                            }

                            String seed = thirdParty.getJSONObject(index).getJSONArray("Ids").toString();

                            String Resultingtrack = "The program returned the song: " + track1.getString("TrackName") + " - by:  " + track1.getString("ArtistName")
                                    + " with the spotify ID: " + seed + " and extracted is: " + seed.substring(16, seed.length() - 2);
                            Log.d("PROGRAM-RESULT", Resultingtrack);
                            seeds.add(seed.substring(16, seed.length() - 2));
                        }
                        String output = "";
                        for (String s : seeds)
                        {
                            output += s + "\t";
                        }
                        Log.d("PROGRAM-RESULT", output);

                        /************
                         * SAVES SPOTIFY JSON RESPONSE STRING
                         ************/

                        /***PUT FUNCTION HERE FOR SPOTIFY WEB API**/
                        new Thread() {
                            public void run(){
                                SpotifyWebAPIParser(spotifyApiRequest(voiceMessage, seeds.get(0), 10));
                            }
                        }.start();

                    } catch (JSONException ex){
                        jsonString = "failed";
                        Log.d("PROGRAM-RESULT", jsonString);
                    }
                }
                catch (final JSONException ex) {
                    Log.d("hafsd", "oke");
                }
            }
            else {
                Log.d("failed text: ", response.toString());
            }

            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(final Exception ex, final VoiceSearchInfo info) {
            Log.d("onError is here", ex.getMessage());
            asyncTextSearch = null;
        }

        @Override
        public void onAbort(final VoiceSearchInfo info) {
            Log.d("onAbort is here", "alrighty");
            asyncTextSearch = null;
        }
    };

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    //TODO: Make Playlist run like playlist, i.e. make one song play after the other
    //Why does this method have to be static?
    public void SpotifyWebAPIParser(String rawResponse) {
        try {
            JSONObject raw = new JSONObject(rawResponse);
            JSONArray tracksObj = raw.getJSONArray("tracks");

            String name;
            String uri;
            String id;
            String imgUrl;
            String album;
            String artists;
            int duration;
            String[] arr = new String[6];
            JSONObject temp = null;
            for (int i = 0; i < tracksObj.length(); i++) {
                temp = tracksObj.getJSONObject(i);

                arr[0] = temp.getString("name");
                arr[2] = temp.getString("uri");
                arr[3] = temp.getString("id");
                arr[4] = temp.getJSONObject("album").getJSONArray("images").getJSONObject(temp.getJSONObject("album").getJSONArray("images").length() - 1).getString("url");
                arr[5] = temp.getJSONObject("album").getString("name");
                arr[6] = temp.getJSONArray("artists").getJSONObject(0).getString("name");
                duration = Integer.parseInt(temp.getString("duration_ms"));

                Song song = new Song(duration);
                song.setSongInfo(arr);
                songs.add(song);
                state.setSongList(songs);
            }
        } catch (Exception e) {

        }
    }
}