package com.hunjeong.kr.workbookproject.ui.MainUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Dictionary;
import com.hunjeong.kr.workbookproject.model.Word;
import com.hunjeong.kr.workbookproject.ui.MaterialSheetFab.Fab;
import com.hunjeong.kr.workbookproject.ui.splash.SplashActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private Realm realm;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CardAdapter cardAdapter;
    private MaterialSheetFab materialSheetFab;
    private int seed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splash();
        actionBarInit();
        initRealm();

        initValue();
        initFloatingActionButton();
    }

    /**
     * Initialize ActionBar
     */
    private void actionBarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Initialize Realm Configuration
     * if don't use it Realm make error because App has no default Realm
     */
    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getInstance(config);

    }

    /**
     * Initialize RecyclerView
     */
    private void initValue() {
        layoutManager = new LinearLayoutManager(this);
        cardAdapter = new CardAdapter(this, realm.where(Dictionary.class).findAllAsync());

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
    }

    /**
     * Initialize FloatingActionButton
     */
    private void initFloatingActionButton() {
        Fab fab = (Fab) findViewById(R.id.main_fab);
        View sheetView = findViewById(R.id.main_fab_sheet);
        View overlay = findViewById(R.id.main_overlay);
        int sheetColor = getResources().getColor(R.color.white);
        int fabColor = getResources().getColor(R.color.colorAccent);

        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);
        findViewById(R.id.main_fab_sheet_item_csv).setOnClickListener(this);
        findViewById(R.id.main_fab_sheet_item_excel).setOnClickListener(this);
        findViewById(R.id.main_fab_sheet_item_do).setOnClickListener(this);
    }


    /**
     * @param item : the item that user want to delete from dictionary list
     */
    public void deleteItem(final Dictionary item) {
        final String id = item.getDictionaryId();
        realm.beginTransaction();
        realm.where(Dictionary.class).equalTo("dictionaryId", id)
                .findAll()
                .deleteAllFromRealm();
        realm.where(Word.class).equalTo("dictionaryId", id)
                .findAll()
                .deleteAllFromRealm();
        realm.commitTransaction();
    }

    /**
     * Make seed data for test Realm database
     */
    private void initSeed() {
        realm.beginTransaction();
        switch (seed) {
            case 0:
                realm.copyToRealm(new Dictionary("한국어", "한국어 단어장"));
                break;
            case 1:
                realm.copyToRealm(new Dictionary("영어", "영어 단어장"));
                break;
            case 2:
                realm.copyToRealm(new Dictionary("프랑스어", "프랑스어 단어장"));
                break;
            case 3:
                realm.copyToRealm(new Dictionary("중국어", "프랑스어 단어장"));
                break;
            case 4:
                realm.copyToRealm(new Dictionary("일본어", "프랑스어 단어장"));
                break;
            case 5:
                realm.copyToRealm(new Dictionary("독일어", "프랑스어 단어장"));
                break;
            default:
                break;
        }
        seed = (seed + 1)%6;
        realm.commitTransaction();
    }

    private void addSeedWord() {
        realm.beginTransaction();
        for (int i = 0; i < 10; i++) {
            realm.copyToRealm(new Word("0000-0000-0000-0000", "Hi" + i, "안녕" + i));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        realm.commitTransaction();
    }

    /**
     * Popup Splash Activity
     */
    private void splash() {
        startActivity(new Intent(this, SplashActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:

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
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.main_fab_sheet_item_csv:
                Toast.makeText(getApplicationContext(), "csv", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_fab_sheet_item_excel:
                Toast.makeText(getApplicationContext(), "excel", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_fab_sheet_item_do:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("단어장 생성");
                LinearLayout linearLayout = new LinearLayout(this);
                View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_explain, linearLayout, true);
                final EditText wordEdit = (EditText)v.findViewById(R.id.dialog_word);
                final EditText explanEdit = (EditText)v.findViewById(R.id.dialog_mean);
                builder.setView(v);
                builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (wordEdit.getText().length() == 0) {
                            Toast.makeText(getApplicationContext(), "단어장 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        realm.beginTransaction();
                        realm.copyToRealm(new Dictionary(wordEdit.getText().toString(), explanEdit.getText().toString()));
                        realm.commitTransaction();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
                break;
        }
    }
}
