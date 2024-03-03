package com.probashiincltd.probashilive.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.databinding.FragmentSettingsBinding;
import com.probashiincltd.probashilive.viewmodels.SettingsFragmentViewModel;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }
    static SettingsFragment settingsFragment;
    FragmentSettingsBinding binding;
    SettingsFragmentViewModel model;

    public static SettingsFragment getInstance() {
        if(settingsFragment == null){
            settingsFragment = new SettingsFragment();
        }
        return settingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        model = new ViewModelProvider(this).get(SettingsFragmentViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(binding.getLifecycleOwner());
        return binding.getRoot();
    }
}