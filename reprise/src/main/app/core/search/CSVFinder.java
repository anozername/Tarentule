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

        System.out.println("pimp 0");
        //System.out.println(linesTMP);
        return linesTMP;
    }

    public List<Object[]> getValueWithoutIndex(Map<String, Object> queries, int compute){
        List<Object[]> res = new ArrayList<>();
        boolean satisfaction;
        String lineSTR;
        List<Object> line;
        int line_nb = 50000;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((lineSTR = br.readLine()) != null) {
                if (line_nb >= 50000) {
                    line = CSVHelper.read(lineSTR.split(cvsSplitBy));
                    satisfaction = getSatisfaction(queries, line.toArray(), compute);
                    if (satisfaction) {
                        res.add(line.toArray());
                    }
                }
                line_nb++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("pimp 1");
        //System.out.println(res);"
        return res;
    }

    public List<Object[]> getValueWithoutIndex(Map<String, Object> queries, List<Object[]> lines, int compute){
        List<Object[]> res = new ArrayList<>();
        boolean satisfaction;
        for (Object[] line : lines) {
                satisfaction = getSatisfaction(queries, line, compute);
                if (satisfaction) {
                    res.add(line);
                }
        }
        System.out.println("pimp 2");
        // System.out.println(res);
        return res;
    }

    public List<Object[]> getValueWithoutIndexGB(Map<String, Object> queries, List<String> groupBy, List<Object[]> lines, int compute){
        List<List<Object[]>> groupedRes = new ArrayList<>();
        List<Object[]> res;
        List<Integer> indicesGroup = new ArrayList<>();
        boolean satisfaction;
        int friend = 0;
        boolean added = false;
        for (String attribute : groupBy) {
            indicesGroup.add(CSVHelper.getNameIndexes().indexOf(attribute));
        }
        for (Object[] line : lines) {
            satisfaction = getSatisfaction(queries, line, compute);
            if (satisfaction) {
                for (List<Object[]> group : groupedRes) {
                    for (Integer i : indicesGroup) {
                        if (line[i].equals(group.get(0)[i])) friend++;
                        else friend = 0;
                    }
                    if (friend == indicesGroup.size()) {
                        group.add(line);
                        friend = 0;
                        added = true;
                    }
                }
                if (!added) {
                    res = new ArrayList<>();
                    res.add(line);
                    groupedRes.add(res);
                }
                added = false;
            }
        }
        res = new ArrayList<>();
        for (List<Object[]> l : groupedRes) {
            res.addAll(l);
        }
        System.out.println("pimp 3");
        // System.out.println(res);
        return res;
    }

    public List<Object[]> getValueWithoutIndexGB(Map<String, Object> queries, List<String> groupBy, int compute){
        List<List<Object[]>> groupedRes = new ArrayList<>();
        List<Object[]> res;
        List<Integer> indicesGroup = new ArrayList<>();
        boolean satisfaction;
        int friend = 0;
        String lineSTR;
        List<Object> line;
        boolean added = false;
        for (String attribute : groupBy) {
            indicesGroup.add(CSVHelper.getNameIndexes().indexOf(attribute));
        }
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((lineSTR = br.readLine()) != null) {
                line = CSVHelper.read(lineSTR.split(cvsSplitBy));
                satisfaction = getSatisfaction(queries, line.toArray(), compute);
                if (satisfaction) {
                    for (List<Object[]> group : groupedRes) {
                        for (Integer i : indicesGroup) {
                            if (line.get(i).equals(group.get(0)[i])) friend++;
                            else friend = 0;
                        }
                        if (friend == indicesGroup.size()) {
                            group.add(line.toArray());
                            friend = 0;
                            added = true;
                        }
                    }
                    if (!added) {
                        res = new ArrayList<>();
                        res.add(line.toArray());
                        groupedRes.add(res);
                    }
                    added = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        res = new ArrayList<>();
        for (List<Object[]> l : groupedRes) {
            res.addAll(l);
        }
        System.out.println("pimp 4");
        // System.out.println(res);
        return res;
    }

    public static boolean getSatisfaction(Map<String, Object> queries, Object[] line, int compute) {
        int satisfaction = 0;
        String[] splitentry;
        for (Map.Entry<String, Object> querie : queries.entrySet()) {
            if (compute == 2) {
                if (line[CSVHelper.getNameIndexes().indexOf(querie.getKey())].equals(querie.getValue())) {
                    return true;
                }
            }
            if (compute == 1) {
                if (line[CSVHelper.getNameIndexes().indexOf(querie.getKey())].equals(querie.getValue())) {
                    satisfaction++;
                }
                else {
                    break;
                }
            }
        }
        return satisfaction == queries.size();
    }
}
