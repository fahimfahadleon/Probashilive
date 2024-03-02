package com.probashiincltd.probashilive.models;

public class CommentModel {
    String pp;
    String name;
    String comment;
    String vip;

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
