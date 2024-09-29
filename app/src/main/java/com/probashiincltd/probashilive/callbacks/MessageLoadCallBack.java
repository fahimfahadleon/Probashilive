package com.probashiincltd.probashilive.callbacks;

import org.jivesoftware.smack.packet.Message;

import java.util.LinkedList;

public interface MessageLoadCallBack {
    public void onCache(LinkedList<Message> messageList);
    public void onServer(LinkedList<Message> messageList);
}
