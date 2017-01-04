package com.stuff.bizzy;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

/**
 * Created by Derian on 12/25/2016.
 */

public class Building {

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

    /**
     * Draws the building on the map and creates a marker at the center
     * @param map the map to draw the building on
     * @param color the outline color for the building (disabled currently)
     * @return the drawing created on the map
     */
    public Polygon createBuilding(final GoogleMap map, int color) {
        center = map.addMarker(new MarkerOptions().position(getCenter()).title(name));
        center.setVisible(false);
        final Polygon p = map.addPolygon(shape.strokeColor(color).clickable(true));
        p.setVisible(false);
        return p;
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

    public Marker getCenterMarker() {
        return center;
    }
}
