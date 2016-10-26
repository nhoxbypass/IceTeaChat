package com.gdg.firebase.iceteachat.ui.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.gdg.firebase.iceteachat.R;
import com.gdg.firebase.iceteachat.model.ChatMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nhoxb on 10/27/2016.
 */
public class PrivateChatAdapter extends FirebaseRecyclerAdapter<ChatMessage,RecyclerView.ViewHolder> {
    private static final int SENDER = 0;
    private static final int RECEIVER = 1;
    private String mSender;
    public PrivateChatAdapter(String sender,Class<ChatMessage> modelClass, int modelLayout, Class<RecyclerView.ViewHolder> viewHolderClass, Firebase ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mSender = sender;
    }

    public PrivateChatAdapter(String sender, Class<ChatMessage> modelClass, Firebase ref)
    {
        super(modelClass,R.layout.item_private_sender,null,ref);
        this.mSender = sender;
    }


    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getSender().equals(mSender))
        {
            return SENDER;
        }
        else
            return RECEIVER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewGroup view;
        switch (viewType)
        {
            case SENDER:
                view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_private_sender, parent, false);
                return new ViewHolderSender(view);
            case RECEIVER:
                view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_private_receiver, parent, false);
                return new ViewHolderReceiver(view);
        }
        return null;
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, ChatMessage chatMessage, int i) {
        if (chatMessage.getSender().equals(mSender))
        {
            //Is sender message
            ViewHolderSender viewHolderSender = (ViewHolderSender) viewHolder;
            viewHolderSender.textView.setText(chatMessage.getText());
        }
        else
        {
            ViewHolderReceiver viewHolderReceiver = (ViewHolderReceiver) viewHolder;
            viewHolderReceiver.textView.setText(chatMessage.getText());
        }
    }

    public class ViewHolderSender extends RecyclerView.ViewHolder {

        public TextView textView;

        public ViewHolderSender(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.sender_message);
        }
    }

    public class ViewHolderReceiver extends RecyclerView.ViewHolder {

        public TextView textView;

        public ViewHolderReceiver(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.receiver_message);
        }
    }
}
