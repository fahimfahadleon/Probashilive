package com.probashiincltd.probashilive.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.databinding.ActivityProfileBinding;
import com.probashiincltd.probashilive.viewmodels.ActivityProfileViewModel;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    ActivityProfileViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile);
        model = new ViewModelProvider(this).get(ActivityProfileViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);
        model.initViewModel(this,getIntent());

    }
}