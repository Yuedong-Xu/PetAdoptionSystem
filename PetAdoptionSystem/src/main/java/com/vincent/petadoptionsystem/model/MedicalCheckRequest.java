/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.model;

/**
 * Model class representing a medical check request.
 * This class stores the request information exchanged between the shelter
 * and the veterinary clinic for pet health examination.
 *
 * @author Yuedong Xu
 * @version demo final version
 */

public class MedicalCheckRequest {
    private int medicalCheckRequestId;
    private int petId;
    private String petName;
    private int shelterId;
    private int clinicId;
    private String requestDate;
    private String status;
    private String description;
    private int createdByUserId;
    private Integer handledByUserId;
    private String processedAt;

    public MedicalCheckRequest() {
    }

    public int getMedicalCheckRequestId() {
        return medicalCheckRequestId;
    }

    public void setMedicalCheckRequestId(int medicalCheckRequestId) {
        this.medicalCheckRequestId = medicalCheckRequestId;
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

    public int getShelterId() {
        return shelterId;
    }

    public void setShelterId(int shelterId) {
        this.shelterId = shelterId;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Integer getHandledByUserId() {
        return handledByUserId;
    }

    public void setHandledByUserId(Integer handledByUserId) {
        this.handledByUserId = handledByUserId;
    }

    public String getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }
}