package com.hunjeong.kr.workbookproject.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by JeongHun on 2016. 11. 15..
 */

public class Word extends RealmObject {

    private String dictionaryId;
    private String word;
    private String mean;
    private long createAt = new Date().getTime();
    private int numOfMistake;
    private boolean isImportant;

    public Word() {
        this.numOfMistake = 0;
    }

    public Word(String dictionaryId, String word, String mean) {
        this.dictionaryId = dictionaryId;
        this.word = word;
        this.mean = mean;
        this.isImportant = false;
    }

    public Word(String word, String mean) {
        this.word = word;
        this.mean = mean;
        this.numOfMistake = 0;
        this.isImportant = false;
    }

    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public int getNumOfMistake() {
        return numOfMistake;
    }

    public void setNumOfMistake(int numOfMistake) {
        if (numOfMistake >= 0)
            this.numOfMistake = numOfMistake;
        else
            this.numOfMistake = 0;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public long getCreateAt() {
        return createAt;
    }

}
