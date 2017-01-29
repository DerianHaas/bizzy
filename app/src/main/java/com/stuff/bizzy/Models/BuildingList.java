package com.stuff.bizzy.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by Derian on 12/29/2016.
 */

public final class BuildingList {

    public static Set<Building> buildings = new HashSet<>();

    //TODO Add rest of building coordinates here
    static {
        LatLng[] clough = {new LatLng(33.775189, -84.396615), new LatLng(33.775189, -84.396169),
                new LatLng(33.774210, -84.396169), new LatLng(33.774210, -84.396615)};
        buildings.add(new Building("CULC", clough));

        LatLng[] studentcenter = {new LatLng(33.774500, -84.399042), new LatLng(33.773732, -84.399042),
                new LatLng(33.773732, -84.398655), new LatLng(33.773421, -84.398655),
                new LatLng(33.773421, -84.397637), new LatLng(33.773888, -84.397637),
                new LatLng(33.773888, -84.398530), new LatLng(33.774500, -84.398530)};
        buildings.add(new Building("Student Center", studentcenter));
    }


    private BuildingList(){}



}
