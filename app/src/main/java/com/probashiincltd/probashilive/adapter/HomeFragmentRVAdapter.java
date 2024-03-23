package com.probashiincltd.probashilive.adapter;

import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_VIDEO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.databinding.SingleHomeItemBinding;
import com.probashiincltd.probashilive.pubsubItems.LiveItem;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class HomeFragmentRVAdapter extends RecyclerView.Adapter<HomeFragmentRVAdapter.HomeFragmentViewHolder> {
    ArrayList<LiveItem> models;
    Context context;
    public HomeFragmentRVAdapter(Context context){
        this.context = context;
        this.models = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearData(){
        models.clear();
        notifyDataSetChanged();
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
        return new HomeFragmentViewHolder(SingleHomeItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFragmentViewHolder holder, int position) {
        holder.setUpData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class HomeFragmentViewHolder extends RecyclerView.ViewHolder{
        SingleHomeItemBinding binding;
        public void setUpData(LiveItem model){
            if(model!=null){
                binding.name.setText(model.getContent().get(LiveItem.NAME));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'");
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(model.getContent().get(LiveItem.STARTED_AT), formatter);
                ZonedDateTime newYorkDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm a");
                String formattedDateTime = newYorkDateTime.format(formatter1);
                binding.country.setText(model.getContent().get(LiveItem.COUNTRY));
                binding.type.setImageDrawable(ContextCompat.getDrawable(context, Objects.equals(model.getContent().get(LiveItem.TYPE), LIVE_TYPE_VIDEO) ?R.drawable.video_camera:R.drawable.microphone));
                binding.startedat.setText(context.getString(R.string.from,formattedDateTime));
                binding.watching.setText(context.getString(R.string.viewers,model.getContent().get("viewers")));
                Glide.with(binding.profile).load(model.getContent().get(LiveItem.PROFILE_IMAGE)).into(binding.profile);
                binding.profile.setClipToOutline(true);
                binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(model));
            }

        }
        public HomeFragmentViewHolder(SingleHomeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
