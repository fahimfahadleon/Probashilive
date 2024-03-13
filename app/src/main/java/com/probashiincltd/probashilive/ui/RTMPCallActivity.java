package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.connectionutils.RosterHandler.getRosterHandler;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION_TYPE_LIVE_ENDED;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION_TYPE_LIVE_LEFT;
import static com.probashiincltd.probashilive.utils.Configurations.ADD_PERSON;
import static com.probashiincltd.probashilive.utils.Configurations.CLOSE_LIVE;
import static com.probashiincltd.probashilive.utils.Configurations.DATA;
import static com.probashiincltd.probashilive.utils.Configurations.GIFT;
import static com.probashiincltd.probashilive.utils.Configurations.HIDE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.JOIN_REQUEST;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.SWITCH_CAMERA;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.OnlineFriendsAdapter;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.ActivityRtmpcallBinding;
import com.probashiincltd.probashilive.databinding.CommentOptionsBinding;
import com.probashiincltd.probashilive.databinding.OpenInviteFriendBinding;
import com.probashiincltd.probashilive.databinding.ProfileDialogBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.CommentModel;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.utils.Configurations;
import com.probashiincltd.probashilive.viewmodels.RTMPCallViewModel;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.List;
import java.util.Objects;

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
        if (!isDestroyed) {
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
        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }


    void observeViewModel() {

        model.getOpenProfile().observe(this, this::openProfile);
        model.getSendComment().observe(this, s -> {
            switch (s) {
                case SUBJECT_TYPE_COMMENT: {
                    String cmnt = binding.commentEDT.getText().toString();
                    if (!cmnt.isEmpty()) {
                        model.sendComment(cmnt);
                        binding.commentEDT.setText(null);
                    }
                    break;
                }
                case CLOSE_LIVE: {
                    Toast.makeText(this, "CLOSE LIVE", Toast.LENGTH_SHORT).show();
                    break;
                }
                case JOIN_REQUEST: {
                    if (isSpliterOpen) {
                        closeSpliter();
                    } else {
                        openSpliter();
                    }
                    break;
                }
                case SWITCH_CAMERA: {
                    model.switchCamera();
                    break;
                }
                case HIDE_COMMENT: {
                    binding.commentLayout.setVisibility(binding.commentLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    break;
                }
                case GIFT: {
                    openGift();
                    break;
                }
                case ADD_PERSON: {
                    openFriendList();
                    break;
                }

            }
        });
        model.getSelectedItem().observe(this, this::openCommentDialog);
        model.getLiveViewerCount().observe(this, i -> {
            binding.viewers.setText(getString(R.string.viewers, String.valueOf(model.getViewersCount())));
        });
        model.getOnSetUpComplete().observe(this, map -> {
            if (!map.isEmpty()) {
                Glide.with(binding.profile).load(map.get("profile_image")).placeholder(R.drawable.person).into(binding.profile);
                binding.name.setText(map.get("name"));
                binding.vip.setText(map.get("vip"));
                binding.viewers.setText(getString(R.string.viewers, map.get("viewers")));
            } else {
                Glide.with(binding.profile).load(CM.getProfile().getContent().get("profile_picture")).placeholder(R.drawable.person).into(binding.profile);
                binding.name.setText(CM.getProfile().getContent().get("name"));
                binding.vip.setText(CM.getProfile().getContent().get("vip"));
                binding.viewers.setText(getString(R.string.viewers, String.valueOf(model.getViewersCount())));
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
        model.getOptionsMenuVisibility().observe(this, b -> binding.optionsLayout.setVisibility(b ? View.VISIBLE : View.GONE));
    }

    AlertDialog cd;
    int isFriendStatus = 0;

    private void openCommentDialog(CommentModel cm) {
        if (cm.getName().equals(CM.getProfile().getName())) {
            return;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(this, R.style.Base_Theme_ProbashiLive);
        CommentOptionsBinding c = CommentOptionsBinding.inflate(getLayoutInflater());
        c.userName.setText(cm.getName());
        Glide.with(c.profile).load(cm.getPp()).placeholder(R.drawable.person).into(c.profile);
        try {
            if (getRosterHandler().roster.contains(JidCreate.bareFrom(cm.getId()))) {
                if (!getRosterHandler().roster.getEntry(JidCreate.bareFrom(cm.getId())).getGroups().isEmpty()) {
                    isFriendStatus = 1;
                    c.follow.setText(R.string.unfollow);
                } else {
                    isFriendStatus = 2;
                }
            }
        } catch (Exception e) {
            //ingored
        }


        c.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RTMPCallActivity.this, "block", Toast.LENGTH_SHORT).show();
            }
        });

        c.follow.setOnClickListener(v -> {
            try {
                if (isFriendStatus == 1) {
                    getRosterHandler().removeEntry(cm.getId());
                    isFriendStatus = 0;
                    c.follow.setText(R.string.follow);
                } else if (isFriendStatus == 0) {
                    getRosterHandler().createEntry(cm.getId(), cm.getName());
                    isFriendStatus = 1;
                    c.follow.setText(R.string.unfollow);
                } else {
                    getRosterHandler().addGroup(cm.getId());
                    isFriendStatus = 1;
                    c.follow.setText(R.string.unfollow);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        c.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RTMPCallActivity.this, "invite", Toast.LENGTH_SHORT).show();
            }
        });
        c.visit.setOnClickListener(v -> {
            if (model.getAction().equals(LIVE_USER_TYPE_HOST)) {
                try {
                    openProfileDialog(cm.getName());
                } catch (JSONException e) {
                    e.fillInStackTrace();
                }
            } else {
                openProfile(cm.getName());
            }
        });


        b.setView(c.getRoot());
        cd = b.create();
        cd.show();
        Objects.requireNonNull(cd.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cd.getWindow().setGravity(Gravity.CENTER);
        cd.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        cd.getWindow().setDimAmount(0.5f);
    }

    AlertDialog pd;

    private void openProfileDialog(String name) throws JSONException {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Base_Theme_ProbashiLive);
        ProfileDialogBinding binding1 = ProfileDialogBinding.inflate(getLayoutInflater());
        Item item = Functions.getSingleItemOfNode(CM.NODE_USERS, name);
        ProfileItem profileItem = ProfileItem.parseProfileItem(item);
        Log.e("profileItem", profileItem.toString());
        binding1.coin.setVisibility(View.GONE);
        binding1.email.setText(profileItem.getContent().get(ProfileItem.EMAIL));
        binding1.userName.setText(profileItem.getContent().get(ProfileItem.NAME));
        binding1.phone.setText(profileItem.getContent().get(ProfileItem.PHONE));
        binding1.status.setText(profileItem.getContent().get(ProfileItem.VIP));
        Glide.with(binding1.profile).load(profileItem.getContent().get(ProfileItem.PROFILE_PICTURE)).placeholder(R.drawable.person).into(binding1.profile);

        if (isFriendStatus == 1) {
            binding1.follow.setText(R.string.unfollow);
        }
        binding1.message.setOnClickListener(v -> Toast.makeText(RTMPCallActivity.this, "Coming Soon!", Toast.LENGTH_SHORT).show());

        binding1.follow.setOnClickListener(v -> {
            try {
                if (isFriendStatus == 1) {
                    getRosterHandler().removeEntry(profileItem.getContent().get(ProfileItem.NAME + "@" + Configurations.getHostName()));
                    isFriendStatus = 0;
                    binding1.follow.setText(R.string.follow);
                } else if (isFriendStatus == 0) {
                    getRosterHandler().createEntry(profileItem.getContent().get(ProfileItem.NAME + "@" + Configurations.getHostName()), profileItem.getName());
                    isFriendStatus = 1;
                    binding1.follow.setText(R.string.unfollow);
                } else {
                    getRosterHandler().addGroup(profileItem.getContent().get(ProfileItem.NAME + "@" + Configurations.getHostName()));
                    isFriendStatus = 1;
                    binding1.follow.setText(R.string.unfollow);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        builder.setView(binding1.getRoot());
        pd = builder.create();
        pd.show();
        Objects.requireNonNull(pd.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pd.getWindow().setGravity(Gravity.CENTER);
        pd.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        pd.getWindow().setDimAmount(0.5f);
    }

    AlertDialog iFD;

    private void openFriendList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Base_Theme_ProbashiLive);
        OpenInviteFriendBinding binding1 = OpenInviteFriendBinding.inflate(getLayoutInflater());
        OnlineFriendsAdapter adapter = new OnlineFriendsAdapter(profileItem -> {
            try {
                for (Presence presence : getRosterHandler().roster.getAvailablePresences(JidCreate.bareFrom(profileItem.getContent().get(ProfileItem.NAME + "@" + Configurations.getHostName())))) {
                    model.inviteFriend(presence.getFrom().asFullJidOrThrow());
                }

            } catch (XmppStringprepException e) {
                throw new RuntimeException(e);
            }
        });
        binding1.inviteFriendRV.setAdapter(adapter);

        builder.setView(binding1.getRoot());
        iFD = builder.create();
        iFD = builder.create();
        iFD.show();
        Objects.requireNonNull(iFD.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iFD.getWindow().setGravity(Gravity.CENTER);
        iFD.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        iFD.getWindow().setDimAmount(0.5f);
    }

    private void openGift() {
        Toast.makeText(this, "Open Gift", Toast.LENGTH_SHORT).show();
    }

    private void openProfile(String s) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(DATA, s);
        startActivity(i);
    }

    boolean isSpliterOpen = false;

    void openSpliter() {
        isSpliterOpen = true;
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.cameraView.getLayoutParams();
        layoutParams.height = getResources().getDisplayMetrics().heightPixels / 2;
        layoutParams.width = getResources().getDisplayMetrics().widthPixels / 2;
        binding.cameraView.setLayoutParams(layoutParams);
        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) binding.cameraView2.getLayoutParams();
        layoutParams2.height = layoutParams.height;
        layoutParams2.width = layoutParams.width;
        binding.cameraView2.setLayoutParams(layoutParams2);
        binding.cameraView2.setVisibility(View.VISIBLE);
        binding.addPeople.setVisibility(View.VISIBLE);
    }

    void closeSpliter() {
        isSpliterOpen = false;
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.cameraView.getLayoutParams();
        layoutParams.height = getResources().getDisplayMetrics().heightPixels;
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        binding.cameraView.setLayoutParams(layoutParams);
        binding.cameraView2.setVisibility(View.GONE);
        binding.addPeople.setVisibility(View.GONE);
    }

}

