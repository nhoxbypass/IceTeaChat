package com.gdg.firebase.iceteachat.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gdg.firebase.iceteachat.R;
import com.gdg.firebase.iceteachat.helper.ReferenceURL;
import com.gdg.firebase.iceteachat.model.UserChat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhoxb on 10/27/2016.
 */
public class UserChatAdapter extends ArrayAdapter<UserChat> {

    private List<UserChat> mUserChatList;
    private int mLayoutResId;
    public UserChatAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
        mLayoutResId = layoutResId;
        mUserChatList = new ArrayList<>();
    }

    @Override
    public UserChat getItem(int position) {
        return mUserChatList.get(position);
    }

    @Override
    public void add(UserChat userChat) {
        if (mUserChatList == null)
        {
            mUserChatList = new ArrayList<>();
        }

        mUserChatList.add(userChat);
        notifyDataSetChanged();
    }

    public void changeUser(int index, UserChat user) {

        // Handle change on each user and notify change
        if (index < mUserChatList.size() && index >= 0) {
            mUserChatList.set(index, user);
            notifyDataSetChanged();
        }
        else
            Log.e("FIREBASE","java.lang.ArrayIndexOutOfBoundsException - Change user");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserChat userChat = mUserChatList.get(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(mLayoutResId, parent, false);
        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_username);
        TextView tvConnected = (TextView) convertView.findViewById(R.id.tv_connect_status);
        // Populate the data into the template view using the data object
        tvName.setText(userChat.getName());
        // Set presence status
        tvConnected.setText(userChat.getConnection());

        // Set presence text color
        if(userChat.getConnection().equals(ReferenceURL.KEY_ONLINE)) {
            // Green color
            tvConnected.setTextColor(ContextCompat.getColor(getContext(),R.color.googleGreen));
        }else {
            // Red color
            tvConnected.setTextColor(ContextCompat.getColor(getContext(),R.color.googleRed));
        }
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        if (mUserChatList != null)
            return mUserChatList.size();
        else
            return 0;
    }
}
