package main.app.core.search;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class CastHelper {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    public static Object casting(String data) {
        Optional<Date> dt = CastHelper.castToDate(data);
        Optional<Double> db = CastHelper.castToDouble(data);
        Optional<Integer> it;
        if (dt.isPresent()) {
            return dt.get();
        }
        if (db.isPresent()) {
            it = CastHelper.castToInteger(data);
            if (it.isPresent()) {
                return it.get();
            }
            return db.get();
        }
        return data;
    }
}
