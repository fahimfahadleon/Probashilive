package com.probashiincltd.probashilive.viewmodels;

import static com.google.firebase.messaging.Constants.MessageTypes.MESSAGE;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;

public class ActivityProfileViewModel extends ViewModel {
    public MutableLiveData<String> buttonClick = new MutableLiveData<>();
    public ProfileItem userData;

    public void onClick(View vi){
        int id = vi.getId();
        if(id == R.id.message){
            buttonClick.setValue(MESSAGE);
        }
    }

    public void initViewModel(ProfileItem profileItem) {
        this.userData = profileItem;
    }
}
