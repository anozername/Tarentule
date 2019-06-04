package main.app.core.entity;

import main.app.core.search.CSVHelper;
import main.app.core.search.GBHelper;

import java.util.*;

public class Lines extends ArrayList<Object[]> {
    private static final int posID = 0;
    private static Integer[] posIndex;
    private static Object[] nameIndex;
    private static Object[] types;

    public Lines(Integer[] posIndex, Object[] nameIndex, List<Object[]> lines, Object[] types) {
        super();
        this.posIndex = posIndex;
        this.nameIndex = nameIndex;
        this.types = types;
        this.addAll(lines);
    }

    public Lines(List<Object[]> list) {
        super();
        this.addAll(list);
    }

    public Lines() {
        super();
    }

    public static Lines createLines(List<List<Object[]>> list) {
        Lines res = new Lines();
        for (List<Object[]> l : list) {
            res.addAll(l);
        }
        return res;
    }

    public void cast() {
        int acc = 0;
        for (Object[] line : this) {
            set(acc, CSVHelper.read(line).toArray());
            acc++;
        }
    }

    public Integer[] getPosIndex() {
        return posIndex;
    }

    public int getPosID() {
        return posID;
    }

    public Object[] getNameIndex() {
        return nameIndex;
    }

    public Object[] getTypes() {
        return types;
    }

    public static int getPosNameIndex(Object name) {
        for (int i = 0; i < nameIndex.length; i++) {
            if (name.equals(nameIndex[i])) {
                return i;
            }
        }
        return -1;
    }

    /********************************************************		insert		*/

    public void setLines(List<Object[]> lines) {
        this.clear();
        this.addAll(lines);
    }

    /********************************************************		find		*/

    public Lines AND(Lines lines) {
        int compare;
        Lines res = new Lines();
        for (Object[] ls1 : this) {
            for (Object[] ls2 : lines) {
                if ((compare = ((Integer)ls1[posID]).compareTo((Integer)ls2[posID])) == 0) {
                    res.add(ls1);
                }
                if (compare < 0) break;
            }
        }
        return res;
    }

    public Lines OR(Lines lines, List<String> groupBy) {
        if (groupBy.isEmpty()) return OR(lines);
        else return ORGB(lines, groupBy);
    }

    public Lines ORGB(Lines lines, List<String> groupBy) {
        Lines res = new Lines();
        List<Integer> indicesGroup = new ArrayList<>();
        res.addAll(this);
        int acc = 0;
        if (this.isEmpty()) return lines;
        for (String attribute : groupBy) {
            indicesGroup.add(CSVHelper.getNameIndexes().indexOf(attribute));
        }
        for (Object[] line : lines) {
            if (!rechercheDicho((Integer) line[posID])) {
                acc = GBHelper.placeToInsert(indicesGroup, line, res);
                res.add(acc, line);
                System.out.println(res);
            }
        }
        return res;
    }

    public Lines OR(Lines lines) {
        Lines res = new Lines();
        res.addAll(this);
        if (this.isEmpty()) return lines;
        for (Object[] line : lines) {
            if (!rechercheDicho((Integer)line[posID])) res.add(line);
        }
        return res;
    }

    public Lines getLinesFormatted(List<String> groupBy) {
        List<List<Object[]>> groupedRes = new ArrayList<>();
        ArrayList<Object[]> res;
        List<Integer> indicesGroup = new ArrayList<>();
        boolean added = false;
        int satisfaction = 0;
        //if (groupBy.size() == 1 && groupBy.get(0).equals(()))
        for (String attribute : groupBy) {
            indicesGroup.add(CSVHelper.getNameIndexes().indexOf(attribute));
        }
            for (Object[] line : this) {
                for (List<Object[]> li : groupedRes) {
                    for (Integer i : indicesGroup) {
                        if (line[i].equals(li.get(0)[i])) {
                            satisfaction++;
                        } else satisfaction = 0;
                    }
                    if (satisfaction == indicesGroup.size()) {
                        li.add(line);
                        added = true;
                    }
                    satisfaction = 0;
                }
                if (!added) {
                    res = new ArrayList<>();
                    res.add(line);
                    groupedRes.add(res);
                }
                added = false;
            }
        res = new ArrayList<>();
        for (List<Object[]> l : groupedRes) {
            res.addAll(l);
        }
        return new Lines(res);
    }

    public int getPosName(String name) {
        for (int i = 0; i < nameIndex.length; i++) {
            if (name.equals(nameIndex[i].toString())) {
                return i;
            }
        }
        return -1;
    }

    public Lines getLinesWithSelect(List<String> selection) {
        if (selection.size() == 1 && selection.get(0).equals("*")) {
            return this;
        }
        ArrayList<Object[]> res = new ArrayList<Object[]>();
        Object[] selectLine;
        int pos;
        List<Integer> select = new ArrayList<>();
        for (String attribute : selection) {
            if ((pos = CSVHelper.getNameIndexes().indexOf(attribute)) != -1 ){
                select.add(pos);
            }
        }

        for (Object[] line : this) {
            selectLine = new Object[select.size()];
            for (int x = 0; x < select.size(); x++) {
                selectLine[x] = line[select.get(x)];
            }
            res.add(selectLine);
        }
        return new Lines(res);
    }

    public Integer getCountWithSelect(String selection) {
        return this.size();
    }

    public Double getSumWithSelect(String selection) {
        Lines l = new Lines();
        int pos;
        Double res = 0.0;
        if ((pos = CSVHelper.getNameIndexes().indexOf(selection)) != -1 && CSVHelper.getTypes().get(pos).equals("double")) {
            for (Object[] line : this) {
                if (line[pos] instanceof Integer) {
                    res += (double)((Integer)line[pos]).intValue();
                }
                else {
                    res += (Double)line[pos];
                }
            }
        }
        return res;
    }

    public Double getAvgWithSelect(String selection) {
        System.out.println(getSumWithSelect(selection) + " " + getCountWithSelect(selection));
        return getSumWithSelect(selection) / getCountWithSelect(selection);
    }

    public Lines getMinWithSelect(String selection) {
        Object[] selectLine = get(0);
        Lines l = new Lines();
        int pos;
        double val1;
        double val2;
        if ((pos = CSVHelper.getNameIndexes().indexOf(selection)) != -1 && CSVHelper.getTypes().get(pos).equals("double")) {
            for (Object[] line : this) {
                if (selectLine[pos] instanceof Integer) val1 = (double)((Integer)selectLine[pos]).intValue();
                else val1 = (Double)selectLine[pos];
                if (line[pos] instanceof Integer) val2 = (double)((Integer)line[pos]).intValue();
                else val2= (Double)line[pos];
                if (val1 > val2) selectLine = line;
            }
        }
        l.add(selectLine);
        return l;
    }

    public Lines getMaxWithSelect(String selection) {
        Object[] selectLine = get(0);
        Lines l = new Lines();
        int pos;
        double val1;
        double val2;
        if ((pos = CSVHelper.getNameIndexes().indexOf(selection)) != -1 && CSVHelper.getTypes().get(pos).equals("double")) {
            for (Object[] line : this) {
                if (selectLine[pos] instanceof Integer) val1 = (double)((Integer)selectLine[pos]).intValue();
                else val1 = (Double)selectLine[pos];
                if (line[pos] instanceof Integer) val2 = (double)((Integer)line[pos]).intValue();
                else val2= (Double)line[pos];
                if (val1 < val2) selectLine = line;
            }
        }
        l.add(selectLine);
        return l;
    }


    //@IDEA peut etre rechercher a partir de sublines a chaque fois car ids donnés dans l'ordre
    public boolean rechercheDicho(Integer val) {

        /* déclaration des variables locales à la fonction */
        boolean trouve;
        int id;
        int ifin;
        int im;
        Integer tmp;

        /* initialisation de ces variables avant la boucle de recherche */
        trouve = false;
        id = 0;
        ifin = size();

        /* boucle de recherche */
        while (!trouve && ((ifin - id) > 1)) {

            im = (id + ifin) / 2;
            tmp = (Integer) get(im)[posID];
            trouve = (tmp == val);

            if (tmp > val) ifin = im;
            else id = im;
        }

        /* test conditionnant la valeur que la fonction va renvoyer */
        if (((Integer)get(id)[posID]).equals(val)) return true;
        else return false;

    }

    public ArrayList<Object[]> getLines(int pos, Object value) {
        ArrayList<Object[]> res = new ArrayList<Object[]>();
        for (Object[] line : this) {
            if (line[pos].equals(value)) {
                res.add(line);
            }
        }
        return res;
    }

    /********************************************************		print		*/

    public String toString() {
        StringBuffer sb = new StringBuffer();
        /*
        for (Object[] line : this) {
            sb.append("[");
            for (Object e : line) {
                sb.append(e.toString() + ", ");
            }
            sb.append("]");
            sb.append("\n");
        }
        */
        for (Iterator<Object[]> iter = this.iterator(); iter.hasNext();) {
            Object[] line = iter.next();
            sb.append("[");
            for (Object e : line) {
                sb.append(e.toString() + ", ");
            }
            sb.append("]");
            if (iter.hasNext()){
                sb.append("\n");
            }

        }
        return sb.toString();
    }

    public Lines computeResults(Lines l, int compute) {
        //if (l == null) return new ArrayList<>();
        Lines list = new Lines();
        if (compute == 1) {
            for (Object[] iq1 : this) {
                for (Object[] iq2 : l) {
                    if (iq1[posID].equals(iq2[posID])) {
                        list.add(iq2);
                        break;
                    }
                }
            }
        }
        if (compute == 2) {
            list.addAll(this);
            if (this.isEmpty()) return l;
            for (Object[] line : l) {
                if (!rechercheDicho((Integer)line[posID])) list.add(line);
            }
        }
        return list;
    }
}
