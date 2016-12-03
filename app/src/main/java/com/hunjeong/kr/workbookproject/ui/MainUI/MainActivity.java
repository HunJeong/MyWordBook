package com.hunjeong.kr.workbookproject.ui.MainUI;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private static final int CSV_SELECT = 1;
    private static final int EXCEL_SELECT = 2;

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
        Toast.makeText(getApplicationContext(), "단어장이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void modifyItem(final Dictionary item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("단어장 수정");
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_explain, linearLayout, true);
        final EditText titleEdit = (EditText)v.findViewById(R.id.dialog_word);
        final EditText explainEdit = (EditText)v.findViewById(R.id.dialog_mean);
        titleEdit.setHint("Title");
        explainEdit.setHint("Explain");
        titleEdit.setText(item.getTitle());
        explainEdit.setText(item.getExplain());
        builder.setView(linearLayout);
        builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (titleEdit.getText().length() != 0)
                    editItem(item, titleEdit.getText().toString(), explainEdit.getText().toString());
                else if (titleEdit.getText().length() == 0)
                    Toast.makeText(getApplicationContext(), "단어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void editItem(final Dictionary dictionary, final String modifiedTitle, final String modifiedExplain) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Dictionary item = realm.where(Dictionary.class).equalTo("dictionaryId", dictionary.getDictionaryId()).findFirst();
                item.setTitle(modifiedTitle);
                item.setExplain(modifiedExplain);
            }
        });
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
                int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent csvSelectIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    csvSelectIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    csvSelectIntent.setType("text/*");
                    try {
                        startActivityForResult(Intent.createChooser(csvSelectIntent, "Choose CSV file that load words"), CSV_SELECT);
                    } catch (android.content.ActivityNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Please install a File Manager", Toast.LENGTH_LONG).show();
                    }
                }
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
        materialSheetFab.hideSheet();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CSV_SELECT:
                try {
                    Log.d(TAG, "Start AsyncTask");
                    FileLoadAsyncTask fileLoadAsyncTask = new FileLoadAsyncTask(getApplicationContext(), data.getData(), requestCode);
                    fileLoadAsyncTask.execute();
                } catch (Exception e) {}
                break;
        }
    }

    class FileLoadAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        private Context context;
        private Uri uri;
        private int code;
        private List<CSVRecord> records;
        private ProgressDialog dialog;
        private Realm realm;
        private Dictionary dictionary;
        private ArrayList<Word> words;

        public FileLoadAsyncTask(Context context, Uri uri, int code) {
            super();
            this.context = context;
            this.uri = uri;
            this.code = code;
            this.realm = Realm.getDefaultInstance();
            words = new ArrayList<>();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            Log.d(TAG, "Run AsyncTask");
            dictionary = new Dictionary("새로운 단어장", "");
            String dictionaryId = dictionary.getDictionaryId();
            for (int i = 0; i < records.size(); i++) {
                if (isCancelled())
                    return null;
                words.add(new Word(dictionaryId, records.get(i).get(0), records.get(i).get(1)));
                Log.d(TAG, "doInBackground : add Word" + words.get(words.size()-1).toString());
                publishProgress(i + 1);
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "input " + i + "'s index");
            }
            return words.size();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "FileLoadAsyncTask : onPreExecute");
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT);
                records = csvParser.getRecords();
                dialog = new ProgressDialog(context);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMessage("파일을 추가하고 있습니다.");
                dialog.setProgress(0);
                dialog.setCancelable(false);
            } catch (NullPointerException e) {
                Log.d(TAG, "FileLoadAsyncTask : NullPointerException");
                e.printStackTrace();
                cancel(true);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "FileLoadAsyncTask : FileNotFoundException");
                e.printStackTrace();
                cancel(true);
            } catch (IOException e) {
                Log.d(TAG, "FileLoadAsyncTask : IOException");
                e.printStackTrace();
                cancel(true);
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "FileLoadAsyncTask : IllegalArgumentException");
                e.printStackTrace();
                cancel(true);
            }
            Log.d(TAG, "Dialog");

            Log.d(TAG, "FileLoadAsyncTask : onPreExecute finish");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (dialog != null)
                dialog.dismiss();
            if (integer == records.size()) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(dictionary);
                        for (int i = 0; i < words.size(); i++) {
                            realm.copyToRealm(words.get(i));
                        }
                        Toast.makeText(context, "단어장 추가가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "단어장 추가를 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            try {
                dialog.setProgress(values[0]);
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(TAG, "FileLoadAsyncTask : onCancelled");
            if (dialog != null)
                dialog.dismiss();
        }
    }

}
