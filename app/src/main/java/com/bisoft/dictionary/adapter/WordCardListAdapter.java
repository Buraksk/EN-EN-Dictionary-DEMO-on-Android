package com.bisoft.dictionary.adapter;

/**
 * Created by burakisik on 16.04.2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisoft.dictionary.R;
import com.bisoft.dictionary.helper.WordsDBHelper;
import com.bisoft.dictionary.model.WordObject;

import java.util.List;
public class WordCardListAdapter extends RecyclerView.Adapter<WordCardListAdapter.AllWordViewHolder> {
    private Context context;
    private List<WordObject> wordsCartList;

    public class AllWordViewHolder extends RecyclerView.ViewHolder {
        private TextView word, phoneSpelling,description;
        public RelativeLayout viewBackground, viewForeground;

        public AllWordViewHolder(View view) {
            super(view);
            word = (TextView) view.findViewById(R.id.tvWord);
            phoneSpelling = (TextView) view.findViewById(R.id.tvPhoneSpelling);
            description = (TextView) view.findViewById(R.id.tvdescription);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }

    public WordCardListAdapter(Context context, List<WordObject> wordsCartList) {
        this.context = context;
        this.wordsCartList = wordsCartList;
    }

    @Override
    public AllWordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_item, parent, false);

        return new AllWordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AllWordViewHolder holder, int position) {
        WordObject word = wordsCartList.get(position);
        holder.word.setText(word.getWord());
        holder.phoneSpelling.setText(word.getPhoneSpelling());

        String[] parts = word.getDefinition().split("2");
        holder.description.setText((parts[0].trim()).replaceAll("1.", ""));
    }

    @Override
    public int getItemCount() {
        return wordsCartList.size();
    }

    public List<WordObject> getWords() {
        return wordsCartList;
    }

    public void removeItem(int position) {
        final WordObject word = wordsCartList.get(position);
        wordsCartList.remove(position);
        // notify the item removed by position
        notifyItemRemoved(position);
    }

    public void restoreItem(final WordObject word, int position) {
        wordsCartList.add(position, word);
        // notify item added by position
        notifyItemInserted(position);
    }
}