package com.danil.crud.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class RepositoryUtils {
    public static final String JDBC_DRIVER;
    public static final String DATABASE_URL;
    public static final String USER;
    public static final String PASSWORD;

    static {
        Properties properties = new Properties();
        try (InputStream input = RepositoryUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("ERROR: Unable to load application.properties file!");
                System.exit(1);
            }
            properties.load(input);
        } catch (IOException e) {
            System.out.println("ERROR: Unable to load application.properties file! " + e);
            System.exit(1);
        }

        JDBC_DRIVER = properties.getProperty("driver");
        DATABASE_URL = properties.getProperty("url");
        USER = properties.getProperty("username");
        PASSWORD = properties.getProperty("password");
    }

    private static Connection connection;

    private static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        RepositoryUtils.DATABASE_URL,
                        RepositoryUtils.USER,
                        RepositoryUtils.PASSWORD);
            } catch (SQLException e) {
                System.out.println("ERROR: Unable to create connection to database! " + e);
                System.exit(1);
            }
        }

        return connection;
    }

    public static PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    public static PreparedStatement getPreparedStatement(String sql, int key) throws SQLException {
        return getConnection().prepareStatement(sql, key);
    }
}
