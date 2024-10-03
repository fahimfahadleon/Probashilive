package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.DATA;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.databinding.ActivityProfileBinding;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.viewmodels.ActivityProfileViewModel;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    ActivityProfileViewModel model;
    ProfileItem profileItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile);
        model = new ViewModelProvider(this).get(ActivityProfileViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);
        profileItem = new Gson().fromJson(getIntent().getStringExtra(DATA),ProfileItem.class);
        model.initViewModel(profileItem);

        setUpReceiver();

    }

    private void setUpReceiver() {

        model.buttonClick.observe(this,s->{
            Log.e("buttonClick",s);
            Intent i = new Intent(this,Inbox.class);
            i.putExtra(DATA,new Gson().toJson(profileItem));
            startActivity(i);
        });

    }
}