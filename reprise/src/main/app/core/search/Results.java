package main.app.core.search;

import java.util.*;

public class Results extends ArrayList<Integer> {

    public Results() {
        super();
    }

    public Results(List<Integer> l) {
        super();
        System.out.println(l);
        if (l != null) addAll(l);
    }

    public void addAllList(List<List<Integer>> list) {
        for (List<Integer> l : list) addAll(l);
    }

    //et
    public Results computeResults(List<Integer> l, int compute)  {
        //if (l == null) return new ArrayList<>();
        Results list = new Results();
        if (compute == 1) {
            for (Integer iq1 : this) {
                for (Integer iq2 : l) {
                    if (iq1.equals(iq2)) {
                        list.add(iq2);
                        break;
                    }
                }
            }
        }
        if (compute == 2) {
            list.addAll(this);
            for (Integer i : l) {
                if (!list.contains(i)) list.add(i);
            }
        }
        return list;
    }
}
