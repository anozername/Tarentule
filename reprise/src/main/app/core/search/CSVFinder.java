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

        return res;
    }

    public List<Object[]> getValueWithoutIndexGB(Map<String, Object> queries, List<String> groupBy, List<Object[]> lines, int compute){
        System.out.println("OK");
        List<Object[]> res = new ArrayList<>();
        List<Integer> indicesGroup = new ArrayList<>();
        boolean satisfaction;
        for (String attribute : groupBy) {
            indicesGroup.add(CSVHelper.getNameIndexes().indexOf(attribute));
        }
        for (Object[] line : lines) {
            satisfaction = getSatisfaction(queries, line, compute);
            if (satisfaction) {
                System.out.println(GBHelper.placeToInsert(indicesGroup, line, res) + "ok");
                res.add(GBHelper.placeToInsert(indicesGroup, line, res), line);
            }
        }

        return res;
    }

    public List<Object[]> getValueWithoutIndexGB(Map<String, Object> queries, List<String> groupBy, int compute){
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
                satisfaction = getSatisfaction(queries, line.toArray(), compute);

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
