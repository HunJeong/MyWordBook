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
    private Date createAt;

    public Dictionary() {
        this.dictionaryId = UUID.randomUUID().toString();
        this. createAt = new Date();
    }

    public Dictionary(String title, String explain) {
        this.dictionaryId = UUID.randomUUID().toString();
        this.title = title;
        this.explain = explain;
        this. createAt = new Date();
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

    public Date getCreateAt() {
        return createAt;
    }
}
