package com.probashiincltd.probashilive.viewmodels;

import androidx.lifecycle.ViewModel;

import com.probashiincltd.probashilive.adapter.ChatAdapter;

public class NotificationFragmentViewModel extends ViewModel {
    ChatAdapter chatAdapter;

    public void setChatAdapter(ChatAdapter chatAdapter) {
        this.chatAdapter = chatAdapter;
    }

    public ChatAdapter getChatAdapter() {
        return chatAdapter;
    }

}
