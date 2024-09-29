package com.probashiincltd.probashilive.viewmodels;

import androidx.lifecycle.ViewModel;

import com.probashiincltd.probashilive.adapter.InboxAdapter;

import org.jivesoftware.smack.packet.Message;

import java.util.LinkedList;

public class ActivityInboxViewModel extends ViewModel {
    InboxAdapter inboxAdapter;
    LinkedList<Message> messages = new LinkedList<>();
    public LinkedList<Message> getMessages;
    public void setAdapter(InboxAdapter adapter){
        this.inboxAdapter = adapter;
    }
    public InboxAdapter getAdapter(){
        return inboxAdapter;
    }
    public void addNewMessage(Message message){
        messages.add(message);
        inboxAdapter.addNewMessage(message);
    }
}
