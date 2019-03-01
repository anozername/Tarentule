package com.dant.entity;

import java.io.Serializable;
import java.util.HashMap;

public class Table {

    private String name;
    private Column[] attributes;

    public Table(String name, Column[] attributes) {
        this.name = name;
        this.attributes = attributes;
        this.index = new HashMap();
    }

    // a redeclarer pour les types usuels
    public void setIndex(String attribute, Object value) {
        this.index.put(findIndex(attribute), value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Table table = (Table) o;

        return name.equals(table.name);
    }

    @Override
    public String toString() {
        return name + ": " + attributes.toString();
    }

    public String createTableString() {
        String sb = "CREATE TABLE " + name + " (";
        for (Column attribute : attributes) {
            sb += attributes.toString() + ",";
        }
        //enlever la derniere virgule
        sb = sb.subString(0, sb.length-1);
        sb += ");";
        return sb;
    }

    public String insertTableString(String values) {
        String sb = "INSERT INTO " + name + " VALUES (" + values + ");";
    }
}
