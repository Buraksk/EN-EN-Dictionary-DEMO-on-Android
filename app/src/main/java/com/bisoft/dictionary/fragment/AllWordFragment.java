package com.bisoft.dictionary.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bisoft.dictionary.R;
import com.bisoft.dictionary.adapter.WordCardListAdapter;
import com.bisoft.dictionary.helper.WordsDBHelper;
import com.bisoft.dictionary.helper.RecyclerSwipeControllerHelper;
import com.bisoft.dictionary.model.WordObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by burakisik on 18.03.2018.
 */

public class AllWordFragment extends Fragment implements RecyclerSwipeControllerHelper.RecyclerItemTouchListener {
    private RecyclerView recyclerView;
    private List<WordObject> cartList;
    private WordCardListAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wordlist, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        coordinatorLayout = view.findViewById(R.id.coordinator_layout);
        cartList = new ArrayList<>();
        mAdapter = new WordCardListAdapter(getActivity(), cartList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        //adding item touch helper
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerSwipeControllerHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        fillRecyclerView();
        return view;
    }
    private void fillRecyclerView(){
        Thread thFillRecyclerView= new Thread() {
            @Override
            public void run() {
                //getting word in local database
                WordsDBHelper allWordsDBHelper = new WordsDBHelper(getActivity());
                final ArrayList<WordObject> wordList = allWordsDBHelper.getAllWords();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //fill in recyclerView
                        cartList.clear();
                        cartList.addAll(wordList);
                        //refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        thFillRecyclerView.start();
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof WordCardListAdapter.AllWordViewHolder) {
            // get the removed item name to display it in snack bar
            String name = cartList.get(viewHolder.getAdapterPosition()).getWord();
            // backup of removed item for undo purpose
            final WordObject deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            mAdapter.removeItem(viewHolder.getAdapterPosition());// remove the item from recyclerview
            removeFromDB(deletedItem.getWord());// remove the item from local DB

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from List!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                    undoToDB(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void removeFromDB(final String word){
        //delete word on local db
        Thread thDeleteWord = new Thread() {
            @Override
            public void run() {
                //getting word in local database
                WordsDBHelper dbHelper = new WordsDBHelper(getContext());
                dbHelper.deleteWord(word);
            }
        };
        thDeleteWord.start();
    }

    private void undoToDB(final WordObject word){
        //reinsert word on local db
        Thread thRestoreWord = new Thread() {
            @Override
            public void run() {
                //insert word to local database
                WordsDBHelper dbHelper = new WordsDBHelper(getContext());
                dbHelper.insertWord(word);
            }
        };
        thRestoreWord.start();
    }
}
