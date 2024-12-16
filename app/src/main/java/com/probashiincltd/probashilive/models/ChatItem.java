package com.probashiincltd.probashilive.models;

public class ChatItem {
    String profilePicture;
    String name;
    String jid;

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    @Override
    public String toString() {
        return "ChatItem{" +
                "profilePicture='" + profilePicture + '\'' +
                ", name='" + name + '\'' +
                ", jid='" + jid + '\'' +
                '}';
    }
}
