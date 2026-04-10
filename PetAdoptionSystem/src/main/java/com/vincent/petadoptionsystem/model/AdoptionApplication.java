/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.model;

/**
 * Model class representing an adoption application.
 * This class stores adopter information, pet information, and application status.
 *
 * @author Yuedong Xu
 * @version demo final version
 */


public class AdoptionApplication {
    private int applicationId;
    private int userId;
    private String adopterName;
    private int petId;
    private String petName;
    private String species;
    private String breed;
    private String status;

    private Double monthlyIncome;
    private String livingArea;
    private int numberOfPets;
    private String petRaisingExperience;
    private Integer numberOfPeople;
    private String preferredPetType;

    public AdoptionApplication() {
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAdopterName() {
        return adopterName;
    }

    public void setAdopterName(String adopterName) {
        this.adopterName = adopterName;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getLivingArea() {
        return livingArea;
    }

    public void setLivingArea(String livingArea) {
        this.livingArea = livingArea;
    }

    public int getNumberOfPets() {
        return numberOfPets;
    }

    public void setNumberOfPets(int numberOfPets) {
        this.numberOfPets = numberOfPets;
    }

    public String getPetRaisingExperience() {
        return petRaisingExperience;
    }

    public void setPetRaisingExperience(String petRaisingExperience) {
        this.petRaisingExperience = petRaisingExperience;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getPreferredPetType() {
        return preferredPetType;
    }

    public void setPreferredPetType(String preferredPetType) {
        this.preferredPetType = preferredPetType;
    }
}