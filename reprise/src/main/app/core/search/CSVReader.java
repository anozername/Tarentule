package main.app.core.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;
import java.text.SimpleDateFormat;

public class CSVReader {
    private static ArrayList<Integer> posDateTime = new ArrayList<>();
    private static ArrayList<Integer> posDouble = new ArrayList<>();
    private static ArrayList<Integer> posInteger = new ArrayList<>();
    private static List<Object> types = new ArrayList<>();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

    private static List<Double> weights = new ArrayList<>();
    private static List<String> valuesToCompare = new ArrayList<>();

    public static List<Double> getWeights() {
        return weights;
    }

    public static List<Object[]> readLines() {
        posDateTime.clear();
        posDouble.clear();
        posInteger.clear();
        types.clear();
        String csvFile = "input.csv";
        String line = "";
        String cvsSplitBy = ",";
        List<Object[]> res = new ArrayList<Object[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            Optional<Integer> it;
            List<Object> tmp;
            Integer id = 1;

            line = br.readLine();
            Object[] trip = line.split(cvsSplitBy);
            tmp = new ArrayList<>(Arrays.asList(trip));
            tmp.add(0, "id");
            res.add(tmp.toArray());
            //tmp.clear();

           /* pourrait servir au cas ou on decide de lire tous les csv
               la deuxieme ligne servirait alors a determiner les index des types parsables */

            if ((line = br.readLine()) != null) {

                trip = line.split(cvsSplitBy);
                tmp = new ArrayList<>(Arrays.asList(trip));
                tmp.add(0, id);
                types.add("double");
                for (int i=1; i<tmp.size(); i++) {
                    tmp.set(i, casting(tmp.get(i).toString(), i));
                    weights.add(0.0);
                    valuesToCompare.add(tmp.get(i).toString());
                }

                res.add(tmp.toArray());
                tmp.clear();
                id++;
            }

            while ((line = br.readLine()) != null) {

                trip = line.split(cvsSplitBy);
                tmp = new ArrayList<>(Arrays.asList(trip));
                tmp.add(0, id);

                for (int pos=0; pos<tmp.size(); pos++) {
                    if (tmp.get(pos).toString().equals(valuesToCompare.get(pos))) {
                        weights.set(pos, weights.get(pos) + 0.1);
                    }
                    else {
                        weights.set(pos, weights.get(pos) - 0.04);
                    }
                    if ((Math.random() * ((100 - 1) + 1)) + 1 < 10) valuesToCompare.set(pos, tmp.get(pos).toString());
                }
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


                res.add(tmp.toArray());
                tmp.clear();
                id++;
            }
            res.add(types.toArray());


        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
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

}