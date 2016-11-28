
package com.hunjeong.kr.workbookproject.ui.SwipeExam;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Word;
import com.hunjeong.kr.workbookproject.ui.WordList.WordListAdapter;

import java.util.Iterator;
import java.util.LinkedList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import link.fls.swipestack.SwipeStack;

public class SwipeExamActivity extends AppCompatActivity {

    private static final String TAG = "SwipeExamActivity";

    private Realm realm;
    private Intent intent;
    private String dictionaryId;
    private String wordType;
    private String sortType;
    private String sortSequence;
    private int numOfMistake;
    private boolean meanExam;

    private SwipeStack swipeStack;
    private SwipeStackAdapter swipeStactAdapter;
    private LinkedList<Word> linkedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_exam);
        initActionBar();
        initValue();
        initRealm();
        initView();

    }

    private void initRealm() {
        realm = Realm.getDefaultInstance();
        RealmQuery<Word> realmQuery = realm.where(Word.class)
                .equalTo("dictionaryId", dictionaryId)
                .greaterThanOrEqualTo("numOfMistake", numOfMistake);
         if (wordType.equals("체크 단어")) {
            realmQuery = realmQuery.equalTo("isImportant", true);
        } else if (wordType.equals("비체크 단어")){
             realmQuery = realmQuery.equalTo("isImportant", false);
        }

        RealmResults<Word> realmResult = realmQuery.findAll();

        if (sortType.equals("생성 순서")) {
            if (sortSequence.equals("오름차순")) {
                realmResult = realmResult.sort("createAt");
            } else if (sortSequence.equals("내림차순")) {
                realmResult = realmResult.sort("createAt", Sort.DESCENDING);
            }
        } else if (sortType.equals("이름 순서")) {
            if (sortSequence.equals("오름차순")) {
                realmResult = realmResult.sort("word");
            } else if (sortSequence.equals("내림차순")) {
                realmResult = realmResult.sort("word", Sort.DESCENDING);
            }
        } else {
            //Random
        }

        linkedList.addAll(realmResult);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initValue() {
        intent = getIntent();
        dictionaryId = intent.getStringExtra("dictionaryId");
        wordType = intent.getStringExtra("wordType");
        sortType = intent.getStringExtra("sort");
        numOfMistake = intent.getIntExtra("mistake", 0);
        meanExam = intent.getBooleanExtra("mean", true);
        sortSequence = intent.getStringExtra("sequence");
        linkedList = new LinkedList<>();
    }

    private void initView() {
        swipeStack = (SwipeStack)findViewById(R.id.swipeStack);
        swipeStactAdapter = new SwipeStackAdapter(getApplicationContext(), linkedList, meanExam);
        swipeStack.setAdapter(swipeStactAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
