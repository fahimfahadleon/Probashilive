package com.probashiincltd.probashilive.viewmodels;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.InboxAdapter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.jivesoftware.smack.packet.Message;

import java.util.LinkedList;

public class ActivityInboxViewModel extends ViewModel {
    public static final int CLICK_SEND = 0;
    public static final int CLICK_OPTIONS = 1;
    InboxAdapter inboxAdapter;
    public LinkedList<Message> messages = new LinkedList<>();

    public MutableLiveData<Integer> clickAction = new MutableLiveData<>();
    public void setAdapter(InboxAdapter adapter){
        this.inboxAdapter = adapter;
    }
    public InboxAdapter getAdapter(){
        return inboxAdapter;
    }
    public void addNewMessage(Message message,RecyclerView recyclerView){
        Log.e("messageinserted",message.getBody());
        int index = messages.size();
        messages.add(index,message);
        inboxAdapter.notifyItemInserted(index);
        recyclerView.smoothScrollToPosition(index);

    }
    public void onClick(View vi){
        int id = vi.getId();
        if(id == R.id.send){
            clickAction.setValue(CLICK_SEND);
        }
    }
    private final MutableLiveData<Integer> smartLayoutObserver = new MutableLiveData<>();

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
