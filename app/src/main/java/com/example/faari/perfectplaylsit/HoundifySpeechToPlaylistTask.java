package com.example.faari.perfectplaylsit;

import android.os.AsyncTask;
import android.util.Log;

import com.hound.android.sdk.TextSearch;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.core.model.sdk.HoundRequestInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class HoundifySpeechToPlaylistTask extends AsyncTask {
    private MainActivity activity;
    private ArrayList<String> seeds = new ArrayList<>();
    private TextSearch textSearch = null;
    private String voiceMessag;
    private HoundRequestInfo requestInfo;

    public HoundifySpeechToPlaylistTask(MainActivity activity, String voiceMessag, HoundRequestInfo requestInfo) {
        this.activity = activity;
        this.voiceMessag = voiceMessag;
        this.requestInfo = requestInfo;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            if (textSearch == null) {
                TextSearch.Builder builder = new TextSearch.Builder()
                        .setRequestInfo(requestInfo)
                        .setClientId("Bu-exntOPXXobJkeWy7mLQ==")
                        .setClientKey("zEEhEDttMVW5chVm-9JErBOUIlucENjyRT-AO3DPqY6mRCpw2znKerG5b202N5VCELSErvOiAyx2B35vCRnNWg==")
                        .setQuery(("Songs " + voiceMessag));
                Log.d("before build", "coolio");

                textSearch = builder.build();

                try {
                    Log.d("text called", "coolio");
                    final VoiceSearchInfo search = textSearch.search().getSearchInfo();
                    Log.d("after search info", "coolio");

                    try {
                        String message = ("Response\n\n" + search);
                        Log.d("jsonStringy", new JSONObject(search.getContentBody()).toString(4));

                        try {
                            /**************
                             * GETS THE TRANSCRIPTION OF THE VOICE MESSAGE
                             **************/
                            JSONObject jsonObj = new JSONObject(search.getJsonResponse().toString());
                            jsonObj = jsonObj.getJSONObject("Disambiguation");
                            JSONArray resultsArrays = jsonObj.getJSONArray("ChoiceData");
                            jsonObj = resultsArrays.getJSONObject(0);
                            final String voiceMessage = jsonObj.getString("Transcription");

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

//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//
                                    //String recommendations_json = activity.spotifyApiRequest(voiceMessage, seeds.get(0), 10);
                                    activity.SpotifyWebAPIParser(activity.spotifyApiRequest(voiceMessage, seeds.get(0), 10));

//                                }
//                            }).start();
                        } catch (JSONException ex) {
                            textSearch.abort();
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
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.d("Post execute", "alright");
        activity.setFirstSong(voiceMessag);
    }
}
