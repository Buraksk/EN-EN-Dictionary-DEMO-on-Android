package com.bisoft.dictionary.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisoft.dictionary.R;
import com.bisoft.dictionary.client.HttpClientAsyncTask;
import com.bisoft.dictionary.client.HttpClientCallback;
import com.bisoft.dictionary.constants.Constant;
import com.bisoft.dictionary.helper.AllWordsDBHelper;
import com.bisoft.dictionary.model.WordObject;
import com.bisoft.dictionary.parser.JSONParseAsyncTask;
import com.bisoft.dictionary.parser.JSONParseCallback;

/**
 * Created by burakisik on 24.02.2018.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{
    private Context mContext;
    WordObject word;
    EditText etSearch;
    TextView tvResult;
    ImageView ivSearch;
    TextView tvPhoneticSpelling;
    TextView tvWord;
    ImageView ivPlayAudioButton;

    /*
    public HomeFragment(Context mContext){
        this.mContext = mContext;
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_dictionary,container,false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //fixing the textview
        tvResult = (TextView) view.findViewById(R.id.tvResult);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        ivSearch = (ImageView) view.findViewById(R.id.ivSearch);
        tvPhoneticSpelling = (TextView)view.findViewById(R.id.tvPhoneticSpelling);
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

    @Override
    public void onClick(View v) {
        ivPlayAudioButton.setVisibility(View.INVISIBLE);
        tvPhoneticSpelling.setVisibility(View.INVISIBLE);
        tvWord.setVisibility(View.INVISIBLE);
        tvResult.setVisibility(View.INVISIBLE);

        v = getActivity().getCurrentFocus();
        if(v != null) { //the virtual keyboard is turned off when the search button is pressed
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        client();
    }

    private String dictionaryEntries(){
        final String language = "en";
        final String Word = etSearch.getText().toString().trim();
        final String word_id = Word.toLowerCase();
        return Constant.API_URL+language + "/" + word_id;
    }
    private void client(){
        //new DictionaryActivity.CallbackTask().execute(dictionaryEntries());
        HttpClientAsyncTask clientAsyncTask = new HttpClientAsyncTask(new HttpClientCallback() {
            @Override
            public void onResponseFinished(String result){
                Log.i("Response From Task",result);
                parse(result);
            }
        });
        clientAsyncTask.execute(dictionaryEntries());
    }
    private void parse(final String jsonString){
        //new DictionaryActivity.CallbackTask().execute(dictionaryEntries());
        JSONParseAsyncTask JsonparserAsyncTask = new JSONParseAsyncTask(new JSONParseCallback() {

            @Override
            public void onParseFinished(WordObject word) {
                if(word != null) {
                    Log.i("word:", word.getWord());
                    Log.i("defination:", word.getDefinition());
                    Log.i("spelling:", word.getPhoneSpelling());
                    Log.i("audiopath:", word.getAudioPath());

                    ivPlayAudioButton.setVisibility(View.VISIBLE);
                    tvPhoneticSpelling.setVisibility(View.VISIBLE);
                    tvResult.setVisibility(View.VISIBLE);
                    tvWord.setVisibility(View.VISIBLE);

                    tvWord.setText(word.word);
                    tvPhoneticSpelling.setText(word.phoneSpelling);
                    tvResult.setText(word.definition);

                    //adding word in local database
                    AllWordsDBHelper db = new AllWordsDBHelper(getActivity());
                    if(!db.search(word.word.toLowerCase())){
                        db.insertWord(word);
                    }
                    db.close();
                }
            }
        },getContext());
        JsonparserAsyncTask.execute(jsonString);
    }
}
