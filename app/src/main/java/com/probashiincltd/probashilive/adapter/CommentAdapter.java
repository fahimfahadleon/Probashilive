package com.probashiincltd.probashilive.adapter;


import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.databinding.SingleCommentBinding;
import com.probashiincltd.probashilive.models.CommentModel;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    ArrayList<CommentModel> models;
    public static final int COMMENT_ADAPTER_TYPE_HOST = 0;
    public static final int COMMENT_ADAPTER_TYPE_AUDIENCE = 1;
    public static final int COMMENT_ADAPTER_TYPE_COMPETITOR = 2;
    int type;

    public CommentAdapter(int cat){
        this.type = cat;
        this.models = new ArrayList<>();
    }

    public void addData(CommentModel commentModel){
        Log.e("addData","called");
        if(!models.contains(commentModel)){
            this.models.add(0,commentModel);
            new Handler(Looper.getMainLooper()).postDelayed(() -> notifyItemInserted(0),100);
            onItemInsertedListener.onItemInserted(commentModel);
        }

    }

    public interface OnItemInsertedListener{
        void onItemInserted(CommentModel commentModel);
    }


    OnItemClickListener onItemClickListener;
    OnItemInsertedListener onItemInsertedListener;

    public void setOnItemInsertedListener(OnItemInsertedListener onItemInsertedListener) {
        this.onItemInsertedListener = onItemInsertedListener;
    }

    public interface OnItemClickListener {
        void onItemClick(CommentModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(SingleCommentBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Log.e("onBindViewHolder","called");
        holder.setUpData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        SingleCommentBinding binding;

        public void setUpData(CommentModel cm){
            Log.e("comment",cm.toString());
            if(!cm.getContent().isEmpty()){
                binding.getRoot().setBackgroundResource(R.color.iconColor);
            }
            binding.vip.setText(cm.getVip().isEmpty()?"":cm.getVip());
            binding.comment.setText(cm.getComment());
            binding.name.setText(cm.getName());
            Glide.with(binding.profile).load(cm.getPp()).placeholder(R.drawable.person).into(binding.profile);
            binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(cm));
        }
        protected CommentViewHolder(SingleCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
