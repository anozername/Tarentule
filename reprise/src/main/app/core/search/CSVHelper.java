package main.app.core.search;

import main.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVHelper {

    private static ArrayList<Integer> posDateTime = new ArrayList<>();
    private static ArrayList<Integer> posDouble = new ArrayList<>();
    private static List<String> nameIndexes;
    private static List<Object> types = new ArrayList<>();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    private static final String csvFile = Main.file_path;

    private static HashMap<Object, Integer>[] indexes;

    public static ArrayList<Integer> getPosDateTime() {
        return posDateTime;
    }

    public static ArrayList<Integer> getPosDouble() {
        return posDouble;
    }

    public static List<String> getNameIndexes() {
        return nameIndexes;
    }

    public static List<Object> getTypes() {
        return types;
    }

    public static HashMap<Object, Integer>[] getIndexes() {
        return indexes;
    }

    public static List<Object> read(Object[] line) {
        List<Object> tmp = new ArrayList<>(Arrays.asList(line));
        Optional<Integer> it;
        for (Integer i : posDateTime) {
            try {
                tmp.set(i, sdf.parse(line[i].toString()));
            }
            catch (Exception e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
        }
        return tmp;
    }

    public static void determineColumnsAndTypes() {
        List<Integer> posIndex = new ArrayList<>();
        HashMap<Object, Integer> mapTMP = new HashMap<>();
        Object valTMP;
        String line = "";
        String cvsSplitBy = ",";
        List<Object> tmp;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            line = "id," + br.readLine();
            String[] trip = line.split(cvsSplitBy);
            nameIndexes = new ArrayList<String>(Arrays.asList(trip));

            if ((line = br.readLine()) != null) {
                line = 1 + "," + line;
                trip = line.split(cvsSplitBy);
                indexes = new HashMap[trip.length];
                tmp = new ArrayList<>(Arrays.asList(trip));
                for (int i=0; i<trip.length; i++) {
                    valTMP = casting(trip[i], i);
                    tmp.set(i, valTMP);
                    indexes[i] = new HashMap<>();
                }
                //res.add(tmp.toArray());
                tmp.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object casting(String data, int i) {
        Optional<Date> dt = CastHelper.castToDate(data);
        Optional<Double> db = CastHelper.castToDouble(data);
        Optional<Integer> it;
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
