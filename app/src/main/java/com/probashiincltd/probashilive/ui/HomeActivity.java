package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_AUDIO;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_VIDEO;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_PASSWORD;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_STATUS;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_USER;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
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
import com.probashiincltd.probashilive.databinding.LiveChooserBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.viewmodels.ActivityHomeViewModel;

import java.util.Objects;

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

        Log.e("oncreate","called");
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
                    openChooser();
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
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragmentcontainer, f);
        ft.addToBackStack(null);
        ft.commit();
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



    AlertDialog cd;
    void openChooser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_Theme_ProbashiLive);
        LiveChooserBinding b = LiveChooserBinding.inflate(getLayoutInflater());
        builder.setView(b.getRoot());
        cd = builder.create();
        cd.show();
        Objects.requireNonNull(cd.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cd.getWindow().setGravity(Gravity.CENTER);
        cd.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        cd.getWindow().setDimAmount(0.5f);

        b.joinVideo.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, RTMPCallActivity.class);
            i.putExtra(ACTION,LIVE_USER_TYPE_HOST);
            i.putExtra(LIVE_TYPE,LIVE_TYPE_VIDEO);
            startActivity(i);
            cd.dismiss();
        });

        b.joinAudio.setOnClickListener(v->{
            Intent i = new Intent(HomeActivity.this, ConferenceActivity.class);
            i.putExtra(ACTION,LIVE_USER_TYPE_HOST);
            i.putExtra(LIVE_TYPE,LIVE_TYPE_AUDIO);
            startActivity(i);
            cd.dismiss();
        });
    }





}