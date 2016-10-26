package com.gdg.firebase.iceteachat.ui.chat;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;

import com.gdg.firebase.iceteachat.model.ChatMessage;
import com.gdg.firebase.iceteachat.ui.base.BaseViewModel;

/**
 * Created by nhoxb on 10/26/2016.
 */
public class PublicItemViewModel extends BaseViewModel {

    public ObservableField<String> avatar = new ObservableField<>();

    public ObservableField<String> name = new ObservableField<>();

    public ObservableField<String> text = new ObservableField<>();

    public PublicItemViewModel() {
    }

    public void bind(ChatMessage message) {
        name.set(message.getSender());
        text.set(message.getText());
        avatar.set(message.getPhotoUrl());
    }
}