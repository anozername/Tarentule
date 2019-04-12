package com.dant.entity;

import java.util.*;

public class Lines extends ArrayList<Object[]>{
    private int posID;
    private int[] posIndex;
    private String[] nameIndex;

    public Lines(int posID, int[] posIndex, String[] nameIndex) {
        super();
        this.posID = posID;
        this.posIndex = posIndex;
        this.nameIndex = nameIndex;
    }

    public int getPosID() {
        return posID;
    }

    public int[] getPosIndex() {
        return posIndex;
    }

    public String[] getNameIndex() {
        return nameIndex;
    }
    
    /********************************************************		insert		*/
    
    public void insert(Object[] lines) {
        this.add(lines);
    }
    
    /********************************************************		find		*/
    
    /* return lines matching the ids TODO: EFFICIENT SEARCH (dichotomie?) */
    public ArrayList<Object[]> getLines(Integer[] ids) {
    	ArrayList<Object[]> res = new ArrayList<Object[]>();
    	for (Object[] line : this) {
    		if (Arrays.binarySearch(ids, line[posID]) != -1) {
    			res.add(line);
    		}
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
    
}
