package com.stuff.bizzy;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Derian on 1/11/2017.
 */

public class User {

    private String username;
    private String password;

    private Set<User> friends;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        friends = new HashSet<>();
    }

    public Set<User> viewFriends() {
        return friends;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void addFriends(User... users) {
        for (User u : users) {
            friends.add(u);
        }
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User u = (User) obj;
        return username.equals(u.getUsername()) && password.equals(u.getPassword());
    }
}
