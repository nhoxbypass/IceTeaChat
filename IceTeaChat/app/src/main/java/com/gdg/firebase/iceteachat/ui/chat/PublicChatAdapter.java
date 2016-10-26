package com.gdg.firebase.iceteachat.ui.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.gdg.firebase.iceteachat.R;
import com.gdg.firebase.iceteachat.model.ChatMessage;

/**
 * Created by nhoxb on 10/27/2016.
 */
public class PublicChatAdapter extends FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder> {


    public PublicChatAdapter(Class<ChatMessage> modelClass, int modelLayout, Class<RecyclerView.ViewHolder> viewHolderClass, Firebase ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    public PublicChatAdapter(Class<ChatMessage> modelClass, Firebase ref) {
        super(modelClass, R.layout.item_public_chat, null, ref);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);
        return new PublicItemViewHolder(view);
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, ChatMessage chatMessage, int i) {
        ((PublicItemViewHolder)viewHolder).bind(chatMessage);
    }
}
