package com.hunjeong.kr.workbookproject.ui.WordList;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Word;
import com.hunjeong.kr.workbookproject.ui.MaterialSheetFab.Fab;

import io.realm.Realm;
import io.realm.Sort;

public class WordListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener{

    private static final String TAG = "WordListActivity";

    private SwipeMenuListView listView;

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
        //wordListAdapter = new WordListAdapter(getApplicationContext(), realm.where(Word.class).equalTo("dictionaryId", dictionaryId).findAll());
        initListView();
    }

    private void initListView() {
        wordListAdapter = new WordListAdapter(getApplicationContext(), realm.where(Word.class).equalTo("dictionaryId", dictionaryId).findAll()); //Test
        wordListAdapter.setSortBasis(WordListAdapter.SortBasis.CREATE_AT);
        wordListAdapter.setSortSequence(Sort.DESCENDING);
        listView = (SwipeMenuListView) findViewById(R.id.word_list);
        listView.setAdapter(wordListAdapter);
        listView.setEmptyView(findViewById(R.id.empty_view));
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        initListViewMenu();
    }

    private void initListViewMenu() {
        SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(270);
                // set a icon
                deleteItem.setIcon(android.R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(menuCreator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        realm.beginTransaction();
                        Word word = wordListAdapter.getSortedDatas().get(position);
                        Log.d(TAG, "" + position + " : " + word.getWord());
                        realm.where(Word.class).equalTo("dictionaryId", word.getDictionaryId()).equalTo("word", word.getWord()).equalTo("mean", word.getMean()).findFirst().deleteFromRealm();
                        realm.commitTransaction();
                        wordListAdapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });
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
        Toast.makeText(getApplicationContext(), ((Word)adapterView.getItemAtPosition(i)).getWord(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("단어 수정");
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_explain, linearLayout, true);
        final EditText wordEdit = (EditText)v.findViewById(R.id.dialog_word);
        wordEdit.getBackground().setColorFilter(0xff4081, PorterDuff.Mode.SRC_IN);
        final EditText meanEdit = (EditText)v.findViewById(R.id.dialog_mean);
        meanEdit.getBackground().setColorFilter(0xff4081, PorterDuff.Mode.SRC_IN);
        wordEdit.setText(((Word)adapterView.getItemAtPosition(position)).getWord());
        meanEdit.setText(((Word)adapterView.getItemAtPosition(position)).getMean());
        builder.setView(linearLayout);
        builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (wordEdit.getText().length() != 0 && meanEdit.getText().length() != 0)
                    editItem((Word)adapterView.getItemAtPosition(position), wordEdit.getText().toString(), meanEdit.getText().toString());
                else if (wordEdit.getText().length() == 0)
                    Toast.makeText(getApplicationContext(), "단어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "뜻을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
        return true;
    }

    public void editItem(final Word editWord, final String modifiedWord, final String modifiedMean) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Word word = realm.where(Word.class).equalTo("dictionaryId", editWord.getDictionaryId()).equalTo("word", editWord.getWord()).equalTo("mean", editWord.getMean()).findFirst();
                word.setWord(modifiedWord);
                word.setMean(modifiedMean);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add_word:
                Intent intent = new Intent(this, WordAddActivity.class);
                intent.putExtra("dictionaryId", dictionaryId);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
