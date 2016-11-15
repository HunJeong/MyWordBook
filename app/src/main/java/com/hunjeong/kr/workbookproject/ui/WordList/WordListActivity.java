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

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Word;
import com.hunjeong.kr.workbookproject.ui.MaterialSheetFab.Fab;

import io.realm.Realm;

public class WordListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener{

    private static final String TAG = "WordListActivity";

    private ListView listView;

    private Realm realm;
    private String dictionaryId;
    private String dictionaryName;
    private Intent intent;
    private WordListAdapter wordListAdapter;
    private MaterialSheetFab materialSheetFab;

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
        Fab fab = (Fab) findViewById(R.id.list_fab);

        View sheetView = findViewById(R.id.list_fab_sheet);
        View overlay = findViewById(R.id.list_overlay);
        int sheetColor = getResources().getColor(R.color.white);
        int fabColor = getResources().getColor(R.color.CustomAccent);

        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);
        findViewById(R.id.list_fab_sheet_item_csv).setOnClickListener(this);
        findViewById(R.id.list_fab_sheet_item_excel).setOnClickListener(this);
        findViewById(R.id.list_fab_sheet_item_do).setOnClickListener(this);
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
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        return true;
    }

    @Override
    public void onClick(View view) {

    }
}
