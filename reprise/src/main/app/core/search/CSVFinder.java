package main.app.core.search;

import main.app.core.entity.Lines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVFinder {
    private static String csvFile = "input.csv";
    private static String line = "";
    private static String cvsSplitBy = ",";
    private static int posID = 0;

    /* @params: sorted ids


    public static Lines findLinesWithIds(List<Integer> ids) {
        List<Object[]> res = new ArrayList<Object[]>();
        Object[] trip;
        List<Object> tmp;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            line = br.readLine();
            while (((line = br.readLine()) != null) && !ids.isEmpty()) {
                trip = line.split(cvsSplitBy);
                if (Integer.parseInt(trip[posID].toString()) == ids.get(0)) {
                    ids.remove(0);
                    tmp = new ArrayList<>(Arrays.asList(trip));
                    res.add(CastHelper.casting())
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }*/
}
