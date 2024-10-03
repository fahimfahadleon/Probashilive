package com.probashiincltd.probashilive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.InboxItemLeftBinding;
import com.probashiincltd.probashilive.databinding.InboxItemRightBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;

import org.jivesoftware.smack.packet.Message;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {
    LinkedList<Message> messages;
    Context context;
    ProfileItem profileItem;
    final int MESSAGE_TYPE_RIGHT = 0;
    final int MESSAGE_TYPE_LEFT = 1;


    public InboxAdapter(Context context, LinkedList<Message> messages,ProfileItem profileItem) {
        this.context = context;
        this.messages = messages == null? new LinkedList<>():messages;
        this.profileItem = profileItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_RIGHT) {
            return new ViewHolder(InboxItemRightBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot(),viewType);
        } else {
            return new ViewHolder(InboxItemLeftBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot(),viewType);

        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (CM.getConnection().getUser().asBareJid().toString().equals(message.getFrom().asBareJid().toString())) {
            return MESSAGE_TYPE_RIGHT;
        } else {
            return MESSAGE_TYPE_LEFT;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setUpData(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        Message message;
        CircleImageView imageView;
        TextView textView;
        int viewType;
        public void setUpData(Message message) {
            this.message = message;
            Functions.loadImage(context,imageView,viewType == MESSAGE_TYPE_LEFT?profileItem.getContent().get(ProfileItem.PROFILE_PICTURE):CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE));
            textView.setText(message.getBody());

        }
        public ViewHolder(@NonNull View itemView,int viewType) {
            super(itemView);
            this.viewType = viewType;
            imageView = itemView.findViewById(R.id.profilePictre);
            textView = itemView.findViewById(R.id.textview);
        }
    }
}
