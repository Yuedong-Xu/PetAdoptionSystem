/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.dao;

/**
 *
 * @author yuedong Xu
 */




import com.vincent.petadoptionsystem.db.DatabaseManager;
import com.vincent.petadoptionsystem.model.AdoptionApplication;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdoptionApplicationDAO {

    public boolean createApplication(int userId, int petId) {
        String sql = "INSERT INTO AdoptionApplications (UserId, PetId, Status, HandledByUserId) VALUES (?, ?, 'Submitted', NULL)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, petId);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<AdoptionApplication> getApplicationsByUserId(int userId) {
        List<AdoptionApplication> applicationList = new ArrayList<>();

        String sql = "SELECT aa.PetId, p.Name, p.Species, p.Breed, aa.Status " +
                     "FROM AdoptionApplications aa " +
                     "JOIN Pets p ON aa.PetId = p.PetId " +
                     "WHERE aa.UserId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AdoptionApplication application = new AdoptionApplication();
                    application.setPetId(rs.getInt("PetId"));
                    application.setPetName(rs.getString("Name"));
                    application.setSpecies(rs.getString("Species"));
                    application.setBreed(rs.getString("Breed"));
                    application.setStatus(rs.getString("Status"));

                    applicationList.add(application);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return applicationList;
    }
}