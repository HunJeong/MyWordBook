package com.hunjeong.kr.workbookproject.ui.WordList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Word;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.Sort;

/**
 * Created by JeongHun on 2016. 11. 15..
 */

public class WordListAdapter extends RealmBaseAdapter<Word> {

    private static final String TAG = "WordListAdapter";

    private Realm realm;
    private SortBasis sortBasis;
    private Sort sortSequence;

    private OrderedRealmCollection<Word> data;

    public enum SortBasis {
        WORD("word"), CREATE_AT("createAt"), NUM_OF_MISTAKE("numOfMistake");

        private String basis;

        SortBasis(String createAt) {
            this.basis = createAt;
        }

        public String getBasis() {
            return basis;
        }

    }

    public void setSortBasis(SortBasis sortBasis) {
        this.sortBasis = sortBasis;
    }

    public void setSortSequence(Sort sortSequence) {
        this.sortSequence = sortSequence;
    }

    public class ViewHolder {
        TextView word;
        TextView mean;
        TextView mistake;
        CheckBox important;
    }

    public WordListAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Word> data) {
        super(context, data);
        realm = Realm.getDefaultInstance();
        this. data = data;
    }

    public OrderedRealmCollection<Word> getDatas(){
        return data;
    }

    public OrderedRealmCollection<Word> getSortedDatas() {
        return getDatas().sort(sortBasis.getBasis(), sortSequence);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder  viewHolder;
        final Word word = adapterData.sort(sortBasis.getBasis(), sortSequence).get(i);

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.word_row, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.word = (TextView)view.findViewById(R.id.row_word);
            viewHolder.mean = (TextView)view.findViewById(R.id.row_mean);
            viewHolder.mistake = (TextView)view.findViewById(R.id.row_mistake);
            viewHolder.important = (CheckBox)view.findViewById(R.id.important_word);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        if (word != null) {
            Log.d(TAG, "" + i + " : " + word.getWord());
            viewHolder.word.setText(word.getWord());
            viewHolder.mean.setText(word.getMean());
            viewHolder.mistake.setText("틀린 횟수 : " + word.getNumOfMistake());
            viewHolder.important.setChecked(word.isImportant());
            viewHolder.important.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, i + "", Toast.LENGTH_SHORT).show();
                    realm.beginTransaction();
                    word.setImportant(!word.isImportant());
                    realm.commitTransaction();
                }
            });
            return view;
        }
        return null;
    }

}
