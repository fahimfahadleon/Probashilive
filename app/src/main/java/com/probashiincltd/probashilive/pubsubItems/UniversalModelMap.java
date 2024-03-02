package com.probashiincltd.probashilive.pubsubItems;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class UniversalModelMap {
    ArrayList<String>requiredFields = new ArrayList<>();
    public final ArrayList<String>getNewList(){
        return new ArrayList<>(requiredFields);
    }
    HashMap<String,String> content;
    public abstract String getName();
    public abstract String getXmlns();

    public final HashMap<String, String> getContent() {
        return content;
    }
    public abstract String getID();

    public UniversalModelMap(HashMap<String,String> content,ArrayList<String>list ){
        this.content = content;
        this.requiredFields.addAll(list);
    }

}
