package com.stuff.bizzy.Models;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Derian on 2/19/2017.
 */

public class User {

    private String uid;

    private String displayName;
    private String email;
    private Uri imageURL;
    private String currentGroup = "";

    public User() {}

    public User(FirebaseUser user) {
        uid = user.getUid();
        displayName = user.getDisplayName();
        email = user.getEmail();
        imageURL = user.getPhotoUrl();
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Uri getImageURL() {
        return imageURL;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setImageURL(Uri imageURL) {
        this.imageURL = imageURL;
    }

    public String isCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("displayName", displayName);
        map.put("imageURL", imageURL);
        map.put("currentGroup", currentGroup);
        map.put("email", email);
        return map;
    }
}
