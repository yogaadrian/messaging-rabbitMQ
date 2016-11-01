/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author FiqieUlya
 */
public class DBConnector {
    // JDBC driver name and database URL
    private static final String driver = "org.sqlite.JDBC";
    private static final String database = "jdbc:sqlite:pat3.db";
    
    
    public static Connection connect() {
        Connection c = null;
        try {
            Class.forName(driver);
            c = DriverManager.getConnection(database);
            /*  If not exist: 
                CREATE TABLE `user` (
                    `id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    `username`	TEXT NOT NULL,
                    `score`	INT DEFAULT 0
                );
            */
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return c;
    }
}
