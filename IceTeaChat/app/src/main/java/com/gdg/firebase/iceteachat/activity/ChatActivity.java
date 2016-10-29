package com.gdg.firebase.iceteachat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.gdg.firebase.iceteachat.helper.ReferenceURL;
import com.gdg.firebase.iceteachat.model.ChatMessage;
import com.gdg.firebase.iceteachat.R;
import com.gdg.firebase.iceteachat.ui.chat.PrivateChatAdapter;
import com.gdg.firebase.iceteachat.ui.chat.PublicChatAdapter;
import com.gdg.firebase.iceteachat.ui.chat.PublicItemViewHolder;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


//TODO: This class use to display conversation of private chat or public chat
//TODO: Receive the Firebase Reference link of the prev Activity
public class ChatActivity extends Activity{

    public static final int PUBLIC_CHAT = 0;
    public static final int PRIVATE_CHAT = 1;

    //Declare variables
    private Firebase mFirebaseRef;

    FirebaseRecyclerAdapter<ChatMessage,RecyclerView.ViewHolder> mFirebaseRecyclerAdapter;
    String senderName;
    String refLink;
    RecyclerView recyclerView;
    EditText textEdit;
    LinearLayoutManager mLinearLayoutManager;
    int type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Get information from user, and link to the recipient
        Intent intent = getIntent();
        senderName = intent.getStringExtra(MainActivity.KEY_SENDER_NAME);
        refLink = intent.getStringExtra(MainActivity.KEY_REF_LINK);
        type = intent.getIntExtra(MainActivity.KEY_CHAT_TYPE, PUBLIC_CHAT);

        //Set view
        textEdit = (EditText)this.findViewById(R.id.text_edit);
        ImageButton sendButton = (ImageButton)this.findViewById(R.id.send_button);
        recyclerView = (RecyclerView) this.findViewById(R.id.rv_chat);

        //Set firebase Reference
        mFirebaseRef = new Firebase(refLink);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get message input
                String text = textEdit.getText().toString();

                //Map to an Object
                Map<String, Object> values = new HashMap<>();

                //Consider if auth user or anonymous user
                if(mFirebaseRef.getAuth() != null)
                {
                    //Auth user, put their name or email to firebase
                    if (!senderName.equals("") || senderName != null)
                    {
                        values.put("sender", senderName);
                    }
                    else
                        values.put("sender", mFirebaseRef.getAuth().getProviderData().get(ReferenceURL.EMAIL_PATH));
                }
                else
                {
                    //Trial user
                    if (!senderName.equals("") || senderName != null)
                    {
                        Random r = new Random();
                        senderName = "Anon" + (r.nextInt(1000) + 123);
                    }

                    values.put("sender",senderName);
                }

                values.put("text", text);

                //push obj data len firebase
                mFirebaseRef.push().setValue(values);

                //xoa khung nhap
                textEdit.setText("");
            }
        });


        //Set adapter
        if (type == PUBLIC_CHAT)
        {
            mFirebaseRecyclerAdapter = new PublicChatAdapter(ChatMessage.class, mFirebaseRef);
        }
        else if (type == PRIVATE_CHAT)
        {
            mFirebaseRecyclerAdapter = new PrivateChatAdapter(senderName, ChatMessage.class, mFirebaseRef);
        }

        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mFirebaseRecyclerAdapter);


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseRecyclerAdapter.cleanup();
    }


}