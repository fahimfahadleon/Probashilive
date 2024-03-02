package com.probashiincltd.probashilive.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.probashiincltd.probashilive.adapter.HomeFragmentRVAdapter;
import com.probashiincltd.probashilive.pubsubItems.LiveItem;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

public class HomeFragmentViewModel extends ViewModel {
    HomeFragmentRVAdapter adapter;
    private final MutableLiveData<LiveItem> selectedItem = new MutableLiveData<>();
    private final MutableLiveData<Integer> smartLayoutObserver = new MutableLiveData<>();


    public LiveData<Integer> getSmartLayoutObserver(){
        return smartLayoutObserver;
    }

    public HomeFragmentRVAdapter getAdapter(){
        return adapter;
    }

    public void setAdapter(HomeFragmentRVAdapter adapter){
        this.adapter = adapter;
    }
    public void setSelectedItem(LiveItem item) {
        selectedItem.setValue(item);
    }

    public LiveData<LiveItem> getSelectedItem() {
        return selectedItem;
    }

    public void initViewModel(){
        adapter.setOnItemClickListener(this::setSelectedItem);
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
