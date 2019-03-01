package com.dant.extraction;

import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLConnexion {
    private Connection conn;

    public SQLConnexion() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("admin");
        dataSource.setPassword("admin");
        dataSource.setServerName("jdbc:mysql://localhost:8080/data");
        conn = dataSource.getConnection();
    }

    public void performQuery(String query) {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        rs.close();
        stmt.close();
    }

    public void closeConnection() {
        conn.close();
    }
}
