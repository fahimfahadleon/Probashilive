package com.probashiincltd.probashilive.models;

import java.util.HashMap;

public class LiveAction {
    String actionType;
    String actionContent;
    HashMap<String,String>contentMap;

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionContent() {
        return actionContent;
    }

    public void setActionContent(String actionContent) {
        this.actionContent = actionContent;
    }

    public HashMap<String, String> getContentMap() {
        return contentMap;
    }

    public void setContentMap(HashMap<String, String> contentMap) {
        //user name
        //user jid
        this.contentMap = contentMap;
    }

    @Override
    public String toString() {
        return "LiveAction{" +
                "actionType='" + actionType + '\'' +
                ", actionContent='" + actionContent + '\'' +
                ", contentMap=" + contentMap +
                '}';
    }
}
