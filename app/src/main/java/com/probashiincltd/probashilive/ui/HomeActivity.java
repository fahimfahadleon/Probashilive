package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.DATA;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_AUDIO;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_VIDEO;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_PASSWORD;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_STATUS;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_USER;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_INVITATION;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_INVITATION_ACCEPTED;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_INVITATION_DECLINED;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.badge.BadgeDrawable;
import com.google.gson.Gson;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.ActivityHomeBinding;
import com.probashiincltd.probashilive.databinding.LiveChooserBinding;
import com.probashiincltd.probashilive.databinding.VideoJoinInvitationBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.viewmodels.ActivityHomeViewModel;

import org.jivesoftware.smack.packet.Message;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    ActivityHomeViewModel model;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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


        if(Functions.getSP("IS_NEW_MESSAGE","false").equals("true")){
            showMessageOrNotification("message");

// Optional: Configure the badge appearance
//            badge.setBackgroundColor(getResources().getColor(R.color));
//            badge.setBadgeTextColor(getResources().getColor(R.color.white));
        }



    }
    public void showMessageOrNotification(String button){
        if(button.equals("message")){
            BadgeDrawable badge = binding.bottomNavigationView.getOrCreateBadge(R.id.notification);
            badge.setVisible(true); // Show the badge
            badge.setNumber(1);
        }

    }
    public void hideMessageOrNotification(String button){
        if(button.equals("message")){
            BadgeDrawable badge = binding.bottomNavigationView.getOrCreateBadge(R.id.notification);
            badge.setVisible(false); // Show the badge

        }

    }

    void initViewModel(){
        CM.notification.observe(this,s->{
            Log.e("notification",s);
            if(s.equals("true")){
                showMessageOrNotification("message");
            }else {
                hideMessageOrNotification("message");
            }
        });
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

        CM.headlineObserver.observe(this,message -> {
            if(message.getSubject().equals(SUBJECT_TYPE_VIDEO_INVITATION)){
                openInvitationDialog(message);
            }
        });

    }



    AlertDialog id;
    private void openInvitationDialog(Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_Theme_ProbashiLive);
        VideoJoinInvitationBinding binding1 = VideoJoinInvitationBinding.inflate(getLayoutInflater());
        ProfileItem profileItem = new Gson().fromJson(message.getBody(),ProfileItem.class);
        binding1.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CM.sendHLM(SUBJECT_TYPE_VIDEO_INVITATION_ACCEPTED,new Gson().toJson(CM.getProfile()), CM.getConnection().getUser().asFullJidOrThrow().toString(),message.getFrom().asFullJidOrThrow().toString());
                id.dismiss();
                Intent i = new Intent(HomeActivity.this,RTMPCallActivity.class);
                i.putExtra(ACTION,LIVE_USER_TYPE_COMPETITOR);
                i.putExtra(LIVE_TYPE,LIVE_TYPE_VIDEO);
                i.putExtra(DATA,new Gson().toJson(profileItem));
                startActivity(i);
            }
        });
        binding1.decline.setOnClickListener(v -> {
            CM.sendHLM(SUBJECT_TYPE_VIDEO_INVITATION_DECLINED,"", CM.getConnection().getUser().asFullJidOrThrow().toString(),message.getFrom().asFullJidOrThrow().toString());
            id.dismiss();
        });

        binding1.userName.setText(profileItem.getContent().get(ProfileItem.NAME));
        binding1.vip.setText(profileItem.getContent().get(ProfileItem.VIP));
        String s = profileItem.getContent().get(ProfileItem.NAME)+" invited you to join his "+(message.getSubject().equals(SUBJECT_TYPE_VIDEO_INVITATION)?"Video Live":"Audio Live");
        binding1.content.setText(s);

        Glide.with(binding1.profile).load(profileItem.getContent().get(ProfileItem.PROFILE_PICTURE)).placeholder(R.drawable.person).into(binding1.profile);
        builder.setView(binding1.getRoot());
        id = builder.create();
        id.show();
        Objects.requireNonNull(id.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        id.getWindow().setGravity(Gravity.CENTER);
        id.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        id.getWindow().setDimAmount(0.5f);
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
        Functions.setSharedPreference(LOGIN_STATUS,"false");
        Functions.setSharedPreference(LOGIN_USER,"");
        Functions.setSharedPreference(LOGIN_PASSWORD,"");
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