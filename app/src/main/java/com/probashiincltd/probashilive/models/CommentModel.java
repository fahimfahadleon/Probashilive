package com.probashiincltd.probashilive.models;

import java.util.HashMap;

public class CommentModel {
    String pp;
    String name;
    String comment;
    String vip;
    String id;
    HashMap<String,String>content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, String> getContent() {
        return content;
    }

    public void setContent(HashMap<String, String> content) {
        this.content = content;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    @Override
    public String toString() {
        return "CommentModel{" +
                "pp='" + pp + '\'' +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", vip='" + vip + '\'' +
                '}';
    }
}
