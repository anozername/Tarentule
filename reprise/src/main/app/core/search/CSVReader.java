package main.app.core.search;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;

public class CSVReader {

    private String csvFile;
    //WTF
    private int line_max = 9000000;

    private static HashMap<Object, Integer>[] indexes;

    public CSVReader(String csvFile) {
        this.csvFile = csvFile;
    }

    public static Map<Object, Integer>[] getIndexes() {
        return indexes;
    }

    public HashMap<String, MultivaluedMap<Object, Integer>> readForIndexing() {

        List<Integer> posIndex = new ArrayList<>();
        Integer valueTMP;
        ArrayList<Integer> scoresTMP;
        String line = "";
        String cvsSplitBy = ",";
        List<Object> tmp;
        int line_nb = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            indexes = Arrays.copyOf(CSVHelper.getIndexes(), CSVHelper.getIndexes().length);
            while ((line = br.readLine()) != null) {
                if (line_nb < line_max){
                    tmp = new ArrayList<>(CSVHelper.read(line.split(cvsSplitBy)));
                    for (int pos = 1; pos < indexes.length; pos++) {
                        if (indexes[pos] != null) {
                            if (!indexes[pos].containsKey(tmp.get(pos))) {
                                indexes[pos].put(tmp.get(pos), 1);
                            } else {
                                valueTMP = indexes[pos].get(tmp.get(pos)) + 1;
                                indexes[pos].put(tmp.get(pos), valueTMP);
                            }

                        }
                    }
                    tmp.clear();
                    deleteFatMap();
                }
                line_nb++;
            }
            ArrayList<Integer> scores = new ArrayList<>(getScoresForIndexing());
            scores.add(0, null);
            scoresTMP = new ArrayList<>(scores);
            Integer min;
            Integer indexmin;
            Integer position;
            for (int nbIndex = 0; nbIndex < 2; nbIndex++) {
                min = min(scoresTMP);
                indexmin = scores.indexOf(min);
                while (posIndex.contains(indexmin)) {
                    position = indexmin;
                    indexmin = scores.indexOf(min(scores.subList(position + 1, scores.size()))) + position;
                }
                posIndex.add(indexmin);
                scoresTMP.remove(min);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return readforHashMap(posIndex);
    }

    public void deleteFatMap() {
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] != null) {
                if (indexes[i].size() > 11) indexes[i] = null;
            }
        }
    }

    public Integer min(List<Integer> scores) {
        Integer min = scores.get(0);
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) != null) {
                min = scores.get(i);
                break;
            }
        }
        for (Integer score : scores) {
            if (score != null) {
                if (min > score) min = score;
            }
        }
        return min;
    }

    public HashMap<String, MultivaluedMap<Object, Integer>> readforHashMap(List<Integer> listIndex) {
        String[] trip;
        String line;
        List<Object> tmp;
        int acc = 0;
        String cvsSplitBy = ",";
        MultivaluedMap<Object, Integer> htmp;
        HashMap<String, MultivaluedMap<Object, Integer>> index = new HashMap<>();
        for (Integer indice : listIndex) {
            index.put(CSVHelper.getNameIndexes().get(indice), new MultivaluedHashMap<>());
        }
        int line_nb = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                if (line_nb < line_max) {
                    trip = line.split(cvsSplitBy);
                    tmp = new ArrayList<>(CSVHelper.read(trip));
                    for (Integer indice : listIndex) {
                        htmp = index.get(CSVHelper.getNameIndexes().get(indice));
                        if (htmp.containsKey(tmp.get(listIndex.get(acc)))) {
                            htmp.add(tmp.get(listIndex.get(acc)), (Integer) tmp.get(0));
                        } else {
                            htmp.putSingle(tmp.get(listIndex.get(acc)), (Integer) tmp.get(0));
                        }
                        acc++;
                    }
                    acc = 0;
                }
                line_nb++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return index;

    }

    public List<Integer> getScoresForIndexing() {
        ArrayList<Integer> scores = new ArrayList<>();
        int max = -1;
        int min = -1;
        for (int ind = 1; ind < indexes.length; ind++) {
            if (indexes[ind] != null) {
                for (Integer nbIds : indexes[ind].values()) {
                    if (max < nbIds) max = nbIds;
                    if (min > nbIds) min = nbIds;
                }
                scores.add(indexes[ind].size() + (max - min));
                max = -1;
                min = -1;
            } else scores.add(null);
        }
        return scores;
    }
}