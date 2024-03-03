package com.probashiincltd.probashilive.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.databinding.FragmentNotificationBinding;
import com.probashiincltd.probashilive.viewmodels.NotificationFragmentViewModel;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        model = new ViewModelProvider(this).get(NotificationFragmentViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(binding.getLifecycleOwner());
        return binding.getRoot();
    }
}