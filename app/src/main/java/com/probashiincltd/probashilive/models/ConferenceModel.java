package com.probashiincltd.probashilive.models;

import java.util.HashMap;

public class ConferenceModel {
    String name;
    String profielPicture;
    String vip;
    String id;
    HashMap<String,String>content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfielPicture() {
        return profielPicture;
    }

    public void setProfielPicture(String profielPicture) {
        this.profielPicture = profielPicture;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public HashMap<String, String> getContent() {
        return content;
    }

    public void setContent(HashMap<String, String> content) {
        this.content = content;
    }
}
