package com.probashiincltd.probashilive.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.databinding.SingleConferenceItemBinding;
import com.probashiincltd.probashilive.models.ConferenceModel;

import java.util.ArrayList;

public class ConferenceAdapter extends RecyclerView.Adapter<ConferenceAdapter.ConferenceViewHolder> {
    ArrayList<ConferenceModel>models;
    public ConferenceAdapter(){
        models = new ArrayList<>();
    }
    OnConferenceItemClickListener listener;



    public interface OnConferenceItemClickListener{
        void onClick(ConferenceModel cm);
    }

    public void setOnConferenceClickListener(OnConferenceItemClickListener listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public ConferenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SingleConferenceItemBinding binding = SingleConferenceItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ConferenceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConferenceViewHolder holder, int position) {
        holder.setUpData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class ConferenceViewHolder extends RecyclerView.ViewHolder{
        SingleConferenceItemBinding binding;
        public void setUpData(ConferenceModel cm){
            binding.name.setText(cm.getName());
            binding.vip.setVisibility(cm.getVip().isEmpty()? View.GONE:View.VISIBLE);
            Glide.with(binding.profile).load(cm.getProfielPicture()).placeholder(R.drawable.person).into(binding.profile);
            binding.getRoot().setOnClickListener(v -> listener.onClick(cm));
        }

        public ConferenceViewHolder(@NonNull SingleConferenceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
