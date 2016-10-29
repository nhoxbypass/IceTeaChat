package com.gdg.firebase.iceteachat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.Firebase;
import com.gdg.firebase.iceteachat.helper.ReferenceURL;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xa Tao Ra on 3/11/2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserChat implements Serializable{
    private String name;
    private String email;
    private String password;
    private String uid;
    private String photoUrl;
    private String connection;
    // boolean isLogged;

    public UserChat()
    {
        this.uid = "";
        //isLogged = false;
    }

    public UserChat(String name, String email, String password, String photoUrl, String connection,String uid)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.uid = uid;
        this.photoUrl = photoUrl;
        this.connection = connection;
        //isLogged = false;

    }

    //TODO: Save user data to Firebase. Can extense to save other attribute like address, avatar image link
    public void saveFullData()
    {
        //Get Firebase ref -> user
        Firebase mFireBaseRef = new Firebase(ReferenceURL.FIREBASE_APP_URL).child(ReferenceURL.CHILD_USERS);

        //Map data to object
        Map<String, Object> values = new HashMap<>();
        values.put("name",this.name);
        values.put("email", this.email);
        values.put("password", this.password);
        values.put("photoUrl",this.photoUrl);
        values.put("connection", this.connection);

        //Push to firebase
        mFireBaseRef.child(this.uid).setValue(values);
    }


    //Create Ref link by email of user and the receiver
    public String getChatRef(String receiverMail){
        return createUniqueChatRef(receiverMail);
    }


    private String createUniqueChatRef(String receiverMail){
        String uniqueChatRef = "";

        if (this.email.length() > receiverMail.length())
        {
            uniqueChatRef=cleanEmailAddress(this.email)+"-"+cleanEmailAddress(receiverMail);
        }
        else if (this.email.length() < receiverMail.length())
        {
            uniqueChatRef=cleanEmailAddress(receiverMail)+"-"+cleanEmailAddress(this.email);
        }
        else
        {
            if (this.email.charAt(1) > receiverMail.charAt(1))
            {
                uniqueChatRef=cleanEmailAddress(this.email)+"-"+cleanEmailAddress(receiverMail);
            }
            else
            {
                uniqueChatRef=cleanEmailAddress(receiverMail)+"-"+cleanEmailAddress(this.email);
            }
        }


        return uniqueChatRef;
    }

    private String cleanEmailAddress(String email){

        //Replace . with -
        //Firebase doesnt support .
        return email.replace(".","-");

    }

    public String getName()
    {
        return this.name;
    }

    public String getEmail()
    {
        return this.email;
    }

    public String getPassword()
    {
        return this.password;
    }

    public String getUid()
    {
        return this.uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getConnection(){
        return connection;
    }

   /* public boolean isLoggedIn()
    {
        return isLogged;
    }

    public void loggedIn()
    {
        isLogged = true;
    }
    */
}
