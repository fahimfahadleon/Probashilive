package com.probashiincltd.probashilive.adapter;



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

    public CommentAdapter(){
        this.models = new ArrayList<>();
    }

    public void addData(CommentModel commentModel){

        Log.e("addData","called");
        if(!models.contains(commentModel)){
            this.models.add(0,commentModel);
            new Handler(Looper.getMainLooper()).postDelayed(() -> notifyItemInserted(0),100);
        }

    }



    OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(CommentModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SingleCommentBinding binding = SingleCommentBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CommentViewHolder(binding);
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

    protected class CommentViewHolder extends RecyclerView.ViewHolder {
        SingleCommentBinding binding;

        protected void setUpData(CommentModel cm){

            Log.e("comment",cm.toString());
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
