package com.bisoft.dictionary.client;

import android.os.AsyncTask;

import com.bisoft.dictionary.constants.Constant;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by burakisik on 24.02.2018.
 */

public class HttpClientAsyncTask extends AsyncTask<String, Object, String> {
    public HttpClientCallback delegate = null;
    public HttpClientAsyncTask(HttpClientCallback response){
        delegate = response;
    }

    @Override
    protected String doInBackground(String...params) {
        try{
            URL url = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("app_id", Constant.APP_ID);
            urlConnection.setRequestProperty("app_key", Constant.APP_KEY);

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        }catch (Exception e) {
            e.printStackTrace();
            return "Please check internet connection";
        }
    }

    @Override
    protected void onPostExecute(String result){
        delegate.onResponseFinished(result);
    }
}