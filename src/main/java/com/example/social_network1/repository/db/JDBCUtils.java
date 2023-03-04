package com.example.social_network1.repository.db;


import com.example.social_network1.HelloApplication;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtils {
    private String url;
    private String user;
    private String password;

    public JDBCUtils() {
        loadDBCredentials();
    }

    public Connection getConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,user,password);
        }catch (SQLException ex){
            System.out.println("Error getting connection" + ex);
        }
        return connection;
    }

    private void loadDBCredentials() {
        Properties properties = new Properties();
        try (InputStream input = HelloApplication.class.getClassLoader().getResourceAsStream("application.propreties")) {
            properties.load(input);
            url = properties.getProperty("jdbc.url");
            user = properties.getProperty("jdbc.user");
            password = properties.getProperty("jdbc.password");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
