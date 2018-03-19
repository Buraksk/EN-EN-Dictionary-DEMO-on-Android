package com.bisoft.dictionary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.bisoft.dictionary.R;
import com.bisoft.dictionary.helper.AllWordsDBHelper;
import com.bisoft.dictionary.model.WordObject;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by burakisik on 18.03.2018.
 */

public class AllWordsFragment extends Fragment {

    ListView list;
    public AllWordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allwords, container, false);

        list=(ListView) view.findViewById(R.id.listview);
        getFavouriteWords();

       /*
        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, array);
        list.setAdapter(adapter);
      */
        return view;
    }

    private void getFavouriteWords(){
        Thread thGetWord = new Thread() {
            @Override
            public void run() {
                //getting word in local database
                AllWordsDBHelper allWordsDBHelper = new AllWordsDBHelper(getActivity());
                ArrayList<WordObject> wordList = allWordsDBHelper.getAllWords();
                final List<String> wordname = new ArrayList<String>();
                for (WordObject word: wordList) {
                    wordname.add(word.getWord());
                    //Log.i("word",word.getWord());
                }
                //Log.i("count",wordList.size()+"");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                getActivity(),android.R.layout.simple_list_item_1,wordname);
                        list.setAdapter(arrayAdapter); //fill in listview
                    }
                });
            }
        };
        thGetWord.start();
    }
}
