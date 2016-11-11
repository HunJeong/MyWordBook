package com.hunjeong.kr.workbookproject.model;

import java.util.UUID;

import io.realm.RealmObject;

/**
 * Created by JeongHun on 2016. 11. 11..
 */

public class Dictionary extends RealmObject {

    public static final String DICTIONARY = "dictionary";

    private String dictionaryId;
    private String title;
    private String explaination;

    public Dictionary() {
        this.dictionaryId = UUID.randomUUID().toString();
    }

    public Dictionary(String title, String explaination) {
        this.title = title;
        this.explaination = explaination;
        this.dictionaryId = UUID.randomUUID().toString();
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplaination() {
        return explaination;
    }

    public void setExplaination(String explaination) {
        this.explaination = explaination;
    }
}
