/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.db;

/**
 *
 * @author Yuedong Xu
 */
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(conn);
        }
    }
}
