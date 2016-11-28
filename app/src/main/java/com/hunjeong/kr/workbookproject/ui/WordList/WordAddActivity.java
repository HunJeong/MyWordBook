package com.hunjeong.kr.workbookproject.ui.WordList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Word;

import io.realm.Realm;
import io.realm.Sort;

public class WordAddActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private String dictionaryId;

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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.word_add_fab);
        fab.setOnClickListener(this);
    }

    private void initListView() {
        listView = (ListView)findViewById(R.id.add_word_list);
        wordListAdapter = new WordListAdapter(getApplicationContext(), realm.where(Word.class).equalTo("dictionaryId", dictionaryId).findAll());
        wordListAdapter.setSortBasis(WordListAdapter.SortBasis.CREATE_AT);
        wordListAdapter.setSortSequence(Sort.DESCENDING);
        listView.setAdapter(wordListAdapter);
        listView.setOnItemClickListener(this);
    }

    private void initValue() {
        realm = Realm.getDefaultInstance();
        addMeanEdit = (EditText)findViewById(R.id.add_mean_edit);
        addWordEdit = (EditText)findViewById(R.id.add_word_edit);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.word_add_fab:
                if (addWordEdit.getText().length() == 0 || addMeanEdit.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요,", Toast.LENGTH_SHORT).show();
                    break;
                }
                realm.beginTransaction();
                realm.copyToRealm(new Word(dictionaryId, addWordEdit.getText().toString(), addMeanEdit.getText().toString()));
                realm.commitTransaction();
                addMeanEdit.setText("");
                addWordEdit.setText("");
                Toast.makeText(getApplicationContext(), "단어가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
