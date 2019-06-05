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

    public static int fromDoubleVersion(Integer affinity, Object[] line, List<Object[]> res, int from, int to) {
        double v1 = 1.0;
        double v2 = 0.0;
        while (from < to && v1 > v2) {
            v1 = CastHelper.castToCompare(line[affinity]);
            v2 = CastHelper.castToCompare(res.get(from)[affinity]);
            from++;

        }
        if (from == to && v1 > v2) return from;
        return from-1;
    }

    public static int to(Integer affinity, Object[] line, List<Object[]> res, int from, int to) {
        while (from < to && line[affinity].equals(res.get(from)[affinity])) {
            from++;
        }
        return from;
    }

    public static int toDoubleVersion(Integer affinity, Object[] line, List<Object[]> res, int from, int to) {
        double v1 = 0.0;
        double v2 = 0.0;
        if (from == to) {
            return from;
        }
        while (from < to && v1 == v2) {
            v1 = CastHelper.castToCompare(line[affinity]);
            v2 = CastHelper.castToCompare(res.get(from)[affinity]);
            from++;
        }
        return from;
    }

    public static int placeToInsert(List<Integer> affinities, Object[] line, List<Object[]> res) {
        int from = 0;
        int to = res.size();
        int acc = from;
        if (res.isEmpty()) return 0;
        for (Integer affinity : affinities) {
            if (CSVHelper.getTypes().get(affinity).equals("double")) {
                if (from == to) {
                    return from;
                }
                from = fromDoubleVersion(affinity, line, res, from, to);
                to = toDoubleVersion(affinity, line, res, from, to);
            }
            else {
                from = from(affinity, line, res, from, to);
                to = to(affinity, line, res, from, to);
                if (from == to) {
                    return from;
                }
            }

        }
        return from;
    }
}
