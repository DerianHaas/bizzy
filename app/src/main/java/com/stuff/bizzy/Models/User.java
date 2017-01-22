package com.stuff.bizzy.Models;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Derian on 1/11/2017.
 */

public class User {

    private String username;
    private String password;
    private String email;

    private Set<User> friends;

    public User(String email, String username, String password) {
        this.username = username;
        this.email = email;
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

    public String getEmail() { return email; }

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
    public boolean equals(Object o) {
        return (this == o) || ((o != null) && (o instanceof User)
                && (this.getUsername() == ((User) o).getUsername())
                && (this.getPassword() == ((User) o).getPassword()));
    }

}
