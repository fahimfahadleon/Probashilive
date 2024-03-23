package com.probashiincltd.probashilive.adapter;

import static com.probashiincltd.probashilive.functions.Functions.removeDuplicateProfiles;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.probashiincltd.probashilive.databinding.SingleFollowerOrFollowingBinding;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;

import java.util.ArrayList;

public class FollowerOrFollowingAdapter extends RecyclerView.Adapter<FollowerOrFollowingAdapter.FollowerOrFollowingViewHolder>{
    ArrayList<ProfileItem>models;
    OnItemClickListener listener;
    boolean isFollowing;
    public FollowerOrFollowingAdapter(boolean isFollowing,ArrayList<ProfileItem> items,OnItemClickListener listener){
        this.listener = listener;
        models = removeDuplicateProfiles(items);
        this.isFollowing = isFollowing;
    }




    public interface OnItemClickListener{
        void onItemClick(ProfileItem profileItem);
    }
    @NonNull
    @Override
    public FollowerOrFollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowerOrFollowingViewHolder(SingleFollowerOrFollowingBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerOrFollowingViewHolder holder, int position) {
holder.setUpData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class FollowerOrFollowingViewHolder extends RecyclerView.ViewHolder{
        SingleFollowerOrFollowingBinding binding;
        public void setUpData(ProfileItem item){
            binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        }
        public FollowerOrFollowingViewHolder(SingleFollowerOrFollowingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
