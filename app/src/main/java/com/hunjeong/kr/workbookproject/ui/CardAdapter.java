package com.hunjeong.kr.workbookproject.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Dictionary;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by JeongHun on 2016. 11. 11..
 */

public class CardAdapter extends RealmRecyclerViewAdapter<Dictionary, CardAdapter.ViewHolder> {

    private MainActivity activity;

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
        Dictionary dictionary = getData().get(position);
        holder.title.setText(dictionary.getTitle());
        holder.explanation.setText(dictionary.getExplain());
        holder.dictionary = dictionary;
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
            menu.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.card_menu:
                    initPopupMenu(view);
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
                    break;
                case R.id.action_delete:
                    activity.deleteItem(dictionary);
                    break;
            }
            return false;
        }
    }

}
