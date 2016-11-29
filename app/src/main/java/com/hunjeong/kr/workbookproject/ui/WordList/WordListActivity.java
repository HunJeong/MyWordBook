package com.hunjeong.kr.workbookproject.ui.WordList;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.ui.SwipeExam.SwipeExamActivity;
import com.hunjeong.kr.workbookproject.model.Word;

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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_exam);
        fab.setOnClickListener(this);
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
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(getApplicationContext(), ((Word)adapterView.getItemAtPosition(i)).getWord(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("단어 수정");
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_explain, linearLayout, true);
        final EditText wordEdit = (EditText)v.findViewById(R.id.dialog_word);
        final EditText meanEdit = (EditText)v.findViewById(R.id.dialog_mean);
        final Word word = wordListAdapter.getSortedDatas().get(position);
        wordEdit.setText(word.getWord());
        meanEdit.setText(word.getMean());
        builder.setView(linearLayout);
        builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (wordEdit.getText().length() != 0 && meanEdit.getText().length() != 0)
                    editItem(word, wordEdit.getText().toString(), meanEdit.getText().toString());
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
        int id = view.getId();
        switch (id) {
            case R.id.start_exam:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("시험 설정");
                LinearLayout linearLayout = new LinearLayout(this);
                View content = getLayoutInflater().inflate(R.layout.dialog_exam, linearLayout, true);
                final Spinner wordSpinner = (Spinner)content.findViewById(R.id.exam_word_slt);
                final Spinner sortSpinner = (Spinner)content.findViewById(R.id.exam_sort_slt);
                final Spinner typeSpinner = (Spinner)content.findViewById(R.id.exam_type_slt);
                final Spinner sortSequenceSpinner = (Spinner)content.findViewById(R.id.exam_sort_sequence);
                final EditText numEdit = (EditText)content.findViewById(R.id.exam_num_edit);
                final RadioButton meadRadio = (RadioButton)content.findViewById(R.id.meadRadio);
                final RadioButton wordRadio = (RadioButton)content.findViewById(R.id.wordRadio);
                meadRadio.setChecked(true);

                String[] wordTyps = {"모든 단어", "체크 단어", "비체크 단어"};
                String[] sorts = {"생성 순서", "이름 순서", "랜덤"};
                String[] exams = {"넘기면서 외우기", "직접 쓰기", "발음 연습"};
                String[] sort_sequence = {"오름차순", "내림차순"};

                numEdit.setText("0");
                wordSpinner.setAdapter(new CustomSpinnerAdapter(getApplicationContext(), wordTyps));
                sortSpinner.setAdapter(new CustomSpinnerAdapter(getApplicationContext(), sorts));
                sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (adapterView.getItemAtPosition(i).equals("랜덤")) {
                            sortSequenceSpinner.setEnabled(false);
                            sortSequenceSpinner.setClickable(false);
                        } else {
                            sortSequenceSpinner.setEnabled(true);
                            sortSequenceSpinner.setClickable(true);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                typeSpinner.setAdapter(new CustomSpinnerAdapter(getApplicationContext(), exams));
                sortSequenceSpinner.setAdapter(new CustomSpinnerAdapter(getApplicationContext(), sort_sequence));

                builder.setPositiveButton("시작", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent exam = new Intent();
                        exam.putExtra("dictionaryId", dictionaryId);
                        exam.putExtra("mead", meadRadio.isChecked());
                        if (((String)typeSpinner.getSelectedItem()).equals("넘기면서 외우기")) {
                            try {
                                exam.setClass(getApplicationContext(), SwipeExamActivity.class);
                                exam.putExtra("wordType", (String) wordSpinner.getSelectedItem());
                                exam.putExtra("sort", (String) sortSpinner.getSelectedItem());
                                exam.putExtra("mistake", Integer.valueOf(numEdit.getText().toString()));
                                exam.putExtra("sequence", (String) sortSequenceSpinner.getSelectedItem());
                                startActivity(exam);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "숫자를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } else if (((String)typeSpinner.getSelectedItem()).equals("직접 쓰기")) {

                        } else {

                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setView(content);
                builder.show();
                break;
        }
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
