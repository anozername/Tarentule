package com.dant.app;

import java.util.*;

public class Results extends ArrayList<Integer> {

    public Results() {
        super();
    }

    public Results(List<Integer> l) {
        super();
        addAll(l);
    }

    public void addAllList(List<List<Integer>> list) {
        for (List<Integer> l : list) addAll(l);
    }

    //et
    public Results computeResults(List<Integer> l)  {
        //if (l == null) return new ArrayList<>();
        Results list = new Results();
        for (Integer iq1 : this) {
            for (Integer iq2 : l) {
                if (iq1.equals(iq2)) {
                    list.add(iq2);
                    break;
                }
            }
        }
        return list;
    }
}
