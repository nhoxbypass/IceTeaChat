package com.gdg.firebase.iceteachat.ui.main;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gdg.firebase.iceteachat.R;
import com.gdg.firebase.iceteachat.activity.ChatActivity;
import com.gdg.firebase.iceteachat.activity.MainActivity;
import com.gdg.firebase.iceteachat.helper.ReferenceURL;
import com.gdg.firebase.iceteachat.model.UserChat;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";
    public static final int KEY_PUBLIC_CHAT = 0;
    public static final int KEY_ANDROID_CHAT = 1;
    CardView cardView;
    TextView textView;
    ImageView imageView;
    UserChat mCurrUserChat;
    int mType;

    public PublicChatFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PublicChatFragment newInstance(UserChat userChat, int type) {
        PublicChatFragment fragment = new PublicChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, userChat);
        args.putInt(ARG_PARAM2, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrUserChat = (UserChat) getArguments().getSerializable(ARG_PARAM1);
            mType = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_public_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        cardView = (CardView) view.findViewById(R.id.cardview);
        textView = (TextView)view.findViewById(R.id.tv_public_chat);
        imageView = (ImageView) view.findViewById(R.id.iv_public_chat);

        if (mType == KEY_ANDROID_CHAT)
        {
            textView.setText("ANDROID ROOM");
            Glide.with(getActivity().getApplicationContext())
                    .load(R.drawable.android_chat)
                    .into(imageView);
        }
        else if (mType == KEY_PUBLIC_CHAT)
        {
            textView.setText("PUBLIC ROOM");
            Glide.with(getActivity().getApplicationContext())
                    .load(R.drawable.public_chat)
                    .into(imageView);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String refLink =  ReferenceURL.FIREBASE_APP_URL + (mType == KEY_PUBLIC_CHAT ? ReferenceURL.CHILD_PUBLIC_CHAT : ReferenceURL.CHILD_ANDROID_CHAT);

                Intent navToPublicChat = new Intent(getActivity(), ChatActivity.class);
                navToPublicChat.putExtra(MainActivity.KEY_REF_LINK, refLink);
                navToPublicChat.putExtra(MainActivity.KEY_SENDER_NAME, mCurrUserChat.getName());
                navToPublicChat.putExtra(MainActivity.KEY_CHAT_TYPE, ChatActivity.PUBLIC_CHAT);
                getActivity().startActivity(navToPublicChat);
            }
        });
    }
}
