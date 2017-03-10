package com.stuff.bizzy.Models;


import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Derian on 12/29/2016.
 */

public class Group {

    private List<String> users;
    private String name;
    private String details;
    private String location;
    private String uid;

    /**
     * Creates an empty (one example person) group at a specified location.
     * @param location the building the group is in
     * @param name the name of this group
     * @param details extra info for the group
     */

    public Group(String location, String name, String details) {
        this.location = location;
        this.name = name;
        this.details = details;
        users = new ArrayList<>();
    }

    /**
     * Creates a group at a specified location.
     * @param location the building the group is in
     * @param name the name of this group
     * @param users the users currently in the group
     * @param details extra info for the group
     */
    public Group(String location, String name, String details, List<String> users) {
        this.location = location;
        this.name = name;
        this.details = details;
        this.users = users;
    }

    public Group() {
        users = new ArrayList<>();
    }

    /**
     * Creates a marker to display group information
     * @return a MarkerOptions containing the group information
     */
//    public MarkerOptions display() {
//        String title = name + "\n" + (building == null ? "" : "Building: "+ building.getName() + "\n") + "Number of People: "+people.size();
//        return new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(location).title(title);
//    }

    /**
     * Adds a user to this group
     * @param user the user to add to the group
     */
    public void addToGroup(User user) {
        users.add(user.getUid());
    }

    /**
     * @return the location of this group
     */
    public String getLocation() {
        return location;
    }

    public void setUsers(List<String> people) {
        this.users = people;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getUsers() {
        return users;
    }

    /**
     * @return the name of this group
     */
    public String getName() {
        return name;
    }

    /**
     * @return the details of this group
     */
    public String getDetails() {
        return details;
    }

    /**
     * @return the number of people in this group
     */
    public int getNumPeople() {
        return users.size();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("users", users);
        result.put("location", location);
        result.put("details", details);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Group)) {
            return false;
        }
        Group g = (Group) obj;
        return g.uid.equals(uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }
}
