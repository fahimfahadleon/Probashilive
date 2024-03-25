package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_AUDIENCE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMMENT;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.databinding.ActivityConferenceBinding;
import com.probashiincltd.probashilive.databinding.OpenInviteFriendBinding;
import com.probashiincltd.probashilive.viewmodels.ConferenceViewModel;

public class ConferenceActivity extends AppCompatActivity {
    ActivityConferenceBinding binding;
    ConferenceViewModel model;

    boolean doubleBackToExitPressedOnce = false;
    boolean isDestroyed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_conference);
        model = new ViewModelProvider(this).get(ConferenceViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);
        model.initViewModel(this,getIntent());
        initModelObserver();

    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            isDestroyed = true;
            model.onDestroy();
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
    @Override
    protected void onDestroy() {
        if(!isDestroyed){
            model.onDestroy();
        }
        super.onDestroy();
    }
    void initModelObserver(){
        model.getOnCommentInserted().observe(this,commentModel -> {
            binding.conferenceRV.smoothScrollToPosition(0);
        });
        model.getSendComment().observe(this,s->{
            if (s.equals(SUBJECT_TYPE_COMMENT)) {
                String cmnt = binding.commentEDT.getText().toString();
                if (!cmnt.isEmpty()) {
                    model.sendComment(cmnt);
                    binding.commentEDT.setText(null);
                }
            }
        });
        model.getCommentAdapterItemClick().observe(this,cm->{

        });
        model.getConferenceAdapterlItemClick().observe(this,cm->{
            if(cm.getId().equals("requestID")){
                String user = model.getAction();
                switch (user){
                    case LIVE_USER_TYPE_HOST:{
                        openInviteFriend();
                        break;
                    }
                    case LIVE_USER_TYPE_AUDIENCE:{
                        requestToJoin();
                        break;
                    }
                    case LIVE_USER_TYPE_COMPETITOR:{
                        requestForAuthority();
                        break;
                    }
                }
            }
        });
        model.getLiveAction().observe(this,la->{

        });
    }

    AlertDialog inviteFriendDialog;



    private void requestForAuthority() {
    }

    private void requestToJoin() {
    }

    private void openInviteFriend() {
       AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_Theme_ProbashiLive);
        OpenInviteFriendBinding binding1 = OpenInviteFriendBinding.inflate(getLayoutInflater());
        builder.setView(binding1.getRoot());
        inviteFriendDialog = builder.create();

    }


}