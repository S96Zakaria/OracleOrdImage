/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imagengine.demo.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ELIOT
 */
public class Connect {
    
    private static Connect instance = new Connect();
    private static Connection cnx=null;
    public static final String URL = "jdbc:oracle:thin:@192.168.1.150:1521:FST1";
    public static final String USER = "system";
    public static final String PASSWORD = "system";
    public static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver"; 
    
    private Connect() {
        try {
            //Step 2: Load ORACLE Java driver
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
     
    private Connection createConnection() {
        try {
            //Step 3: Establish Java MySQL connection
            cnx= DriverManager.getConnection(URL, USER, PASSWORD);
            cnx.setAutoCommit(false);
            System.out.println("Connection est bien effecué.");
            
        } catch (SQLException e) {
            System.out.println("ERROR: Unable to Connect to Database.");
        }
        return cnx;
    }   
     
    public static Connection getConnection() {
        try {
            if(cnx==null || cnx.isClosed())
                return instance.createConnection();
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnx;
    }
}
