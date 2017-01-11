package com.stuff.bizzy;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;
import java.util.Set;

import static android.R.attr.shape;

/**
 * Created by Derian on 12/29/2016.
 */

public class Group {

    private Set<User> people;
    private String name;
    private LatLng location;
    private Building building;

    /**
     * Creates an empty (one example person) group at a specified location.
     * @param location the location of the group
     * @param name the name of this group
     * @throws IllegalArgumentException If the location is not on GT campus
     */

    public Group(LatLng location, String name) {
        if (!MapScreen.boundaries.contains(location)) {
            throw new IllegalArgumentException("Location must be on GT campus!");
        }
        this.location = location;
        this.name = name;
        people = new HashSet<>();
        people.add(new User("Someone", "...")); //Example person
    }

    /**
     * Creates an empty (one example person) group at a specified location.
     * @param location the location of the group
     * @param name the name of this group
     * @param people the users currently in the group
     * @throws IllegalArgumentException If the location is not on GT campus
     */
    public Group(LatLng location, String name, Set<User> people) {
        if (!MapScreen.boundaries.contains(location)) {
            throw new IllegalArgumentException("Location must be on GT campus!");
        }
        this.location = location;
        this.name = name;
        this.people = people;
    }

    /**
     * Creates a marker to display group information
     * @return a MarkerOptions containing the group information
     */
    public MarkerOptions display() {
        String title = (building == null ? "" : "Building: "+ building.getName() + "\n") + "Number of People: "+people.size();
        return new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(location).title(title);
    }

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
    public LatLng getLocation() {
        return location;
    }

    /**
     *  @param b the building this group resides in
     */
    public void setBuilding(Building b){
        building = b;
    }

    /**
     * @return the name of this group
     */
    public String getName() {
        return name;
    }

    /**
     * @return the name of the building this group resides in
     */
    public String getBuildingName() {
        return building == null ? "" : building.getName();
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
        return g.location.equals(location) && g.people.equals(people);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
