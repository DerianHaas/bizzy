package com.stuff.bizzy.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

/**
 * Created by Derian on 12/25/2016.
 */

public class Building {

    private PolygonOptions shape = new PolygonOptions();;
    private String name;

    /**
     * Creates a Building with a name at the specified location
     * @param name the name of the building
     * @param coords the Lat/Long coords enclosing the location
     */
    public Building(String name, LatLng[] coords) {
        this.name = name;
        shape.add(coords);
    }

    /**
     * @return the center point of this building
     */
    public LatLng getCenter(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng l : shape.getPoints()) {
            builder.include(l);
        }
        LatLngBounds bounds = builder.build();
        return bounds.getCenter();
    }


    /**
     * @return the list of coordinates specifying the building
     */
    public List<LatLng> getCoords() {
        return shape.getPoints();
    }

    /**
     * @return the name of the building
     */
    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof  Building)) {
            return false;
        }
        Building b = (Building) obj;
        return b.shape.getPoints().equals(shape.getPoints());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
