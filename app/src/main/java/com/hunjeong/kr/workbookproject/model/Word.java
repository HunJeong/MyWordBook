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


}
