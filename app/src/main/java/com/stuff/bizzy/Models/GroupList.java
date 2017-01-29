package com.stuff.bizzy.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Derian on 1/5/2017.
 */

public final class GroupList {

    public static Map<String, List<Group>> groups = new HashMap<>();



    public static void refreshGroupList() {
        //TODO Get groups from server
        ArrayList<Group>groupList = new ArrayList<>();//example
        groupList.add((new Group("CULC", "Test 1", "Details 1")));//example group
        groupList.add(new Group("Student Center", "Test 2", "Details 2"));//example group
        groupList.add(new Group("CULC", "Other 3", "Details 3"));//example group


        for (Group g : groupList) {
            if (groups.get(g.getBuilding()) == null) {
                groups.put(g.getBuilding(), new ArrayList<Group>());
            }
            groups.get(g.getBuilding()).add(g);
        }
    }

    public static List<Group> getSortedGroups(String building, GroupComparator.Method method) {
        List<Group> list = groups.get(building);
        Collections.sort(list, new GroupComparator(method));
        return list;
    }

    private GroupList() {}
}
