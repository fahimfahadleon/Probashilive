package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.DATA;

import android.os.Bundle;
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
import com.probashiincltd.probashilive.models.ChatItem;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.viewmodels.ActivityInboxViewModel;

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
        });
    }

    private void setUpMessage() {
        if(model.getMessages.isEmpty()){
            messageController = new MessageController(chatItem.getJid());
            model.getMessages = messageController.load(30, new WarningCallback() {
                @Override
                public void onSuccess() {}
                @Override
                public void onFailed() {}
            });
        }
        model.setAdapter(new InboxAdapter(this,model.getMessages));
    }

    private void setUpPersonalData() {
        chatItem = new ChatItem();
        chatItem.setProfilePicture(profileItem.getContent().get(ProfileItem.PROFILE_PICTURE));
        chatItem.setName(profileItem.getContent().get(ProfileItem.NAME));
        chatItem.setJid(profileItem.getContent().get(ProfileItem.EMAIL).split("@")[0] +"@"+ CM.getConnection().getHost().toString());
        //todo set up personal data
    }
}