package com.dant.entity;

import java.io.Serializable;
import java.util.HashMap;

public class Table {

    private String name;
    private Column[] attributes;
    private Index index;

    public Table(String name, Column[] attributes, Column[] keys) {
        this.name = name;
        this.attributes = attributes;
        this.index = new Index(keys);
    }
    
    public String statementIndexSeed(Column attribute) {
    	return "SELECT id, " + attribute.getName() + " FROM " + name + " GROUP BY " + attribute.getName();
    }

    //a redeclarer pour les types usuels
    public void putIndex(Column attribute, HashMapValues values) {
        this.index.putValues(attribute, values);
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
            sb += attribute.toString() + ",";
        }
        //enlever la derniere virgule
        sb = sb.substring(0, sb.length()-1);
        sb += ");";
        return sb;
    }

    public String insertTableString(String values) {
        return "INSERT INTO " + name + " VALUES (" + values + ");";
    }
}
