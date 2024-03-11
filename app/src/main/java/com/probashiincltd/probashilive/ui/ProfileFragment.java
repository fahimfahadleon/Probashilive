package com.probashiincltd.probashilive.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.FragmentProfileBinding;
import com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }
    static ProfileFragment profileFragment;
    FragmentProfileBinding binding;
    ProfileFragmentViewModel model;
    public static ProfileFragment getInstance() {
       if(profileFragment == null){
           profileFragment = new ProfileFragment();
       }
       return profileFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        model = new ViewModelProvider(this).get(ProfileFragmentViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(binding.getLifecycleOwner());
        model.init();
        initViewModelObserver();
        Glide.with(binding.profile).load(CM.getProfile().getContent().get("profile_picture")).into(binding.profile);
        binding.userName.setText(CM.getProfile().getContent().get("name"));
        binding.coin.setText(CM.getProfile().getContent().get("coin"));
        binding.phone.setText(CM.getProfile().getContent().get("phone"));
        binding.email.setText(CM.getProfile().getContent().get("email"));
        binding.status.setText(CM.getProfile().getContent().get("vip"));

        return binding.getRoot();
    }

    private void initViewModelObserver() {

    }
}