/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.dao;

/**
 *
 * @author Yuedong Xu
 */

import com.vincent.petadoptionsystem.db.DatabaseManager;
import com.vincent.petadoptionsystem.model.Pet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PetDAO {

    public List<Pet> getAvailablePets() {
        List<Pet> petList = new ArrayList<>();

        String sql = "SELECT * FROM Pets WHERE AdoptionStatus <> 'Adopted'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pet pet = new Pet();
                pet.setPetId(rs.getInt("PetId"));
                pet.setName(rs.getString("Name"));
                pet.setSpecies(rs.getString("Species"));
                pet.setBreed(rs.getString("Breed"));
                pet.setAge(rs.getInt("Age"));
                pet.setGender(rs.getString("Gender"));
                pet.setColor(rs.getString("Color"));
                pet.setHealthStatus(rs.getString("HealthStatus"));
                pet.setAdoptionStatus(rs.getString("AdoptionStatus"));

                petList.add(pet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return petList;
    }
}
