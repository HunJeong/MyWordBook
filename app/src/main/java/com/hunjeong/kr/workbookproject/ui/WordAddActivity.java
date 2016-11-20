package com.hunjeong.kr.workbookproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Word;
import com.hunjeong.kr.workbookproject.ui.WordList.WordListAdapter;

import io.realm.Realm;
import io.realm.Sort;

public class WordAddActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private String dictionaryId;
    private boolean isAdd = false;
    private Word modifiedWord;

    private Realm realm;
    private ListView listView;
    private EditText addWordEdit;
    private EditText addMeanEdit;
    private WordListAdapter wordListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add);
        getDictionaryId();
        initActionBar();
        initFloatingActionButton();
        initValue();
        initListView();
    }

    private void getDictionaryId() {
        Intent intent = getIntent();
        this.dictionaryId = intent.getStringExtra("dictionaryId");
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initListView() {
        listView = (ListView)findViewById(R.id.add_word_list);
        wordListAdapter = new WordListAdapter(getApplicationContext(), realm.where(Word.class).findAll());
        wordListAdapter.setSortBasis(WordListAdapter.SortBasis.CREATE_AT);
        wordListAdapter.setSortSequence(Sort.DESCENDING);
        listView.setAdapter(wordListAdapter);
        listView.setOnItemClickListener(this);
    }

    private void initValue() {
        realm = Realm.getDefaultInstance();
        addMeanEdit = (EditText)findViewById(R.id.add_word_edit);
        addWordEdit = (EditText)findViewById(R.id.add_mean_edit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
