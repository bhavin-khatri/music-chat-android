package com.example.musicchatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicchatapp.Models.Message;
import com.example.musicchatapp.databinding.ItemRecieveBinding;
import com.example.musicchatapp.databinding.ItemSendBinding;
import com.example.musicchatapp.R;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Message> messages;
    final int ITEM_SENT='1';
    final int ITEM_RECEIVE='2';
    String senderRoom;
    String receiverRoom;
    public MessagesAdapter(Context context, ArrayList<Message> messages,String senderRoom, String receiverRoom){
        this.context=context;
        this.messages=messages;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SENT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.item_send,parent,false);
            return new SendViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.item_recieve,parent,false);
            return new RecieveViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message=messages.get(position);
        int reactions[]=new int[]{
                R.drawable.likeicon,
                R.drawable.loveicon,
                R.drawable.laughicon,
                R.drawable.wowicon,
                R.drawable.sadicon,
                R.drawable.angryicon
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass()==SendViewHolder.class)
            {
                SendViewHolder viewHolder=(SendViewHolder)holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            }else
            {
                RecieveViewHolder viewHolder=(RecieveViewHolder)holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            message.setFeeling(pos);
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);
            return true; // true is closing popup, false is requesting a new selection
        });
        if(holder.getClass()==SendViewHolder.class)
        {
            SendViewHolder viewHolder=(SendViewHolder)holder;
            if(message.getMessage().equals("photo"))
            {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.imageholder)
                         .into(viewHolder.binding.image);

            }
            viewHolder.binding.message.setText(message.getMessage());
            if(message.getFeeling()>=0)
            {
                viewHolder.binding.feeling.setImageResource(reactions[(int) message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else
            {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });
        }
        else
        {
            RecieveViewHolder viewHolder=(RecieveViewHolder)holder;
            if(message.getMessage().equals("photo"))
            {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.imageholder)
                        .into(viewHolder.binding.image);

            }
            viewHolder.binding.message.setText(message.getMessage());
            if(message.getFeeling()>=0)
            {
               viewHolder.binding.feeling.setImageResource(reactions[(int) message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else
            {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message=messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId()))
        {
            return ITEM_SENT;
        }
        else
        {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder{
        ItemSendBinding binding;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemSendBinding.bind(itemView);
        }
    }
    public class RecieveViewHolder extends RecyclerView.ViewHolder{
        ItemRecieveBinding binding;
        public RecieveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemRecieveBinding.bind(itemView);
        }
    }
}
