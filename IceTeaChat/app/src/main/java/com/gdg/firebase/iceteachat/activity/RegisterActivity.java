package com.gdg.firebase.iceteachat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.gdg.firebase.iceteachat.helper.ReferenceURL;
import com.gdg.firebase.iceteachat.model.UserChat;
import com.gdg.firebase.iceteachat.R;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    //Declare variables
    EditText nameInput;
    EditText emailInput;
    EditText passwordInput;
    UserChat userChat;
    Button regSubmit;
    Button aboutButton;
    Firebase mFireBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Set Firebase context and reference
        Firebase.setAndroidContext(this);
        mFireBaseRef = new Firebase(ReferenceURL.FIREBASE_APP_URL);

        //Get view
        nameInput = (EditText) findViewById(R.id.name_input);
        emailInput = (EditText) findViewById(R.id.email_input);
        passwordInput = (EditText) findViewById(R.id.pass_input);
        regSubmit = (Button) findViewById(R.id.reg_submit_button);
        aboutButton = (Button)findViewById(R.id.about_button);

        //Navigate to about page
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navToAboutPage= new Intent(getBaseContext(), AboutActivity.class);
                startActivity(navToAboutPage);
            }
        });

        //Create new user by email
        regSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getBaseContext(),"Đang tạo tài khoản...",Toast.LENGTH_SHORT);
                userChat = new UserChat(nameInput.getText().toString(), emailInput.getText().toString(), passwordInput.getText().toString(), "http://www.caprisunandomd.com/wp-content/uploads/2015/09/no-avatar.jpg", "null");

                mFireBaseRef.createUser(userChat.getEmail(), userChat.getPassword(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        Toast.makeText(getBaseContext(), "Tạo thành công tài khoản\n" + "Đang đăng nhập...", Toast.LENGTH_SHORT).show();
                        userChat.setUid(result.get("uid").toString());
                        userChat.saveFullData();

                        //Login
                        mFireBaseRef.authWithPassword(userChat.getEmail(), userChat.getPassword(), new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                //Login succeed navigate to main page
                                //userChat.loggedIn();
                                Intent navToMainPage = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(navToMainPage);
                                finish();
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                // there was an error
                                Toast.makeText(getBaseContext(), "Lỗi kết nối!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Toast.makeText(getBaseContext(), "Đăng ký không thành công\nTài khoản đã tồn tại hoăc lỗi kết nối", Toast.LENGTH_LONG).show();

                    }
                });


            }
        });



    }

}
