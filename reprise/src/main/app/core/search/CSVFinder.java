package main.app.core.search;

import main.app.core.entity.Lines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CSVFinder {
    private String csvFile;
    private static String cvsSplitBy = ",";

    public CSVFinder(String fileName) {
        this.csvFile = fileName;
    }

    public Lines findLinesWithIds(List<Integer> ids) {
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
        return new Lines(linesTMP);
    }

    public List<Object[]> getValueWithoutIndex(Map<String, Object[]> queries){
        List<Object[]> res = new ArrayList<>();
        int satisfaction;
        String lineSTR;
        List<Object> line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((lineSTR = br.readLine()) != null) {
                line = CSVHelper.read(lineSTR.split(cvsSplitBy));
                satisfaction = getSatisfaction(queries, line.toArray());
                if (satisfaction == queries.size()) {
                    res.add(line.toArray());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public List<Object[]> getValueWithoutIndex(Map<String, Object[]> queries, List<Object[]> lines){
        List<Object[]> res = new ArrayList<>();
        int satisfaction;
        for (Object[] line : lines) {
                satisfaction = getSatisfaction(queries, line);
                if (satisfaction == queries.size()) {
                    res.add(line);
                }
        }
        return res;
    }

    public List<Object[]> getValueWithoutIndexGB(Map<String, Object[]> queries, List<String> groupBy, List<Object[]> lines){
        List<List<Object[]>> groupedRes = new ArrayList<>();
        List<Object[]> res;
        List<Integer> indicesGroup = new ArrayList<>();
        int satisfaction;
        int friend = 0;
        boolean added = false;
        for (String attribute : groupBy) {
            indicesGroup.add(Lines.getIndiceForAttribute(attribute));
        }
        for (Object[] line : lines) {
            satisfaction = getSatisfaction(queries, line);
            if (satisfaction == queries.size()) {
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
        return res;
    }

    public List<Object[]> getValueWithoutIndexGB(Map<String, Object[]> queries, List<String> groupBy){
        List<List<Object[]>> groupedRes = new ArrayList<>();
        List<Object[]> res;
        List<Integer> indicesGroup = new ArrayList<>();
        int satisfaction;
        int friend = 0;
        String lineSTR;
        List<Object> line;
        boolean added = false;
        for (String attribute : groupBy) {
            indicesGroup.add(Lines.getIndiceForAttribute(attribute));
        }
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((lineSTR = br.readLine()) != null) {
                line = CSVHelper.read(lineSTR.split(cvsSplitBy));
                satisfaction = getSatisfaction(queries, line.toArray());
                if (satisfaction == queries.size()) {
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
        return res;
    }

    public static int getSatisfaction(Map<String, Object[]> queries, Object[] line) {
        int satisfaction = 0;
        for (Map.Entry<String, Object[]> query : queries.entrySet()) {
            //0 car seule la premiere entree est consideree pour l instant
            if (line[CSVHelper.getNameIndexes().indexOf(query.getKey())].equals(query.getValue()[0])) {
                satisfaction++;
            } else break;
        }
        return satisfaction;
    }
}
