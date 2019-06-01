package main.app.core.search;

import main.app.core.entity.Index;
import main.app.core.entity.Lines;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.text.SimpleDateFormat;
import java.util.*;

public class Parser {

    private static Map<String, Object> indexTMP = new HashMap<>();
    private static Map<String, Object> notIndexTMP = new HashMap<>();
    private static List<String> selection = new ArrayList<>();
    private static List<String> groupBy = new ArrayList<>();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    private static Index index;

    public Parser(Index index) {
        this.index = index;
    }

    public static void groupBy(List<String[]> cmds) {
        for (String[] cmd : cmds) {
            for (int i = 0; i < cmd.length ; i++) {
                if (cmd[i].equals("GROUPBY")) {
                    for (int j = i+1; j < cmd.length ; j++) groupBy.add(cmd[j].replace(",", ""));
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

    public String getLinesSelect(Lines lines, int select) {
        switch (select) {
            case 0:
                return lines.getLinesWithSelect(selection).toString();
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
        return lines.toString();
    }

    public String parse(String command) {
        indexTMP.clear();
        notIndexTMP.clear();
        selection.clear();
        groupBy.clear();
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
        int typeSelection = 0;
        groupBy(cmds);
        if (cmds.get(accCMDS)[0].equals("SELECT")) {
            typeSelection = selectType(cmds.get(accCMDS)[acc].replace(",", ""));
            if (typeSelection != 0) {
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
                    resultsLines = resultsLines.AND(getResults());
                    and = false;
                }
                if (or) {
                    resultsLines = resultsLines.OR(getResults());
                    or = false;
                }
                //ou = 0 et taille = 1 mais pas tout de suite OK
                if (accCMDS == 2 || (accCMDS == 4 && typeSelection != 0)) {
                    resultsLines = getResults();
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


        return getLinesSelect(resultsLines, typeSelection);
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
            //peut etre set des lines pour recherche ici => creer fct param lines dans csvfinder
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
                } else {
                    queriesAND.put(query.getKey(), query.getValue());
                }
            }
            if (acc != 1) {
                if (!queriesAND.isEmpty()) {
                    linesTMP = index.getWithoutIndexGroupBy(queriesAND, groupBy, 1, linesTMP).computeResults(linesTMP, 1);
                }
                if (!queriesOR.isEmpty()) {
                    linesTMP = index.getWithoutIndexGroupBy(queriesOR, groupBy, 2).computeResults(linesTMP, 2);
                }
            }
            else {
                if (!queriesAND.isEmpty()){
                    linesTMP = index.getWithoutIndexGroupBy(queriesAND, groupBy, 1);
                }
                if (!queriesOR.isEmpty()){
                    linesTMP = index.getWithoutIndexGroupBy(queriesOR, groupBy, 2).computeResults(linesTMP, 2);
                }
            }
            acc++;
        }
        else {

           /* dans le cas ou toutes les recherches sont indexees
            il faut donc formater le resultat par les attributs du grouby
            @TODO regarder si les recherches portent egalement sur le groupby = ne rien faire

            */

            if (!groupBy.isEmpty()) {
                linesTMP = linesTMP.getLinesFormatted(groupBy);
            }
        }
        //return linesTMP.toString();
        /*return index.getWithoutIndexGroupBy(notIndexTMP, groupBy).toString();
        les 2 queries reoturnent le bon resultat mais ne se computent pas */
        //if (!selection.isEmpty()) return linesTMP.getLinesWithSelect(selection);
        return linesTMP;
    }

    public static void pushCommand(String cmd, Object value) {
        String[] splitentry;
        String command = cmd;
        if ((splitentry = cmd.split("[ ]")).length == 2) command = splitentry[1];

        if (index.getHashmap().containsKey(command)) {
            indexTMP.put(cmd, value);
        }
        else {
            notIndexTMP.put(cmd, value);
        }
    }
}
