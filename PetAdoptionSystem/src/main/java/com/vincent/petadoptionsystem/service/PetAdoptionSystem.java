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
import com.vincent.petadoptionsystem.dao.PetDAO;
import com.vincent.petadoptionsystem.dao.UserDAO;
import com.vincent.petadoptionsystem.model.AdoptionApplication;
import com.vincent.petadoptionsystem.model.Pet;
import com.vincent.petadoptionsystem.model.User;
import java.util.List;

public class PetAdoptionSystem {

    private static PetAdoptionSystem instance;
    private AdoptionApplicationDAO adoptionApplicationDAO;
    private UserDAO userDAO;
    private PetDAO petDAO;

    private PetAdoptionSystem() {
        userDAO = new UserDAO();
        petDAO = new PetDAO();
        adoptionApplicationDAO = new AdoptionApplicationDAO();
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
}