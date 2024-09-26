package com.probashiincltd.probashilive.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.ChatAdapter;
import com.probashiincltd.probashilive.databinding.FragmentNotificationBinding;
import com.probashiincltd.probashilive.models.ChatItem;
import com.probashiincltd.probashilive.viewmodels.NotificationFragmentViewModel;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    public NotificationFragment() {
        // Required empty public constructor
    }

    static NotificationFragment notificationFragment;
    FragmentNotificationBinding binding;
    NotificationFragmentViewModel model;

    public static NotificationFragment getInstance() {
        if (notificationFragment == null) {
            notificationFragment = new NotificationFragment();
        }
        return notificationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        model = new ViewModelProvider(this).get(NotificationFragmentViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(binding.getLifecycleOwner());
        initViewModelObserver();
        return binding.getRoot();
    }

    private void initViewModelObserver() {

        ChatItem chatItem = new ChatItem();
        chatItem.setName("SomeUserName");
        chatItem.setProfilePicture("https://server.probashilive.xyz:5443/upload/4ffa42c956a1f9043452f55135ac4e1989fced5f/fGJRpHXUbbSEUGQMt0SoOuxQruR3EMZ4tLQXhnb2/cache_IMG_20240619_185233.jpg");


        ChatItem chatItem1 = new ChatItem();
        chatItem1.setName("SomeUserName1");
        chatItem1.setProfilePicture("https://server.probashilive.xyz:5443/upload/4ffa42c956a1f9043452f55135ac4e1989fced5f/fGJRpHXUbbSEUGQMt0SoOuxQruR3EMZ4tLQXhnb2/cache_IMG_20240619_185233.jpg");


        ChatItem chatItem2 = new ChatItem();
        chatItem2.setName("SomeUserName2");
        chatItem2.setProfilePicture("https://server.probashilive.xyz:5443/upload/4ffa42c956a1f9043452f55135ac4e1989fced5f/fGJRpHXUbbSEUGQMt0SoOuxQruR3EMZ4tLQXhnb2/cache_IMG_20240619_185233.jpg");





        ArrayList<ChatItem>chatItems = new ArrayList<>();
        chatItems.add(chatItem);
        chatItems.add(chatItem1);
        chatItems.add(chatItem2);
        ChatAdapter chatAdapter = new ChatAdapter(chatItems, getContext(), new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ChatItem chatItem) {
                Toast.makeText(getContext(), "Clicked "+chatItem.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        model.setChatAdapter(chatAdapter);
    }
}