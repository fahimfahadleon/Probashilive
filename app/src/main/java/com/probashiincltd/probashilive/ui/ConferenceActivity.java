package com.probashiincltd.probashilive.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.CommentAdapter;
import com.probashiincltd.probashilive.adapter.ConferenceAdapter;
import com.probashiincltd.probashilive.databinding.ActivityConferenceBinding;
import com.probashiincltd.probashilive.viewmodels.ConferenceViewModel;

public class ConferenceActivity extends AppCompatActivity {
    ActivityConferenceBinding binding;
    ConferenceViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_conference);
        model = new ViewModelProvider(this).get(ConferenceViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);
        ConferenceAdapter adapter = new ConferenceAdapter();
        CommentAdapter commentAdapter = new CommentAdapter();
        model.setCommentAdapter(commentAdapter);
        model.setConferenceAdapter(adapter);
        model.initViewModel(this,getIntent());
        initModelObserver();

    }
    void initModelObserver(){
        model.getItemClick().observe(this,cm->{

        });
    }
}