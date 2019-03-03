package com.dant.extraction;

import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;
import com.dant.entity.*;

public class SQLConnexion { 
    private Connection conn; //peut etre plutot a extends ou implements

    public SQLConnexion() {
        DataSource dataSource = new DataSource();
        dataSource.setUser("admin");
        dataSource.setPassword("admin");
        dataSource.setServerName("jdbc:mysql://localhost:8080/data");
        conn = dataSource.getConnection();
    }
    
    

    public HashMapValues performQuerySeedIndex(String query, Column attribute) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        HashMapValues hashValues = new HashMapValues();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        Object tmp = new Object();
        if (rs.next()) {
        	ids.add(rs.getInt("id"));
        	tmp = rs.getObject(attribute.getName());
        }
        while(rs.next()) {
        	Object value = rs.getObject(attribute.getName());
        	Integer id = rs.getInt("id"); //pe apparemment c est string on verra
        	if (value.equals(tmp)) {
        		ids.add(id);
        	}
        	else { //eventuellement ajouter un cast ici en fonction du type
        		hashValues.put(value, ids.toArray(new Integer[0]));
        	}
        }
        rs.close();
        stmt.close();
        return hashValues;
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }
}
