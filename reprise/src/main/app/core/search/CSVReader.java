package main.app.core.search;

import main.app.core.entity.HashMapValues;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;
import java.text.SimpleDateFormat;

public class CSVReader {

    private String csvFile = "input.csv";
    private static ArrayList<Integer> posDateTime = new ArrayList<>();
    private static ArrayList<Integer> posDouble = new ArrayList<>();
    private static ArrayList<Integer> posInteger = new ArrayList<>();
    private static List<Object> types = new ArrayList<>();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

   /* private static List<Double> weights = new ArrayList<>();
    private static List<Integer> changeValue = new ArrayList<>();
    //private static List<String> valuesToCompare = new ArrayList<>();
    private static List<List<String>> valuesToCompare = new ArrayList<>();
    */
    private static HashMap<Object, Integer>[] indexes;

    private static List<String> nameIndexes;

    /*public static List<Double> getWeights() {
        return weights;
    }

    public static List<Integer> getChangeValue() {
        return changeValue;
    }*/

    public CSVReader(String csvFile) {
        this.csvFile = csvFile;
    }

    public static List<String> getNameIndexes() {
        return nameIndexes;
    }

    public static List<Object> getTypes() {
        return types;
    }

    public static Map<Object, Integer>[] getIndexes() {
        return indexes;
    }

    public List<Object> read(Object[] line, Integer id) {
        List<Object> tmp = new ArrayList<>(Arrays.asList(line));
        tmp.add(0, id);
        Optional<Integer> it;
        for (Integer i : posDateTime) {
            try {
                tmp.set(i, sdf.parse(line.toString()));
            }
            catch (Exception e) {
                //faire un truc mais en meme temps la position est connue en avance
            }
        }

        for (Integer i : posDouble) {
            try {
                it = CastHelper.castToInteger(line[i].toString());
                if (it.isPresent()) {
                    tmp.set(i, Integer.parseInt(line[i].toString()));
                }
                else {
                    tmp.set(i, Double.parseDouble(line[i].toString()));
                }
            }
            catch (Exception e) {
                //faire un truc mais en meme temps la position est connue en avance
            }
        }
        return tmp;
    }

    public MultivaluedMap<Object, Integer>[] readForIndexing() {
        posDateTime.clear();
        posDouble.clear();
        posInteger.clear();
        types.clear();

        List<Integer> posIndex = new ArrayList<>();
        HashMap<Object, Integer> mapTMP = new HashMap<>();
        Integer valueTMP;
        Object valTMP;
        ArrayList<Integer> scoresTMP;
        String line = "";
        String cvsSplitBy = ",";
        List<Object> tmp;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            Integer id = 1;

            line = "id," + br.readLine();
            String[] trip = line.split(cvsSplitBy);
            nameIndexes = new ArrayList<String>(Arrays.asList(trip));
            indexes = new HashMap[trip.length];

            if ((line = br.readLine()) != null) {
                line = id + "," + line;
                trip = line.split(cvsSplitBy);
                tmp = new ArrayList<>(Arrays.asList(trip));
                tmp.add(0, id);
                types.add("double");
                for (int i=1; i<trip.length; i++) {
                    valTMP = casting(trip[i], i);
                    tmp.set(i, valTMP);
                    mapTMP = new HashMap<>();
                    mapTMP.put(valTMP, 1);
                    indexes[i] = mapTMP;
                   /*  weights.add(0.0);
                    changeValue.add(0);
                    valuesToCompare.add(tmp.get(i).toString());*/
                }
                indexes[0] = null;

                //res.add(tmp.toArray());
                //tmp.clear();
                id++;
            }

            while ((line = br.readLine()) != null) {
                line = id.toString() + "," + line;
                tmp = new ArrayList<>(read(line.split(cvsSplitBy), id));
                for (int pos=1; pos<indexes.length; pos++) {
                    //lun ou lautre mais pas les deux
                    //valueTMP = tmp.get(pos);
                    if (indexes[pos] != null) {
                        if (!indexes[pos].containsKey(tmp.get(pos))) {
                            indexes[pos].put(tmp.get(pos), 1);
                        } else {
                            valueTMP = indexes[pos].get(tmp.get(pos)) + 1;
                            indexes[pos].put(tmp.get(pos), valueTMP);
                        }

                    }
                }
                //res.add(tmp.toArray());
                tmp.clear();
                id++;
                deleteFatMap();
            }
           // res.add(types.toArray());
            ArrayList<Integer> scores = new ArrayList<>(getScoresForIndexing());
            scores.add(0, null);
            scoresTMP = new ArrayList<>(scores);
            Object min;
            for (int nbIndex=0; nbIndex<2; nbIndex++) {
                min = min(scoresTMP);
                //hashMap.put(nameIndexes.get(scores.indexOf(max)), scores.indexOf(max));
                posIndex.add(scores.indexOf(min));
                scoresTMP.remove(min);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return readforHashMap(posIndex);
    }

    public void deleteFatMap() {
        for (int i=0; i<indexes.length; i++) {
            if (indexes[i] != null) {
                if (indexes[i].size() > 11) indexes[i] = null;
            }
        }
    }

    public Integer min(List<Integer> scores) {
        Integer min = scores.get(0);
        for (int i=0; i<scores.size(); i++) {
            if (scores.get(i) != null) {min = scores.get(i); break;}
        }
        for (Integer score : scores) {
            if (score != null) {
                if (min > score) min = score;
            }
        }
        return min;
    }

    public MultivaluedMap<Object, Integer>[] readforHashMap(List<Integer> listIndex) {
        Object[] trip;
        String line;
        Integer id = 1;
        List<Object> tmp;
        String cvsSplitBy = ",";
        List<Integer> ids;
        MultivaluedMap<Object, Integer>[] index = new MultivaluedHashMap[listIndex.size()];
        for (int indice=0; indice<listIndex.size(); indice++) index[indice] = new MultivaluedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                tmp = new ArrayList<>(read(line.split(cvsSplitBy), id));
                for (int i=0; i<listIndex.size(); i++) {
                    if (index[i].containsKey(tmp.get(listIndex.get(i)))) {
                        ids = index[i].get(tmp.get(listIndex.get(i)));
                        ids.add(id);
                    }
                    else {
                        ids = new ArrayList<>(id);
                    }
                    index[i].put(tmp.get(listIndex.get(i)), ids);
                    //ids.clear();
                }
                id++;
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
        for (int ind=1; ind<indexes.length; ind++) {
            if (indexes[ind] != null) {
                for (Integer nbIds : indexes[ind].values()) {
                    if (max < nbIds) max = nbIds;
                    if (min > nbIds) min = nbIds;
                }
                scores.add(indexes[ind].size() + (max - min));
                max = -1;
                min = -1;
            }
            else scores.add(null);
        }
        return scores;
    }

    public Object casting(String data, int i) {
        Optional<Date> dt = CastHelper.castToDate(data);
        Optional<Double> db = CastHelper.castToDouble(data);
        Optional<Integer> it = CastHelper.castToInteger(data);
        if (dt.isPresent()) {
            posDateTime.add(i);
            types.add("date");
            return dt.get();
        }
        if (db.isPresent()) {
            posDouble.add(i);
            types.add("double");
            it = CastHelper.castToInteger(data);
            if (it.isPresent()) return it.get();
            return db.get();
        }
        types.add("string");
        return data;
    }

    /*public static int getIndex() {
        return getBestPosForIndex(getIndicesMax());
    }

    public static int getBestPosForIndex(List<Integer> posHighestWeights) {
        int max = changeValue.get(posHighestWeights.get((0)));
        for (Integer i : posHighestWeights) {
            if (max < changeValue.get(i)) max = changeValue.get(i);
        }
        return changeValue.indexOf(max);
    }

    public static List<Integer> getIndicesMax() {
       // Double max = weights.get(0);
       // List<Double> tmp = new ArrayList<>(weights);
        List<Integer> res = new ArrayList<>();
        for (int i=0; i<weights.size(); i++) {
            if (weights.get(i) > 0) res.add(i);
        }
        /* for (int i=0; i<4; i++) {
            for (Double d : tmp) {
                if (max < d) max = d;
            }
            res.add(weights.indexOf(max));
            tmp.remove(max);
            max = tmp.get(0);
        }
        return res;
    }

    public void cleanValuesToCompare() {
        for (List<String> values : valuesToCompare) {
            if (values.size() > 50 )
        }
    }*/

}