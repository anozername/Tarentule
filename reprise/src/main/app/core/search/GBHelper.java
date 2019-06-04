package main.app.core.search;

import main.app.core.entity.Lines;

import java.util.List;

public class GBHelper {
    public static int from(Integer affinity, Object[] line, List<Object[]> res, int acc, int to) {
        for (int igline = acc; igline < to; igline++) {
            if (line[affinity].equals(res.get(igline)[affinity])) return igline;
        }
        return acc;
    }

    public static int fromDoubleVersion(Integer affinity, Object[] line, List<Object[]> res, int acc, int to) {
        int igline = acc;
        double v1, v2;
        do {
            if (line[affinity] instanceof Integer) v1 = (double)((Integer)line[affinity]).intValue();
            else v1 = (Double)line[affinity];
            if (res.get(igline)[affinity] instanceof Integer) v2 = (double)((Integer)res.get(igline)[affinity]).intValue();
            else v2 = (Double)res.get(igline)[affinity];
            igline++;

        } while (igline < to && v1 > v2);
        if (igline == to && v1 > v2) return igline;
        return igline-1;
    }

    public static int to(Integer affinity, Object[] line, List<Object[]> res, int from, int to) {
        int i = from;
        while (i < to && line[affinity].equals(res.get(i)[affinity])) {
            i++;
        }
        if (i == from) return from;
        else return i;
    }

    public static int toDoubleVersion(Integer affinity, Object[] line, List<Object[]> res, int from, int to) {
        int i = from;
        double v1, v2;
        if (from == to) {
            return from;
        }
        do {

            if (line[affinity] instanceof Integer) v1 = (double)((Integer)line[affinity]).intValue();
            else v1 = (Double)line[affinity];
            if (res.get(i)[affinity] instanceof Integer) v2 = (double)((Integer)res.get(i)[affinity]).intValue();
            else v2 = (Double)res.get(i)[affinity];
            i++;
            System.out.println(line[0] + " " + v1 + ", " +  v2 + ", " + i + ", " + from + ", " + to);
        } while (i < to && v1 > v2);
        return to(affinity, line, res, i-1, to);
    }

    public static int placeToInsert(List<Integer> affinities, Object[] line, List<Object[]> res) {
        int from = 0;
        int to = res.size();
        int acc = from;
        if (res.isEmpty()) return 0;
        for (Integer affinity : affinities) {
            if (CSVHelper.getTypes().get(affinity).equals("double")) {
                from = fromDoubleVersion(affinity, line, res, from, to);
                to = toDoubleVersion(affinity, line, res, from, to);
                if (from == to) { //from == to
                    return from;
                }
            }
            else {
                from = from(affinity, line, res, from, to);
                to = to(affinity, line, res, from, to);
                if (from == to) {
                    return from;
                }
            }

        }
        return to;
    }
}
