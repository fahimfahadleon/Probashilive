package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_VIDEO;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_AUDIENCE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.HomeFragmentRVAdapter;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.FragmentHomeBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.pubsubItems.LiveItem;
import com.probashiincltd.probashilive.utils.Pair;
import com.probashiincltd.probashilive.viewmodels.HomeFragmentViewModel;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;

import org.jivesoftware.smackx.pubsub.Item;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    static HomeFragment homeFragment;
    FragmentHomeBinding binding;
    HomeFragmentViewModel model;
    String page = "";
    public HomeFragment() {

    }
    public static HomeFragment getInstance() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        model = new ViewModelProvider(this).get(HomeFragmentViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(binding.getLifecycleOwner());
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(requireContext()));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(requireContext()));
        binding.refreshLayout.setOnRefreshLoadMoreListener(model.onRefreshLoadMoreListener);
        HomeFragmentRVAdapter adapter = new HomeFragmentRVAdapter(getContext());
        model.setAdapter(adapter);
        model.initViewModel();
        binding.refreshLayout.autoRefresh();
        initViewModel();
        return binding.getRoot();
    }




    void initViewModel() {
        model.getSelectedItem().observe(getViewLifecycleOwner(), liveItem -> {
            Intent i = new Intent(getActivity(), RTMPCallActivity.class);
            i.putExtra(ACTION, LIVE_USER_TYPE_AUDIENCE);
            i.putExtra("data",liveItem.getContent());
            i.putExtra(LIVE_TYPE, LIVE_TYPE_VIDEO);
            startActivity(i);
        });

        model.getSmartLayoutObserver().observe(getViewLifecycleOwner(), i -> {
            switch (i) {
                case 0: {
                    loadData(true);
                    break;
                }
                case 1: {
                    loadData(false);
                    break;
                }
            }
        });
    }
    Pair getLiveItems(String args) {
        Pair pair;
        if (args.isEmpty()) {
            pair = Functions.getPagenatedDataStart(CM.NODE_LIVE_USERS);
        } else {
            pair = Functions.getNextPagenatedData(CM.NODE_LIVE_USERS, page);
        }
        return pair;
    }

    private void loadData(boolean isRefresh) {
        binding.recyclerview.post(() -> {
            if (isRefresh) {
                model.getAdapter().clearData();
                page = "";
            }else {
                Log.e("isRefresh","not called");
            }
            Pair pair = getLiveItems(page);
            ArrayList<LiveItem> models = new ArrayList<>();
            if(pair==null){
                binding.refreshLayout.finishLoadMoreWithNoMoreData();
                return;
            }
            for(Item i:pair.getList()){
                try {
                    models.add(LiveItem.parseLiveItem(i));
                }catch (Exception e){
                    Log.e("error",e.toString());
                }
            }
            model.getAdapter().addMoreData(models);
            if (page.equals(pair.getSet())) {
                binding.refreshLayout.finishLoadMoreWithNoMoreData();
            } else {
                binding.refreshLayout.finishRefresh();
                binding.refreshLayout.finishLoadMore();
            }
            page = pair.getSet();
        });
    }
}