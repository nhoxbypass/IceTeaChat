package com.gdg.firebase.iceteachat.ui.chat;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gdg.firebase.iceteachat.R;
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
    }

    public void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_account_circle_black_36dp)
                .into(view);
    }
}