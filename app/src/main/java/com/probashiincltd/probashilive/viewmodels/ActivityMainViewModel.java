package com.probashiincltd.probashilive.viewmodels;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.probashiincltd.probashilive.R;

public class ActivityMainViewModel extends ViewModel {

    private final MutableLiveData<String> registerAction = new MutableLiveData<>();
    private final MutableLiveData<String> loginAction = new MutableLiveData<>();

    public LiveData<String>getRegisterAction(){
        return registerAction;
    }
    public LiveData<String>getLoginAction(){
        return loginAction;
    }

    public void onRegisterClick(View vi){
        if(vi.getId() == R.id.facebook){
            registerAction.setValue("fb");
        }else if(vi.getId() == R.id.google){
            registerAction.setValue("go");
        }else if(vi.getId() == R.id.raw){
            registerAction.setValue("ra");
        }else if(vi.getId() == R.id.login){
            loginAction.setValue("login");
        }
    }


}
