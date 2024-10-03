package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.connectionutils.CM.NODE_USERS;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.NAME;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.PROFILE_IMAGE;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.ALERTDIALOG_TYPE_OPEN_FRIEND_LIST;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.ALERTDIALOG_TYPE_OPEN_GIFT;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.ALERTDIALOG_TYPE_OPEN_JOIN_REQUESTS;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.ALERTDIALOG_TYPE_OPEN_PROFILE;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.ALERTDIALOG_TYPE_OPEN_TIMER;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.ALERTDIALOG_TYPE_PREVIEW_GIFT;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.REPLAY_TYPE_BLOCK;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.REPLAY_TYPE_GIFT_PREVIEW;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.REPLAY_TYPE_GIFT_SELECTED;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.REPLAY_TYPE_INVITATION_ACCEPTED;
import static com.probashiincltd.probashilive.ui.AlertDialogViewer.REPLAY_TYPE_INVITATION_DECLINED;
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
import static com.probashiincltd.probashilive.utils.Configurations.GIFT1;
import static com.probashiincltd.probashilive.utils.Configurations.HIDE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.JOIN_REQUEST;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_AUDIENCE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_JOIN_REQUEST;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_PROFILE_1;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_PROFILE_2;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_TIMER_DIALOG;
import static com.probashiincltd.probashilive.utils.Configurations.SHOW_VIEWERS;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_HOST_REMOVED_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.SWITCH_CAMERA;
import static com.probashiincltd.probashilive.utils.Configurations.isOccupied;

import android.annotation.SuppressLint;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.permissionx.guolindev.PermissionX;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.CommentAdapter;
import com.probashiincltd.probashilive.callbacks.OnAlertDialogEventListener;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.connectionutils.RosterHandler;
import com.probashiincltd.probashilive.databinding.ActivityRtmpcallBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.CommentModel;
import com.probashiincltd.probashilive.models.GiftItem;
import com.probashiincltd.probashilive.models.GiftModel;
import com.probashiincltd.probashilive.models.MessageProfileModel;
import com.probashiincltd.probashilive.pubsubItems.LiveItem;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.utils.Configurations;
import com.probashiincltd.probashilive.viewmodels.RTMPCallViewModel;

import org.json.JSONException;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
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
        switch (Objects.requireNonNull(action)) {
            case LIVE_USER_TYPE_AUDIENCE: {
                model.setAdapter(new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_AUDIENCE));
                break;
            }
            case LIVE_USER_TYPE_COMPETITOR: {
                model.setAdapter(new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_COMPETITOR));
                break;
            }
            case LIVE_USER_TYPE_HOST: {
                model.setAdapter(new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_HOST));
                break;
            }
        }
//"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE",
        PermissionX.init(this).permissions( "android.permission.CAMERA", "android.permission.RECORD_AUDIO").request((allGranted, grantedList, deniedList) -> {
            if (allGranted) {
                Log.e("checking","checking for permission");
                try {
                    model.initViewModel(RTMPCallActivity.this, getIntent(), binding.cameraView, binding.cameraView2);
                } catch (JSONException e) {
                    //ignored
                }
                observeViewModel();
            } else {
                Log.e("deniedlist",deniedList.toString());
                Log.e("checking","checking for permission 2");
                PermissionX.init(RTMPCallActivity.this).permissions("android.permission.CAMERA", "android.permission.RECORD_AUDIO").explainReasonBeforeRequest().request((allGranted1, grantedList1, deniedList1) -> {
                    if (allGranted1) {
                        Log.e("checking","checking for permission3 ");
                        try {
                            model.initViewModel(RTMPCallActivity.this, getIntent(), binding.cameraView, binding.cameraView2);
                        } catch (JSONException e) {
                            //ignored
                        }
                        observeViewModel();
                    } else {
                        Log.e("checking","checking for permission4 ");
                        Toast.makeText(RTMPCallActivity.this, "Permission is needed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

    }

    boolean isDestroyed = false;

    @Override
    protected void onDestroy() {
        if (!isDestroyed) {
            if (nodePlayer != null) {
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
            if (nodePlayer != null) {
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

        model.getGiftReceived().observe(this, this::showGift);
        model.getOnCommentInserted().observe(this, commentModel -> {
            binding.commentLayout.smoothScrollToPosition(0);
        });
        model.getCompetitorJoined().observe(this, profileItem -> openSpliter());
        model.getViewUpdate().observe(this, this::updateCompetitor);
        model.getOpenProfile().observe(this, this::openProfile);
        model.getReceivedJoinRequest().observe(this, message -> {
            binding.count.setVisibility(View.VISIBLE);
            Log.e("requestSize", String.valueOf(model.getRequests().size()));
            Log.e("requestSize", model.getRequests().toString());
            binding.count.setText(String.valueOf(model.getRequests().size()));
        });
        model.getSendComment().observe(this, s -> {
            switch (s) {
                case SUBJECT_TYPE_COMMENT: {
                    model.print();
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

                case SWITCH_CAMERA: {
                    model.switchCamera();
                    break;
                }
                case HIDE_COMMENT: {
                    binding.commentLayout.setVisibility(binding.commentLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    break;
                }
                case GIFT: {
                    openGift(0);
                    break;
                }
                case GIFT1: {
                    openGift(1);
                    break;
                }
                case JOIN_REQUEST: {
                    if (model.getAction().equals(LIVE_USER_TYPE_HOST)) {
                        openFriendList();
                    } else {
                        model.sendJoinRequest();
                        Toast.makeText(this, "Request sent!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case ADD_PERSON: {
                    if (model.getAction().equals(LIVE_USER_TYPE_HOST)) {
                        openFriendList();
                    }

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
                    } catch (Exception e) {
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
                    if (model.getAction().equals(LIVE_USER_TYPE_HOST)) {
                        openRemoveCompetitorConfirmation();
                    } else {
                        model.requestToLeave();
                    }
                    break;
                }
                case SHOW_VIEWERS: {
                    showViewers();
                    break;
                }
                case SUBJECT_TYPE_HOST_REMOVED_COMPETITOR: {

//                    model.onDestroy();
//                    finish();
                    break;
                }
                case OPEN_JOIN_REQUEST: {
                    ArrayList<MessageProfileModel> messages = new ArrayList<>(model.getRequests());
                    new AlertDialogViewer(this, event -> {
                        switch (event[0]) {
                            case REPLAY_TYPE_INVITATION_ACCEPTED: {
                                MessageProfileModel message = new Gson().fromJson(event[1], MessageProfileModel.class);
                                model.getRequests().removeIf(messageProfileModel -> Objects.equals(messageProfileModel.getProfileItem().getContent().get(ProfileItem.NAME), message.getProfileItem().getContent().get(ProfileItem.NAME)));
                                if (model.getRequests().isEmpty()) {
                                    binding.count.setVisibility(View.GONE);
                                }
                                performAudienceToCompetitor(message);
                                break;
                            }
                            case REPLAY_TYPE_INVITATION_DECLINED: {
                                MessageProfileModel message = new Gson().fromJson(event[1], MessageProfileModel.class);
                                model.getRequests().removeIf(messageProfileModel -> Objects.equals(messageProfileModel.getProfileItem().getContent().get(ProfileItem.NAME), message.getProfileItem().getContent().get(ProfileItem.NAME)));
                                if (model.getRequests().isEmpty()) {
                                    binding.count.setVisibility(View.GONE);
                                }
                                break;
                            }
                        }
                    }, ALERTDIALOG_TYPE_OPEN_JOIN_REQUESTS, new Gson().toJson(messages));
                    break;
                } case OPEN_TIMER_DIALOG:{
                    new AlertDialogViewer(RTMPCallActivity.this, new OnAlertDialogEventListener() {
                        @Override
                        public void onEvent(String... event) {
                            Toast.makeText(RTMPCallActivity.this, "clicked "+event[1], Toast.LENGTH_SHORT).show();
                        }
                    },ALERTDIALOG_TYPE_OPEN_TIMER);
                    break;
                }

            }
        });
        model.getFollowClicked().observe(this, integer -> {
            switch (integer) {
                case RosterHandler.TYPE_FOLLOWING: {
                    binding.follow.setImageDrawable(ContextCompat.getDrawable(RTMPCallActivity.this, R.drawable.close));
                    break;
                }
                case RosterHandler.TYPE_FOLLOWER:
                case RosterHandler.TYPE_NO_FRIEND: {
                    binding.follow.setImageDrawable(ContextCompat.getDrawable(RTMPCallActivity.this, R.drawable.add_blue));
                    break;
                }
            }
        });
        model.getSelectedItem().observe(this, this::openCommentDialog);
        model.getLiveViewerCount().observe(this, i -> binding.viewers.setText(getString(R.string.viewers, String.valueOf(model.getViewersCount()))));
        model.getOnSetUpComplete().observe(this, map -> {
            if (!map.isEmpty()) {
                Glide.with(binding.profile).load(map.get(PROFILE_IMAGE)).placeholder(R.drawable.person).into(binding.profile);
                binding.name.setText(map.get(NAME));
                binding.vip.setText(map.get(LiveItem.VIP));
                binding.viewers.setText(getString(R.string.viewers, map.get("viewers")));
                if (model.getAction().equals(LIVE_USER_TYPE_AUDIENCE)) {
                    binding.options.setVisibility(View.GONE);
                } else {
                    binding.option1.setVisibility(View.GONE);
                }
                binding.requestHolder.setVisibility(View.GONE);
                if (Functions.isFollowingOrFollower(model.getData().get(NAME) + "@" + Configurations.getHostName()) == RosterHandler.TYPE_FOLLOWING) {
                    binding.follow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.close));
                }

            } else {
                binding.requestHolder.setVisibility(View.VISIBLE);
                binding.option4.setVisibility(View.GONE);
                Glide.with(binding.profile).load(CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE)).placeholder(R.drawable.person).into(binding.profile);
                binding.name.setText(CM.getProfile().getContent().get(ProfileItem.NAME));
                binding.vip.setText(CM.getProfile().getContent().get(ProfileItem.VIP));
                binding.viewers.setText(getString(R.string.viewers, String.valueOf(model.getViewersCount())));
                binding.follow.setVisibility(View.GONE);
                binding.option7.setVisibility(View.VISIBLE);
                binding.option5.setVisibility(View.VISIBLE);
                binding.option1.setVisibility(View.GONE);
            }
        });
        model.getShowToast().observe(this,this::showToast);
        model.getUpdateCommentSection().observe(this,this::updateCommentSection);

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
        model.getOnCompetitorAccepted().observe(this, s -> model.setUpForCompetitor(this, binding.cameraView, binding.cameraView2));
        model.getOptionsMenuVisibility().observe(this, b -> binding.optionsLayout.setVisibility(b ? View.VISIBLE : View.GONE));
    }

    private void updateCommentSection(Boolean aBoolean) {
        binding.send.setEnabled(!aBoolean);
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void showGift(GiftModel giftModel) {
        GiftItem giftItem = giftModel.getGiftItem();
        binding.showGift.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.showGift.setVisibility(View.GONE);
            binding.imageview.setVisibility(View.GONE);
            binding.lottie.setVisibility(View.GONE);
            binding.svgaImageView.setVisibility(View.GONE);
        }, 3000);

        switch (giftItem.getGiftType()) {
            case GiftItem.GIFT_TYPE_IMAGE: {
                binding.imageview.setVisibility(View.VISIBLE);
                Functions.setImageView(this, giftItem.getGiftName(), binding.imageview);
                break;
            }
            case GiftItem.GIFT_TYPE_LOTTIE: {
                binding.lottie.setVisibility(View.VISIBLE);
                Functions.setLottieAnimation(giftItem.getGiftName(), binding.lottie);
                break;
            }
            case GiftItem.GIFT_TYPE_SVGA_HALF: {
                binding.svgaImageView.setVisibility(View.VISIBLE);
                Functions.loadSVGAAnimation(this, giftItem.getGiftName(), binding.svgaImageView);
                break;
            }
        }
        if (Integer.parseInt(giftModel.getIndex()) == 0) {
            if (model.getAction().equals(LIVE_USER_TYPE_HOST)) {
                binding.textview.setText(model.getViewerProfile(giftModel.getFrom().split("@")[0]).getContent().get(ProfileItem.NAME) + " sent a gift to " + model.getLive().get(NAME));
            } else {
                binding.textview.setText(model.getViewerProfile(giftModel.getFrom().split("@")[0]).getContent().get(ProfileItem.NAME) + " sent a gift to " + model.getData().get(NAME));
            }
        } else {
            binding.textview.setText(model.getViewerProfile(giftModel.getFrom().split("@")[0]).getContent().get(ProfileItem.NAME) + " sent a gift to " + model.getCompetitorList().get(0).getContent().get(NAME));
        }
    }

    private void performAudienceToCompetitor(MessageProfileModel profileModel) {
        isOccupied = true;
        model.requestAccepted(profileModel);
    }

    private void openRemoveCompetitorConfirmation() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setCancelButton("Cancel", Dialog::dismiss).setConfirmButton("Confirm", sweetAlertDialog1 -> {
            model.removeCompetitor(0);
            sweetAlertDialog.dismiss();
        }).setContentText("Are you sure you want to remove " + model.getCompetitorList().get(0).getContent().get(NAME)).setTitle("Warning");
        sweetAlertDialog.show();
    }

    private void showViewers() {

    }

    NodePlayer nodePlayer = null;

    private void updateCompetitor(ArrayList<LiveItem> liveItems) {
        if (liveItems.isEmpty()) {
            closeSpliter();
//            binding.addPeople.setVisibility(View.VISIBLE);
            if (nodePlayer != null) {
                nodePlayer.stop();
            }
            binding.profileInfo1.setVisibility(View.GONE);
            binding.profileInfo2.setVisibility(View.GONE);
            binding.endCall1.setVisibility(View.GONE);
            binding.endCall2.setVisibility(View.GONE);
            return;
        }
        LiveItem liveItem = liveItems.get(0);

        if (model.getAction().equals(LIVE_USER_TYPE_HOST)) {
            Glide.with(binding.profile1).load(CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE)).placeholder(R.drawable.person).into(binding.profile1);
            binding.name1.setText(CM.getProfile().getContent().get(ProfileItem.NAME));
            binding.vip1.setText(CM.getProfile().getContent().get(ProfileItem.VIP));
            binding.profileInfo1.setVisibility(View.VISIBLE);
        } else {
            Glide.with(binding.profile1).load(model.getData().get(PROFILE_IMAGE)).placeholder(R.drawable.person).into(binding.profile1);
            binding.name1.setText(model.getData().get(LiveItem.NAME));
            binding.vip1.setText(model.getData().get(LiveItem.VIP));
            binding.profileInfo1.setVisibility(View.VISIBLE);
        }

        binding.name2.setText(liveItem.getContent().get(LiveItem.NAME));
        binding.vip2.setText(liveItem.getContent().get(ProfileItem.VIP));
        binding.profileInfo2.setVisibility(View.VISIBLE);
        Glide.with(binding.profile2).load(liveItem.getContent().get(PROFILE_IMAGE)).placeholder(R.drawable.person).into(binding.profile2);

        openSpliter();
//        binding.addPeople.setVisibility(View.GONE);

        if (model.getAction().equals(LIVE_USER_TYPE_AUDIENCE)) {
            nodePlayer = new NodePlayer(this, "https://github.com/fahimfahadleon/Probashilive/tree/master");
            nodePlayer.attachView(binding.cameraView2);
            nodePlayer.start(Configurations.RTMP_URL + liveItem.getContent().get(NAME));
            return;
        }

        if (model.getAction().equals(LIVE_USER_TYPE_COMPETITOR)) {
            binding.endCall1.setVisibility(View.GONE);
            binding.endCall2.setVisibility(View.VISIBLE);
        } else {
            binding.endCall1.setVisibility(View.VISIBLE);
            binding.endCall2.setVisibility(View.VISIBLE);
            nodePlayer = new NodePlayer(this, "https://github.com/fahimfahadleon/Probashilive/tree/master");
            nodePlayer.attachView(binding.cameraView2);
            nodePlayer.start(Configurations.RTMP_URL + liveItem.getContent().get(NAME));
        }
    }

    private void openCommentDialog(CommentModel cm) {

        new AlertDialogViewer(this, event -> {
            switch (event[0]) {
                case REPLAY_TYPE_VISIT: {
                    if (model.getAction().equals(LIVE_USER_TYPE_HOST)) {
                        try {
                            openProfileDialog(event[1]);
                        } catch (JSONException e) {
                            e.fillInStackTrace();
                        }
                    } else {
                        try {
                            ProfileItem profileItem = ProfileItem.parseProfileItem(Functions.getSingleItemOfNode(NODE_USERS,event[1].split("@")[0]));
                            openProfile(profileItem);
                        }catch (Exception e){
                            e.fillInStackTrace();
                        }

                    }
                    break;
                }
                case REPLAY_TYPE_BLOCK: {
                    Toast.makeText(this, "Block " + event[1], Toast.LENGTH_SHORT).show();
                    break;
                }
                case REPLAY_TYPE_INVITE: {
                    Toast.makeText(this, "Invite " + event[1], Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }, AlertDialogViewer.ALERTDIALOG_TYPE_OPEN_COMMENT, new Gson().toJson(cm));
    }

    private void openProfileDialog(String name) throws JSONException {
        new AlertDialogViewer(this, event -> {
            if (event[0].equals(REPLAY_TYPE_MESSAGE)) {
                Intent i = new Intent(this,Inbox.class);
                i.putExtra(DATA,event[1]);
                startActivity(i);
            }
        }, ALERTDIALOG_TYPE_OPEN_PROFILE, name);
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
        }, ALERTDIALOG_TYPE_OPEN_FRIEND_LIST);
    }

    private void openGift(int index) {
        new AlertDialogViewer(this, event -> {
            switch (event[0]) {
                case REPLAY_TYPE_GIFT_SELECTED: {
                    model.sendGift(index, event[1]);
                    break;
                }
                case REPLAY_TYPE_GIFT_PREVIEW: {
                    new AlertDialogViewer(RTMPCallActivity.this, event1 -> {
                    }, ALERTDIALOG_TYPE_PREVIEW_GIFT, event[1]);
                    break;
                }
            }

        }, ALERTDIALOG_TYPE_OPEN_GIFT);
    }

    private void openProfile(ProfileItem s) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(DATA, new Gson().toJson(s));
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
        binding.option1.setVisibility(View.GONE);
        binding.requestHolder.setVisibility(View.GONE);

//        binding.addPeople.setVisibility(View.VISIBLE);
    }

    void closeSpliter() {
        isSpliterOpen = false;
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.cameraView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.cameraView.setLayoutParams(layoutParams);
        binding.cameraView2.setVisibility(View.GONE);
        binding.option1.setVisibility(View.VISIBLE);
        if (model.getAction().equals(LIVE_USER_TYPE_HOST)) {
            binding.requestHolder.setVisibility(View.VISIBLE);
        }

//
    }

}

