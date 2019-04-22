package com.dant.entity;

import java.time.LocalDateTime;
import java.util.*;

public class Lines extends ArrayList<Object[]>{
    private int[] posIndex;
    private Object[] nameIndex;

    public Lines(int[] posIndex, Object[] nameIndex) {
        super();
        this.posIndex = posIndex;
        this.nameIndex = nameIndex;
    }

    public Lines(int[] posIndex, Object[] nameIndex, List<Object[]> list) {
        super();
        this.addAll(list);
        this.posIndex = posIndex;
        this.nameIndex = nameIndex;
    }

    public int[] getPosIndex() {
        return posIndex;
    }

    public Object[] getNameIndex() {
        return nameIndex;
    }
    
    /********************************************************		insert		*/
    
    public void insert(Object[] lines) {
        this.add(lines);
    }
    
    /********************************************************		find		*/
    
    /* return lines matching the ids */
    public ArrayList<Object[]> getLines(ArrayList<Integer> ids) {
    	ArrayList<Object[]> res = new ArrayList<Object[]>();
        for (Integer i : ids) {
            res.add(get(i));
        }
        return res;
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
                sb.append(e.getClass() + ", ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
    
}
