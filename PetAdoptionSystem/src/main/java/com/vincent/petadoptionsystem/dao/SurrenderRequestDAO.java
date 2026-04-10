/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.dao;

/**
 * Data access object for surrender request operations.
 * This class handles pet surrender submission, request retrieval, review,
 * approval or rejection, and withdrawal logic.
 *
 * @author Yuedong Xu
 * @version demo final version
 */

import com.vincent.petadoptionsystem.db.DatabaseManager;
import com.vincent.petadoptionsystem.model.SurrenderRequest;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SurrenderRequestDAO {

    
        public boolean createSurrenderRequest(
        int userId,
        String petName,
        String species,
        String breed,
        int age,
        String gender,
        double weight,
        String color,
        String healthStatus,
        String reason,
        String description
) {
    Connection conn = null;

    try {
        conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);

        int petId = -1;

        String insertPetSql = "INSERT INTO Pets " +
                "(Name, Species, Breed, Age, Gender, Weight, Color, HealthStatus, " +
                "HealthCheckStatus, AdoptionStatus, IntakeDate, ShelterId, CreatedAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending', 'Submitted', CURDATE(), NULL, NOW())";

        try (PreparedStatement petPs = conn.prepareStatement(insertPetSql, Statement.RETURN_GENERATED_KEYS)) {
            petPs.setString(1, petName);
            petPs.setString(2, species);
            petPs.setString(3, breed);
            petPs.setInt(4, age);
            petPs.setString(5, gender);
            petPs.setDouble(6, weight);
            petPs.setString(7, color);
            petPs.setString(8, healthStatus);

            int rows = petPs.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
            }

            try (ResultSet keys = petPs.getGeneratedKeys()) {
                if (keys.next()) {
                    petId = keys.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }
        }

        String insertRequestSql = "INSERT INTO SurrenderRequests " +
                "(UserId, PetId, RequestDate, Status, Description, Reason, HandledByUserId, ProcessedAt) " +
                "VALUES (?, ?, NOW(), 'Submitted', ?, ?, NULL, NULL)";

        try (PreparedStatement requestPs = conn.prepareStatement(insertRequestSql)) {
            requestPs.setInt(1, userId);
            requestPs.setInt(2, petId);
            requestPs.setString(3, description);
            requestPs.setString(4, reason);

            int rows = requestPs.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
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

    public List<SurrenderRequest> getRequestsByUserId(int userId) {
        List<SurrenderRequest> requestList = new ArrayList<>();

        String sql = "SELECT sr.SurrenderRequestId, sr.UserId, sr.PetId, p.Name AS PetName, " +
             "p.Species, p.Breed, p.Age, p.Gender, p.Weight, p.Color, p.HealthStatus, " +
             "sr.RequestDate, sr.Status, sr.Description, sr.Reason, " +
             "sr.HandledByUserId, sr.ProcessedAt " +
             "FROM SurrenderRequests sr " +
             "JOIN Pets p ON sr.PetId = p.PetId " +
             "WHERE sr.UserId = ? " +
             "ORDER BY sr.SurrenderRequestId DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SurrenderRequest request = new SurrenderRequest();
                    request.setSurrenderRequestId(rs.getInt("SurrenderRequestId"));
                    request.setUserId(rs.getInt("UserId"));
                    request.setPetId(rs.getInt("PetId"));
                    request.setPetName(rs.getString("PetName"));
                    request.setRequestDate(String.valueOf(rs.getTimestamp("RequestDate")));
                    request.setStatus(rs.getString("Status"));
                    request.setDescription(rs.getString("Description"));
                    request.setReason(rs.getString("Reason"));
                    request.setSpecies(rs.getString("Species"));
                    request.setBreed(rs.getString("Breed"));
                    request.setAge(rs.getInt("Age"));
                    request.setGender(rs.getString("Gender"));
                    request.setWeight(rs.getDouble("Weight"));
                    request.setColor(rs.getString("Color"));
                    request.setHealthStatus(rs.getString("HealthStatus"));

                    int handledBy = rs.getInt("HandledByUserId");
                    if (rs.wasNull()) {
                        request.setHandledByUserId(null);
                    } else {
                        request.setHandledByUserId(handledBy);
                    }

                    if (rs.getTimestamp("ProcessedAt") != null) {
                        request.setProcessedAt(String.valueOf(rs.getTimestamp("ProcessedAt")));
                    } else {
                        request.setProcessedAt(null);
                    }

                    requestList.add(request);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestList;
    }

    public List<SurrenderRequest> getAllSurrenderRequests() {
        List<SurrenderRequest> requestList = new ArrayList<>();

        String sql = "SELECT sr.SurrenderRequestId, sr.UserId, u.Name AS OwnerName, " +
             "sr.PetId, p.Name AS PetName, p.Species, p.Breed, p.Age, p.Gender, p.Weight, p.Color, p.HealthStatus, " +
             "sr.RequestDate, sr.Status, sr.Description, sr.Reason, sr.HandledByUserId, sr.ProcessedAt " +
             "FROM SurrenderRequests sr " +
             "JOIN Users u ON sr.UserId = u.UserId " +
             "JOIN Pets p ON sr.PetId = p.PetId " +
             "ORDER BY sr.SurrenderRequestId DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SurrenderRequest request = new SurrenderRequest();
                request.setSurrenderRequestId(rs.getInt("SurrenderRequestId"));
                request.setUserId(rs.getInt("UserId"));
                request.setOwnerName(rs.getString("OwnerName"));
                request.setPetId(rs.getInt("PetId"));
                request.setPetName(rs.getString("PetName"));
                request.setRequestDate(String.valueOf(rs.getTimestamp("RequestDate")));
                request.setStatus(rs.getString("Status"));
                request.setDescription(rs.getString("Description"));
                request.setReason(rs.getString("Reason"));
                request.setSpecies(rs.getString("Species"));
                request.setBreed(rs.getString("Breed"));
                request.setAge(rs.getInt("Age"));
                request.setGender(rs.getString("Gender"));
                request.setWeight(rs.getDouble("Weight"));
                request.setColor(rs.getString("Color"));
                request.setHealthStatus(rs.getString("HealthStatus"));

                int handledBy = rs.getInt("HandledByUserId");
                if (rs.wasNull()) {
                    request.setHandledByUserId(null);
                } else {
                    request.setHandledByUserId(handledBy);
                }

                if (rs.getTimestamp("ProcessedAt") != null) {
                    request.setProcessedAt(String.valueOf(rs.getTimestamp("ProcessedAt")));
                } else {
                    request.setProcessedAt(null);
                }

                requestList.add(request);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestList;
    }

    public boolean reviewSurrenderRequest(int requestId, String status, int handledByUserId, Integer shelterId) {
    Connection conn = null;

    try {
        conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);

        int petId = -1;

        String findPetSql = "SELECT PetId FROM SurrenderRequests WHERE SurrenderRequestId = ?";
        try (PreparedStatement findPs = conn.prepareStatement(findPetSql)) {
            findPs.setInt(1, requestId);

            try (ResultSet rs = findPs.executeQuery()) {
                if (rs.next()) {
                    petId = rs.getInt("PetId");
                } else {
                    conn.rollback();
                    return false;
                }
            }
        }

        String updateRequestSql = "UPDATE SurrenderRequests " +
                                  "SET Status = ?, HandledByUserId = ?, ProcessedAt = NOW() " +
                                  "WHERE SurrenderRequestId = ?";
        try (PreparedStatement updatePs = conn.prepareStatement(updateRequestSql)) {
            updatePs.setString(1, status);
            updatePs.setInt(2, handledByUserId);
            updatePs.setInt(3, requestId);

            int rows = updatePs.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
            }
        }

        if ("Approved".equalsIgnoreCase(status)) {
            if (shelterId == null) {
                conn.rollback();
                return false;
            }

            String updatePetSql = "UPDATE Pets " +
                                  "SET ShelterId = ?, AdoptionStatus = 'Accepted', HealthCheckStatus = 'Pending' " +
                                  "WHERE PetId = ?";
            try (PreparedStatement petPs = conn.prepareStatement(updatePetSql)) {
                petPs.setInt(1, shelterId);
                petPs.setInt(2, petId);
                petPs.executeUpdate();
            }

        } else if ("Rejected".equalsIgnoreCase(status)) {
            String updatePetSql = "UPDATE Pets " +
                                  "SET AdoptionStatus = 'Rejected' " +
                                  "WHERE PetId = ?";
            try (PreparedStatement petPs = conn.prepareStatement(updatePetSql)) {
                petPs.setInt(1, petId);
                petPs.executeUpdate();
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
    public boolean withdrawSurrenderRequest(int requestId, int userId) {
    Connection conn = null;

    try {
        conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);

        int petId = -1;
        String status = null;

        String findSql = "SELECT PetId, Status FROM SurrenderRequests " +
                         "WHERE SurrenderRequestId = ? AND UserId = ?";
        try (PreparedStatement findPs = conn.prepareStatement(findSql)) {
            findPs.setInt(1, requestId);
            findPs.setInt(2, userId);

            try (ResultSet rs = findPs.executeQuery()) {
                if (rs.next()) {
                    petId = rs.getInt("PetId");
                    status = rs.getString("Status");
                } else {
                    conn.rollback();
                    return false;
                }
            }
        }

        if (!"Submitted".equalsIgnoreCase(status)) {
            conn.rollback();
            return false;
        }

        String deleteRequestSql = "DELETE FROM SurrenderRequests " +
                                  "WHERE SurrenderRequestId = ? AND UserId = ? AND Status = 'Submitted'";
        try (PreparedStatement deleteRequestPs = conn.prepareStatement(deleteRequestSql)) {
            deleteRequestPs.setInt(1, requestId);
            deleteRequestPs.setInt(2, userId);

            int requestRows = deleteRequestPs.executeUpdate();
            if (requestRows == 0) {
                conn.rollback();
                return false;
            }
        }

        String deletePetSql = "DELETE FROM Pets " +
                              "WHERE PetId = ? AND ShelterId IS NULL AND AdoptionStatus = 'Submitted'";
        try (PreparedStatement deletePetPs = conn.prepareStatement(deletePetSql)) {
            deletePetPs.setInt(1, petId);

            int petRows = deletePetPs.executeUpdate();
            if (petRows == 0) {
                conn.rollback();
                return false;
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
}