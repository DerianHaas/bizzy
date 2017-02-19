package com.stuff.bizzy.Models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.R.attr.name;

/**
 * Created by Derian on 1/11/2017.
 */

public class User {

    private String uid;

    private String password;
    private String email;

    private List<User> friends;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        friends = new ArrayList<>();
    }

    public User() {}

    public List<User> viewFriends() {
        return friends;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() { return email; }

    public void addFriends(User... users) {
        for (User u : users) {
            friends.add(u);
        }
    }

    @Override
    public String toString() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || ((o != null) && (o instanceof User)
                && (this.getEmail() == ((User) o).getEmail())
                && (this.getPassword() == ((User) o).getPassword()));
    }

}
