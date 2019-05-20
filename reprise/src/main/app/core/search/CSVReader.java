package main.app.core.search;

import main.Main;
import main.app.core.entity.HashMapValues;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;
import java.text.SimpleDateFormat;

public class CSVReader {

    private static String csvFile = Main.file_path;
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

    private static List<Object> nameIndexes;

    /*public static List<Double> getWeights() {
        return weights;
    }

    public static List<Integer> getChangeValue() {
        return changeValue;
    }*/

    public static List<Object> getNameIndexes() {
        return nameIndexes;
    }

    public static List<Object> getTypes() {
        return types;
    }

    public static Map<Object, Integer>[] getIndexes() {
        return indexes;
    }
    public static List<String> readForIndexing(int debut, int fin) {
        posDateTime.clear();
        posDouble.clear();
        posInteger.clear();
        types.clear();

        HashMap hashMap = new HashMap();
        List<String> posIndex = new ArrayList<>();
        Object valTMP;
        HashMap<Object, Integer> mapTMP = new HashMap<>();

        ArrayList<Integer> scoresTMP;
        String line = "";
        String cvsSplitBy = ",";
        //List<Object[]> res = new ArrayList<Object[]>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            Optional<Integer> it;
            List<Object> tmp;
            Integer id = 1;

            Integer valueTMP;
            ArrayList<Integer> idsTMP;

            line = br.readLine();
            Object[] trip = line.split(cvsSplitBy);
            tmp = new ArrayList<>(Arrays.asList(trip));
            tmp.add(0, "id");
            nameIndexes = new ArrayList<>(tmp);
            //res.add(tmp.toArray());
            indexes = new HashMap[tmp.size()];
            tmp.clear();

           /* pourrait servir au cas ou on decide de lire tous les csv
               la deuxieme ligne servirait alors a determiner les index des types parsables */

            if ((line = br.readLine()) != null) {
                trip = line.split(cvsSplitBy);
                tmp = new ArrayList<>(Arrays.asList(trip));
                tmp.add(0, id);
                types.add("double");
                mapTMP.put(id, 1);
                indexes[0] = mapTMP;
                for (int i=1; i<tmp.size(); i++) {
                    valTMP = casting(tmp.get(i).toString(), i);
                    tmp.set(i, casting(tmp.get(i).toString(), i));
                    mapTMP = new HashMap<>();
                    mapTMP.put(casting(tmp.get(i).toString(), i), 1);
                    indexes[i] = mapTMP;
                   /*  weights.add(0.0);
                    changeValue.add(0);
                    valuesToCompare.add(tmp.get(i).toString());*/
                }

                //res.add(tmp.toArray());
                //tmp.clear();
                id++;
            }

            while ((line = br.readLine()) != null) {

                trip = line.split(cvsSplitBy);
                tmp = new ArrayList<>(Arrays.asList(trip));
                tmp.add(0, id);

                /* for (int pos=0; pos<tmp.size(); pos++) {
                    if (tmp.get(pos).toString().equals(valuesToCompare.get(pos))) {
                        weights.set(pos, weights.get(pos) + 0.1);
                    }
                    else {
                        weights.set(pos, weights.get(pos) - 0.04);
                        valuesToCompare.set(pos, tmp.get(pos).toString());
                        changeValue.set(pos, changeValue.get(pos) + 1);
                    }
                    //if ((Math.random() * ((100 - 1) + 1)) + 1 < 10) valuesToCompare.set(pos, tmp.get(pos).toString());
                }*/
                /*  dans le csv actuel:
                    3 7 13 15 16: pos integer value
                    2 3: pos date value
                    4 5 6 9 10 12 14: double value
                    17: float value
                 */
                //l.set(3, Integer.parseInt(trip[3].toString()));
                for (Integer i : posDateTime) {
                    try {
                        tmp.set(i, sdf.parse(tmp.get(i).toString()));
                    }
                    catch (Exception e) {
                        //faire un truc mais en meme temps la position est connue en avance
                    }
                }

                for (Integer i : posDouble) {
                    try {
                        it = CastHelper.castToInteger(tmp.get(i).toString());
                        if (it.isPresent()) {
                            tmp.set(i,  Integer.parseInt(tmp.get(i).toString()));
                        }
                        else {
                            tmp.set(i,  Double.parseDouble(tmp.get(i).toString()));
                        }
                    }
                    catch (Exception e) {
                        //faire un truc mais en meme temps la position est connue en avance
                    }
                }
                for (int pos=1; pos<indexes.length-1; pos++) {
                    //lun ou lautre mais pas les deux
                    //valueTMP = tmp.get(pos);
                    if (indexes[pos] != null) {
                        mapTMP = new HashMap<>();
                        //mapTMP.putAll(indexes[pos]);
                        if (!indexes[pos].containsKey(tmp.get(pos))) {
                            mapTMP.put(tmp.get(pos), 1);
                            indexes[pos] = mapTMP;
                            //indexes[pos].compute(tmp.get(pos), (key, value) -> value = 1);
                        }/* else {
                            valueTMP = indexes[pos].get(tmp.get(pos)) + 1;
                            indexes[pos].put(tmp.get(pos), valueTMP);
                        }*/

                    }
                }
                //res.add(tmp.toArray());
                tmp.clear();
                id++;
                deleteFatMap();
            }
           // res.add(types.toArray());
            ArrayList<Integer> scores = new ArrayList<>(getScoresForIndexing());
            //scores.remove(0);
            scoresTMP = new ArrayList<>(scores);
            Integer min;
            for (int nbIndex=0; nbIndex<1; nbIndex++) {
                min = min(scoresTMP);
                //hashMap.put(nameIndexes.get(scores.indexOf(max)), scores.indexOf(max));
                posIndex.add(scores.toString());
                scoresTMP.remove(min);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return posIndex;
    }

    public static void deleteFatMap() {
        for (int i=0; i<indexes.length; i++) {
            if (indexes[i].size() > 11) indexes[i] = null;
        }
    }

    public static Integer min(List<Integer> scores) {
        Integer min = scores.get(0);
        for (Integer score : scores) {
            if (min > score) min = score;
        }
        return min;
    }

    public static List<Integer> getScoresForIndexing() {
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
            else scores.add(999999999);
        }
        return scores;
    }

    public static Object casting(String data, int i) {
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