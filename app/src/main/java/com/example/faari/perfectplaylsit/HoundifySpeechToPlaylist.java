package com.example.faari.perfectplaylsit;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.hound.android.fd.UserIdFactory;
import com.hound.android.sdk.TextSearch;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.android.sdk.util.HoundRequestInfoFactory;
import com.hound.core.model.sdk.HoundRequestInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

class HoundifySpeechToPlaylistTask extends AsyncTask {
    private MainActivity activity;

    public HoundifySpeechToPlaylistTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            JSONObject jsonObj = new JSONObject(rawResponse);
            jsonObj = jsonObj.getJSONObject("Disambiguation");
            JSONArray resultsArray = jsonObj.getJSONArray("ChoiceData");
            jsonObj = resultsArray.getJSONObject(0);
            final String voiceMessag = jsonObj.getString("Transcription");
            Log.d("newTHread: ", "running");
            if (TextSearch == null) {
                Log.d("text called", "coolio");
                TextSearch.Builder builder = new TextSearch.Builder()
                        .setRequestInfo(buildRequestInfo())
                        .setClientId("n06WnSgzJbML7AuGNJou3Q==")
                        .setClientKey("ZzWH-lZ41uFCHq75opj9T5Zykux3aAWdDWLCCL8mPPzGR51Erds4gvnLT5v-TBz8Ds-qH9CoHNpdEG-oyDwVbmw==")
                        .setQuery(("Songs " + voiceMessag));
                TextSearch = builder.build();
                try {
                    final VoiceSearchInfo search = TextSearch.search().getSearchInfo();

                    try {
                        String message = ("Response\n\n" + search);
                        Log.d("jsonStringy", new JSONObject(search.getContentBody()).toString(4));

                        largeLog("strangey", search.getContentBody());

                        try {
                            /**************
                             * GETS THE TRANSCRIPTION OF THE VOICE MESSAGE
                             **************/
                            JSONObject jsonObj = new JSONObject(search.getJsonResponse().toString());
                            jsonObj = jsonObj.getJSONObject("Disambiguation");
                            JSONArray resultsArray = jsonObj.getJSONArray("ChoiceData");
                            jsonObj = resultsArray.getJSONObject(0);
                            voiceMessage = jsonObj.getString("Transcription");

                            /**********
                             * GETS SEEDS FROM THE JSON RESPONSE
                             **********/

                            //TODO The problem is here, the VoiceSearchInfo will not give use the entire JSONResponse

                            jsonObj = new JSONObject(search.getJsonResponse().toString());
                            JSONArray results = jsonObj.getJSONArray("AllResults");
                            JSONObject nativeData = results.getJSONObject(0).getJSONObject("NativeData");


                                        /*
                                        ArrayList<Command> commands = state.getCommandList();
                                        commands.add(0, new Command(voiceMessage));
                                        state.setCommandList(commands);
                                        */
                            JSONArray tracks = nativeData.getJSONArray("Tracks");

                            for (int t = 0; t < tracks.length(); t++) {
                                JSONArray thirdParty = tracks.getJSONObject(t).getJSONArray("MusicThirdPartyIds");
                                String seed = "";
                                for (int i = 0; i < thirdParty.length(); i++) {

                                    try {
                                        Log.d("PROGRAM-RESULT", "Trying ID block in for loop");
                                        String name = thirdParty.getJSONObject(i).getJSONObject("MusicThirdParty").getString("Name");
                                        if (name.equals("Spotify")) {
                                            seed = thirdParty.getJSONObject(i).getJSONArray("Ids").toString();
                                            if (!seed.equals("[]")) {
                                                Log.d("good seeds: ", seed);

                                                Log.d("printSeed: ", seed);
                                                String Resultingtrack = "The program returned the song: " + tracks.getJSONObject(t).getString("TrackName") + " - by:  " + tracks.getJSONObject(t).getString("ArtistName")
                                                        + " with the spotify ID: " + seed + " and extracted is: " + seed.substring(16, seed.length() - 2);
                                                Log.d("PROGRAM-RESULT", Resultingtrack);

                                                seed = thirdParty.getJSONObject(i).getString("Ids");

                                                seeds.add(seed.substring(16, seed.length() - 2));
                                            } else {
                                                Log.d("bad seeds: ", seed);
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.d("PROGRAM-RESULT", "Catching empty id block");
                                    }
                                }
                            }

                            String output = "";
                            for (String s : seeds) {
                                output += s + "\t";
                            }
                            Log.d("PROGRAM-RESULT", output);

                            /************
                             * SAVES SPOTIFY JSON RESPONSE STRING
                             ************/

                            /***PUT FUNCTION HERE FOR SPOTIFY WEB API**/
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String recommendations_json = spotifyApiRequest(voiceMessage, seeds.get(0), 10);

                                    //SpotifyWebAPIParser(spotifyApiRequest(voiceMessage, seeds.get(0), 10));
                                }
                            }).start();
                        } catch (JSONException ex) {
                            jsonString = "failed";
                            Log.d("PROGRAM-RESULT", jsonString);
                            ex.printStackTrace();
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } catch (com.hound.android.sdk.TextSearch.TextSearchException ts) {
                    Log.d("ts: ", ts.getMessage());
                }
            }
        } catch (Exception e) {
            Log.d("Build failed: ", "fuu");
        }
        return null;
    }

    protected void onPostExecute(Long result) {
        activity.setFirstSong();
    }
}
