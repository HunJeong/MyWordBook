package com.hunjeong.kr.workbookproject.ui.MainUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Dictionary;
import com.hunjeong.kr.workbookproject.ui.splash.SplashActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Realm realm;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CardAdapter cardAdapter;
    private AlertDialog.Builder dialogBuilder;

    private int seed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splash();
        actionBarInit();
        initRealm();
        //initSeed();
        initValue();
        initDialog();
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
    }

    /**
     * Initialize FloatingActionButton
     */
    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.show();
            }
        });
    }

    /**
     * Make dialog that appear when user click the floating button
     * case 0 is bringing dictionary from CSV file
     * case 1 is bringing dictionary from xlsx file
     * case 2 is that users make dictionary themselves
     */
    private void initDialog() {
        final CharSequence[] listItem = {"csv파일에서 가져오기", "엑셀파일에서 가져오기", "직접 추가하기"};
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setItems(listItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        Toast.makeText(getApplicationContext(), listItem[i], Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), listItem[i], Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), listItem[i], Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            initSeed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
