package com.bisoft.dictionary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisoft.dictionary.model.WordObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by burakisik on 23.02.2018.
 */

public class DictionaryActivity  extends Activity implements View.OnClickListener {

        WordObject word;
        EditText etSearch;
        TextView tvResult;
        //Button ara;
        ImageView ivSearch;
        TextView tvPhoneticSpelling;
        TextView tvWord;
        ImageView ivPlayAudioButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dictionary);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //fixing the textview
        tvResult = (TextView) findViewById(R.id.tvResult);
        etSearch = (EditText) findViewById(R.id.etSearch);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);

        tvPhoneticSpelling = (TextView)findViewById(R.id.tvPhoneticSpelling);
        tvWord = (TextView) findViewById(R.id.tvWord);
        ivPlayAudioButton = (ImageView) findViewById(R.id.ivPlayAudioButton);
        ivPlayAudioButton.setVisibility(View.INVISIBLE);

        ivSearch.setOnClickListener(this);
        ivPlayAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(word.audioPath);
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
/*
    @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view= inflater.inflate(R.layout.fragment_dictionary, container, false);


            tvResult = (TextView) view.findViewById(R.id.tvResult);
            etSearch = (EditText) view.findViewById(R.id.etSearch);
            ivSearch = (ImageView) view.findViewById(R.id.ivSearch);

            tvPhoneticSpelling = (TextView) view.findViewById(R.id.tvPhoneticSpelling);
            tvWord = (TextView) view.findViewById(R.id.tvWord);
            ivPlayAudioButton = (ImageView) view.findViewById(R.id.ivPlayAudioButton);
            ivPlayAudioButton.setVisibility(View.INVISIBLE);

            ivSearch.setOnClickListener(this);
            ivPlayAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        MediaPlayer player = new MediaPlayer();
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.setDataSource(word.audioPath);
                        player.prepare();
                        player.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return view;
        }
*/
        @Override
        public void onClick(View view) {
            // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            view = getCurrentFocus();
            if(view != null) { // arama butonuna bastığında sanal klavyenin kapanmasını saylıyot
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            new CallbackTask().execute(dictionaryEntries());
        }

        private String dictionaryEntries(){
            final String language = "en";
            final String Word = etSearch.getText().toString().trim();
            final String word_id = Word.toLowerCase();

            return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id; //"https://od-api.oxforddictionaries.com:443/api/v1/search/" + language + "?q=" + word_id;
        }

        public class CallbackTask extends AsyncTask<String,Integer,JSONObject> {

            private ProgressDialog pd;

            public CallbackTask(){
                pd = new ProgressDialog(DictionaryActivity.this);
            }

            @Override
            protected void onPreExecute() {
                this.pd.setMessage("Word is searching...");
                this.pd.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                String stringJsondata = DictionaryHttpClient.getJsonString(params[0]);

                if(stringJsondata.equals("Please check internet connection"))
                    return null;

                JSONObject jsonData=null;

                try {
                    jsonData = new JSONObject(stringJsondata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return jsonData;
            }
            @Override
            protected void onPostExecute(JSONObject jsonData) {
                super.onPostExecute(jsonData);
                ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE); //internetbaglantısı controlu yapıyor

                pd.dismiss();

                ivPlayAudioButton.setVisibility(View.INVISIBLE);
                tvPhoneticSpelling.setVisibility(View.INVISIBLE);
                tvResult.setVisibility(View.INVISIBLE);

                if(cm.getActiveNetworkInfo() == null && jsonData == null)
                {
                    tvWord.setText("Please check internet connection");
                    return;
                }
                else if(jsonData == null) {
                    tvWord.setText("Word not found");
                    return;
                }


                word = new WordObject();

                try {
                    JSONArray resultsArr = jsonData.getJSONArray("results");
                    word.word =resultsArr.getJSONObject(0).get("id").toString();

                    JSONArray lexicalEntriesArr = resultsArr.getJSONObject(0).getJSONArray("lexicalEntries");
                    JSONArray entriesArr = lexicalEntriesArr.getJSONObject(0).getJSONArray("entries");
                    JSONArray sensesArr = entriesArr.getJSONObject(0).getJSONArray("senses");

                    JSONArray pronunciationsArr =  lexicalEntriesArr.getJSONObject(0).getJSONArray("pronunciations");


                    word.phoneSpelling = pronunciationsArr.getJSONObject(0).getString("phoneticSpelling");
                    String audioFilePath="";
                    if(pronunciationsArr.getJSONObject(0).getString("audioFile") != null) {
                        audioFilePath = pronunciationsArr.getJSONObject(0).get("audioFile").toString();
                        word.audioPath = audioFilePath;
                    }
                    String definition="";
                    word.definition ="";
                    for(int i=0;i<sensesArr.length();i++)
                    {
                        JSONObject object= sensesArr.getJSONObject(i);
                        JSONArray defArr = object.getJSONArray("definitions");
                        definition =" " + (i+1)+ "." + defArr.getString(0);
                        word.definition += definition + "\n\n";
                    }
                    Log.d("Temp isssssssss",resultsArr.toString());

                    ivPlayAudioButton.setVisibility(View.VISIBLE);
                    tvPhoneticSpelling.setVisibility(View.VISIBLE);
                    tvResult.setVisibility(View.VISIBLE);

                    tvWord.setText(word.word);
                    tvPhoneticSpelling.setText(word.phoneSpelling);
                    tvResult.setText(word.definition);

                } catch (JSONException e) {
                    e.printStackTrace();
                    tvWord.setText("Word not found");
                    ivPlayAudioButton.setVisibility(View.INVISIBLE);
                }

            }
        }
    }


