package com.hunjeong.kr.workbookproject.model;

import android.util.Log;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;

/**
 * Created by JeongHun on 2016. 11. 11..
 */

public class Dictionary extends RealmObject {

    public static final String DICTIONARY = "dictionary";

    private String dictionaryId;
    private String title;
    private String explain;
    private int numOfWords;
    private long createAt;

    public Dictionary() {
        this.dictionaryId = UUID.randomUUID().toString();
        this.numOfWords = 0;
        this. createAt = new Date().getTime();
    }

    public Dictionary(String title, String explain) {
        this.dictionaryId = UUID.randomUUID().toString();
        this.title = title;
        this.explain = explain;
        this.numOfWords = 0;
        this. createAt = new Date().getTime();
        Log.d(DICTIONARY, dictionaryId);
    }

    public String getDictionaryId() {
        return this.dictionaryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public long getCreateAt() {
        return createAt;
    }

    public int getNumOfWords() {
        return numOfWords;
    }

    public void setNumOfWords(int numOfWords) {
        if (numOfWords >= 0)
            this.numOfWords = numOfWords;
        else
            this.numOfWords = 0;
    }
}
