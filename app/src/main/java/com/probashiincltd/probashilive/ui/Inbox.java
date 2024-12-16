package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.connectionutils.CM.FIREBASE_CHAT_BOX;
import static com.probashiincltd.probashilive.connectionutils.CM.getConnection;
import static com.probashiincltd.probashilive.utils.Configurations.DATA;
import static com.probashiincltd.probashilive.viewmodels.ActivityInboxViewModel.CLICK_OPTIONS;
import static com.probashiincltd.probashilive.viewmodels.ActivityInboxViewModel.CLICK_SEND;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.InboxAdapter;
import com.probashiincltd.probashilive.callbacks.WarningCallback;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.connectionutils.MessageController;
import com.probashiincltd.probashilive.databinding.ActivityInboxBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.ChatItem;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.viewmodels.ActivityInboxViewModel;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jxmpp.jid.impl.JidCreate;

import java.util.ArrayList;

public class Inbox extends AppCompatActivity {
    ActivityInboxBinding binding;
    ActivityInboxViewModel model;
    ProfileItem profileItem;
    MessageController messageController;
    ChatItem chatItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_inbox);
        model = new ViewModelProvider(this).get(ActivityInboxViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);
        profileItem = new Gson().fromJson(getIntent().getStringExtra(DATA),ProfileItem.class);
        setUpPersonalData();
        setUpMessage();
        setUpObserver();



    }

    private void setUpObserver() {
        CM.newMessage.observe(this,message -> {
           if(message.getFrom().asBareJid().toString().equals(chatItem.getJid())){
               model.addNewMessage(message,binding.recyclerview);
           }
        });
        model.clickAction.observe(this,i ->{
            switch (i){
                case CLICK_SEND:{
                    //todo send to server
                    String s = binding.editText.getText().toString();
                    if(!s.isEmpty()){
                        try {
                            MessageBuilder builder = CM.getConnection().getStanzaFactory().buildMessageStanza();
                            builder.setSubject("message");
                            builder.setBody(s);
                            builder.from(CM.getConnection().getUser().asFullJidIfPossible());
                            builder.to(JidCreate.bareFrom(chatItem.getJid()));
                            builder.ofType(Message.Type.chat);
                            Message m = builder.build();
                            m.setFrom(CM.getConnection().getUser().asFullJidIfPossible());
                            m.setTo(JidCreate.bareFrom(chatItem.getJid()));
                            model.addNewMessage(m,binding.recyclerview);
                            binding.editText.setText(null);
                            CM.chatManager.chatWith(JidCreate.entityBareFrom(JidCreate.bareFrom(chatItem.getJid()))).send(m);

                            ChatItem mychatitem = new ChatItem();
                            mychatitem.setJid(CM.getConnection().getUser().asBareJid().toString());
                            mychatitem.setName(CM.getProfile().getContent().get(ProfileItem.NAME));
                            mychatitem.setProfilePicture(CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE));


                            ChatItem friendChatItem = new ChatItem();
                            friendChatItem.setJid(chatItem.getJid());
                            friendChatItem.setName(chatItem.getName());
                            friendChatItem.setProfilePicture(chatItem.getProfilePicture());

                            updateFirebaseData(CM.getProfile().getContent().get(ProfileItem.JID).split("@")[0],chatItem);
                            updateFirebaseData(chatItem.getJid().split("@")[0],mychatitem);

                        }catch (Exception e){
                            e.fillInStackTrace();
                        }

                    }
                    break;
                }
                case CLICK_OPTIONS:{
                    //todo open options
                    break;
                }
            }
        });
    }

    private void updateFirebaseData(String s,ChatItem chatItem) {
        ArrayList<ChatItem>currentItems =new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(FIREBASE_CHAT_BOX).child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        currentItems.add(ds.getValue(ChatItem.class));
                    }
                }
                boolean shouldAdd = true;
                for(ChatItem ci : currentItems){
                    if (ci.getJid().equals(chatItem.getJid())) {
                        shouldAdd = false;
                        break;
                    }
                }
                if(shouldAdd){
                    currentItems.add(chatItem);
                }
                FirebaseDatabase.getInstance().getReference().child(FIREBASE_CHAT_BOX).child(s).setValue(currentItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setUpMessage() {
        if(model.messages.isEmpty()){
            Log.e("checkingfordata",chatItem.getJid());
            messageController = new MessageController(chatItem.getJid());
            model.messages = messageController.load(30, new WarningCallback() {
                @Override
                public void onSuccess() {}
                @Override
                public void onFailed() {}
            });
        }
        model.setAdapter(new InboxAdapter(this,model.messages,profileItem));
    }

    private void setUpPersonalData() {

        //todo fix problem not getting email address in some accounts.
        chatItem = new ChatItem();
        chatItem.setProfilePicture(profileItem.getContent().get(ProfileItem.PROFILE_PICTURE));
        chatItem.setName(profileItem.getContent().get(ProfileItem.NAME));
        chatItem.setJid(profileItem.getContent().get(ProfileItem.JID));
        Functions.loadImage(this,binding.profile,chatItem.getProfilePicture());
        binding.titleName.setText(chatItem.getName());

        //todo set up personal data
    }
}