package com.hunjeong.kr.workbookproject.model;

import io.realm.RealmObject;

/**
 * Created by JeongHun on 2016. 11. 15..
 */

public class Word extends RealmObject {

    private String dictionaryId;
    private String word;
    private String mean;
    private int numOfMistake;

    public Word() {
    }

    public Word(String word, String mean) {
        this.word = word;
        this.mean = mean;
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
}
