/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.service;

/**
 *
 * @author Yuedong Xu
 */

import com.vincent.petadoptionsystem.dao.AdoptionApplicationDAO;
import com.vincent.petadoptionsystem.dao.MedicalCheckRequestDAO;
import com.vincent.petadoptionsystem.dao.PetDAO;
import com.vincent.petadoptionsystem.dao.SurrenderRequestDAO;
import com.vincent.petadoptionsystem.dao.UserDAO;
import com.vincent.petadoptionsystem.model.AdoptionApplication;
import com.vincent.petadoptionsystem.model.MedicalCheckRequest;
import com.vincent.petadoptionsystem.model.Pet;
import com.vincent.petadoptionsystem.model.SurrenderRequest;
import com.vincent.petadoptionsystem.model.User;
import java.util.List;

public class PetAdoptionSystem {

    private static PetAdoptionSystem instance;
    private AdoptionApplicationDAO adoptionApplicationDAO;
    private UserDAO userDAO;
    private PetDAO petDAO;
    private MedicalCheckRequestDAO medicalCheckRequestDAO;
    private SurrenderRequestDAO surrenderRequestDAO;

    private PetAdoptionSystem() {
        userDAO = new UserDAO();
        petDAO = new PetDAO();
        adoptionApplicationDAO = new AdoptionApplicationDAO();
        medicalCheckRequestDAO = new MedicalCheckRequestDAO();
        surrenderRequestDAO = new SurrenderRequestDAO();
    }

    public static PetAdoptionSystem getInstance() {
        if (instance == null) {
            instance = new PetAdoptionSystem();
        }
        return instance;
    }

    public User authenticateUser(String email, String password) {
        return userDAO.authenticateUser(email, password);
    }

    public List<Pet> getAvailablePets() {
        return petDAO.getAvailablePets();
    }

    public boolean createAdoptionApplication(int userId, int petId) {
        return adoptionApplicationDAO.createApplication(userId, petId);
    }

    public List<AdoptionApplication> getApplicationsByUserId(int userId) {
        return adoptionApplicationDAO.getApplicationsByUserId(userId);
    }

    public List<AdoptionApplication> getAllAdoptionApplications() {
        return adoptionApplicationDAO.getAllApplications();
    }

    public boolean updateAdoptionApplicationStatus(int applicationId, String status, int handledByUserId) {
        return adoptionApplicationDAO.updateApplicationStatus(applicationId, status, handledByUserId);
    }

    public boolean reviewAdoptionApplication(int applicationId, String status, int handledByUserId) {
        return adoptionApplicationDAO.reviewApplication(applicationId, status, handledByUserId);
    }

    public boolean createMedicalCheckRequest(int petId, int createdByUserId) {
        return medicalCheckRequestDAO.createMedicalCheckRequest(petId, createdByUserId);
    }

    public List<MedicalCheckRequest> getAllMedicalCheckRequests() {
        return medicalCheckRequestDAO.getAllMedicalCheckRequests();
    }

    public boolean submitMedicalReport(int requestId, int vetUserId, String reportNotes, String newHealthStatus) {
        return medicalCheckRequestDAO.submitMedicalReport(requestId, vetUserId, reportNotes, newHealthStatus);
    }

    public boolean createSurrenderRequest(int userId, int petId, String reason, String description) {
        return surrenderRequestDAO.createSurrenderRequest(userId, petId, reason, description);
    }

    public List<SurrenderRequest> getSurrenderRequestsByUserId(int userId) {
        return surrenderRequestDAO.getRequestsByUserId(userId);
    }

    public List<SurrenderRequest> getAllSurrenderRequests() {
        return surrenderRequestDAO.getAllSurrenderRequests();
    }

    public boolean reviewSurrenderRequest(int requestId, String status, int handledByUserId, Integer shelterId) {
        return surrenderRequestDAO.reviewSurrenderRequest(requestId, status, handledByUserId, shelterId);
    }
}