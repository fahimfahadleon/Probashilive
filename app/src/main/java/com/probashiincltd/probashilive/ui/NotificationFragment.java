package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.connectionutils.CM.FIREBASE_CHAT_BOX;
import static com.probashiincltd.probashilive.utils.Configurations.DATA;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.ChatAdapter;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.FragmentNotificationBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.ChatItem;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.viewmodels.NotificationFragmentViewModel;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;

import org.jivesoftware.smack.chat2.Chat;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    public NotificationFragment() {
        // Required empty public constructor
    }

    static NotificationFragment notificationFragment;
    FragmentNotificationBinding binding;
    ChatAdapter chatAdapter;
    NotificationFragmentViewModel model;
    ArrayList<ChatItem>items = new ArrayList<>();

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
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(requireContext()));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(requireContext()));
        binding.refreshLayout.setOnRefreshLoadMoreListener(model.onRefreshLoadMoreListener);

        initViewModelObserver();
        return binding.getRoot();
    }

    private void initViewModelObserver() {

        chatAdapter = new ChatAdapter(items, requireContext(), chatItem3 -> {
            Intent i = new Intent(requireContext(),Inbox.class);
            i.putExtra(DATA,new Gson().toJson(chatItem3));
            startActivity(i);
        });

        model.setChatAdapter(chatAdapter);
        model.getSmartLayoutObserver().observe(getViewLifecycleOwner(), i -> {
            switch (i) {
                case 0:
                case 1: {
                    updateData();
                    break;
                }
            }
        });
        binding.refreshLayout.autoRefresh();
    }

    void updateData(){
        Log.e("updateData","called");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(FIREBASE_CHAT_BOX).child(CM.getConnection().getUser().asBareJid().toString().split("@")[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    items.add(ds.getValue(ChatItem.class));
                }
                if(items.size() == 1){
                    Toast.makeText(requireContext(), "No Chat found!", Toast.LENGTH_SHORT).show();
                    items.clear();
                }

                chatAdapter.notifyItemRangeInserted(0,items.size());
                binding.refreshLayout.finishRefresh();
                binding.refreshLayout.finishLoadMore();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

}