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
import com.vincent.petadoptionsystem.model.SurrenderRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SurrenderRequestDAO {

    public boolean createSurrenderRequest(int userId, int petId, String reason, String description) {
        String duplicateSql = "SELECT COUNT(*) FROM SurrenderRequests " +
                              "WHERE UserId = ? AND PetId = ? AND Status IN ('Submitted', 'Approved')";

        String insertSql = "INSERT INTO SurrenderRequests " +
                           "(UserId, PetId, RequestDate, Status, Description, Reason, HandledByUserId, ProcessedAt) " +
                           "VALUES (?, ?, NOW(), 'Submitted', ?, ?, NULL, NULL)";

        try (Connection conn = DatabaseManager.getConnection()) {

            try (PreparedStatement checkPs = conn.prepareStatement(duplicateSql)) {
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
                insertPs.setString(3, description);
                insertPs.setString(4, reason);

                int rows = insertPs.executeUpdate();
                return rows > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<SurrenderRequest> getRequestsByUserId(int userId) {
        List<SurrenderRequest> requestList = new ArrayList<>();

        String sql = "SELECT sr.SurrenderRequestId, sr.UserId, sr.PetId, p.Name AS PetName, " +
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
                     "sr.PetId, p.Name AS PetName, sr.RequestDate, sr.Status, " +
                     "sr.Description, sr.Reason, sr.HandledByUserId, sr.ProcessedAt " +
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
                if (shelterId != null) {
                    String updatePetSql = "UPDATE Pets " +
                                          "SET ShelterId = ?, AdoptionStatus = 'Submitted', HealthCheckStatus = 'Pending' " +
                                          "WHERE PetId = ?";
                    try (PreparedStatement petPs = conn.prepareStatement(updatePetSql)) {
                        petPs.setInt(1, shelterId);
                        petPs.setInt(2, petId);
                        petPs.executeUpdate();
                    }
                } else {
                    String updatePetSql = "UPDATE Pets " +
                                          "SET AdoptionStatus = 'Submitted', HealthCheckStatus = 'Pending' " +
                                          "WHERE PetId = ?";
                    try (PreparedStatement petPs = conn.prepareStatement(updatePetSql)) {
                        petPs.setInt(1, petId);
                        petPs.executeUpdate();
                    }
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