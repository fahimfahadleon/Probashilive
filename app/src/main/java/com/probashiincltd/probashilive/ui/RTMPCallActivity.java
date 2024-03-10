package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.ACTION_TYPE_LIVE_ENDED;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION_TYPE_LIVE_LEFT;
import static com.probashiincltd.probashilive.utils.Configurations.CLOSE_LIVE;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_PROFILE;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMMENT;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.ActivityRtmpcallBinding;
import com.probashiincltd.probashilive.viewmodels.RTMPCallViewModel;

import java.util.List;

public class RTMPCallActivity extends AppCompatActivity {
    ActivityRtmpcallBinding binding;
    RTMPCallViewModel model;
    boolean doubleBackToExitPressedOnce = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rtmpcall);
        model = new ViewModelProvider(this).get(RTMPCallViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);

        PermissionX.init(this).permissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA", "android.permission.RECORD_AUDIO").request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                if (allGranted) {
                    model.initViewModel(RTMPCallActivity.this, getIntent(), binding.cameraView);
                    observeViewModel();
                } else {
                    PermissionX.init(RTMPCallActivity.this).permissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA", "android.permission.RECORD_AUDIO").explainReasonBeforeRequest().request(new RequestCallback() {
                        @Override
                        public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                            if (allGranted) {
                                model.initViewModel(RTMPCallActivity.this, getIntent(), binding.cameraView);
                                observeViewModel();
                            } else {
                                Toast.makeText(RTMPCallActivity.this, "Permission is needed", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            }
        });

    }

    boolean isDestroyed = false;

    @Override
    protected void onDestroy() {
        if(!isDestroyed){
            model.onDestroy();
        }
        super.onDestroy();
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



    void observeViewModel() {
        model.getSendComment().observe(this, s -> {
            switch (s) {
                case SUBJECT_TYPE_COMMENT:
                    String cmnt = binding.commentEDT.getText().toString();
                    if (!cmnt.isEmpty()) {
                        model.sendComment(cmnt);
                        binding.commentEDT.setText(null);
                    }
                    break;
                case CLOSE_LIVE:
                    Toast.makeText(this, "CLOSE LIVE", Toast.LENGTH_SHORT).show();
                    break;
                case OPEN_PROFILE:
                    openProfile();
                    break;
            }
        });
        model.getSelectedItem().observe(this, model -> {

        });
        model.getLiveViewerCount().observe(this,i->{
            binding.viewers.setText(getString(R.string.viewers,String.valueOf(model.getViewersCount())));
        });
        model.getOnSetUpComplete().observe(this,map->{
            if(!map.isEmpty()){
                Glide.with(binding.profile).load(map.get("profile_image")).placeholder(R.drawable.person).into(binding.profile);
                binding.name.setText(map.get("name"));
                binding.vip.setText(map.get("vip"));
                binding.viewers.setText(getString(R.string.viewers,map.get("viewers")));
            }else {
                Glide.with(binding.profile).load(CM.getProfile().getContent().get("profile_picture")).placeholder(R.drawable.person).into(binding.profile);
                binding.name.setText(CM.getProfile().getContent().get("name"));
                binding.vip.setText(CM.getProfile().getContent().get("vip"));
                binding.viewers.setText(getString(R.string.viewers,String.valueOf(model.getViewersCount())));
            }
        });

        model.getLiveAction().observe(this, liveAction -> {
            switch (liveAction.getActionType()) {
                case ACTION_TYPE_LIVE_ENDED: {
                    Toast.makeText(this, "The live ended!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
                case ACTION_TYPE_LIVE_LEFT: {
                    model.viewerLeft(liveAction.getContentMap().get("jid"));
                    break;
                }
            }
        });
    }

    private void openProfile() {
        Toast.makeText(this, "Open Profile", Toast.LENGTH_SHORT).show();
    }
}

