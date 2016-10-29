package com.gdg.firebase.iceteachat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;
import com.gdg.firebase.iceteachat.helper.ReferenceURL;
import com.gdg.firebase.iceteachat.R;


//TODO: this class use to login w email, facebook
public class LoginActivity extends FirebaseLoginBaseActivity {

    public static final String KEY_LOGIN_TYPE  = "login_type";
    public static final int LOGIN_NORMAL = 0;
    public static final int LOGIN_ANNONYMOUS = 1;

    //Declare variables
    Button loginButton;
    Button registerButton;
    Button tryButton;
    Button aboutButton;
    Firebase mFireBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set firebase context
        Firebase.setAndroidContext(this);

        //Set view
        loginButton = (Button)findViewById(R.id.login_button);
        registerButton = (Button)findViewById(R.id.reg_button);
        tryButton = (Button)findViewById(R.id.ano_try_button);
        aboutButton = (Button)findViewById(R.id.about_button);

        //Set Firebase Reference
        mFireBaseRef = new Firebase(ReferenceURL.FIREBASE_APP_URL);

        //Show login form
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFirebaseLoginPrompt();
            }
        });

        //Navigate to Registration page
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navToRegPage = new Intent(getBaseContext(),RegisterActivity.class);
                startActivity(navToRegPage);
            }
        });

        //Navigate to Main page with anonymous auth
        tryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Đang đăng nhập ẩn danh...",Toast.LENGTH_SHORT);
                Intent navToMainPage= new Intent(getBaseContext(), MainActivity.class);
                navToMainPage.putExtra(KEY_LOGIN_TYPE, LOGIN_ANNONYMOUS);
                startActivity(navToMainPage);
                finish();
            }
        });

        //Navigate to About page
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navToAboutPage= new Intent(getBaseContext(), AboutActivity.class);
                startActivity(navToAboutPage);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Providers are optional! Add or remove any you don't want.
        setEnabledAuthProvider(AuthProviderType.PASSWORD);
        setEnabledAuthProvider(AuthProviderType.FACEBOOK);
    }

    @Override
    public Firebase getFirebaseRef() {
        return mFireBaseRef;
    }

    @Override
    public void onFirebaseLoginProviderError(FirebaseLoginError firebaseError) {
        // TODO: Handle an error from the authentication provider
        Toast.makeText(getBaseContext(),"Lỗi hệ thống\nVui lòng kiểm tra lại kết nối",Toast.LENGTH_LONG);
    }


    @Override
    public void onFirebaseLoginUserError(FirebaseLoginError firebaseError) {
        // TODO: Handle an error from the user
        Toast.makeText(getBaseContext(), "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFirebaseLoggedIn(AuthData authData) {
        //TODO: Handle successful login
        Intent navToMainPage= new Intent(getBaseContext(), MainActivity.class);
        navToMainPage.putExtra(KEY_LOGIN_TYPE, LOGIN_NORMAL);
        startActivity(navToMainPage);
        finish();
    }

    @Override
    public void onFirebaseLoggedOut() {
        // TODO: Handle logout
        Toast.makeText(getBaseContext(),"Đăng xuất thành công!",Toast.LENGTH_SHORT);
    }


}
