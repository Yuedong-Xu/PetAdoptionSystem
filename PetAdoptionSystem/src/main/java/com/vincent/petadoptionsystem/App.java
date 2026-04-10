/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem;

/**
 * Entry point of the Pet Adoption System application.
 * This class launches the main login window and starts the Swing-based system demo.
 *
 * @author Yuedong Xu
 * @version demo final version
 */


import com.vincent.petadoptionsystem.ui.MainFrame;

public class App {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
