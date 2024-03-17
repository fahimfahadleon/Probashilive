package com.probashiincltd.probashilive.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.connectionutils.RosterHandler;
import com.probashiincltd.probashilive.databinding.SingleInviteFriendBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;

import java.util.ArrayList;

public class OnlineFriendsAdapter extends RecyclerView.Adapter<OnlineFriendsAdapter.OnlineFriendsViewHolder> {
    ArrayList<ProfileItem>models;
    OnItemClickListener listener;
    public OnlineFriendsAdapter(OnItemClickListener listener){
        this.listener = listener;


        Log.e("items",new ArrayList<>(RosterHandler.getRosterHandler().getOnlineFriends()).toString());
        Log.e("items",Functions.removeDuplicateProfiles(new ArrayList<>(RosterHandler.getRosterHandler().getOnlineFriends())).toString());

        models = Functions.removeDuplicateProfiles(new ArrayList<>(RosterHandler.getRosterHandler().getOnlineFriends()));
    }
    public interface OnItemClickListener{
        void onItemClick(ProfileItem profileItem);
    }
    @NonNull
    @Override
    public OnlineFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SingleInviteFriendBinding binding = SingleInviteFriendBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new OnlineFriendsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineFriendsViewHolder holder, int position) {
        holder.setUpData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class OnlineFriendsViewHolder extends RecyclerView.ViewHolder{
        SingleInviteFriendBinding binding;
        public void setUpData(ProfileItem profileItem){
            binding.name.setText(profileItem.getContent().get(ProfileItem.NAME));
            binding.vip.setText(profileItem.getContent().get(ProfileItem.VIP));
            Glide.with(binding.profile).load(profileItem.getContent().get(ProfileItem.PROFILE_PICTURE)).placeholder(R.drawable.person).into(binding.profile);
            binding.inviteButton.setOnClickListener(v -> listener.onItemClick(profileItem));
        }

        public OnlineFriendsViewHolder(@NonNull SingleInviteFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
