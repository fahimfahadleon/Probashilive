package com.probashiincltd.probashilive.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.probashiincltd.probashilive.databinding.SingleHomeItemBinding;
import com.probashiincltd.probashilive.pubsubItems.LiveItem;

import java.util.ArrayList;

public class HomeFragmentRVAdapter extends RecyclerView.Adapter<HomeFragmentRVAdapter.HomeFragmentViewHolder> {
    ArrayList<LiveItem> models;
    public HomeFragmentRVAdapter(){
        this.models = new ArrayList<>();
    }

    public void clearData(){
        int size = models.size();
        models.clear();
        notifyItemRangeRemoved(0,size);
    }



    public void addMoreData(ArrayList<LiveItem> model){
        int startPosition = models.size();
        models.addAll(model);
        notifyItemRangeInserted(startPosition, model.size());
    }
    OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(LiveItem model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    @NonNull
    @Override
    public HomeFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SingleHomeItemBinding binding = SingleHomeItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new HomeFragmentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFragmentViewHolder holder, int position) {
        holder.setUpData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class HomeFragmentViewHolder extends RecyclerView.ViewHolder{
        SingleHomeItemBinding binding;
        public void setUpData(LiveItem model){
            if(model!=null){
                binding.name.setText(model.getContent().get("name"));
                binding.startedat.setText(model.getContent().get("startedAt"));
                binding.watching.setText(model.getContent().get("viewers"));
                Glide.with(binding.profile).load(model.getContent().get("profile_image")).into(binding.profile);
                binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(model));
            }

        }
        public HomeFragmentViewHolder(SingleHomeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
