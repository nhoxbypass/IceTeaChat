package com.gdg.firebase.iceteachat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.gdg.firebase.iceteachat.helper.ReferenceURL;
import com.gdg.firebase.iceteachat.model.UserChat;
import com.gdg.firebase.iceteachat.R;
import com.gdg.firebase.iceteachat.ui.main.PublicChatFragment;
import com.gdg.firebase.iceteachat.ui.main.UserChatAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_REF_LINK = "refLink";
    public static final String KEY_CHAT_TYPE = "type";
    //Declare variables
    Firebase mFireBaseRef;
    /* Updating connection status */
    Firebase connectionsStatusRef;
    UserChat currUserChat;
    private ListView mListview;
    private ChildEventListener mUsersListener;
    /* Listen for user presence */
    private ValueEventListener mConnectedListener;
    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;
    private Firebase mUsersRef;
    AuthData mAuthData;
    //List hold uid of all user
    private List<String> mUsersUidList;
    //UID of current user
    private String mCurrUserUid;
    private UserChatAdapter mUserChatAdapter;
    Toolbar mToolbar;
    TabHost mTabHost;
    FloatingActionButton fab;
    int mLoginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init
        getIntent().getIntExtra(LoginActivity.KEY_LOGIN_TYPE,LoginActivity.LOGIN_NORMAL);
        // Initialize keys list
        mUsersUidList=new ArrayList<String>();

        //Findview
        mListview = (ListView) findViewById(R.id.lv_list_user);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mTabHost = (TabHost) findViewById(R.id.tabHost);

        //Set action bar
        mToolbar.showOverflowMenu();
        mToolbar.setTitle("IceTea Chat");
        setSupportActionBar(mToolbar);

        //Set firebase context and reference
        Firebase.setAndroidContext(this);
        mFireBaseRef = new Firebase(ReferenceURL.FIREBASE_APP_URL);
        mUsersRef = new Firebase(ReferenceURL.FIREBASE_APP_URL).child(ReferenceURL.CHILD_USERS);


        //FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Chức năng đang được nâng cấp", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Tab Layout
        mTabHost.setup();

        //Khoi tao TabSpec
        TabHost.TabSpec tab1 = mTabHost.newTabSpec("PRIVATE CHAT");
        tab1.setIndicator("", ContextCompat.getDrawable(MainActivity.this, R.drawable.message_text));
        tab1.setContent(R.id.private_tab_layout);
        //Add vao tabHost
        mTabHost.addTab(tab1);

        //tab 2 etc...
        TabHost.TabSpec tab2 = mTabHost.newTabSpec("GROUP CHAT");
        tab2.setIndicator("", ContextCompat.getDrawable(MainActivity.this, R.drawable.message_group_text));
        tab2.setContent(R.id.public_tab_layout);
        mTabHost.addTab(tab2);


        //Set adapter
        mUserChatAdapter = new UserChatAdapter(MainActivity.this,R.layout.item_user_chat);
        mListview.setAdapter(mUserChatAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mail = mUserChatAdapter.getItem(position).getEmail();
                String path = currUserChat.getChatRef(mail);

                Intent navToPrivateChat = new Intent(getBaseContext(), ChatActivity.class);
                navToPrivateChat.putExtra(KEY_REF_LINK, ReferenceURL.FIREBASE_APP_URL + ReferenceURL.CHILD_PRIVATE_CHAT + "/" + path);
                navToPrivateChat.putExtra(KEY_SENDER_NAME, currUserChat.getName());
                navToPrivateChat.putExtra(KEY_CHAT_TYPE, ChatActivity.PRIVATE_CHAT);
                startActivity(navToPrivateChat);
            }
        });


        //Set auth date of user
        setAuthenticatedUser(mFireBaseRef.getAuth());



        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        };

        // Register the authentication state listener
        mFireBaseRef.addAuthStateListener(mAuthStateListener);







    }

    private void setAuthenticatedUser(AuthData authData) {
        mAuthData = authData;

        if (authData != null)
        {
            /* User auth has not expire yet */
            mCurrUserUid = authData.getUid();
            currUserChat = new UserChat("Registered user", authData.getProviderData().get(ReferenceURL.EMAIL_PATH).toString(), "password", authData.getProviderData().get(ReferenceURL.PHOTO_URL).toString(),ReferenceURL.KEY_ONLINE,mCurrUserUid);
        }
        else if (mLoginType == LoginActivity.LOGIN_ANNONYMOUS)
        {
            //Must check for anonymous login or not -> nav back to login screen
            Random r = new Random();
            int randNumber = r.nextInt(1000) + 123;
            mCurrUserUid = randNumber + "";
            currUserChat = new UserChat("Anonymous" + randNumber, "ano@gmail.com", "123", "http://www.caprisunandomd.com/wp-content/uploads/2015/09/no-avatar.jpg",ReferenceURL.KEY_ONLINE,mCurrUserUid);

        }
        else
        {
            // Token expires or user log out
            // So show logIn screen to reinitiate the token
            Toast.makeText(MainActivity.this, "Phiên bản đăng nhập đã hết hạn\n vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            navigateBackToLogin();
        }

        //Query data of other user
        queryAllChatUsers();
    }

    private void queryAllChatUsers()
    {
        // Store current user status as online
        connectionsStatusRef= mUsersRef.child(mCurrUserUid).child(ReferenceURL.CHILD_CONNECTION);
        connectionsStatusRef.setValue(ReferenceURL.KEY_ONLINE);
        //Firebase is realtime sync, so child with add when you ref to Firebase
        //You can go to your firebase workspace app and refresh and you will see
        mUsersListener = mUsersRef.limitToFirst(50).addChildEventListener(new ChildEventListener() {
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
                        //Work around!
                        if (!mUsersUidList.contains(userUid)) {
                            UserChat otherUser = dataSnapshot.getValue(UserChat.class);

                            //make sure that uid not null (it can cause app crash in some case)
                            if (userUid != null) {
                                otherUser.setUid(userUid);
                                mUsersUidList.add(userUid); //Add other user UID to list
                            }

                            //Add other user to list
                            mUserChatAdapter.add(otherUser);
                        }

                        //TODO: Create a dynamic button to show other user, Click one of them and chat with this user
                        /*
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
                        */
                    }
                    else
                    {
                        //TODO: Current user, do nothing
                        UserChat currentUser = dataSnapshot.getValue(UserChat.class);
                        currUserChat.setPassword(currentUser.getPassword());
                        currUserChat.setName(currentUser.getName());
                        currUserChat.setPhotoUrl(currentUser.getPhotoUrl());


                        inflateFragment(R.id.public_chat_placeholder, currUserChat, PublicChatFragment.KEY_PUBLIC_CHAT);
                        inflateFragment(R.id.android_chat_placeholder, currUserChat, PublicChatFragment.KEY_ANDROID_CHAT);
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists())
                {
                    String userUid = dataSnapshot.getKey();
                    if(!userUid.equals(mCurrUserUid))
                    {
                        UserChat user = dataSnapshot.getValue(UserChat.class);

                        //Add recipient uid
                        user.setUid(userUid);

                        //Add current user (or sender) info
                        //.setCurrentUserEmail(mCurrentUserEmail); //email
                        //user.setCurrentUserUid(mCurrentUserUid);//uid
                        int index = mUsersUidList.indexOf(userUid);
                        Log.e("TAG", "change index "+index);
                        mUserChatAdapter.changeUser(index, user);
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



        // Indication of connection status
        mConnectedListener = connectionsStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String connected = dataSnapshot.getValue().toString();
                if (connected.equals(ReferenceURL.KEY_ONLINE)) {
                    // When this device disconnects, remove it
                    connectionsStatusRef.onDisconnect().setValue(ReferenceURL.KEY_OFFLINE);
                    Log.i("Firebase","Connected to Firebase");

                }
                else
                {
                    Log.i("Firebase","Disconnected from Firebase");

                }

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
                connectionsStatusRef.setValue(ReferenceURL.KEY_OFFLINE);
                /* Update authenticated user and show login screen */
                setAuthenticatedUser(null);
            }

            //Back to login screen
            Intent navBackToLogin = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(navBackToLogin);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        // If changing configurations, stop tracking firebase session.
        mFireBaseRef.removeAuthStateListener(mAuthStateListener);

        mUsersUidList.clear();

        // Stop all listeners
        // Make sure to check if they have been initialized
        if(mUsersListener != null) {
            mUsersRef.removeEventListener(mUsersListener);
        }
        if(mConnectedListener!=null) {
            connectionsStatusRef.removeEventListener(mConnectedListener);
        }
    }

    void inflateFragment(int layoutResId, UserChat userChat, int type)
    {
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(layoutResId, PublicChatFragment.newInstance(userChat, type));
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commitAllowingStateLoss();

    }

    private void navigateBackToLogin()
    {
        // Go to LogIn screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // LoginActivity is a New Task
        // The old task when coming back to this activity should be cleared so we cannot come back to it.
        startActivity(intent);
    }
}
