
package com.hunjeong.kr.workbookproject.ui.SwipeExam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Word;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import link.fls.swipestack.SwipeStack;

public class SwipeExamActivity extends AppCompatActivity {

    private static final String TAG = "SwipeExamActivity";

    private Realm realm;
    private Context context;
    private Intent intent;
    private String dictionaryId;
    private String wordType;
    private String sortType;
    private String sortSequence;
    private int numOfMistake;
    private boolean meanExam;

    private SwipeStack swipeStack;
    private SwipeStackAdapter swipeStackAdapter;
    private FloatingActionButton fab;
    private LinkedList<Word> linkedList;
    private LinkedList<Word> mistakeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_exam);
        initActionBar();
        initValue();
        initRealm();
        initView();

    }

    /**
     * Init and Get Database's information
     * Make linkedList consist of selected words
     */
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

        linkedList = initLinkedList(realmResult, sortType, sortSequence);
        mistakeList = new LinkedList<>();
        shuffleLinkedList(linkedList, sortType.equals("랜덤"));
    }

    /**
     * Init linkedList with information that received by previous Activity's intent
     * @param realmResult : Selected data set
     * @param sort : Condition of sort
     * @param sequence : Condition of sequence
     * @return : LinkedList consist of selected words
     */
    private LinkedList<Word> initLinkedList(RealmResults<Word> realmResult, String sort, String sequence) {
        if (sort.equals("생성 순서")) {
            if (sequence.equals("오름차순")) {
                realmResult = realmResult.sort("createAt");
            } else if (sequence.equals("내림차순")) {
                realmResult = realmResult.sort("createAt", Sort.DESCENDING);
            }
        } else if (sort.equals("이름 순서")) {
            if (sequence.equals("오름차순")) {
                realmResult = realmResult.sort("word");
            } else if (sequence.equals("내림차순")) {
                realmResult = realmResult.sort("word", Sort.DESCENDING);
            }
        }
        return new LinkedList<>(realmResult);
    }

    /**
     * Shuffle linkedList
     * @param linkedList
     * @param isSort
     */
    private void shuffleLinkedList(LinkedList<Word> linkedList, boolean isSort) {
        if (isSort) {
             Collections.shuffle(linkedList);
        }
    }

    /**
     * User is incorrect words then add mistake count to every word that user is incorrect
     * @param linkedList
     */
    private void addMistake(LinkedList<Word> linkedList) {
        realm.beginTransaction();
        for (Word word : linkedList) {
            word.setNumOfMistake(word.getNumOfMistake() + 1);
        }
        realm.commitTransaction();
    }

    /**
     * Make backbutton in actionbar
     */
    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Initialize Activity's member value
     */
    private void initValue() {
        intent = getIntent();
        dictionaryId = intent.getStringExtra("dictionaryId");
        wordType = intent.getStringExtra("wordType");
        sortType = intent.getStringExtra("sort");
        numOfMistake = intent.getIntExtra("mistake", 0);
        meanExam = intent.getBooleanExtra("mean", true);
        sortSequence = intent.getStringExtra("sequence");
        context = this;
    }

    /**
     * Initialize Activity's View
     */
    private void initView() {
        fab = (FloatingActionButton)findViewById(R.id.show_detail);
        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //Toast.makeText(getApplicationContext(), "Pressed", Toast.LENGTH_SHORT).show();
                        View topView = swipeStack.getTopView();
                        topView.findViewById(R.id.swipe_exam_down).setBackgroundColor(Color.WHITE);
                        return true;
                    case MotionEvent.ACTION_UP:
                        View topView1 = swipeStack.getTopView();
                        topView1.findViewById(R.id.swipe_exam_down).setBackgroundColor(Color.BLACK);
                        //Toast.makeText(getApplicationContext(), "Released", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        swipeStack = (SwipeStack)findViewById(R.id.swipeStack);
        Log.d(TAG, meanExam + "");
        swipeStackAdapter = new SwipeStackAdapter(getApplicationContext(), linkedList, meanExam);
        swipeStack.setAdapter(swipeStackAdapter);
        swipeStack.setListener(new SwipeStack.SwipeStackListener() {
            @Override
            public void onViewSwipedToLeft(int position) {

            }

            @Override
            public void onViewSwipedToRight(int position) {
                mistakeList.add(linkedList.get(position));
            }

            @Override
            public void onStackEmpty() {

                LinkedList<Word> tmp = mistakeList;
                mistakeList = linkedList;
                linkedList = tmp;

                mistakeList.clear();

                Log.d(TAG, linkedList.size() + "");
                Log.d(TAG, mistakeList.size() + "");
                if (linkedList.isEmpty()) {
                    AlertDialog.Builder examComplete = new AlertDialog.Builder(context);
                    examComplete.setTitle("단어시험이 종료되었습니다.");
                    examComplete.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    examComplete.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    });
                    examComplete.show();
                    return;
                }
                addMistake(linkedList);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("단어시험이 종료되었습니다,");
                builder.setMessage("틀린 단어를 다시 학습하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shuffleLinkedList(linkedList, sortType.equals("랜덤"));
                        swipeStackAdapter.setList(linkedList);
                        swipeStackAdapter.notifyDataSetChanged();
                        swipeStack.invalidate();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                exit();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    /**
     * Make finish dialog when user press backbutton
     */
    private void exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("종료");
        builder.setMessage("종료하시겠습니까?");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog.Builder saveState = new AlertDialog.Builder(context);
                saveState.setTitle("종료");
                saveState.setMessage("현재까지 진행한 결과를 저장하시겠습니까?");
                saveState.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addMistake(mistakeList);
                        finish();
                    }
                });
                saveState.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                saveState.show();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
}
