package com.dant.entity;

import com.dant.extraction.*;

import java.sql.SQLException;
import java.util.Scanner;

public class Moteur {
	private Table[] tables;
	private SQLConnexion conn = new SQLConnexion();
	
	public Moteur() {
		//TODO creer les tables comment ? query ou csv je ne sais pas....
	}
	
	public void setIndex() throws SQLException {
		for (Table table : tables) {
			System.out.println(table + "\nchoose index");
			String name = "";
			Column col;
			Scanner sc = new Scanner(System.in);
			while (sc.hasNext()) {
				name = sc.next();
				col = table.findColumn(name);
				HashMapValues values = conn.performQuerySeedIndex(table.statementIndexSeed(col), col);
				table.putIndex(col, values);
			}
		}
	}
}
