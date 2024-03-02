package com.probashiincltd.probashilive.viewmodels;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.probashiincltd.probashilive.R;

public class ActivityHomeViewModel extends ViewModel {
private final MutableLiveData<String>onClickEvents = new MutableLiveData<>();
private final MutableLiveData<Integer>onFragmentChange = new MutableLiveData<>();

public LiveData<String> getOnClickEvent(){
    return onClickEvents;
}
public LiveData<Integer> getFragmentChange(){
    return onFragmentChange;
}
public void setOnClickEvents(View vi){
    if(vi.getId() == R.id.fab){
        onClickEvents.setValue("live");
    }
}

public void setFragmentChanged(int position){
    onFragmentChange.setValue(position);
}


}
