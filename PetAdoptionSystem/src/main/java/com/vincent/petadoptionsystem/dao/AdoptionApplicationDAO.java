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
    String checkSql = "SELECT COUNT(*) FROM AdoptionApplications " +
                      "WHERE UserId = ? AND PetId = ? AND Status IN ('Submitted', 'Approved')";

    String insertSql = "INSERT INTO AdoptionApplications (UserId, PetId, Status, HandledByUserId) " +
                       "VALUES (?, ?, 'Submitted', NULL)";

    try (Connection conn = DatabaseManager.getConnection()) {

        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, userId);
            checkPs.setInt(2, petId);

            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
        }

        try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
            insertPs.setInt(1, userId);
            insertPs.setInt(2, petId);

            int rows = insertPs.executeUpdate();
            return rows > 0;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

    public List<AdoptionApplication> getAllApplications() {
    List<AdoptionApplication> applicationList = new ArrayList<>();

    String sql = "SELECT aa.ApplicationId, aa.UserId, u.Name AS AdopterName, " +
                 "aa.PetId, p.Name AS PetName, p.Species, p.Breed, aa.Status, " +
                 "ap.MonthlyIncome, ap.LivingArea, ap.NumberOfPets, " +
                 "ap.PetRaisingExperience, ap.NumberOfPeople, ap.PreferredPetType " +
                 "FROM AdoptionApplications aa " +
                 "JOIN Users u ON aa.UserId = u.UserId " +
                 "JOIN Pets p ON aa.PetId = p.PetId " +
                 "LEFT JOIN AdopterProfiles ap ON aa.UserId = ap.UserId " +
                 "ORDER BY aa.ApplicationId DESC";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            AdoptionApplication application = new AdoptionApplication();
            application.setApplicationId(rs.getInt("ApplicationId"));
            application.setUserId(rs.getInt("UserId"));
            application.setAdopterName(rs.getString("AdopterName"));
            application.setPetId(rs.getInt("PetId"));
            application.setPetName(rs.getString("PetName"));
            application.setSpecies(rs.getString("Species"));
            application.setBreed(rs.getString("Breed"));
            application.setStatus(rs.getString("Status"));

            double income = rs.getDouble("MonthlyIncome");
            if (rs.wasNull()) {
                application.setMonthlyIncome(null);
            } else {
                application.setMonthlyIncome(income);
            }

            application.setLivingArea(rs.getString("LivingArea"));
            application.setNumberOfPets(rs.getInt("NumberOfPets"));
            application.setPetRaisingExperience(rs.getString("PetRaisingExperience"));

            int people = rs.getInt("NumberOfPeople");
            if (rs.wasNull()) {
                application.setNumberOfPeople(null);
            } else {
                application.setNumberOfPeople(people);
            }

            application.setPreferredPetType(rs.getString("PreferredPetType"));

            applicationList.add(application);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return applicationList;
}
    
public boolean reviewApplication(int applicationId, String status, int handledByUserId) {
    Connection conn = null;

    try {
        conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);

        int petId = -1;

        String findPetSql = "SELECT PetId FROM AdoptionApplications WHERE ApplicationId = ?";
        try (PreparedStatement findPs = conn.prepareStatement(findPetSql)) {
            findPs.setInt(1, applicationId);

            try (ResultSet rs = findPs.executeQuery()) {
                if (rs.next()) {
                    petId = rs.getInt("PetId");
                } else {
                    conn.rollback();
                    return false;
                }
            }
        }

        String updateApplicationSql = "UPDATE AdoptionApplications " +
                                      "SET Status = ?, HandledByUserId = ? " +
                                      "WHERE ApplicationId = ?";
        try (PreparedStatement updatePs = conn.prepareStatement(updateApplicationSql)) {
            updatePs.setString(1, status);
            updatePs.setInt(2, handledByUserId);
            updatePs.setInt(3, applicationId);

            int rows = updatePs.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
            }
        }

        if ("Approved".equalsIgnoreCase(status)) {
            String updatePetSql = "UPDATE Pets SET AdoptionStatus = 'Adopted' WHERE PetId = ?";
            try (PreparedStatement petPs = conn.prepareStatement(updatePetSql)) {
                petPs.setInt(1, petId);
                petPs.executeUpdate();
            }

            String rejectOtherSql = "UPDATE AdoptionApplications " +
                                    "SET Status = 'Rejected' " +
                                    "WHERE PetId = ? AND ApplicationId <> ? AND Status = 'Submitted'";
            try (PreparedStatement rejectPs = conn.prepareStatement(rejectOtherSql)) {
                rejectPs.setInt(1, petId);
                rejectPs.setInt(2, applicationId);
                rejectPs.executeUpdate();
            }
        }

        conn.commit();
        return true;

    } catch (Exception e) {
        e.printStackTrace();

        if (conn != null) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    return false;
}
public boolean withdrawApplication(int applicationId, int userId) {
    String sql = "DELETE FROM AdoptionApplications " +
                 "WHERE ApplicationId = ? AND UserId = ? AND Status = 'Submitted'";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, applicationId);
        ps.setInt(2, userId);

        int rows = ps.executeUpdate();
        return rows > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
    public boolean updateApplicationStatus(int applicationId, String status, int handledByUserId) {
        String sql = "UPDATE AdoptionApplications " +
                     "SET Status = ?, HandledByUserId = ? " +
                     "WHERE ApplicationId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, handledByUserId);
            ps.setInt(3, applicationId);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public List<AdoptionApplication> getApplicationsByShelterId(int shelterId) {
    List<AdoptionApplication> applicationList = new ArrayList<>();

    String sql = "SELECT aa.ApplicationId, aa.UserId, u.Name AS AdopterName, " +
                 "aa.PetId, p.Name AS PetName, p.Species, p.Breed, aa.Status, " +
                 "ap.MonthlyIncome, ap.LivingArea, ap.NumberOfPets, " +
                 "ap.PetRaisingExperience, ap.NumberOfPeople, ap.PreferredPetType " +
                 "FROM AdoptionApplications aa " +
                 "JOIN Users u ON aa.UserId = u.UserId " +
                 "JOIN Pets p ON aa.PetId = p.PetId " +
                 "LEFT JOIN AdopterProfiles ap ON aa.UserId = ap.UserId " +
                 "WHERE p.ShelterId = ? " +
                 "ORDER BY aa.ApplicationId DESC";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, shelterId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AdoptionApplication application = new AdoptionApplication();
                application.setApplicationId(rs.getInt("ApplicationId"));
                application.setUserId(rs.getInt("UserId"));
                application.setAdopterName(rs.getString("AdopterName"));
                application.setPetId(rs.getInt("PetId"));
                application.setPetName(rs.getString("PetName"));
                application.setSpecies(rs.getString("Species"));
                application.setBreed(rs.getString("Breed"));
                application.setStatus(rs.getString("Status"));

                double income = rs.getDouble("MonthlyIncome");
                if (rs.wasNull()) {
                    application.setMonthlyIncome(null);
                } else {
                    application.setMonthlyIncome(income);
                }

                application.setLivingArea(rs.getString("LivingArea"));
                application.setNumberOfPets(rs.getInt("NumberOfPets"));
                application.setPetRaisingExperience(rs.getString("PetRaisingExperience"));

                int people = rs.getInt("NumberOfPeople");
                if (rs.wasNull()) {
                    application.setNumberOfPeople(null);
                } else {
                    application.setNumberOfPeople(people);
                }

                application.setPreferredPetType(rs.getString("PreferredPetType"));

                applicationList.add(application);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return applicationList;
}
    public List<AdoptionApplication> getApplicationsByUserId(int userId) {
    List<AdoptionApplication> applicationList = new ArrayList<>();

    String sql = "SELECT aa.ApplicationId, aa.UserId, aa.PetId, " +
                 "p.Name AS PetName, p.Species, p.Breed, aa.Status " +
                 "FROM AdoptionApplications aa " +
                 "JOIN Pets p ON aa.PetId = p.PetId " +
                 "WHERE aa.UserId = ? " +
                 "ORDER BY aa.ApplicationId DESC";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AdoptionApplication application = new AdoptionApplication();
                application.setApplicationId(rs.getInt("ApplicationId"));
                application.setUserId(rs.getInt("UserId"));
                application.setPetId(rs.getInt("PetId"));
                application.setPetName(rs.getString("PetName"));
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