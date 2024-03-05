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
    public static final int CONFERENCE_ADAPTER_TYPE_HOST = 0;
    public static final int CONFERENCE_ADAPTER_TYPE_AUDIENCE = 1;
    public static final int CONFERENCE_ADAPTER_TYPE_COMPETITOR = 2;
    int type;
    public ConferenceAdapter(int type){
        this.type = type;
        models = new ArrayList<>();
        ConferenceModel conferenceModel = new ConferenceModel();
        conferenceModel.setVip("");
        conferenceModel.setId("requestID");
        switch (type){
            case CONFERENCE_ADAPTER_TYPE_AUDIENCE:{
                conferenceModel.setName("Request");
                break;
            }case CONFERENCE_ADAPTER_TYPE_COMPETITOR:{
                conferenceModel.setName("Permission");
                break;
            } case CONFERENCE_ADAPTER_TYPE_HOST:{
                conferenceModel.setName("Invite");

                break;
            }
        }
        models.add(conferenceModel);

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
            if(cm.getId().equals("requestID")){
                switch (type) {
                    case CONFERENCE_ADAPTER_TYPE_HOST:
                        Glide.with(binding.profile).load(R.drawable.person_add).placeholder(R.drawable.person_add).into(binding.profile);
                        break;
                    case CONFERENCE_ADAPTER_TYPE_AUDIENCE:
                        Glide.with(binding.profile).load(R.drawable.person_add_request).placeholder(R.drawable.person_add_request).into(binding.profile);
                        break;
                    case CONFERENCE_ADAPTER_TYPE_COMPETITOR:
                        Glide.with(binding.profile).load(R.drawable.request_join).placeholder(R.drawable.request_join).into(binding.profile);
                        break;
                }
            }else {
                Glide.with(binding.profile).load(cm.getProfielPicture()).placeholder(R.drawable.person).into(binding.profile);
            }
            binding.getRoot().setOnClickListener(v -> listener.onClick(cm));
        }

        public ConferenceViewHolder(@NonNull SingleConferenceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
