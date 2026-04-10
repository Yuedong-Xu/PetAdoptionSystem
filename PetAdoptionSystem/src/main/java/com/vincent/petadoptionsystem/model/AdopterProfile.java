/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.model;

/**
 * Model class representing an adopter profile.
 * This class stores additional adopter information used for adoption screening.
 *
 * @author Yuedong Xu
 * @version demo final version
 */


public class AdopterProfile {
    private int adopterProfileId;
    private int userId;
    private Double monthlyIncome;
    private String livingArea;
    private int numberOfPets;
    private String petRaisingExperience;
    private Integer numberOfPeople;
    private String preferredPetType;

    public AdopterProfile() {
    }

    public int getAdopterProfileId() {
        return adopterProfileId;
    }

    public void setAdopterProfileId(int adopterProfileId) {
        this.adopterProfileId = adopterProfileId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
