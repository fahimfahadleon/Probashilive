package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.adapter.GiftAdapter.GIFT_TYPE_IMAGE;
import static com.probashiincltd.probashilive.adapter.GiftAdapter.GIFT_TYPE_LOTTIE;
import static com.probashiincltd.probashilive.adapter.GiftAdapter.GIFT_TYPE_SVGA;
import static com.probashiincltd.probashilive.adapter.GiftTitleAdapter.GIFT_TITLE_IMAGE;
import static com.probashiincltd.probashilive.adapter.GiftTitleAdapter.GIFT_TITLE_LOTTIE;
import static com.probashiincltd.probashilive.adapter.GiftTitleAdapter.GIFT_TITLE_SVGA;
import static com.probashiincltd.probashilive.connectionutils.RosterHandler.TYPE_FOLLOWING;
import static com.probashiincltd.probashilive.connectionutils.RosterHandler.TYPE_NO_FRIEND;
import static com.probashiincltd.probashilive.connectionutils.RosterHandler.getRosterHandler;
import static com.probashiincltd.probashilive.functions.Functions.loadSVGAAnimation;
import static com.probashiincltd.probashilive.functions.Functions.setImageView;
import static com.probashiincltd.probashilive.functions.Functions.setLottieAnimation;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.GiftAdapter;
import com.probashiincltd.probashilive.adapter.GiftTitleAdapter;
import com.probashiincltd.probashilive.adapter.JoinRequestAdapter;
import com.probashiincltd.probashilive.adapter.OnlineFriendsAdapter;
import com.probashiincltd.probashilive.callbacks.OnAlertDialogEventListener;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.CommentOptionsBinding;
import com.probashiincltd.probashilive.databinding.GiftLayoutBinding;
import com.probashiincltd.probashilive.databinding.JoinRequestLayoutBinding;
import com.probashiincltd.probashilive.databinding.OpenInviteFriendBinding;
import com.probashiincltd.probashilive.databinding.PreviewGiftBinding;
import com.probashiincltd.probashilive.databinding.ProfileDialogBinding;
import com.probashiincltd.probashilive.databinding.SetCountdownTimerLayoutBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.CommentModel;
import com.probashiincltd.probashilive.models.GiftItem;
import com.probashiincltd.probashilive.models.MessageProfileModel;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.utils.Configurations;
import com.shawnlin.numberpicker.NumberPicker;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class AlertDialogViewer {

    public static final String ALERTDIALOG_TYPE_OPEN_COMMENT = "open_comment_dialog";
    public static final String ALERTDIALOG_TYPE_OPEN_TIMER = "open_timer_dialog";
    public static final String ALERTDIALOG_TYPE_OPEN_PROFILE = "open_profile_dialog";
    public static final String ALERTDIALOG_TYPE_OPEN_FRIEND_LIST = "open_friend_list_dialog";
    public static final String ALERTDIALOG_TYPE_OPEN_GIFT = "open_gift_dialog";
    public static final String ALERTDIALOG_TYPE_OPEN_JOIN_REQUESTS = "open_join_request_dialog";
    public static final String ALERTDIALOG_TYPE_PREVIEW_GIFT = "open_gift_preview";
    public static final String REPLAY_TYPE_BLOCK = "replay_type_block";
    public static final String REPLAY_TYPE_INVITE = "replay_type_invite";
    public static final String REPLAY_TYPE_VISIT = "replay_type_visit";
    public static final String REPLAY_TYPE_MESSAGE = "replay_type_message";
    public static final String REPLAY_TYPE_INVITE_FRIEND = "replay_type_invite_friend";
    public static final String REPLAY_TYPE_INVITATION_ACCEPTED = "replay_type_invitation_accepted";
    public static final String REPLAY_TYPE_INVITATION_DECLINED = "replay_type_invitation_declined";
    public static final String REPLAY_TYPE_GIFT_SELECTED = "replay_type_gift_selected";
    public static final String REPLAY_TYPE_GIFT_PREVIEW = "replay_type_gift_preview";
    public static final String REPLAY_TYPE_TIMER_SELECTED = "replay_type_timer_selected";
    OnAlertDialogEventListener listener;
    String[] contents;
    Context context;
    int isFriendStatus = 0;
    AlertDialog ad;
    AlertDialog.Builder builder;
    LayoutInflater inflater;

    public AlertDialogViewer(Context context, OnAlertDialogEventListener listener, String... content) {
        this.contents = content;
        this.context = context;
        this.listener = listener;

        builder = new AlertDialog.Builder(context, R.style.Base_Theme_ProbashiLive);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            init();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws JSONException {
        switch (contents[0]) {
            case ALERTDIALOG_TYPE_OPEN_COMMENT: {
                openCommentDialog(contents[1]);
                break;
            }
            case ALERTDIALOG_TYPE_OPEN_PROFILE: {
                openProfileDialog(contents[1]);
                break;
            }
            case ALERTDIALOG_TYPE_OPEN_FRIEND_LIST: {
                openFriendListDialog();
                break;
            }
            case ALERTDIALOG_TYPE_OPEN_GIFT: {
                openGift();
                break;
            }
            case ALERTDIALOG_TYPE_OPEN_JOIN_REQUESTS: {
                openJoinRequests(contents[1]);
                break;
            }
            case ALERTDIALOG_TYPE_PREVIEW_GIFT: {
                openGiftPreview(contents[1]);
                break;
            }
            case ALERTDIALOG_TYPE_OPEN_TIMER:{
                openTimer();
                break;
            }
        }
    }
    String pickedValue = "1";
    private void openTimer() {
        SetCountdownTimerLayoutBinding binding= SetCountdownTimerLayoutBinding.inflate(inflater);
        binding.numberPicker.setOnScrollListener((picker, scrollState) -> {
            if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                pickedValue = String.valueOf(picker.getValue());
            }
        });

        binding.cancel.setOnClickListener(v -> ad.dismiss());
        binding.start.setOnClickListener(v -> {
            listener.onEvent(REPLAY_TYPE_TIMER_SELECTED,pickedValue);
            ad.dismiss();
        });

        builder.setView(binding.getRoot());
        ad= builder.create();
        ad.show();
        performPostAction();
    }


    private void openGiftPreview(String content) {
        GiftItem giftItem = new Gson().fromJson(content, GiftItem.class);
        PreviewGiftBinding binding = PreviewGiftBinding.inflate(inflater);

        switch (giftItem.getGiftType()) {
            case GiftItem.GIFT_TYPE_IMAGE: {
                binding.imageview.setVisibility(View.VISIBLE);
                binding.lottie.setVisibility(View.GONE);
                binding.svgaImageView.setVisibility(View.GONE);
                setImageView(context,giftItem.getGiftName(), binding.imageview);
                break;
            }
            case GiftItem.GIFT_TYPE_LOTTIE: {
                binding.imageview.setVisibility(View.GONE);
                binding.lottie.setVisibility(View.VISIBLE);
                binding.svgaImageView.setVisibility(View.GONE);
                setLottieAnimation(giftItem.getGiftName(), binding.lottie);
                break;
            }
            case GiftItem.GIFT_TYPE_SVGA_HALF:
            case GiftItem.GIFT_TYPE_SVGA_FULL: {
                binding.imageview.setVisibility(View.GONE);
                binding.lottie.setVisibility(View.GONE);
                binding.svgaImageView.setVisibility(View.VISIBLE);
                loadSVGAAnimation(context,giftItem.getGiftName(),binding.svgaImageView);
                break;
            }
        }
        binding.name.setText(giftItem.getGiftName().replace(".png",""));
        binding.price.setText(giftItem.getGiftPrice());
        builder.setView(binding.getRoot());
        ad = builder.create();
        ad.show();
        performPostAction();
    }

    GiftAdapter adapter;

    private void openGift() {
        GiftLayoutBinding binding = GiftLayoutBinding.inflate(inflater);
        adapter = new GiftAdapter(context, GIFT_TYPE_IMAGE, giftItem -> {
            listener.onEvent(REPLAY_TYPE_GIFT_SELECTED, new Gson().toJson(giftItem));
            ad.dismiss();
        }, giftItem -> listener.onEvent(REPLAY_TYPE_GIFT_PREVIEW, new Gson().toJson(giftItem)));

        binding.giftList.setAdapter(adapter);
        GiftTitleAdapter giftTitleAdapter = new GiftTitleAdapter(context, s -> {
            int type;
            switch (s) {
                case GIFT_TITLE_IMAGE: {
                    type = GIFT_TYPE_IMAGE;
                    break;
                }
                case GIFT_TITLE_SVGA: {
                    type = GIFT_TYPE_SVGA;
                    break;
                }
                case GIFT_TITLE_LOTTIE: {
                    type = GIFT_TYPE_LOTTIE;
                    break;
                }
                default:
                    type = GIFT_TYPE_IMAGE;
            }
            adapter = new GiftAdapter(context, type, giftItem -> {
                listener.onEvent(REPLAY_TYPE_GIFT_SELECTED, new Gson().toJson(giftItem));
                ad.dismiss();
            }, giftItem -> listener.onEvent(REPLAY_TYPE_GIFT_PREVIEW, new Gson().toJson(giftItem)));
            binding.giftList.setAdapter(adapter);
        });
        binding.titleList.setAdapter(giftTitleAdapter);
        builder.setView(binding.getRoot());
        ad = builder.create();
        ad.show();
        performPostAction();


    }

    private void openJoinRequests(String content) {
        Type listType = new TypeToken<ArrayList<MessageProfileModel>>() {
        }.getType();
        ArrayList<MessageProfileModel> requests = new ArrayList<>(new Gson().fromJson(content, listType));
        JoinRequestLayoutBinding binding = JoinRequestLayoutBinding.inflate(inflater);
        JoinRequestAdapter adapter = new JoinRequestAdapter(requests, new JoinRequestAdapter.OnItemEventListener() {
            @Override
            public void onAccept(MessageProfileModel message) {
                listener.onEvent(REPLAY_TYPE_INVITATION_ACCEPTED, new Gson().toJson(message));
                ad.dismiss();
            }

            @Override
            public void onDecline(MessageProfileModel message) {
                listener.onEvent(REPLAY_TYPE_INVITATION_DECLINED, new Gson().toJson(message));
            }
        });
        binding.rv.setAdapter(adapter);
        builder.setView(binding.getRoot());
        ad = builder.create();
        ad.show();
        performPostAction();

    }


    private void openCommentDialog(String cms) {
        CommentModel cm = new Gson().fromJson(cms, CommentModel.class);
        if (cm.getName().equals(CM.getProfile().getName())) {
            return;
        }

        CommentOptionsBinding c = CommentOptionsBinding.inflate(inflater);
        c.userName.setText(cm.getName());
        Glide.with(c.profile).load(cm.getPp()).placeholder(R.drawable.person).into(c.profile);
        isFriendStatus = Functions.isFollowingOrFollower(cm.getId());
        if(isFriendStatus == TYPE_FOLLOWING){
            c.follow.setText(R.string.unfollow);
        }


        c.block.setOnClickListener(v -> listener.onEvent(REPLAY_TYPE_BLOCK, cm.getName()));

        c.follow.setOnClickListener(v -> {
            try {
                if (isFriendStatus == TYPE_FOLLOWING) {
                    getRosterHandler().removeEntry(cm.getId());
                    isFriendStatus = TYPE_NO_FRIEND;
                    c.follow.setText(R.string.follow);
                } else if (isFriendStatus == TYPE_NO_FRIEND) {
                    getRosterHandler().createEntry(cm.getId(), cm.getName());
                    isFriendStatus = TYPE_FOLLOWING;
                    c.follow.setText(R.string.unfollow);
                } else {
                    getRosterHandler().addGroup(cm.getId());
                    isFriendStatus = TYPE_FOLLOWING;
                    c.follow.setText(R.string.unfollow);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        c.invite.setOnClickListener(v -> listener.onEvent(REPLAY_TYPE_INVITE, cm.getName()));
        c.visit.setOnClickListener(v -> listener.onEvent(REPLAY_TYPE_VISIT, cm.getId()));


        builder.setView(c.getRoot());
        ad = builder.create();
        ad.show();
        performPostAction();
    }

    private void openProfileDialog(String name) throws JSONException {
        ProfileDialogBinding binding1 = ProfileDialogBinding.inflate(inflater);
        Item item = Functions.getSingleItemOfNode(CM.NODE_USERS, name);
        ProfileItem profileItem = ProfileItem.parseProfileItem(item);
        Log.e("profileItem", profileItem.toString());
        binding1.coin.setVisibility(View.GONE);
        binding1.email.setText(profileItem.getContent().get(ProfileItem.EMAIL));
        binding1.userName.setText(profileItem.getContent().get(ProfileItem.NAME));
        binding1.phone.setText(profileItem.getContent().get(ProfileItem.PHONE));
        binding1.status.setText(profileItem.getContent().get(ProfileItem.VIP));
        Glide.with(binding1.profile).load(profileItem.getContent().get(ProfileItem.PROFILE_PICTURE)).placeholder(R.drawable.person).into(binding1.profile);
        isFriendStatus = Functions.isFollowingOrFollower(name + "@" + Configurations.getHostName());
        if (isFriendStatus == TYPE_FOLLOWING) {
            binding1.follow.setText(R.string.unfollow);
        }
        binding1.message.setOnClickListener(v -> listener.onEvent(REPLAY_TYPE_MESSAGE,new Gson().toJson(profileItem)));

        binding1.follow.setOnClickListener(v -> {
            try {
                if (isFriendStatus == TYPE_FOLLOWING) {
                    getRosterHandler().removeEntry(profileItem.getContent().get(ProfileItem.NAME)+ "@" + Configurations.getHostName());
                    isFriendStatus = TYPE_NO_FRIEND;
                    binding1.follow.setText(R.string.follow);
                } else if (isFriendStatus == TYPE_NO_FRIEND) {
                    getRosterHandler().createEntry(profileItem.getContent().get(ProfileItem.NAME) + "@" + Configurations.getHostName(), profileItem.getName());
                    isFriendStatus = TYPE_FOLLOWING;
                    binding1.follow.setText(R.string.unfollow);
                } else {
                    getRosterHandler().addGroup(profileItem.getContent().get(ProfileItem.NAME)+ "@" + Configurations.getHostName());
                    isFriendStatus = TYPE_FOLLOWING;
                    binding1.follow.setText(R.string.unfollow);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        builder.setView(binding1.getRoot());
        ad = builder.create();
        ad.show();
        performPostAction();
    }

    private void openFriendListDialog() {
        OpenInviteFriendBinding binding1 = OpenInviteFriendBinding.inflate(inflater);
        OnlineFriendsAdapter adapter = new OnlineFriendsAdapter(profileItem -> {
            try {
                for (Presence presence : getRosterHandler().roster.getAvailablePresences(JidCreate.bareFrom(profileItem.getContent().get(ProfileItem.NAME) + "@" + Configurations.getHostName()))) {
                    listener.onEvent(REPLAY_TYPE_INVITE_FRIEND, presence.getFrom().asFullJidOrThrow().toString());
                }
                ad.dismiss();
            } catch (XmppStringprepException e) {
                throw new RuntimeException(e);
            }
        });
        binding1.inviteFriendRV.setAdapter(adapter);

        builder.setView(binding1.getRoot());
        ad = builder.create();
        ad = builder.create();
        ad.show();
        performPostAction();
    }

    void performPostAction() {
        Objects.requireNonNull(ad.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ad.getWindow().setGravity(Gravity.CENTER);
        ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ad.getWindow().setDimAmount(0.5f);
    }

}
