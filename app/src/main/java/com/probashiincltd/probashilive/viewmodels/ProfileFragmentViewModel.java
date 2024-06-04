package com.probashiincltd.probashilive.viewmodels;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.probashiincltd.probashilive.R;

public class ProfileFragmentViewModel extends ViewModel {

    public static final int PROFILE_ACTION_EDIT = 0;
    public static final int PROFILE_ACTION_SAVE = 1;
    public static final int PROFILE_ACTION_FOLLOWERS = 2;
    public static final int PROFILE_ACTION_FOLLOWING = 3;
    public static final int PROFILE_ACTION_UPLOAD_IMAGE = 4;

    private final ObservableBoolean isEnabled = new ObservableBoolean(false);
    public ObservableBoolean getIsEnabled(){
        return isEnabled;
    }
    public void setIsEnabled(boolean b){
        isEnabled.set(b);
    }
    private final MutableLiveData<Integer> onButtonclick = new MutableLiveData<>();
    
    public void onButtonClick(View vi){
        int id = vi.getId();
        if(id == R.id.enableEdit){
            onButtonclick.setValue(PROFILE_ACTION_EDIT);
        }else if(id == R.id.save){
            onButtonclick.setValue(PROFILE_ACTION_SAVE);
        }else if(id == R.id.followers){
            onButtonclick.setValue(PROFILE_ACTION_FOLLOWERS);
        }else if(id == R.id.following){
            onButtonclick.setValue(PROFILE_ACTION_FOLLOWING);
        }else if(id == R.id.profileEdit){
            onButtonclick.setValue(PROFILE_ACTION_UPLOAD_IMAGE);
        }

        
    }

    public LiveData<Integer>getOnButtonClick(){
        return onButtonclick;
    }
    
    public void init() {

    }
}
