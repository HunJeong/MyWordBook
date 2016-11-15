package com.hunjeong.kr.workbookproject.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Dictionary;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

/**
 * Created by JeongHun on 2016. 11. 11..
 */

public class CardAdapter extends RealmRecyclerViewAdapter<Dictionary, CardAdapter.ViewHolder> {

    private static final String TAG = "CardAdapter";

    public enum SortBasis {
        NAME("name"), CREATEAT("createAt");

        private String basis;

        SortBasis(String createAt) {
            basis = createAt;
        }

        public String getBasis() {
            return basis;
        }
    }

    private MainActivity activity;
    private SortBasis sortBasis = SortBasis.CREATEAT;
    private Sort sortSequence = Sort.ASCENDING;

    public CardAdapter(@NonNull MainActivity activity, @Nullable OrderedRealmCollection<Dictionary> data) {
        super(activity, data, true);
        this.activity = activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dictionary_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dictionary dictionary = getData().sort(sortBasis.getBasis(), sortSequence).get(position);
        holder.title.setText(dictionary.getTitle());
        holder.explanation.setText(dictionary.getExplain());
        holder.dictionary = dictionary;
        Log.d(TAG, dictionary.getTitle() + " : " + position + " : " + dictionary.getCreateAt());
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{
        Dictionary dictionary;
        TextView title;
        TextView explanation;
        ImageButton menu;

        public ViewHolder(View view) {
            super(view);
            this.title = (TextView)view.findViewById(R.id.card_title);
            this.explanation = (TextView)view.findViewById(R.id.card_explanation);
            this.menu = (ImageButton)view.findViewById(R.id.card_menu);
            view.setOnClickListener(this);
            menu.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.card_menu:
                    initPopupMenu(view);
                    break;
                default:
                    /**
                     * Implement Intent
                     */
                    break;
            }
        }

        private void initPopupMenu(View menu) {
            PopupMenu popupMenu = new PopupMenu(context, menu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_card, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_modify:
                    /**
                     * dictionary modify implement
                     */
                    for (Dictionary dictionary: getData()) {
                        Log.d(TAG, dictionary.getTitle());
                    }
                    break;
                case R.id.action_delete:
                    Log.d(TAG, "delete " + dictionary.getTitle());
                    activity.deleteItem(dictionary);
                    break;
            }
            return true;
        }
    }

    public void setSortBasis(SortBasis sortBasis) {
        this.sortBasis = sortBasis;
    }

    public void setSortSequence(Sort sortSequence) {
        this.sortSequence = sortSequence;
    }
}
