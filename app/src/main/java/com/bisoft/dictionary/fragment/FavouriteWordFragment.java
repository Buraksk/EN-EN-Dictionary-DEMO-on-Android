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
import com.bisoft.dictionary.helper.RecyclerSwipeControllerHelper;
import com.bisoft.dictionary.helper.WordsDBHelper;
import com.bisoft.dictionary.model.WordObject;
import java.util.ArrayList;
import java.util.List;

public class FavouriteWordFragment extends Fragment implements RecyclerSwipeControllerHelper.RecyclerItemTouchListener{

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
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerSwipeControllerHelper(0, ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        fillRecyclerView();
        return view;
    }

    private void fillRecyclerView(){
        Thread thFillRecyclerView = new Thread() {
            @Override
            public void run() {
                //getting word in local database
                WordsDBHelper favouriteWordList = new WordsDBHelper(getActivity());
                final ArrayList<WordObject> wordList = favouriteWordList.getFavouriteWords();
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

            mAdapter.removeItem(viewHolder.getAdapterPosition()); //remove the item from recyclerview
            setFavouriteFlagFalse(deletedItem.getWord());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from List!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                    setFavouriteFlagTrue(deletedItem.getWord());
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public void setFavouriteFlagFalse(final String word){
        Thread thSetFavouriteFlagFalse = new Thread() {
            @Override
            public void run() {
                WordsDBHelper db = new WordsDBHelper(getContext());
                db.makeFavouriteFlagFalse(word);
            }
        };
        thSetFavouriteFlagFalse.start();
    }

    public void setFavouriteFlagTrue(final String word){
        Thread thSetFavouriteFlagTrue = new Thread() {
            @Override
            public void run() {
                WordsDBHelper db = new WordsDBHelper(getContext());
                db.makeFavouriteFlagTrue(word);
            }
        };
        thSetFavouriteFlagTrue.start();
    }
}
