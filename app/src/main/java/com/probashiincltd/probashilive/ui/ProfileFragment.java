package com.probashiincltd.probashilive.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.probashiincltd.probashilive.R;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        model = new ViewModelProvider(this).get(ProfileFragmentViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}