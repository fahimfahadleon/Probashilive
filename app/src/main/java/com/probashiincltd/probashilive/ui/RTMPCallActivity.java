package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.pubsubItems.LiveItem.NAME;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.PROFILE_IMAGE;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.ALERTDIALOG_TYPE_OPEN_FRIEND_LIST;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.ALERTDIALOG_TYPE_OPEN_PROFILE;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.REPLAY_TYPE_BLOCK;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.REPLAY_TYPE_INVITE;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.REPLAY_TYPE_INVITE_FRIEND;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.REPLAY_TYPE_MESSAGE;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.REPLAY_TYPE_VISIT;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION_TYPE_LIVE_ENDED;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION_TYPE_LIVE_LEFT;
import static com.probashiincltd.probashilive.utils.Configurations.ADD_PERSON;
import static com.probashiincltd.probashilive.utils.Configurations.CLOSE_LIVE;
import static com.probashiincltd.probashilive.utils.Configurations.DATA;
import static com.probashiincltd.probashilive.utils.Configurations.END_CALL_1;
import static com.probashiincltd.probashilive.utils.Configurations.END_CALL_2;
import static com.probashiincltd.probashilive.utils.Configurations.GIFT;
import static com.probashiincltd.probashilive.utils.Configurations.HIDE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.JOIN_REQUEST;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_AUDIENCE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_PROFILE_1;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_PROFILE_2;
import static com.probashiincltd.probashilive.utils.Configurations.SHOW_VIEWERS;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_HOST_REMOVED_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.SWITCH_CAMERA;
import static com.probashiincltd.probashilive.utils.Configurations.isOccupied;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.CommentAdapter;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.ActivityRtmpcallBinding;
import com.probashiincltd.probashilive.models.CommentModel;
import com.probashiincltd.probashilive.pubsubItems.LiveItem;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.utils.Configurations;
import com.probashiincltd.probashilive.viewmodels.RTMPCallViewModel;

import org.json.JSONException;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.nodemedia.NodePlayer;
import cn.pedant.SweetAlert.SweetAlertDialog;

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
        String action = getIntent().getStringExtra(ACTION);
        switch (Objects.requireNonNull(action)){
            case LIVE_USER_TYPE_AUDIENCE:{
                model.setAdapter(new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_AUDIENCE));
                break;
            }case LIVE_USER_TYPE_COMPETITOR:{
                model.setAdapter(new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_COMPETITOR));
                break;
            }case LIVE_USER_TYPE_HOST:{
                model.setAdapter(new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_HOST));
                break;
            }
        }

        PermissionX.init(this).permissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA", "android.permission.RECORD_AUDIO").request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                if (allGranted) {
                    try {
                        model.initViewModel(RTMPCallActivity.this, getIntent(), binding.cameraView,binding.cameraView2);
                    } catch (JSONException e) {
                        //ignored
                    }
                    observeViewModel();
                } else {
                    PermissionX.init(RTMPCallActivity.this).permissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA", "android.permission.RECORD_AUDIO").explainReasonBeforeRequest().request(new RequestCallback() {
                        @Override
                        public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                            if (allGranted) {
                                try {
                                    model.initViewModel(RTMPCallActivity.this, getIntent(), binding.cameraView,binding.cameraView2);
                                } catch (JSONException e) {
                                    //ignored
                                }
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
            if(nodePlayer!=null){
                nodePlayer.stop();
            }
            model.onDestroy();
        }
        isOccupied = false;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            isDestroyed = true;
            if(nodePlayer!=null){
                nodePlayer.stop();
            }
            model.onDestroy();
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }


    void observeViewModel() {
        model.getViewUpdate().observe(this, this::updateCompetitor);
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
                case OPEN_PROFILE_1: {
                    try {
                        switch (model.getAction()) {
                            case LIVE_USER_TYPE_HOST:
                                openProfileDialog(CM.getProfile().getContent().get(ProfileItem.NAME));
                                break;
                            case LIVE_USER_TYPE_COMPETITOR:
                            case LIVE_USER_TYPE_AUDIENCE:
                                openProfileDialog(model.getData().get(ProfileItem.NAME));
                                break;
                        }
                    }catch (Exception e){
                        e.fillInStackTrace();
                    }
                    break;
                }
                case OPEN_PROFILE_2: {
                    try {
                        openProfileDialog(model.getCompetitorList().get(0).getContent().get(NAME));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case END_CALL_1: {
                    onBackPressed();
                    break;
                }
                case END_CALL_2: {
                    openRemoveCompetitorConfirmation();
                    break;
                }case SHOW_VIEWERS:{
                    showViewers();
                    break;
                } case SUBJECT_TYPE_HOST_REMOVED_COMPETITOR:{
                    Toast.makeText(this, "Host removed you from live!", Toast.LENGTH_SHORT).show();
                    model.onDestroy();
                    finish();
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
                Glide.with(binding.profile).load(map.get(PROFILE_IMAGE)).placeholder(R.drawable.person).into(binding.profile);
                binding.name.setText(map.get(NAME));
                binding.vip.setText(map.get(LiveItem.VIP));
                binding.viewers.setText(getString(R.string.viewers, map.get("viewers")));
            } else {
                Glide.with(binding.profile).load(CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE)).placeholder(R.drawable.person).into(binding.profile);
                binding.name.setText(CM.getProfile().getContent().get(ProfileItem.NAME));
                binding.vip.setText(CM.getProfile().getContent().get(ProfileItem.VIP));
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

    private void openRemoveCompetitorConfirmation() {
        Log.e("competitorList",model.getCompetitorList().toString());
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setCancelButton("Cancel", Dialog::dismiss).setConfirmButton("Confirm", sweetAlertDialog1 -> {
            CM.sendHLM(SUBJECT_TYPE_HOST_REMOVED_COMPETITOR,"",CM.getConnection().getUser().asFullJidOrThrow().toString(),model.getCompetitorList().get(0).getContent().get(LiveItem.ROOM_ID));
            model.removeCompetitor(0);
            sweetAlertDialog.dismiss();
        }).setContentText("Are you sure you want to remove "+model.getCompetitorList().get(0).getContent().get(NAME)).setTitle("Warning");
        sweetAlertDialog.show();
    }

    private void showViewers() {

    }

    NodePlayer nodePlayer = null;
    private void updateCompetitor(ArrayList<LiveItem>liveItems) {
        if(liveItems.isEmpty()){
            closeSpliter();
            binding.addPeople.setVisibility(View.VISIBLE);
           if(nodePlayer!=null){
               nodePlayer.stop();
           }
            binding.profileInfo1.setVisibility(View.GONE);
            binding.profileInfo2.setVisibility(View.GONE);
            binding.endCall1.setVisibility(View.GONE);
            binding.endCall2.setVisibility(View.GONE);
            return;
        }
        LiveItem liveItem = liveItems.get(0);
        Glide.with(binding.profile1).load(CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE)).placeholder(R.drawable.person).into(binding.profile1);
        Glide.with(binding.profile2).load(liveItem.getContent().get(PROFILE_IMAGE)).placeholder(R.drawable.person).into(binding.profile2);

        binding.name1.setText(CM.getProfile().getContent().get(ProfileItem.NAME));
        binding.name2.setText(liveItem.getContent().get(LiveItem.NAME));
        binding.vip1.setText(CM.getProfile().getContent().get(ProfileItem.VIP));
        binding.vip2.setText(liveItem.getContent().get(ProfileItem.VIP));

        openSpliter();
        binding.addPeople.setVisibility(View.GONE);

        if(model.getAction().equals(LIVE_USER_TYPE_AUDIENCE)){
            nodePlayer = new NodePlayer(this,"");
            nodePlayer.attachView(binding.cameraView2);
            nodePlayer.start(Configurations.RTMP_URL+liveItem.getContent().get(NAME));
            return;
        }
        binding.profileInfo1.setVisibility(View.VISIBLE);
        binding.profileInfo2.setVisibility(View.VISIBLE);
        if(model.getAction().equals(LIVE_USER_TYPE_COMPETITOR)){
            binding.endCall1.setVisibility(View.GONE);
            binding.endCall2.setVisibility(View.VISIBLE);
        }else {
            binding.endCall1.setVisibility(View.VISIBLE);
            binding.endCall2.setVisibility(View.VISIBLE);
            nodePlayer = new NodePlayer(this,"");
            nodePlayer.attachView(binding.cameraView2);
            nodePlayer.start(Configurations.RTMP_URL+liveItem.getContent().get(NAME));
        }
    }

    private void openCommentDialog(CommentModel cm) {
        new AlertDialogViewer(this, event -> {
            switch (event[0]){
                case REPLAY_TYPE_VISIT:{
                    if (model.getAction().equals(LIVE_USER_TYPE_HOST)) {
                        try {
                            openProfileDialog(event[1]);
                        } catch (JSONException e) {
                            e.fillInStackTrace();
                        }
                    } else {
                        openProfile(event[1]);
                    }
                    break;
                }case REPLAY_TYPE_BLOCK:{
                    Toast.makeText(this, "Block "+event[1], Toast.LENGTH_SHORT).show();
                    break;
                }case REPLAY_TYPE_INVITE:{
                    Toast.makeText(this, "Invite "+event[1], Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        },AlertDialogViewer.ALERTDIALOG_TYPE_OPEN_COMMENT,new Gson().toJson(cm));
    }

    private void openProfileDialog(String name) throws JSONException {
        new AlertDialogViewer(this, event -> {
            if (event[0].equals(REPLAY_TYPE_MESSAGE)) {
                Toast.makeText(RTMPCallActivity.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        },ALERTDIALOG_TYPE_OPEN_PROFILE,name);
    }
    private void openFriendList() {
        new AlertDialogViewer(this, event -> {
            if (event[0].equals(REPLAY_TYPE_INVITE_FRIEND)) {
                try {
                    model.inviteFriend(JidCreate.fullFrom(event[1]));
                } catch (XmppStringprepException e) {
                    throw new RuntimeException(e);
                }
            }
        },ALERTDIALOG_TYPE_OPEN_FRIEND_LIST);
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
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.cameraView.setLayoutParams(layoutParams);
        binding.cameraView2.setVisibility(View.GONE);
        binding.addPeople.setVisibility(View.GONE);
    }

}

