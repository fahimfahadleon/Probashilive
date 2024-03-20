package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.connectionutils.RosterHandler.getRosterHandler;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.OnlineFriendsAdapter;
import com.probashiincltd.probashilive.callbacks.OnAlertDialogEventListener;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.CommentOptionsBinding;
import com.probashiincltd.probashilive.databinding.OpenInviteFriendBinding;
import com.probashiincltd.probashilive.databinding.ProfileDialogBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.CommentModel;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.utils.Configurations;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Objects;

public class AlertDialogViewer {

    public static final String ALERTDIALOG_TYPE_OPEN_COMMENT = "open_comment_dialog";
    public static final String ALERTDIALOG_TYPE_OPEN_PROFILE = "open_profile_dialog";
    public static final String ALERTDIALOG_TYPE_OPEN_FRIEND_LIST = "open_friend_list_dialog";
    public static final String ALERTDIALOG_TYPE_OPEN_GIFT = "open_gift_dialog";
    public static final String REPLAY_TYPE_BLOCK = "replay_type_block";
    public static final String REPLAY_TYPE_INVITE = "replay_type_invite";
    public static final String REPLAY_TYPE_VISIT = "replay_type_visit";
    public static final String REPLAY_TYPE_MESSAGE = "replay_type_message";
    public static final String REPLAY_TYPE_INVITE_FRIEND = "replay_type_invite_friend";
    OnAlertDialogEventListener listener;
    String[]contents;
    Context context;
    int isFriendStatus = 0;
    AlertDialog ad;
    public AlertDialogViewer(Context context, OnAlertDialogEventListener listener, String ... content){
        this.contents = content;
        this.context = context;
        this.listener = listener;
        try {
            init();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void init() throws JSONException {
        switch (contents[0]){
            case ALERTDIALOG_TYPE_OPEN_COMMENT:{
                openCommentDialog(contents[1]);
                break;
            }case ALERTDIALOG_TYPE_OPEN_PROFILE:{
                openProfileDialog(contents[1]);
                break;
            }case ALERTDIALOG_TYPE_OPEN_FRIEND_LIST:{
                openFriendListDialog();
                break;
            } case ALERTDIALOG_TYPE_OPEN_GIFT:{
                break;
            }
        }
    }



    private void openCommentDialog(String cms){
        CommentModel cm = new Gson().fromJson(cms,CommentModel.class);
        if (cm.getName().equals(CM.getProfile().getName())) {
            return;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(context, R.style.Base_Theme_ProbashiLive);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CommentOptionsBinding c = CommentOptionsBinding.inflate(inflater);
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


        c.block.setOnClickListener(v -> listener.onEvent(REPLAY_TYPE_BLOCK,cm.getName()));

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

        c.invite.setOnClickListener(v -> listener.onEvent(REPLAY_TYPE_INVITE,cm.getName()));
        c.visit.setOnClickListener(v -> listener.onEvent(REPLAY_TYPE_VISIT,cm.getName()));


        b.setView(c.getRoot());
        ad = b.create();
        ad.show();
        Objects.requireNonNull(ad.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ad.getWindow().setGravity(Gravity.CENTER);
        ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ad.getWindow().setDimAmount(0.5f);
    }

    private void openProfileDialog(String name) throws JSONException {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Base_Theme_ProbashiLive);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        try {
            if (getRosterHandler().roster.contains(JidCreate.bareFrom(name+"@"+Configurations.getHostName()))) {
                if (!getRosterHandler().roster.getEntry(JidCreate.bareFrom(name+"@"+Configurations.getHostName())).getGroups().isEmpty()) {
                    isFriendStatus = 1;
                } else {
                    isFriendStatus = 2;
                }
            }
        } catch (Exception e) {
            //ingored
        }

        if (isFriendStatus == 1) {
            binding1.follow.setText(R.string.unfollow);
        }
        binding1.message.setOnClickListener(v -> listener.onEvent(REPLAY_TYPE_MESSAGE));

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
        ad = builder.create();
        ad.show();
        Objects.requireNonNull(ad.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ad.getWindow().setGravity(Gravity.CENTER);
        ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ad.getWindow().setDimAmount(0.5f);
    }

    private void openFriendListDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Base_Theme_ProbashiLive);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        OpenInviteFriendBinding binding1 = OpenInviteFriendBinding.inflate(inflater);
        OnlineFriendsAdapter adapter = new OnlineFriendsAdapter(profileItem -> {
            try {
                for (Presence presence : getRosterHandler().roster.getAvailablePresences(JidCreate.bareFrom(profileItem.getContent().get(ProfileItem.NAME) + "@" + Configurations.getHostName()))) {
                   listener.onEvent(REPLAY_TYPE_INVITE_FRIEND,presence.getFrom().asFullJidOrThrow().toString());
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
        Objects.requireNonNull(ad.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ad.getWindow().setGravity(Gravity.CENTER);
        ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ad.getWindow().setDimAmount(0.5f);
    }


}
