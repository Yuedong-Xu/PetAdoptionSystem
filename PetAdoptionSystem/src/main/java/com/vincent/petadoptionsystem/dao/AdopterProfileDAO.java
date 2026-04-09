/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.dao;

/**
 *
 * @author vincent
 */

import com.vincent.petadoptionsystem.db.DatabaseManager;
import com.vincent.petadoptionsystem.model.AdopterProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

public class AdopterProfileDAO {

    public AdopterProfile getProfileByUserId(int userId) {
        String sql = "SELECT AdopterProfileId, UserId, MonthlyIncome, LivingArea, " +
                     "NumberOfPets, PetRaisingExperience, NumberOfPeople, PreferredPetType " +
                     "FROM AdopterProfiles WHERE UserId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AdopterProfile profile = new AdopterProfile();
                    profile.setAdopterProfileId(rs.getInt("AdopterProfileId"));
                    profile.setUserId(rs.getInt("UserId"));

                    double income = rs.getDouble("MonthlyIncome");
                    if (rs.wasNull()) {
                        profile.setMonthlyIncome(null);
                    } else {
                        profile.setMonthlyIncome(income);
                    }

                    profile.setLivingArea(rs.getString("LivingArea"));
                    profile.setNumberOfPets(rs.getInt("NumberOfPets"));
                    profile.setPetRaisingExperience(rs.getString("PetRaisingExperience"));

                    int numberOfPeople = rs.getInt("NumberOfPeople");
                    if (rs.wasNull()) {
                        profile.setNumberOfPeople(null);
                    } else {
                        profile.setNumberOfPeople(numberOfPeople);
                    }

                    profile.setPreferredPetType(rs.getString("PreferredPetType"));
                    return profile;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean saveOrUpdateProfile(AdopterProfile profile) {
        String sql = "INSERT INTO AdopterProfiles " +
                     "(UserId, MonthlyIncome, LivingArea, NumberOfPets, PetRaisingExperience, NumberOfPeople, PreferredPetType) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "MonthlyIncome = VALUES(MonthlyIncome), " +
                     "LivingArea = VALUES(LivingArea), " +
                     "NumberOfPets = VALUES(NumberOfPets), " +
                     "PetRaisingExperience = VALUES(PetRaisingExperience), " +
                     "NumberOfPeople = VALUES(NumberOfPeople), " +
                     "PreferredPetType = VALUES(PreferredPetType)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, profile.getUserId());

            if (profile.getMonthlyIncome() == null) {
                ps.setNull(2, Types.DECIMAL);
            } else {
                ps.setDouble(2, profile.getMonthlyIncome());
            }

            ps.setString(3, profile.getLivingArea());
            ps.setInt(4, profile.getNumberOfPets());
            ps.setString(5, profile.getPetRaisingExperience());

            if (profile.getNumberOfPeople() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, profile.getNumberOfPeople());
            }

            ps.setString(7, profile.getPreferredPetType());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
