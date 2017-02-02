package com.stuff.bizzy.Models;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;
import java.util.Set;

/**
 * Created by Derian on 12/25/2016.
 */

public class Building implements Comparable<Building> {

    private PolygonOptions shape = new PolygonOptions();;
    private String name;
    private Marker center;

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

    public MarkerOptions display() {
        String title = name + "\nNumber of Groups: " + getNumGroups();
        return new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(getCenter()).title(title).visible(getNumGroups() > 0);
    }

//    /**
//     * Draws the building on the map and creates a marker at the center
//     * @param map the map to draw the building on
//     * @param color the outline color for the building (disabled currently)
//     * @return the drawing created on the map
//     */
//    public Polygon createBuilding(final GoogleMap map, int color) {
//        center = map.addMarker(new MarkerOptions().position(getCenter()).title(name+"\nNumber of Groups: "+groups.size()));
//        if (groups.isEmpty()){ center.setVisible(false); }
//        final Polygon p = map.addPolygon(shape.strokeColor(color).clickable(true));
//        p.setVisible(false);
//        return p;
//    }

//    public void addGroups(Group... groups) {
//        for (Group g : groups) {
//            this.groups.add(g);
//        }
//    }

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

    public Marker getCenterMarker() {
        return center;
    }

    public int getNumGroups() {
        return GroupList.groups.get(name) != null ? GroupList.groups.get(name).size() : 0;
    }

    @Override
    public int compareTo(Building o) {
        if (getNumGroups() > o.getNumGroups()) {
            return 1;
        } else if (getNumGroups() < o.getNumGroups()) {
            return -1;
        } else {
            return 0;
        }
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
