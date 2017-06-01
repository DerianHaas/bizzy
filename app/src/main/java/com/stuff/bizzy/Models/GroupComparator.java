package com.stuff.bizzy.Models;

import java.util.Comparator;

/**
 * Created by Derian on 1/7/2017.
 */

public class GroupComparator implements Comparator<Group> {

    public enum Method {
        GROUP_SIZE, GROUP_NAME
    }


    private Method method;

    public GroupComparator(Method method) {
        this.method = method;
    }

    @Override
    public int compare(Group o1, Group o2) {
        if (method == Method.GROUP_NAME) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        } else {
            if (o1.getNumMembers() > o2.getNumMembers()) {
                return -1;
            } else if (o1.getNumMembers() < o2.getNumMembers()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
