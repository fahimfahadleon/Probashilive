package com.probashiincltd.probashilive.viewmodels;

import static com.google.firebase.messaging.Constants.MessageTypes.MESSAGE;
import static com.probashiincltd.probashilive.utils.Configurations.DATA;

import android.content.Intent;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.probashiincltd.probashilive.R;

public class ActivityProfileViewModel extends ViewModel {
    public MutableLiveData<String> buttonClick = new MutableLiveData<>();
    public String userData;

    public void onClick(View vi){
        int id = vi.getId();
        if(id == R.id.message){
            buttonClick.setValue(MESSAGE);
        }
    }

    public void initViewModel(Intent intent) {
        userData = intent.getStringExtra(DATA);
    }
}
