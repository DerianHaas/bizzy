package com.stuff.bizzy.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Derian on 1/5/2017.
 */

public final class GroupList {

    private static List<Group> groups = new ArrayList<>();

    public static void refreshGroupList() {
        //TODO Get groups from server
        groups = new ArrayList<>();//example
        groups.add((new Group(new LatLng(33.774496, -84.396266), "Test 1")));//example group
        groups.add(new Group(new LatLng(33.774651, -84.397263), "Test 2"));//example group
        groups.add(new Group(new LatLng(33.773615, -84.397953), "Other 3"));//example group
    }

    public static List<Group> getGroupList() {
        return groups;
    }

    public static Group[] getGroupArray() {
        return groups.toArray(new Group[groups.size()]);
    }

    public static List<Group> sortGroups(GroupComparator.Method method) {
        List<Group> list = groups;
        Collections.sort(list, new GroupComparator(method));
        return list;
    }

    private GroupList() {}
}
