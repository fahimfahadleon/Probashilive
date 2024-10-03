package com.probashiincltd.probashilive.viewmodels;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.InboxAdapter;

import org.jivesoftware.smack.packet.Message;

import java.util.LinkedList;

public class ActivityInboxViewModel extends ViewModel {
    public static final int CLICK_SEND = 0;
    public static final int CLICK_OPTIONS = 1;
    InboxAdapter inboxAdapter;
    LinkedList<Message> messages = new LinkedList<>();
    public LinkedList<Message> getMessages = new LinkedList<>();
    public MutableLiveData<Integer> clickAction = new MutableLiveData<>();
    public void setAdapter(InboxAdapter adapter){
        this.inboxAdapter = adapter;
    }
    public InboxAdapter getAdapter(){
        return inboxAdapter;
    }
    public void addNewMessage(Message message){
        messages.add(0,message);
        inboxAdapter.notifyItemInserted(0);
    }
    public void onClick(View vi){
        int id = vi.getId();
        if(id == R.id.send){
            clickAction.setValue(CLICK_SEND);
        }
    }

}
