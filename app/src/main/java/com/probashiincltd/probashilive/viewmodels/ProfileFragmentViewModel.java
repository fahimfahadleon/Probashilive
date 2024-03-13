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

    private final MutableLiveData<Integer> insetDrawable = new MutableLiveData<>();
    public void setInsetDrawable(Integer d){
        insetDrawable.setValue(d);
    }

    public LiveData<Integer> getInsetDrawable(){
        return insetDrawable;
    }


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
        }

        
    }

    public LiveData<Integer>getOnButtonClick(){
        return onButtonclick;
    }
    
    public void init() {

    }
}
