package com.probashiincltd.probashilive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.probashiincltd.probashilive.databinding.SingleChatItemBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.ChatItem;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    public ArrayList<ChatItem>chatItems;
    Context context;
    OnItemClickListener onItemClickListener;
    public ChatAdapter(ArrayList<ChatItem>chatItems, Context context,OnItemClickListener onItemClickListener){
        this.chatItems = chatItems;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }
    public void addItem(ChatItem chatItem){
        chatItems.add(0,chatItem);
        notifyItemInserted(0);
    }
    public void removeItem(int index){
        chatItems.remove(index);
        notifyItemRemoved(index);
    }

    public interface OnItemClickListener{
        void onItemClick(ChatItem chatItem);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(SingleChatItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setUpData(chatItems.get(position));
    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        SingleChatItemBinding binding;
        ChatItem chatItem;
        public void setUpData(ChatItem chatItem){
            this.chatItem = chatItem;
            binding.name.setText(chatItem.getName());
            Functions.loadImage(context,binding.profile,chatItem.getProfilePicture());
            binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(chatItem));
        }

        public ViewHolder(SingleChatItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
