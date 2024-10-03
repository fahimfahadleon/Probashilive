package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.DATA;
import static com.probashiincltd.probashilive.viewmodels.ActivityInboxViewModel.CLICK_OPTIONS;
import static com.probashiincltd.probashilive.viewmodels.ActivityInboxViewModel.CLICK_SEND;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

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
            Toast.makeText(this, "new message Arrieved: from:"+message.getFrom()+" type: "+message.getType().toString(), Toast.LENGTH_SHORT).show();
            model.addNewMessage(message);
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
                            builder.ofType(Message.Type.chat);
                            Message m = builder.build();
                            m.setFrom(CM.getConnection().getUser().asBareJid());
                            m.setTo(JidCreate.bareFrom(chatItem.getJid()));
                            model.addNewMessage(m);
                            binding.editText.setText(null);
                            CM.getConnection().sendStanza(m);
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

    private void setUpMessage() {
        if(model.getMessages.isEmpty()){
            Log.e("checkingfordata",chatItem.getJid());
            messageController = new MessageController(chatItem.getJid());
            model.getMessages = messageController.load(30, new WarningCallback() {
                @Override
                public void onSuccess() {}
                @Override
                public void onFailed() {}
            });
        }
        model.setAdapter(new InboxAdapter(this,model.getMessages,profileItem));
    }

    private void setUpPersonalData() {

        //todo fix problem not getting email address in some accounts.
        chatItem = new ChatItem();
        chatItem.setProfilePicture(profileItem.getContent().get(ProfileItem.PROFILE_PICTURE));
        chatItem.setName(profileItem.getContent().get(ProfileItem.NAME));
        Log.e("profileItem",profileItem.toString());
        chatItem.setJid(profileItem.getContent().get(ProfileItem.EMAIL).split("@")[0] +"@"+ CM.getConnection().getHost().toString());
        Functions.loadImage(this,binding.profile,chatItem.getProfilePicture());
        binding.titleName.setText(chatItem.getName());

        //todo set up personal data
    }
}