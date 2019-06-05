package main.app.core.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CSVFinder {
    private String csvFile;
    private static String cvsSplitBy = ",";

    public CSVFinder(String fileName) {
        this.csvFile = fileName;
    }

    public List<Object[]> findLinesWithIds(List<Integer> ids) {
        System.out.println("DODO");
        List<Object[]> res = new ArrayList<Object[]>();
        Object[] trip;
        List<Object> tmp;
        Path path = Paths.get(csvFile);
        List<Object[]> linesTMP = new ArrayList<>();
        Charset charset = Charset.forName("UTF-8");
        try {
            List<String> lines = Files.readAllLines(path, charset);
            for (Integer id : ids) {
                linesTMP.add(lines.get(id-1).split(cvsSplitBy));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linesTMP;
    }

    public List<Object[]> getValueWithoutIndex(Map<String, Object> queriesAND, Map<String, Object> queriesOR){
        List<Object[]> res = new ArrayList<>();
        boolean satisfaction;
        String lineSTR;
        List<Object> line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((lineSTR = br.readLine()) != null) {
                line = CSVHelper.read(lineSTR.split(cvsSplitBy));
                satisfaction = getSatisfaction(queriesAND, queriesOR, line.toArray());
                if (satisfaction) {
                    res.add(line.toArray());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    public List<Object[]> getValueWithoutIndex(Map<String, Object> queriesAND, Map<String, Object> queriesOR, List<Object[]> lines){
        List<Object[]> res = new ArrayList<>();
        boolean satisfaction;
        for (Object[] line : lines) {
                satisfaction = getSatisfaction(queriesAND, queriesOR, line);
                if (satisfaction) {
                    res.add(line);
                }
        }

        return res;
    }

    public List<Object[]> getValueWithoutIndexGB(Map<String, Object> queriesAND, Map<String, Object> queriesOR, List<String> groupBy, List<Object[]> lines){
        System.out.println("OK");
        List<Object[]> res = new ArrayList<>();
        List<Integer> indicesGroup = new ArrayList<>();
        boolean satisfaction;
        for (String attribute : groupBy) {
            indicesGroup.add(CSVHelper.getNameIndexes().indexOf(attribute));
        }
        for (Object[] line : lines) {
            satisfaction = getSatisfaction(queriesAND, queriesOR, line);
            if (satisfaction) {
                System.out.println(GBHelper.placeToInsert(indicesGroup, line, res) + "ok");
                res.add(GBHelper.placeToInsert(indicesGroup, line, res), line);
            }
        }

        return res;
    }

    public List<Object[]> getValueWithoutIndexGB(Map<String, Object> queriesAND, Map<String, Object> queriesOR, List<String> groupBy){
        List<Object[]> res = new ArrayList<>();
        List<Integer> indicesGroup = new ArrayList<>();
        boolean satisfaction;
        String lineSTR;
        List<Object> line;
        for (String attribute : groupBy) {
            indicesGroup.add(CSVHelper.getNameIndexes().indexOf(attribute));
        }
        System.out.println(csvFile);
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((lineSTR = br.readLine()) != null) {

                line = CSVHelper.read(lineSTR.split(cvsSplitBy));
                satisfaction = getSatisfaction(queriesAND, queriesOR, line.toArray());

                if (satisfaction) {
                   // System.out.println(GBHelper.placeToInsert(indicesGroup, line.toArray(), res) + "ok\n" + res);
                    res.add(GBHelper.placeToInsert(indicesGroup, line.toArray(), res), line.toArray());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean getSatisfaction(Map<String, Object> queriesAND, Map<String, Object> queriesOR, Object[] line) {
        int satisfaction = 0;
        boolean or = false;
        for (Map.Entry<String, Object> querieAND : queriesAND.entrySet()) {
            if (line[CSVHelper.getNameIndexes().indexOf(querieAND.getKey())].equals(querieAND.getValue())) {
                satisfaction++;
            } else {
                break;
            }
        }
        for (Map.Entry<String, Object> querieOR : queriesOR.entrySet()) {
            if (line[CSVHelper.getNameIndexes().indexOf(querieOR.getKey())].equals(querieOR.getValue())) {
                or = true;
            }
        }
        return ((satisfaction == queriesAND.size()) || or);
    }
}
