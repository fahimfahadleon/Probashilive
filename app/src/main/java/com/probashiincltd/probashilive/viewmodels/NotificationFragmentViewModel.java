package com.probashiincltd.probashilive.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.probashiincltd.probashilive.adapter.ChatAdapter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

public class NotificationFragmentViewModel extends ViewModel {
    ChatAdapter chatAdapter;

    public void setChatAdapter(ChatAdapter chatAdapter) {
        this.chatAdapter = chatAdapter;
    }

    public ChatAdapter getChatAdapter() {
        return chatAdapter;
    }

    private final MutableLiveData<Integer> smartLayoutObserver = new MutableLiveData<>();
    public MutableLiveData<Integer>getSmartLayoutObserver(){
        return smartLayoutObserver;
    }


    public OnRefreshLoadMoreListener onRefreshLoadMoreListener = new OnRefreshLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            smartLayoutObserver.setValue(1);
        }

        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            smartLayoutObserver.setValue(0);
        }
    };

}
