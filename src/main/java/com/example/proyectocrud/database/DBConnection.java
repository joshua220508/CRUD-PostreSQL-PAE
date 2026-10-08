package com.example.proyectocrud.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/bdventas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    public DBConnection(){

    }

    public static Connection getConnection () throws SQLException {
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }

}

