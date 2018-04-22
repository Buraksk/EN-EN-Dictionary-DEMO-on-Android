package com.bisoft.dictionary.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
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
import com.bisoft.dictionary.helper.WordsDBHelper;
import com.bisoft.dictionary.model.WordObject;
import com.bisoft.dictionary.parser.JSONParseAsyncTask;
import com.bisoft.dictionary.parser.JSONParseCallback;

/**
 * Created by burakisik on 24.02.2018.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, View.OnKeyListener {
    WordsDBHelper db;
    //private Context mContext;
    WordObject word;
    EditText etSearch;
    TextView tvResult;
    ImageView ivSearch;
    TextView tvPhoneticSpelling;
    TextView tvWord;
    ImageView ivPlayAudioButton;
    ImageView ivAddFavourite;

    /*
    public HomeFragment(Context mContext){
        this.mContext = mContext;
    }*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //fixing the textview

        tvResult = (TextView) view.findViewById(R.id.tvResult);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        ivSearch = (ImageView) view.findViewById(R.id.ivSearch);
        tvPhoneticSpelling = (TextView) view.findViewById(R.id.tvPhoneticSpelling);
        tvWord = (TextView) view.findViewById(R.id.tvWord);
        ivPlayAudioButton = (ImageView) view.findViewById(R.id.ivPlayAudioButton);
        ivAddFavourite = (ImageView) view.findViewById(R.id.ivAddFavourite);


        tvResult.setVisibility(view.INVISIBLE);
        tvPhoneticSpelling.setVisibility(view.INVISIBLE);
        tvWord.setVisibility(view.INVISIBLE);
        ivPlayAudioButton.setVisibility(view.INVISIBLE);
        ivAddFavourite.setVisibility(view.INVISIBLE);

        etSearch.setOnKeyListener(this);  //when etSearch(virtual keyword's enter button) or ivSearch are pressed, client function is called
        ivSearch.setOnClickListener(this);

        ivAddFavourite.setOnClickListener(this);
        ivPlayAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(word.getAudioPath());
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
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) ||
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                client();
                return true;
            }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSearch:
                ivPlayAudioButton.setVisibility(View.INVISIBLE);
                tvPhoneticSpelling.setVisibility(View.INVISIBLE);
                tvWord.setVisibility(View.INVISIBLE);
                tvResult.setVisibility(View.INVISIBLE);
                ivAddFavourite.setVisibility(View.INVISIBLE);

                v = getActivity().getCurrentFocus();
                if (v != null) { //the virtual keyboard is turned off when the search button is pressed
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                client();
                break;
            case R.id.ivAddFavourite:
                if (isFavouriteWord(tvWord.getText().toString())) {
                    ivAddFavourite.setImageResource(R.drawable.ic_star_border_black_24dp);
                    setFavouriteFlagFalse(tvWord.getText().toString());
                } else {
                    ivAddFavourite.setImageResource(R.drawable.ic_star_black_24dp);
                    setFavouriteFlagTrue(tvWord.getText().toString());
                }
                break;
        }
    }

    private String dictionaryEntries() {
        final String language = "en";
        final String Word = etSearch.getText().toString().trim();
        final String word_id = Word.toLowerCase();
        return Constant.API_URL + language + "/" + word_id;
    }

    private void client() {
        HttpClientAsyncTask clientAsyncTask = new HttpClientAsyncTask(new HttpClientCallback() {
            @Override
            public void onResponseFinished(String result) {
                Log.i("Response From Task", result);
                parse(result);
            }
        });
        clientAsyncTask.execute(dictionaryEntries());
    }

    private void parse(final String jsonString) {
        JSONParseAsyncTask JsonparserAsyncTask = new JSONParseAsyncTask(new JSONParseCallback() {
            @Override
            public void onParseFinished(WordObject word) {
                if (word != null) {
                    Log.i("word:", word.getWord());
                    Log.i("defination:", word.getDefinition());
                    Log.i("spelling:", word.getPhoneSpelling());
                    Log.i("audiopath:", word.getAudioPath());
                    Log.i("audiopath:", word.getAudioPath());

                    ivPlayAudioButton.setVisibility(View.VISIBLE);
                    tvPhoneticSpelling.setVisibility(View.VISIBLE);
                    tvResult.setVisibility(View.VISIBLE);
                    tvWord.setVisibility(View.VISIBLE);
                    ivAddFavourite.setVisibility(View.VISIBLE);

                    if (isFavouriteWord(word.getWord())) {
                        ivAddFavourite.setImageResource(R.drawable.ic_star_black_24dp);
                    } else {
                        ivAddFavourite.setImageResource(R.drawable.ic_star_border_black_24dp);
                    }

                    tvWord.setText(word.getWord());
                    tvPhoneticSpelling.setText(word.getPhoneSpelling());
                    tvResult.setText(word.getDefinition());


                    //do it in thread!!!!!!!!!!!
                    //adding word in local database
                    db = new WordsDBHelper(getActivity());
                    if (!db.search(word.getWord().toLowerCase())) { //to prevent word repetition
                        db.insertWord(word);
                    }
                    db.close();
                }
            }
        }, getContext());
        JsonparserAsyncTask.execute(jsonString);
    }

    public boolean isFavouriteWord(final String word) {
        db = new WordsDBHelper(getActivity());

        final boolean[] isFavourite = new boolean[1];
        Thread thIsFavourite = new Thread() {
            @Override
            public void run() {
                isFavourite[0] = db.isFavouriteWord(word);
            }
        };
        thIsFavourite.start();

        while (thIsFavourite.isAlive()) ; //mutex!! if thread is done ,below line can executed

        //Log.i("isfavouriteWord",isFavourite[0]+"");

        if (isFavourite[0]) {
            return true;
        } else {
            return false;
        }
    }

    public void setFavouriteFlagFalse(final String word) {
        Thread thSetFavouriteFlagFalse = new Thread() {
            @Override
            public void run() {
                db = new WordsDBHelper(getActivity());
                db.makeFavouriteFlagFalse(word);
            }
        };
        thSetFavouriteFlagFalse.start();
    }

    public void setFavouriteFlagTrue(final String word) {
        Thread thSetFavouriteFlagTrue = new Thread() {
            @Override
            public void run() {
                db = new WordsDBHelper(getActivity());
                db.makeFavouriteFlagTrue(word);
            }
        };
        thSetFavouriteFlagTrue.start();
    }
}
