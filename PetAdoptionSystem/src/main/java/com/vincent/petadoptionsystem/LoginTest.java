/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem;

/**
 *
 * @author Yuedong Xu
 */


import com.vincent.petadoptionsystem.model.User;
import com.vincent.petadoptionsystem.service.PetAdoptionSystem;

public class LoginTest {
    public static void main(String[] args) {
        PetAdoptionSystem system = PetAdoptionSystem.getInstance();

        User user = system.authenticateUser("adopter@test.com", "123456");

        if (user != null) {
            System.out.println("Login success!");
            System.out.println("User ID: " + user.getUserId());
            System.out.println("Name: " + user.getName());
            System.out.println("Role: " + user.getRole());
        } else {
            System.out.println("Login failed!");
        }
    }
}
