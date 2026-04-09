/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.service;

/**
 *
 * @author Yuedong Xu
 */


import com.vincent.petadoptionsystem.dao.AdopterProfileDAO;
import com.vincent.petadoptionsystem.dao.AdoptionApplicationDAO;
import com.vincent.petadoptionsystem.dao.MedicalCheckRequestDAO;
import com.vincent.petadoptionsystem.dao.PetDAO;
import com.vincent.petadoptionsystem.dao.SurrenderRequestDAO;
import com.vincent.petadoptionsystem.dao.UserDAO;
import com.vincent.petadoptionsystem.model.AdopterProfile;
import com.vincent.petadoptionsystem.model.AdoptionApplication;
import com.vincent.petadoptionsystem.model.MedicalCheckRequest;
import com.vincent.petadoptionsystem.model.Pet;
import com.vincent.petadoptionsystem.model.SurrenderRequest;
import com.vincent.petadoptionsystem.model.User;
import java.util.List;

public class PetAdoptionSystem {

    private static PetAdoptionSystem instance;

    private final AdoptionApplicationDAO adoptionApplicationDAO;
    private final UserDAO userDAO;
    private final PetDAO petDAO;
    private final MedicalCheckRequestDAO medicalCheckRequestDAO;
    private final SurrenderRequestDAO surrenderRequestDAO;
    private final AdopterProfileDAO adopterProfileDAO;

    private PetAdoptionSystem() {
        userDAO = new UserDAO();
        petDAO = new PetDAO();
        adoptionApplicationDAO = new AdoptionApplicationDAO();
        medicalCheckRequestDAO = new MedicalCheckRequestDAO();
        surrenderRequestDAO = new SurrenderRequestDAO();
        adopterProfileDAO = new AdopterProfileDAO();
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

    public boolean submitMedicalReport(
            int requestId,
            int vetUserId,
            String diagnosis,
            String vaccinationStatus,
            String recommendation,
            String resultStatus,
            String newHealthStatus
    ) {
        return medicalCheckRequestDAO.submitMedicalReport(
                requestId,
                vetUserId,
                diagnosis,
                vaccinationStatus,
                recommendation,
                resultStatus,
                newHealthStatus
        );
    }

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
        return surrenderRequestDAO.createSurrenderRequest(
                userId,
                petName,
                species,
                breed,
                age,
                gender,
                weight,
                color,
                healthStatus,
                reason,
                description
        );
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

    public AdopterProfile getAdopterProfileByUserId(int userId) {
        return adopterProfileDAO.getProfileByUserId(userId);
    }

    public boolean saveAdopterProfile(AdopterProfile profile) {
        return adopterProfileDAO.saveOrUpdateProfile(profile);
    }
    public boolean withdrawAdoptionApplication(int applicationId, int userId) {
    return adoptionApplicationDAO.withdrawApplication(applicationId, userId);
}
    public List<MedicalCheckRequest> getMedicalCheckRequestsByClinicId(int clinicId) {
    return medicalCheckRequestDAO.getMedicalCheckRequestsByClinicId(clinicId);
}

public List<AdoptionApplication> getAdoptionApplicationsByShelterId(int shelterId) {
    return adoptionApplicationDAO.getApplicationsByShelterId(shelterId);
}
public List<MedicalCheckRequest> getMedicalCheckRequestsByShelterId(int shelterId) {
    return medicalCheckRequestDAO.getMedicalCheckRequestsByShelterId(shelterId);
}

public boolean cancelMedicalCheckRequest(int requestId, int shelterAdminUserId) {
    return medicalCheckRequestDAO.cancelMedicalCheckRequest(requestId, shelterAdminUserId);
}
public boolean withdrawSurrenderRequest(int requestId, int userId) {
    return surrenderRequestDAO.withdrawSurrenderRequest(requestId, userId);
}

}