package com.gdg.firebase.iceteachat.ui.chat;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gdg.firebase.iceteachat.R;
import com.gdg.firebase.iceteachat.databinding.ItemPublicChatBinding;
import com.gdg.firebase.iceteachat.model.ChatMessage;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nhoxb on 10/27/2016.
 */

public class PublicItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemPublicChatBinding mBinding;
    private final PublicItemViewModel mViewModel;
    private CircleImageView imageView;

    public PublicItemViewHolder(View itemView) {
        super(itemView);
        mViewModel = new PublicItemViewModel();
        mBinding = ItemPublicChatBinding.bind(itemView);
        mBinding.setViewModel(mViewModel);
        imageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
    }

    public void bind(ChatMessage message) {
        mViewModel.bind(message);
        mViewModel.loadImage(imageView,message.getPhotoUrl());
    }
}