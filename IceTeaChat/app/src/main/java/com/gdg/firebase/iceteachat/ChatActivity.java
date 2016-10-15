package com.gdg.firebase.iceteachat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.gdg.firebase.iceteachat.Helper.ReferenceURL;
import com.gdg.firebase.iceteachat.Model.ChatMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


//TODO: This class use to display conversation of private chat or public chat
//TODO: Receive the Firebase Reference link of the prev Activity
public class ChatActivity extends Activity{

    //Declare variables
    private Firebase mFirebaseRef;
    FirebaseListAdapter<ChatMessage> mListAdapter;
    String senderName;
    String refLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Get information from user, and link to the recipient
        senderName = getIntent().getStringExtra("senderName");
        refLink = getIntent().getStringExtra("refLink");

        //Set view
        final EditText textEdit = (EditText)this.findViewById(R.id.text_edit);
        ImageButton sendButton = (ImageButton)this.findViewById(R.id.send_button);
        final ListView listView = (ListView) this.findViewById(android.R.id.list);

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

        //Message Adapter, can be replace with recyclerView
        mListAdapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                android.R.layout.two_line_list_item, mFirebaseRef) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getSender());
                ((TextView)v.findViewById(android.R.id.text2)).setText(model.getText());
            }
        };
        listView.setAdapter(mListAdapter);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();
    }


}