package main.app.core.search;

import com.google.gson.Gson;
import main.app.core.entity.Index;
import main.app.core.entity.Lines;
import java.text.SimpleDateFormat;
import java.util.*;

public class Parser {

    private Map<String, Object> indexTMP = new HashMap<>();
    private Map<String, Object> notIndexTMP = new HashMap<>();
    private static List<String> selection = new ArrayList<>();
    private static int sel = 0;
    private static List<String> groupBy = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    private Index index;

    public Parser(Index index) {
        this.index = index;
    }

    public static List<String> getGroupBy() {
        return groupBy;
    }

    private void groupBy(List<String[]> cmds) {
        for (String[] cmd : cmds) {
            for (int i = 0; i < cmd.length ; i++) {
                if (cmd[i].equals("GROUPBY")) {
                    for (int j = i+1; j < cmd.length ; j++) {
                        groupBy.add(cmd[j].replace(",", ""));
                    }
                }
            }
        }
    }

    public int selectType(String cmd) {
        switch(cmd) {
            case "COUNT":
                return 1;
            case "MAX":
                return 2;
            case "MIN":
                return 3;
            case "AVG":
                return 4;
            case "SUM":
                return 5;
            default:
                return 0;
        }
    }

    public static String getLinesSelect(Lines lines) {
        System.out.println(sel);
        switch (sel) {
            case 0:
                return lines.getLinesWithSelect(selection).printer();
            case 1:
                return "count: " + lines.getCountWithSelect(selection.get(0)).toString();
            case 2:
                return "max :" + lines.getMaxWithSelect(selection.get(0));
            case 3:
                return "min :" + lines.getMinWithSelect(selection.get(0));
            case 4:
                return "avg :" + lines.getAvgWithSelect(selection.get(0));
            case 5:
                return "sum :" + lines.getSumWithSelect(selection.get(0));
        }
        return lines.printer();
    }

    public String parse(String command) {
        indexTMP.clear();
        notIndexTMP.clear();
        selection.clear();
        groupBy.clear();
        sel = 0;
        boolean and = false;
        boolean or = false;
        Lines resultsLines = new Lines();
        List<String[]> cmds = new ArrayList<>();
        String[] queries = command.split("[\\(||\\)]");
        for (String query : queries) {
            cmds.add(query.split("[ ]"));
        }
        String cmdTMP;
        int acc = 1;
        int accCMDS = 0;
        int ind;
        groupBy(cmds);
        if (cmds.get(accCMDS)[0].equals("SELECT")) {
            sel = selectType(cmds.get(accCMDS)[acc].replace(",", ""));
            if (sel != 0) {
                accCMDS++;
                acc = 0;
                while (acc < cmds.get(accCMDS).length) {
                    selection.add(cmds.get(accCMDS)[acc].replace(",", ""));
                    acc++;
                }
                accCMDS++;
            }
            else {
                while (!cmds.get(accCMDS)[acc].equals("WHERE")) {
                    selection.add(cmds.get(accCMDS)[acc].replace(",", ""));
                    acc++;
                }
            }

            if (cmds.size() > 1) {
                accCMDS++;
                acc = -1;
            }

            while (accCMDS < cmds.size()) {
                do {
                    acc++;
                    if (acc > 0) {
                        cmdTMP = cmds.get(accCMDS)[acc-1] + " " + cmds.get(accCMDS)[acc];
                    }
                    else {
                        cmdTMP = cmds.get(accCMDS)[acc];
                    }
                    if ((ind = CSVHelper.getNameIndexes().indexOf(cmds.get(accCMDS)[acc])) != -1) {
                        acc++;

                        if (cmds.get(accCMDS)[acc].equals("=")) {
                            acc++;
                            switch (CSVHelper.getTypes().get(ind).toString()) {
                                case "date":
                                    try {
                                        pushCommand(cmdTMP, sdf.parse(cmds.get(accCMDS)[acc].replace(",", "")));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "double":
                                    Optional<Integer> it = CastHelper.castToInteger(cmds.get(accCMDS)[acc].replace(",", ""));
                                    if (it.isPresent()) pushCommand(cmdTMP, it.get());
                                    else
                                        pushCommand(cmdTMP, Double.parseDouble(cmds.get(accCMDS)[acc].replace(",", "")));
                                    break;
                                case "string":
                                    pushCommand(cmdTMP, cmds.get(accCMDS)[acc].replace(",", ""));
                                    break;
                            }
                            acc++;
                        }
                    }
                } while (acc < cmds.get(accCMDS).length && (cmds.get(accCMDS)[acc].equals("AND") || cmds.get(accCMDS)[acc].equals("OR")));

                accCMDS++;
                if (and) {
                    resultsLines = resultsLines.AND(getResults(), groupBy);
                    and = false;
                }
                if (or) {
                    resultsLines = resultsLines.OR(getResults(), groupBy);

                    or = false;
                }

                //ou = 0 et taille = 1 mais pas tout de suite OK
                if (accCMDS == 2 || (accCMDS == 4 && sel != 0)) {
                    System.out.println("MMM");
                    resultsLines = getResults();
                    System.out.println("VA");
                }

                if (accCMDS < cmds.size()) {
                    if (cmds.get((accCMDS))[1].equals(("AND"))) {
                        and = true;
                    }
                    else {
                        if (cmds.get((accCMDS))[1].equals(("OR"))){
                            or = true;
                        }
                    }
                    acc = -1;
                    indexTMP.clear();
                    notIndexTMP.clear();
                    if ((cmds.get((accCMDS))[1].equals(("GROUPBY")))) {
                        break;
                    }
                    accCMDS++;
                }
                else{
                    break;
                }
            }
        }
        Gson gson = new Gson();

        return gson.toJson(resultsLines);
    }

    public Lines getResults() {
        int acc = 1;
        Results tmp = new Results();
        Lines linesTMP = new Lines();
        String[] splitentry;
        String key;
        int compute = 0;

        for (Map.Entry<String, Object> query : indexTMP.entrySet()) {
            if ((splitentry = query.getKey().split("[ ]")).length == 2) {
                key = splitentry[1];
                switch (splitentry[0]) {
                    case "AND":
                        compute = 1;
                        break;
                    case "OR":
                        compute = 2;
                        break;
                }
            }
            else {
                key = query.getKey();
            }

            if (acc != 1) {
                tmp = tmp.computeResults(index.getValueWithIndex(key, query.getValue()), compute);
            }
            else {
                tmp = new Results(index.getValueWithIndex(key, query.getValue()));
            }
            acc++;
        }
        linesTMP.addAll(index.findWithIDS(tmp));
        linesTMP.cast();
        if (!notIndexTMP.isEmpty()) {
            Map<String, Object> queriesOR = new HashMap<>();
            Map<String, Object> queriesAND= new HashMap<>();
            for (Map.Entry<String, Object> query : notIndexTMP.entrySet()) {
                if ((splitentry = query.getKey().split("[ ]")).length == 2) {
                    switch (splitentry[0]) {
                        case "AND":
                            queriesAND.put(splitentry[1], query.getValue());
                            break;
                        case "OR":
                            queriesOR.put(splitentry[1], query.getValue());
                            break;
                    }
                }
                else {
                    queriesAND.put(query.getKey(), query.getValue());
                }
            }
            if (acc != 1) {
                if (linesTMP.isEmpty() && indexTMP.isEmpty()) {
                    linesTMP = index.getWithoutIndexGroupBy(queriesAND, queriesOR, groupBy);
                }
                else {
                    linesTMP = index.getWithoutIndexGroupBy(queriesAND, queriesOR, groupBy, linesTMP).computeResults(linesTMP, 1);
                }
            }
            else {
                linesTMP = index.getWithoutIndexGroupBy(queriesAND, queriesOR, groupBy);
            }
            acc++;
        }
        else {
            if (!groupBy.isEmpty()) {
                linesTMP = linesTMP.getLinesFormatted(groupBy);
            }
        }
        return linesTMP;
    }

    public void pushCommand(String cmd, Object value) {
        String[] splitentry;
        String command = cmd;
        if ((splitentry = cmd.split("[ ]")).length == 2) {
            command = splitentry[1];
        }

        if (index.getHashmap().containsKey(command)) {
            indexTMP.put(cmd, value);
        }
        else {
            notIndexTMP.put(cmd, value);
        }
    }
}
