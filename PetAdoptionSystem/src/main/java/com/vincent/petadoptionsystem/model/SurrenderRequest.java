/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vincent.petadoptionsystem.model;

/**
 *
 * @author Yuedong Xu
 */

public class SurrenderRequest {
    private int surrenderRequestId;
    private int userId;
    private String ownerName;
    private int petId;
    private String petName;
    private String requestDate;
    private String status;
    private String description;
    private String reason;
    private Integer handledByUserId;
    private String processedAt;

    public SurrenderRequest() {
    }

    public int getSurrenderRequestId() {
        return surrenderRequestId;
    }

    public void setSurrenderRequestId(int surrenderRequestId) {
        this.surrenderRequestId = surrenderRequestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
