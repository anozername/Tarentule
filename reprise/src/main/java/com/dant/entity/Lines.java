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

    public void insert(Object[] lines) {
        this.add(lines);
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
}
