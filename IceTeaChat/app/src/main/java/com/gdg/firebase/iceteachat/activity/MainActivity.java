package com.gdg.firebase.iceteachat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.gdg.firebase.iceteachat.helper.ReferenceURL;
import com.gdg.firebase.iceteachat.model.UserChat;
import com.gdg.firebase.iceteachat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_REF_LINK = "refLink";
    public static final String KEY_CHAT_TYPE = "type";
    //Declare variables
    Button publicChat;
    Firebase mFireBaseRef;
    UserChat currUserChat;
    private ChildEventListener mUsersListener;
    private Firebase mUsersRef;
    //List of all user object
    private List<UserChat> mUsersDataList;
    //List hold uid of all user
    private List<String> mUsersUidList;
    //UID of current user
    private String mCurrUserUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        toolbar.setTitle("IceTea Chat");
        setSupportActionBar(toolbar);

        //Set firebase context and reference
        Firebase.setAndroidContext(this);
        mFireBaseRef = new Firebase(ReferenceURL.FIREBASE_APP_URL);
        mUsersRef = new Firebase(ReferenceURL.FIREBASE_APP_URL).child(ReferenceURL.CHILD_USERS);

        //Set view
        publicChat = (Button) findViewById(R.id.public_chat_button);

        //FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Chức năng đang được nâng cấp", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Set auth date of user
        setAuthenticatedUser(mFireBaseRef.getAuth());
        // Initialize keys list
        mUsersUidList=new ArrayList<String>();
        mUsersDataList = new ArrayList<UserChat>();

        /*
        mAuthStateListener=new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        };

        // Register the authentication state listener
        mFireBaseRef.addAuthStateListener(mAuthStateListener);

        */

        //Tab Layout
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        //Khoi tao TabSpec
        TabHost.TabSpec tab1 = tabHost.newTabSpec("PRIVATE CHAT");
        tab1.setIndicator("PRIVATE CHAT");
        tab1.setContent(R.id.tab1);
        //Add vao tabHost
        tabHost.addTab(tab1);

        //tab 2 etc...
        TabHost.TabSpec tab2 = tabHost.newTabSpec("GROUP CHAT");
        tab2.setIndicator("GROUP CHAT");
        tab2.setContent(R.id.public_tab_layout);
        tabHost.addTab(tab2);

        //Navigate to public chat page
        publicChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navToPublicChat = new Intent(getBaseContext(), ChatActivity.class);
                navToPublicChat.putExtra(KEY_REF_LINK, ReferenceURL.FIREBASE_APP_URL + ReferenceURL.CHILD_PUBLIC_CHAT);
                navToPublicChat.putExtra(KEY_SENDER_NAME, currUserChat.getName());
                navToPublicChat.putExtra(KEY_CHAT_TYPE, ChatActivity.PUBLIC_CHAT);
                startActivity(navToPublicChat);
            }
        });

    }

    private void setAuthenticatedUser(AuthData authData) {
        //AuthData auth = mFireBaseRef.getAuth();
        if (authData != null) {
            currUserChat = new UserChat("Registered user", authData.getProviderData().get(ReferenceURL.EMAIL_PATH).toString(), "password", authData.getProviderData().get(ReferenceURL.PHOTO_URL).toString(),authData.getUid());
            mCurrUserUid = authData.getUid();
        }
        else {
            Random r = new Random();
            currUserChat = new UserChat("Anonymous" + r.nextInt(1000) + 123, "ano@gmail.com", "123", "http://www.caprisunandomd.com/wp-content/uploads/2015/09/no-avatar.jpg","null");
            mCurrUserUid = "null";
        }

        //Query data of other user
        queryAllChatUsers();
    }

    private void queryAllChatUsers()
    {
        //Firebase is realtime sync, so child with add when you ref to Firebase
        //You can go to your firebase workspace app and refresh and you will see
        mUsersListener = mUsersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Check snapshot exists
                if (dataSnapshot.exists()) {
                    //Get user uid (getKey mean get name of this sub-folder in firebase)
                    String userUid = dataSnapshot.getKey();

                    //Distinguish current user from the others
                    if (!userUid.equals(mCurrUserUid))
                    {
                        //TODO: Get other user data
                        //Get recipient user name
                        final UserChat otherUser = dataSnapshot.getValue(UserChat.class);

                        //make sure that uid not null (it can cause app crash in some case)
                        if(userUid != null)
                        {
                            otherUser.setUid(userUid);
                            mUsersUidList.add(userUid); //Add other user UID to list
                        }

                        //Add other user to list
                        mUsersDataList.add(otherUser);

                        //TODO: Create a dynamic button to show other user, Click one of them and chat with this user
                        LinearLayout privateChatLayout = (LinearLayout) findViewById(R.id.private_tab_layout);
                        Button dynamicButton = new Button(getBaseContext());
                        //Setting the button
                        dynamicButton.setText(otherUser.getName());
                        dynamicButton.setTextColor(Color.BLACK);
                        dynamicButton.setBackgroundColor(Color.WHITE);
                        //Layout params (set the width match_parent and height 80)
                        LinearLayout.LayoutParams layoutParams = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
                        layoutParams.setMargins(8, 8, 8, 8); // left, top, right, bottom
                        dynamicButton.setLayoutParams(layoutParams);
                        //Add button to layout
                        privateChatLayout.addView(dynamicButton);

                        //Set onClick Handler
                        dynamicButton.setOnClickListener(new View.OnClickListener() {
                            String mail = otherUser.getEmail();

                            @Override
                            public void onClick(View v) {
                                String path = currUserChat.getChatRef(mail);

                                Intent navToPrivateChat = new Intent(getBaseContext(), ChatActivity.class);
                                navToPrivateChat.putExtra(KEY_REF_LINK, ReferenceURL.FIREBASE_APP_URL + ReferenceURL.CHILD_PRIVATE_CHAT + "/" + path);
                                navToPrivateChat.putExtra(KEY_SENDER_NAME, currUserChat.getName());
                                navToPrivateChat.putExtra(KEY_CHAT_TYPE, ChatActivity.PRIVATE_CHAT);
                                startActivity(navToPrivateChat);

                            }
                        });
                    }
                    else
                    {
                        //TODO: Current user, do nothing
                        UserChat currentUser = dataSnapshot.getValue(UserChat.class);
                        currUserChat.setPassword(currentUser.getPassword());
                        currUserChat.setName(currentUser.getName());
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists())
                {
                    String userUid = dataSnapshot.getKey();
                    if(!userUid.equals(mCurrUserUid)) {
                        UserChat user = dataSnapshot.getValue(UserChat.class);

                        //Add recipient uid
                        user.setUid(userUid);

                        //Add current user (or sender) info
                        //.setCurrentUserEmail(mCurrentUserEmail); //email
                        //user.setCurrentUserUid(mCurrentUserUid);//uid
                        //int index = mUsersUidList.indexOf(userUid);
                        //Toast.makeText(MainActivity.this, user.getName() + " " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            if (mFireBaseRef.getAuth() != null) {
                mFireBaseRef.unauth();
            }

            //Back to login screen
            Intent navBackToLogin = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(navBackToLogin);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
