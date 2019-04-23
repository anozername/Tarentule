package com.dant.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.lang.reflect.Array;
import java.util.*;
import java.text.SimpleDateFormat;

public class CSVReader {
    private static ArrayList<Integer> posDateTime = new ArrayList<>();
    private static ArrayList<Integer> posDouble = new ArrayList<>();
    private static ArrayList<Integer> posInteger = new ArrayList<>();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

    public static List<Object[]> readLines() {
        String csvFile = "input.csv";
        String line = "";
        String cvsSplitBy = ",";
        List<Object[]> res = new ArrayList<Object[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            List<Object> tmp;
            Integer id = 0;

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
                for (int i=0; i<trip.length; i++) {
                    tmp.add(casting(trip[i].toString(), i));
                }
                res.add(tmp.toArray());
                tmp.clear();
                id++;
            }

            while ((line = br.readLine()) != null) {

                trip = line.split(cvsSplitBy);
                tmp = new ArrayList<>(Arrays.asList(trip));

                /*  dans le csv actuel:
                    3 7 13 15 16: pos integer value
                    2 3: pos date value
                    4 5 6 9 10 12 14: double value
                    17: float value
                 */
                //l.set(3, Integer.parseInt(trip[3].toString()));
                for (Integer i : posDateTime) {
                    try {
                        tmp.set(i, sdf.parse(trip[i].toString()));
                    }
                    catch (Exception e) {
                        //faire un truc mais en meme temps la position est connue en avance
                    }
                }

                for (Integer i : posInteger) {
                    try {
                        tmp.set(i, Integer.parseInt(trip[i].toString()));
                    }
                    catch (Exception e) {
                        //faire un truc mais en meme temps la position est connue en avance
                    }
                }

                for (Integer i : posDouble) {
                    try {
                        tmp.set(i,  Double.parseDouble(trip[i].toString()));
                    }
                    catch (Exception e) {
                        //faire un truc mais en meme temps la position est connue en avance
                    }
                }
                tmp.add(0, id);

                res.add(tmp.toArray());
                tmp.clear();
                id++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /* pourrait servir de test de cast pour determiner les positions castable en date */

    public static Object casting(String data, int i) {
        Optional<Date> dt = castToDate(data);
        Optional<Double> db = castToDouble(data);
        Optional<Integer> it = castToInteger(data);
        if (dt.isPresent()) {
            posDateTime.add(i);
            return dt.get();
        }
        if (it.isPresent()) {
            posInteger.add(i);
            return it.get();
        }
        if (db.isPresent()) {
            posDouble.add(i);
            return db.get();
        }
        return data;
    }

    public static Optional<Date> castToDate(String data) {
        try {
            return Optional.of(sdf.parse(data));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> castToDouble(String data) {
        try {
            return Optional.of(Double.parseDouble(data));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<Integer> castToInteger(String data) {
        try {
            return Optional.of(Integer.parseInt(data,10));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}