package com.probashiincltd.probashilive.utils;

import org.jivesoftware.smackx.pubsub.Item;

import java.util.List;

public class Pair {
    List<Item> list;
    String lastItemId;


    public Pair(List<Item> items,String lastItemId){
        this.list = items;
        this.lastItemId = lastItemId;
    }

    public List<Item> getList() {
        return list;
    }


    public String getSet() {
        return lastItemId;
    }


}
