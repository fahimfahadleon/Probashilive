package com.probashiincltd.probashilive.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.databinding.SingleGiftTitleBinding;

import java.util.ArrayList;

public class GiftTitleAdapter extends RecyclerView.Adapter<GiftTitleAdapter.GiftTitleViewHolder> {
    Context context;
    ArrayList<String>title;
    OnItemClickListener listener;
    public static final String GIFT_TITLE_IMAGE = "emoji";
    public static final String GIFT_TITLE_LOTTIE = "lottie";
    public static final String GIFT_TITLE_SVGA = "svga";
    public GiftTitleAdapter(Context context,OnItemClickListener listener){
        this.context = context;
        title = new ArrayList<>();
        title.add(GIFT_TITLE_IMAGE);
        title.add(GIFT_TITLE_LOTTIE);
        title.add(GIFT_TITLE_SVGA);
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClicked(String s);
    }
    @NonNull
    @Override
    public GiftTitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GiftTitleViewHolder(SingleGiftTitleBinding.inflate((LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GiftTitleViewHolder holder, int position) {
        holder.setUpData(title.get(position));
    }

    @Override
    public int getItemCount() {
        return title.size();
    }


    public class GiftTitleViewHolder extends RecyclerView.ViewHolder{
        SingleGiftTitleBinding binding;
        public void setUpData(String title){
            binding.title.setText(title);
            switch (title) {
                case "emoji":
                    binding.title.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.emoji), null, null, null);
                    break;
                case "lottie":
                    binding.title.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.animation), null, null, null);
                    break;
                case "svga":
                    binding.title.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.animation_high), null, null, null);
                    break;
            }

            binding.getRoot().setOnClickListener(v -> listener.onItemClicked(title));
        }

        public GiftTitleViewHolder(@NonNull SingleGiftTitleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
