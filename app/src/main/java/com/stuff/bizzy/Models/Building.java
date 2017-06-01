package com.stuff.bizzy.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

import static android.R.attr.shape;

/**
 * Created by Derian on 12/25/2016.
 */

public class Building {

    private LatLng center;
    private String name;
    private int numGroups;

    public Building(String name, LatLng center, int numGroups) {
        this.name = name;
        this.center = center;
        this.numGroups = numGroups;
    }

    /**
     * @return the name of the building
     */
    public String getName() {
        return name;
    }

    public LatLng getCenter() {
        return center;
    }

    public int getNumGroups() {
        return numGroups;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof  Building)) {
            return false;
        }
        Building b = (Building) obj;
        return b.center.equals(center);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
