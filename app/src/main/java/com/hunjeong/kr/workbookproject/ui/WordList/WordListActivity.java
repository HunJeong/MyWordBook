package com.hunjeong.kr.workbookproject.ui.WordList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Word;

import io.realm.Realm;

public class WordListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private static final String TAG = "WordListActivity";

    private ListView listView;

    private Realm realm;
    private String dictionaryId;
    private String dictionaryName;
    private Intent intent;
    private WordListAdapter wordListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        initActionBar();
        initFloatingActionButton();
        initValue();
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        intent = getIntent();
        dictionaryId = intent.getStringExtra("dictionaryId");
        dictionaryName = intent.getStringExtra("dictionaryName");
        actionBar.setTitle(dictionaryName);
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void initValue() {
        realm = Realm.getDefaultInstance();
        wordListAdapter = new WordListAdapter(getApplicationContext(), realm.where(Word.class).equalTo("dictionaryId", dictionaryId).findAll());

        listView = (ListView)findViewById(R.id.word_list);
        listView.setAdapter(wordListAdapter);
        listView.setEmptyView(findViewById(R.id.empty_view));
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        return true;
    }
}
