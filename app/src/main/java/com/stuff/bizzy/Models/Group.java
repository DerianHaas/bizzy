package com.stuff.bizzy.Models;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by Derian on 12/29/2016.
 */

public class Group {

    private Set<User> people;
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
        people = new HashSet<>();
        people.add(new User("Someone", "...", "example@stuff.com")); //Example person
    }

    /**
     * Creates a group at a specified location.
     * @param location the building the group is in
     * @param name the name of this group
     * @param people the users currently in the group
     * @param details extra info for the group
     */
    public Group(String location, String name, String details, Set<User> people) {
        this.location = location;
        this.name = name;
        this.details = details;
        this.people = people;
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
    public String getBuilding() {
        return location;
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
}
