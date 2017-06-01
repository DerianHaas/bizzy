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

    private String name;
    private String details;
    private int numMembers;

    /**
     * Creates an empty group
     *
     * @param name     the name of this group
     * @param details  extra info for the group
     */

    public Group(String name, String details, int numMembers) {
        this.name = name;
        this.details = details;
        this.numMembers = numMembers;
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
    public int getNumMembers() {
        return numMembers;
    }
}
