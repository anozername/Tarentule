package com.dant.entity;

import java.time.LocalDateTime;
import java.util.*;

public class Lines extends ArrayList<Object[]>{
    private int posID = 0;
    private int[] posIndex;
    private final Object[] nameIndex;
    private final Object[] types;

    public Lines(int[] posIndex, Object[] nameIndex, Object[] types) {
        super();
        this.posIndex = posIndex;
        this.nameIndex = nameIndex;
        this.types = types;
    }

    public Lines(int[] posIndex, Object[] nameIndex, List<Object[]> list, Object[] types) {
        super();
        this.addAll(list);
        this.posIndex = posIndex;
        this.nameIndex = nameIndex;
        this.types = types;
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
    
    public void insert(Object[] lines) {
        this.add(lines);
    }
    
    /********************************************************		find		*/
    
    /* return lines matching the ids */
    public Lines getLines(List<Integer> ids) {
    	ArrayList<Object[]> res = new ArrayList<Object[]>();
        for (Integer i : ids) {
            res.add(get(i));
        }
        return new Lines(null,null, res, null);
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
