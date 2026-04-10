/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.db;
/**
 * Utility class for database connection management.
 * This class provides methods to open and close JDBC connections to the MySQL database.
 *
 * @author Yuedong Xu
 * @version demo final version
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.vincent.petadoptionsystem.util.DBConfig;

public class DatabaseManager {
     public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DBConfig.URL,
                DBConfig.USER,
                DBConfig.PASSWORD
        );
    }
     

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
