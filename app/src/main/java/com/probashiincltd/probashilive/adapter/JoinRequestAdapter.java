package com.probashiincltd.probashilive.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.databinding.SingleJoinRequestItemBinding;
import com.probashiincltd.probashilive.models.MessageProfileModel;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

public class JoinRequestAdapter extends RecyclerView.Adapter<JoinRequestAdapter.JoinRequestViewHolder>{
    ArrayList<MessageProfileModel>models;
    OnItemEventListener listener;
    public JoinRequestAdapter(ArrayList<MessageProfileModel> messages,OnItemEventListener itemClickListener){
        this.models = new ArrayList<>(messages);
        this.listener = itemClickListener;
    }

    public interface OnItemEventListener{
        void onAccept(MessageProfileModel message);
        void onDecline(MessageProfileModel message);
    }

    @NonNull
    @Override
    public JoinRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JoinRequestViewHolder(SingleJoinRequestItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull JoinRequestViewHolder holder, int position) {
        holder.setUpData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class JoinRequestViewHolder extends RecyclerView.ViewHolder{
        SingleJoinRequestItemBinding binding;
        public void setUpData(MessageProfileModel message){
            ProfileItem profileItem = message.getProfileItem();
            Glide.with(binding.profile).load(profileItem.getContent().get(ProfileItem.PROFILE_PICTURE)).placeholder(R.drawable.person).into(binding.profile);
            binding.name.setText(profileItem.getContent().get(ProfileItem.NAME));
            binding.vip.setText(profileItem.getContent().get(ProfileItem.VIP));
            binding.acceptButton.setOnClickListener(v -> listener.onAccept(message));
            binding.declineButton.setOnClickListener(v -> {
                listener.onDecline(message);
                int index = models.indexOf(message);
                models.remove(index);
                notifyItemRemoved(index);
            });
        }

        public JoinRequestViewHolder(@NonNull SingleJoinRequestItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
