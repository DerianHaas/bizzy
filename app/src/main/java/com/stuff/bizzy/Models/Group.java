package com.stuff.bizzy.Models;


import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Derian on 12/29/2016.
 */

public class Group {

    private List<User> people;
    private String name;
    private String details;
    private String location;

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
        people = new ArrayList<>();
    }

    /**
     * Creates a group at a specified location.
     * @param location the building the group is in
     * @param name the name of this group
     * @param people the users currently in the group
     * @param details extra info for the group
     */
    public Group(String location, String name, String details, List<User> people) {
        this.location = location;
        this.name = name;
        this.details = details;
        this.people = people;
    }

    public Group() {
        people = new ArrayList<>();
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
     * @return whether the user was added successfully
     */
    public boolean addToGroup(User user) {
        return people.add(user);
    }

    /**
     * @return the location of this group
     */
    public String getLocation() {
        return location;
    }

    public void setPeople(List<User> people) {
        this.people = people;
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

    public List<User> getPeople() {
        return people;
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
        return people.size();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("people", people);
        result.put("location", location);
        result.put("details", details);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof  Group)) {
            return false;
        }
        Group g = (Group) obj;
        return g.name.equals(name) && g.location.equals(location) && g.people.equals(people);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Group of "+getNumPeople()+" people at " + location + " named " + name;
    }
}
