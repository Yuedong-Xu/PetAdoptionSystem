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
import com.vincent.petadoptionsystem.model.MedicalCheckRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MedicalCheckRequestDAO {

    public boolean createMedicalCheckRequest(int petId, int createdByUserId) {
    String duplicateSql = "SELECT COUNT(*) FROM MedicalCheckRequests " +
                          "WHERE PetId = ? AND Status IN ('Submitted', 'In Progress')";

    String petSql = "SELECT ShelterId FROM Pets WHERE PetId = ?";

    String clinicSql = "SELECT OrganizationId FROM Organizations WHERE OrganizationId <> ? ORDER BY OrganizationId LIMIT 1";

    String insertSql = "INSERT INTO MedicalCheckRequests " +
                       "(PetId, ShelterId, ClinicId, RequestDate, Status, Description, CreatedByUserId, HandledByUserId, ProcessedAt) " +
                       "VALUES (?, ?, ?, NOW(), 'Submitted', NULL, ?, NULL, NULL)";

    try (Connection conn = DatabaseManager.getConnection()) {

        
        try (PreparedStatement checkPs = conn.prepareStatement(duplicateSql)) {
            checkPs.setInt(1, petId);

            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
        }

        
        int shelterId;
        try (PreparedStatement petPs = conn.prepareStatement(petSql)) {
            petPs.setInt(1, petId);

            try (ResultSet rs = petPs.executeQuery()) {
                if (rs.next()) {
                    shelterId = rs.getInt("ShelterId");
                } else {
                    return false;
                }
            }
        }

    
        int clinicId;
        try (PreparedStatement clinicPs = conn.prepareStatement(clinicSql)) {
            clinicPs.setInt(1, shelterId);

            try (ResultSet rs = clinicPs.executeQuery()) {
                if (rs.next()) {
                    clinicId = rs.getInt("OrganizationId");
                } else {
                    
                    clinicId = shelterId;
                }
            }
        }

        
        try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
            insertPs.setInt(1, petId);
            insertPs.setInt(2, shelterId);
            insertPs.setInt(3, clinicId);
            insertPs.setInt(4, createdByUserId);

            int rows = insertPs.executeUpdate();
            return rows > 0;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

    public List<MedicalCheckRequest> getAllMedicalCheckRequests() {
        List<MedicalCheckRequest> requestList = new ArrayList<>();

        String sql = "SELECT m.MedicalCheckRequestId, m.PetId, p.Name AS PetName, " +
                     "m.ShelterId, m.ClinicId, m.RequestDate, m.Status, m.Description, " +
                     "m.CreatedByUserId, m.HandledByUserId, m.ProcessedAt " +
                     "FROM MedicalCheckRequests m " +
                     "JOIN Pets p ON m.PetId = p.PetId " +
                     "ORDER BY m.MedicalCheckRequestId DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MedicalCheckRequest request = new MedicalCheckRequest();
                request.setMedicalCheckRequestId(rs.getInt("MedicalCheckRequestId"));
                request.setPetId(rs.getInt("PetId"));
                request.setPetName(rs.getString("PetName"));
                request.setShelterId(rs.getInt("ShelterId"));
                request.setClinicId(rs.getInt("ClinicId"));
                request.setRequestDate(String.valueOf(rs.getTimestamp("RequestDate")));
                request.setStatus(rs.getString("Status"));
                request.setDescription(rs.getString("Description"));
                request.setCreatedByUserId(rs.getInt("CreatedByUserId"));

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

    public boolean submitMedicalReport(
        int requestId,
        int vetUserId,
        String diagnosis,
        String vaccinationStatus,
        String recommendation,
        String resultStatus,
        String newHealthStatus
) {
    Connection conn = null;

    try {
        conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);

        int petId = -1;

        String findPetSql = "SELECT PetId FROM MedicalCheckRequests WHERE MedicalCheckRequestId = ?";
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

        String updateRequestSql = "UPDATE MedicalCheckRequests " +
                                  "SET HandledByUserId = ?, Status = 'Completed', " +
                                  "Description = ?, ProcessedAt = NOW() " +
                                  "WHERE MedicalCheckRequestId = ?";

        try (PreparedStatement requestPs = conn.prepareStatement(updateRequestSql)) {
            requestPs.setInt(1, vetUserId);
            requestPs.setString(2, diagnosis);
            requestPs.setInt(3, requestId);

            int rows = requestPs.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
            }
        }

        String insertReportSql = "INSERT INTO MedicalReports " +
                                 "(PetId, MedicalCheckRequestId, VetStaffUserId, ExamDate, Diagnosis, VaccinationStatus, Recommendation, ResultStatus, CreatedAt) " +
                                 "VALUES (?, ?, ?, CURDATE(), ?, ?, ?, ?, NOW())";

        try (PreparedStatement reportPs = conn.prepareStatement(insertReportSql)) {
            reportPs.setInt(1, petId);
            reportPs.setInt(2, requestId);
            reportPs.setInt(3, vetUserId);
            reportPs.setString(4, diagnosis);
            reportPs.setString(5, vaccinationStatus);
            reportPs.setString(6, recommendation);
            reportPs.setString(7, resultStatus);

            int rows = reportPs.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
            }
        }

        String updatePetSql = "UPDATE Pets SET HealthStatus = ?, HealthCheckStatus = 'Completed' WHERE PetId = ?";
        try (PreparedStatement petPs = conn.prepareStatement(updatePetSql)) {
            petPs.setString(1, newHealthStatus);
            petPs.setInt(2, petId);
            petPs.executeUpdate();
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
