package com.probashiincltd.probashilive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.InboxItemLeftBinding;
import com.probashiincltd.probashilive.databinding.InboxItemRightBinding;

import org.jivesoftware.smack.packet.Message;

import java.util.LinkedList;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder>{
    LinkedList<Message> messages;
    Context context;
    public InboxAdapter (Context context, LinkedList<Message> messages){
        this.context = context;
        this.messages = messages;
    }

    public void addNewMessage(Message message) {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            return new ViewHolder(InboxItemRightBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false).getRoot());
        }else {
            return new ViewHolder(InboxItemLeftBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false).getRoot());

        }

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(CM.getConnection().getUser().asBareJid().toString().equals(message.getFrom().asBareJid().toString())){
            return 0;
        }else {
            return 1;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setUpData(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        View vi;
        Message message;
        public void setUpData(Message message){
            this.message = message;

        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
