package com.dant.entity;

import java.time.LocalDateTime;
import java.util.*;

public class Lines extends ArrayList<Object[]>{
    private static final int posID = 0;
    private static int[] posIndex;
    private static Object[] nameIndex;
    private static Object[] types;

    public Lines(int[] posIndex, Object[] nameIndex, List<Object[]> lines, Object[] types) {
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

    public int[] getPosIndex() {
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

    public int getPosNameIndex(Object name) {
        for (int i=0; i<nameIndex.length; i++) {
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
    
    /* return lines on all data only - obsolete */
    public Lines getLines(List<Integer> ids) {
    	ArrayList<Object[]> res = new ArrayList<Object[]>();
    	Object[] line;
        for (Integer i : ids) {
            if ((line = rechercheDicho(i)) != null) {
                res.add(line);
            }
        }
        return new Lines(res);
    }

    /* return lines matching the ids */
    public Lines getLinesByIDS(List<Integer> ids) {
        ArrayList<Object[]> res = new ArrayList<Object[]>();
        for (Integer i : ids) {
            res.add(get(i));
        }
        return new Lines(res);
    }

    //@IDEA peut etre rechercher a partir de sublines a chaque fois car ids donnés dans l'ordre
    public Object[] rechercheDicho(int val){

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
        while(!trouve && ((ifin - id) > 1)){

            im = (id + ifin)/2;
            tmp = (Integer)get(im)[posID];
            trouve = (tmp == val);

            if(tmp > val) ifin = im;
            else id = im;
        }

        /* test conditionnant la valeur que la fonction va renvoyer */
        if((Integer)get(id)[posID] == val) return(get(id));
        else return null;

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
        for (Object[] line : this) {
            sb.append("[");
            for (Object e : line) {
                sb.append(e.toString() + ", ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
    
}
