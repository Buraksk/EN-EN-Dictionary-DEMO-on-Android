package com.bisoft.dictionary.parser;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import com.bisoft.dictionary.model.WordObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by burakisik on 24.02.2018.
 */

public class JSONParseAsyncTask extends AsyncTask<String, Object, WordObject>{
    public JSONParseCallback delegate = null;
    private ProgressDialog pd;
    private Context mContext;

    public JSONParseAsyncTask(JSONParseCallback response,Context mContext) {
        delegate = response;
        this.mContext = mContext;
        pd = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        this.pd.setMessage("Word is searching...");
        this.pd.show();
    }

    @Override
    protected WordObject doInBackground(String...params) {
        String JsonString = params[0];
        if(JsonString.equals("Please check internet connection"))
            return null;

        JSONObject jsonData=null;

        try {
            jsonData = new JSONObject(JsonString);
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE); //internetbaglantısı controlu yapıyor
            pd.dismiss();

            //ivPlayAudioButton.setVisibility(View.INVISIBLE);
            //tvPhoneticSpelling.setVisibility(View.INVISIBLE);
            //tvResult.setVisibility(View.INVISIBLE);

            if(cm.getActiveNetworkInfo() == null && jsonData == null)
            {
                //tvWord.setText("Please check internet connection");
                return null;
            }
            else if(jsonData == null) {
                //tvWord.setText("Word not found");
                return null;
            }


            WordObject word = new WordObject();


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

                //ivPlayAudioButton.setVisibility(View.VISIBLE);
                //tvPhoneticSpelling.setVisibility(View.VISIBLE);
                //tvResult.setVisibility(View.VISIBLE);

                //tvWord.setText(word.word);
                //tvPhoneticSpelling.setText(word.phoneSpelling);
                //tvResult.setText(word.definition);
             return word;

            } catch (JSONException e) {
                e.printStackTrace();
                //tvWord.setText("Word not found");
                //ivPlayAudioButton.setVisibility(View.INVISIBLE);
            }
        return null;
    }
    @Override
    protected void onPostExecute(WordObject result) {
        delegate.onParseFinished(result);
    }
}
