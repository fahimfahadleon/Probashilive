package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_VIDEO;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_PASSWORD;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_STATUS;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_USER;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.ActivityHomeBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.viewmodels.ActivityHomeViewModel;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    ActivityHomeViewModel model;

    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        model = new ViewModelProvider(this).get(ActivityHomeViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);
        mGoogleSignInClient = Functions.getGoogleSigninClient(this);
        initViewModel();

        setUpFragment(HomeFragment.getInstance());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home){
                model.setFragmentChanged(0);
                return true;
            }else if(item.getItemId() == R.id.notification){
                model.setFragmentChanged(1);
                return true;
            }else if(item.getItemId() == R.id.profile){
                model.setFragmentChanged(2);
                return true;
            }else if(item.getItemId() == R.id.settings){
                model.setFragmentChanged(3);
                return true;
            }

            return false;
        });


    }

    void initViewModel(){
        model.getOnClickEvent().observe(this, s -> {
            switch (s){
                case "live":{
//                    signOut();
                    Intent i = new Intent(HomeActivity.this,RTMPCall.class);
                    i.putExtra(ACTION,LIVE_USER_TYPE_HOST);
                    i.putExtra(LIVE_TYPE,LIVE_TYPE_VIDEO);
                    startActivity(i);
                    break;
                }
            }
        });

        model.getFragmentChange().observe(this,i->{
            switch (i){
                case 1:{
                    setUpFragment(NotificationFragment.getInstance());
                    break;
                } case 2:{
                    setUpFragment(ProfileFragment.getInstance());
                    break;
                } case 3:{
                    setUpFragment(SettingsFragment.getInstance());
                    break;
                } default:{
                    setUpFragment(HomeFragment.getInstance());
                    break;
                }
            }
        });
    }


    void setUpFragment(Fragment f){
        Log.e("fragmentContainer","called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
         // Replace YourFragment with your actual Fragment class
        fragmentTransaction.replace(R.id.fragmentcontainer, f);
        fragmentTransaction.commit();
    }

    public void signOut(){
        mGoogleSignInClient.signOut();
        Functions.setSetSharedPreference(LOGIN_STATUS,"false");
        Functions.setSetSharedPreference(LOGIN_USER,"");
        Functions.setSetSharedPreference(LOGIN_PASSWORD,"");
        startActivity(new Intent(this,MainActivity.class));
        finish();
        CM.getConnection().disconnect();

    }
}