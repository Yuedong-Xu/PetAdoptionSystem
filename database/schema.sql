-- =========================================================
-- Pet Adoption System - FINAL schema.sql
-- MySQL 8.0+
-- =========================================================

CREATE DATABASE IF NOT EXISTS pet_adoption_system
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE pet_adoption_system;

-- ---------------------------------------------------------
-- Drop triggers first
-- ---------------------------------------------------------
DROP TRIGGER IF EXISTS trg_users_bi_validate;
DROP TRIGGER IF EXISTS trg_users_bu_validate;
DROP TRIGGER IF EXISTS trg_adopterprofiles_bi_validate;
DROP TRIGGER IF EXISTS trg_adopterprofiles_bu_validate;
DROP TRIGGER IF EXISTS trg_pets_bi_validate;
DROP TRIGGER IF EXISTS trg_pets_bu_validate;
DROP TRIGGER IF EXISTS trg_surrenderrequests_bi_validate;
DROP TRIGGER IF EXISTS trg_surrenderrequests_bu_validate;
DROP TRIGGER IF EXISTS trg_adoptionapplications_bi_validate;
DROP TRIGGER IF EXISTS trg_adoptionapplications_bu_validate;
DROP TRIGGER IF EXISTS trg_medicalcheckrequests_bi_validate;
DROP TRIGGER IF EXISTS trg_medicalcheckrequests_bu_validate;
DROP TRIGGER IF EXISTS trg_medicalreports_bi_validate;
DROP TRIGGER IF EXISTS trg_medicalreports_bu_validate;

-- ---------------------------------------------------------
-- Drop tables in reverse dependency order
-- ---------------------------------------------------------
DROP TABLE IF EXISTS MedicalReports;
DROP TABLE IF EXISTS MedicalCheckRequests;
DROP TABLE IF EXISTS AdoptionApplications;
DROP TABLE IF EXISTS SurrenderRequests;
DROP TABLE IF EXISTS AdopterProfiles;
DROP TABLE IF EXISTS Pets;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Organizations;

-- =========================================================
-- 1. Organizations
-- =========================================================
CREATE TABLE Organizations (
    OrganizationId INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Phone VARCHAR(20),
    Address VARCHAR(255),
    OrganizationType ENUM('Shelter', 'VeterinaryClinic') NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================================================
-- 2. Users
-- OrganizationId:
--   - required for ShelterAdmin / VetStaff
--   - must be NULL for Adopter / SurrenderOwner
-- enforced by triggers below
-- =========================================================
CREATE TABLE Users (
    UserId INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Gender VARCHAR(20),
    Age INT UNSIGNED,
    Email VARCHAR(100) NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Phone VARCHAR(20),
    Address VARCHAR(255),
    Role ENUM('Adopter', 'SurrenderOwner', 'ShelterAdmin', 'VetStaff') NOT NULL,
    OrganizationId INT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_users_email UNIQUE (Email),

    CONSTRAINT fk_users_organization
        FOREIGN KEY (OrganizationId)
        REFERENCES Organizations(OrganizationId)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT chk_users_age
        CHECK (Age IS NULL OR (Age BETWEEN 18 AND 120))
) ENGINE=InnoDB;

CREATE INDEX idx_users_role ON Users(Role);
CREATE INDEX idx_users_organization ON Users(OrganizationId);

-- =========================================================
-- 3. AdopterProfiles
-- one adopter -> at most one profile
-- =========================================================
CREATE TABLE AdopterProfiles (
    AdopterProfileId INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    MonthlyIncome DECIMAL(10,2),
    LivingArea VARCHAR(100),
    NumberOfPets INT UNSIGNED NOT NULL DEFAULT 0,
    PetRaisingExperience VARCHAR(255),
    NumberOfPeople INT UNSIGNED,
    PreferredPetType VARCHAR(50),
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_adopterprofiles_user UNIQUE (UserId),

    CONSTRAINT fk_adopterprofiles_user
        FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT chk_adopterprofiles_income
        CHECK (MonthlyIncome IS NULL OR MonthlyIncome >= 0),

    CONSTRAINT chk_adopterprofiles_number_of_pets
        CHECK (NumberOfPets >= 0),

    CONSTRAINT chk_adopterprofiles_number_of_people
        CHECK (NumberOfPeople IS NULL OR NumberOfPeople >= 1)
) ENGINE=InnoDB;

-- =========================================================
-- 4. Pets
-- pending surrender pets may be unassigned until intake is approved
-- =========================================================
CREATE TABLE Pets (
    PetId INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Species VARCHAR(50) NOT NULL,
    Breed VARCHAR(100),
    Age INT UNSIGNED,
    Gender VARCHAR(20),
    Weight DECIMAL(6,2),
    Color VARCHAR(50),
    HealthStatus VARCHAR(50),
    HealthCheckStatus ENUM('Pending', 'In Progress', 'Completed') NOT NULL DEFAULT 'Pending',
    AdoptionStatus ENUM('Submitted', 'Accepted', 'Under Medical Check', 'Ready for Adoption', 'Adoption Pending', 'Adopted', 'Rejected') NOT NULL DEFAULT 'Submitted',
    IntakeDate DATE,
    ShelterId INT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pets_shelter
        FOREIGN KEY (ShelterId)
        REFERENCES Organizations(OrganizationId)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT chk_pets_age
        CHECK (Age IS NULL OR Age <= 50),

    CONSTRAINT chk_pets_weight
        CHECK (Weight IS NULL OR Weight > 0)
) ENGINE=InnoDB;
-- =========================================================
-- 5. SurrenderRequests
-- one surrender owner submits a request for one pet
-- =========================================================
CREATE TABLE SurrenderRequests (
    SurrenderRequestId INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    PetId INT NOT NULL,
    RequestDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Status ENUM('Submitted', 'Approved', 'Rejected') NOT NULL DEFAULT 'Submitted',
    Description TEXT,
    Reason VARCHAR(255),
    HandledByUserId INT NULL,
    ProcessedAt DATETIME NULL,

    CONSTRAINT uq_surrenderrequests_pet UNIQUE (PetId),

    CONSTRAINT fk_surrenderrequests_user
        FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_surrenderrequests_pet
        FOREIGN KEY (PetId)
        REFERENCES Pets(PetId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_surrenderrequests_handler
        FOREIGN KEY (HandledByUserId)
        REFERENCES Users(UserId)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_surrenderrequests_user ON SurrenderRequests(UserId);
CREATE INDEX idx_surrenderrequests_status ON SurrenderRequests(Status);

-- =========================================================
-- 6. AdoptionApplications
-- one adopter can apply for many pets
-- one pet can have many applications
-- =========================================================
CREATE TABLE AdoptionApplications (
    ApplicationId INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    PetId INT NOT NULL,
    ApplicationDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Status ENUM('Submitted', 'Approved', 'Rejected', 'Completed') NOT NULL DEFAULT 'Submitted',
    Description TEXT,
    HandledByUserId INT NULL,
    ProcessedAt DATETIME NULL,

    CONSTRAINT fk_adoptionapplications_user
        FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_adoptionapplications_pet
        FOREIGN KEY (PetId)
        REFERENCES Pets(PetId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_adoptionapplications_handler
        FOREIGN KEY (HandledByUserId)
        REFERENCES Users(UserId)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_adoptionapplications_user ON AdoptionApplications(UserId);
CREATE INDEX idx_adoptionapplications_pet ON AdoptionApplications(PetId);
CREATE INDEX idx_adoptionapplications_status ON AdoptionApplications(Status);

-- =========================================================
-- 7. MedicalCheckRequests
-- created by shelter side, sent to clinic side
-- =========================================================
CREATE TABLE MedicalCheckRequests (
    MedicalCheckRequestId INT AUTO_INCREMENT PRIMARY KEY,
    PetId INT NOT NULL,
    ShelterId INT NOT NULL,
    ClinicId INT NOT NULL,
    RequestDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Status ENUM('Submitted', 'In Progress', 'Completed', 'Cancelled') NOT NULL DEFAULT 'Submitted',
    Description TEXT,
    CreatedByUserId INT NULL,
    HandledByUserId INT NULL,
    ProcessedAt DATETIME NULL,

    CONSTRAINT fk_medicalcheckrequests_pet
        FOREIGN KEY (PetId)
        REFERENCES Pets(PetId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_medicalcheckrequests_shelter
        FOREIGN KEY (ShelterId)
        REFERENCES Organizations(OrganizationId)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_medicalcheckrequests_clinic
        FOREIGN KEY (ClinicId)
        REFERENCES Organizations(OrganizationId)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_medicalcheckrequests_creator
        FOREIGN KEY (CreatedByUserId)
        REFERENCES Users(UserId)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT fk_medicalcheckrequests_handler
        FOREIGN KEY (HandledByUserId)
        REFERENCES Users(UserId)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_medicalcheckrequests_pet ON MedicalCheckRequests(PetId);
CREATE INDEX idx_medicalcheckrequests_shelter ON MedicalCheckRequests(ShelterId);
CREATE INDEX idx_medicalcheckrequests_clinic ON MedicalCheckRequests(ClinicId);
CREATE INDEX idx_medicalcheckrequests_status ON MedicalCheckRequests(Status);

-- =========================================================
-- 8. MedicalReports
-- one medical request -> at most one report
-- =========================================================
CREATE TABLE MedicalReports (
    ReportId INT AUTO_INCREMENT PRIMARY KEY,
    PetId INT NOT NULL,
    MedicalCheckRequestId INT NOT NULL,
    VetStaffUserId INT NOT NULL,
    ExamDate DATE NOT NULL,
    Diagnosis TEXT,
    VaccinationStatus VARCHAR(100),
    Recommendation TEXT,
    ResultStatus VARCHAR(50),
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_medicalreports_request UNIQUE (MedicalCheckRequestId),

    CONSTRAINT fk_medicalreports_pet
        FOREIGN KEY (PetId)
        REFERENCES Pets(PetId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_medicalreports_request
        FOREIGN KEY (MedicalCheckRequestId)
        REFERENCES MedicalCheckRequests(MedicalCheckRequestId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_medicalreports_vetstaff
        FOREIGN KEY (VetStaffUserId)
        REFERENCES Users(UserId)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_medicalreports_pet ON MedicalReports(PetId);
CREATE INDEX idx_medicalreports_vetstaff ON MedicalReports(VetStaffUserId);

-- =========================================================
-- Triggers
-- =========================================================
DELIMITER $$

-- ---------------------------------------------------------
-- Users validation
-- ---------------------------------------------------------
CREATE TRIGGER trg_users_bi_validate
BEFORE INSERT ON Users
FOR EACH ROW
BEGIN
    DECLARE v_org_type VARCHAR(50);

    IF NEW.Role IN ('ShelterAdmin', 'VetStaff') AND NEW.OrganizationId IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'ShelterAdmin and VetStaff must belong to an organization';
    END IF;

    IF NEW.Role IN ('Adopter', 'SurrenderOwner') AND NEW.OrganizationId IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Adopter and SurrenderOwner must not be linked to an organization';
    END IF;

    IF NEW.OrganizationId IS NOT NULL THEN
        SELECT OrganizationType
        INTO v_org_type
        FROM Organizations
        WHERE OrganizationId = NEW.OrganizationId;

        IF NEW.Role = 'ShelterAdmin' AND v_org_type <> 'Shelter' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'ShelterAdmin must belong to a Shelter organization';
        END IF;

        IF NEW.Role = 'VetStaff' AND v_org_type <> 'VeterinaryClinic' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'VetStaff must belong to a VeterinaryClinic organization';
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_users_bu_validate
BEFORE UPDATE ON Users
FOR EACH ROW
BEGIN
    DECLARE v_org_type VARCHAR(50);

    IF NEW.Role IN ('ShelterAdmin', 'VetStaff') AND NEW.OrganizationId IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'ShelterAdmin and VetStaff must belong to an organization';
    END IF;

    IF NEW.Role IN ('Adopter', 'SurrenderOwner') AND NEW.OrganizationId IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Adopter and SurrenderOwner must not be linked to an organization';
    END IF;

    IF NEW.OrganizationId IS NOT NULL THEN
        SELECT OrganizationType
        INTO v_org_type
        FROM Organizations
        WHERE OrganizationId = NEW.OrganizationId;

        IF NEW.Role = 'ShelterAdmin' AND v_org_type <> 'Shelter' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'ShelterAdmin must belong to a Shelter organization';
        END IF;

        IF NEW.Role = 'VetStaff' AND v_org_type <> 'VeterinaryClinic' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'VetStaff must belong to a VeterinaryClinic organization';
        END IF;
    END IF;
END$$

-- ---------------------------------------------------------
-- AdopterProfiles validation
-- ---------------------------------------------------------
CREATE TRIGGER trg_adopterprofiles_bi_validate
BEFORE INSERT ON AdopterProfiles
FOR EACH ROW
BEGIN
    DECLARE v_role VARCHAR(30);

    SELECT Role
    INTO v_role
    FROM Users
    WHERE UserId = NEW.UserId;

    IF v_role <> 'Adopter' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'AdopterProfiles can only be created for users with role Adopter';
    END IF;
END$$

CREATE TRIGGER trg_adopterprofiles_bu_validate
BEFORE UPDATE ON AdopterProfiles
FOR EACH ROW
BEGIN
    DECLARE v_role VARCHAR(30);

    SELECT Role
    INTO v_role
    FROM Users
    WHERE UserId = NEW.UserId;

    IF v_role <> 'Adopter' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'AdopterProfiles can only belong to users with role Adopter';
    END IF;
END$$

-- ---------------------------------------------------------
-- Pets validation
-- ---------------------------------------------------------
CREATE TRIGGER trg_pets_bi_validate
BEFORE INSERT ON Pets
FOR EACH ROW
BEGIN
    DECLARE v_org_type VARCHAR(50);

    SELECT OrganizationType
    INTO v_org_type
    FROM Organizations
    WHERE OrganizationId = NEW.ShelterId;

    IF v_org_type <> 'Shelter' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Pets must belong to an organization of type Shelter';
    END IF;
END$$

CREATE TRIGGER trg_pets_bu_validate
BEFORE UPDATE ON Pets
FOR EACH ROW
BEGIN
    DECLARE v_org_type VARCHAR(50);

    IF NEW.ShelterId IS NOT NULL THEN
        SELECT OrganizationType
        INTO v_org_type
        FROM Organizations
        WHERE OrganizationId = NEW.ShelterId;

        IF v_org_type <> 'Shelter' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Pets must belong to an organization of type Shelter';
        END IF;
    END IF;
END$$
-- ---------------------------------------------------------
-- SurrenderRequests validation
-- ---------------------------------------------------------
CREATE TRIGGER trg_surrenderrequests_bi_validate
BEFORE INSERT ON SurrenderRequests
FOR EACH ROW
BEGIN
    DECLARE v_role VARCHAR(30);
    DECLARE v_handler_role VARCHAR(30);

    SELECT Role
    INTO v_role
    FROM Users
    WHERE UserId = NEW.UserId;

    IF v_role <> 'SurrenderOwner' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'SurrenderRequests must be submitted by a SurrenderOwner';
    END IF;

    IF NEW.HandledByUserId IS NOT NULL THEN
        SELECT Role
        INTO v_handler_role
        FROM Users
        WHERE UserId = NEW.HandledByUserId;

        IF v_handler_role <> 'ShelterAdmin' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'SurrenderRequests can only be handled by a ShelterAdmin';
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_surrenderrequests_bu_validate
BEFORE UPDATE ON SurrenderRequests
FOR EACH ROW
BEGIN
    DECLARE v_role VARCHAR(30);
    DECLARE v_handler_role VARCHAR(30);

    SELECT Role
    INTO v_role
    FROM Users
    WHERE UserId = NEW.UserId;

    IF v_role <> 'SurrenderOwner' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'SurrenderRequests must belong to a SurrenderOwner';
    END IF;

    IF NEW.HandledByUserId IS NOT NULL THEN
        SELECT Role
        INTO v_handler_role
        FROM Users
        WHERE UserId = NEW.HandledByUserId;

        IF v_handler_role <> 'ShelterAdmin' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'SurrenderRequests can only be handled by a ShelterAdmin';
        END IF;
    END IF;
END$$

-- ---------------------------------------------------------
-- AdoptionApplications validation
-- ---------------------------------------------------------
CREATE TRIGGER trg_adoptionapplications_bi_validate
BEFORE INSERT ON AdoptionApplications
FOR EACH ROW
BEGIN
    DECLARE v_role VARCHAR(30);
    DECLARE v_handler_role VARCHAR(30);

    SELECT Role
    INTO v_role
    FROM Users
    WHERE UserId = NEW.UserId;

    IF v_role <> 'Adopter' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'AdoptionApplications must be submitted by an Adopter';
    END IF;

    IF NEW.HandledByUserId IS NOT NULL THEN
        SELECT Role
        INTO v_handler_role
        FROM Users
        WHERE UserId = NEW.HandledByUserId;

        IF v_handler_role <> 'ShelterAdmin' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'AdoptionApplications can only be handled by a ShelterAdmin';
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_adoptionapplications_bu_validate
BEFORE UPDATE ON AdoptionApplications
FOR EACH ROW
BEGIN
    DECLARE v_role VARCHAR(30);
    DECLARE v_handler_role VARCHAR(30);

    SELECT Role
    INTO v_role
    FROM Users
    WHERE UserId = NEW.UserId;

    IF v_role <> 'Adopter' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'AdoptionApplications must belong to an Adopter';
    END IF;

    IF NEW.HandledByUserId IS NOT NULL THEN
        SELECT Role
        INTO v_handler_role
        FROM Users
        WHERE UserId = NEW.HandledByUserId;

        IF v_handler_role <> 'ShelterAdmin' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'AdoptionApplications can only be handled by a ShelterAdmin';
        END IF;
    END IF;
END$$

-- ---------------------------------------------------------
-- MedicalCheckRequests validation
-- ---------------------------------------------------------
CREATE TRIGGER trg_medicalcheckrequests_bi_validate
BEFORE INSERT ON MedicalCheckRequests
FOR EACH ROW
BEGIN
    DECLARE v_shelter_type VARCHAR(50);
    DECLARE v_clinic_type VARCHAR(50);
    DECLARE v_creator_role VARCHAR(30);
    DECLARE v_handler_role VARCHAR(30);
    DECLARE v_pet_shelter_id INT;

    SELECT OrganizationType
    INTO v_shelter_type
    FROM Organizations
    WHERE OrganizationId = NEW.ShelterId;

    SELECT OrganizationType
    INTO v_clinic_type
    FROM Organizations
    WHERE OrganizationId = NEW.ClinicId;

    IF v_shelter_type <> 'Shelter' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MedicalCheckRequests.ShelterId must reference a Shelter';
    END IF;

    IF v_clinic_type <> 'VeterinaryClinic' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MedicalCheckRequests.ClinicId must reference a VeterinaryClinic';
    END IF;

    SELECT ShelterId
    INTO v_pet_shelter_id
    FROM Pets
    WHERE PetId = NEW.PetId;

    IF v_pet_shelter_id <> NEW.ShelterId THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MedicalCheckRequests.ShelterId must match the pet''s shelter';
    END IF;

    IF NEW.CreatedByUserId IS NOT NULL THEN
        SELECT Role
        INTO v_creator_role
        FROM Users
        WHERE UserId = NEW.CreatedByUserId;

        IF v_creator_role <> 'ShelterAdmin' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'MedicalCheckRequests can only be created by a ShelterAdmin';
        END IF;
    END IF;

    IF NEW.HandledByUserId IS NOT NULL THEN
        SELECT Role
        INTO v_handler_role
        FROM Users
        WHERE UserId = NEW.HandledByUserId;

        IF v_handler_role <> 'VetStaff' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'MedicalCheckRequests can only be handled by a VetStaff';
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_medicalcheckrequests_bu_validate
BEFORE UPDATE ON MedicalCheckRequests
FOR EACH ROW
BEGIN
    DECLARE v_shelter_type VARCHAR(50);
    DECLARE v_clinic_type VARCHAR(50);
    DECLARE v_creator_role VARCHAR(30);
    DECLARE v_handler_role VARCHAR(30);
    DECLARE v_pet_shelter_id INT;

    SELECT OrganizationType
    INTO v_shelter_type
    FROM Organizations
    WHERE OrganizationId = NEW.ShelterId;

    SELECT OrganizationType
    INTO v_clinic_type
    FROM Organizations
    WHERE OrganizationId = NEW.ClinicId;

    IF v_shelter_type <> 'Shelter' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MedicalCheckRequests.ShelterId must reference a Shelter';
    END IF;

    IF v_clinic_type <> 'VeterinaryClinic' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MedicalCheckRequests.ClinicId must reference a VeterinaryClinic';
    END IF;

    SELECT ShelterId
    INTO v_pet_shelter_id
    FROM Pets
    WHERE PetId = NEW.PetId;

    IF v_pet_shelter_id <> NEW.ShelterId THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MedicalCheckRequests.ShelterId must match the pet''s shelter';
    END IF;

    IF NEW.CreatedByUserId IS NOT NULL THEN
        SELECT Role
        INTO v_creator_role
        FROM Users
        WHERE UserId = NEW.CreatedByUserId;

        IF v_creator_role <> 'ShelterAdmin' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'MedicalCheckRequests can only be created by a ShelterAdmin';
        END IF;
    END IF;

    IF NEW.HandledByUserId IS NOT NULL THEN
        SELECT Role
        INTO v_handler_role
        FROM Users
        WHERE UserId = NEW.HandledByUserId;

        IF v_handler_role <> 'VetStaff' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'MedicalCheckRequests can only be handled by a VetStaff';
        END IF;
    END IF;
END$$

-- ---------------------------------------------------------
-- MedicalReports validation
-- ---------------------------------------------------------
CREATE TRIGGER trg_medicalreports_bi_validate
BEFORE INSERT ON MedicalReports
FOR EACH ROW
BEGIN
    DECLARE v_vet_role VARCHAR(30);
    DECLARE v_request_pet_id INT;

    SELECT Role
    INTO v_vet_role
    FROM Users
    WHERE UserId = NEW.VetStaffUserId;

    IF v_vet_role <> 'VetStaff' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MedicalReports must be created by a VetStaff user';
    END IF;

    SELECT PetId
    INTO v_request_pet_id
    FROM MedicalCheckRequests
    WHERE MedicalCheckRequestId = NEW.MedicalCheckRequestId;

    IF v_request_pet_id <> NEW.PetId THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MedicalReports.PetId must match the related MedicalCheckRequest.PetId';
    END IF;
END$$

CREATE TRIGGER trg_medicalreports_bu_validate
BEFORE UPDATE ON MedicalReports
FOR EACH ROW
BEGIN
    DECLARE v_vet_role VARCHAR(30);
    DECLARE v_request_pet_id INT;

    SELECT Role
    INTO v_vet_role
    FROM Users
    WHERE UserId = NEW.VetStaffUserId;

    IF v_vet_role <> 'VetStaff' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MedicalReports must be created by a VetStaff user';
    END IF;

    SELECT PetId
    INTO v_request_pet_id
    FROM MedicalCheckRequests
    WHERE MedicalCheckRequestId = NEW.MedicalCheckRequestId;

    IF v_request_pet_id <> NEW.PetId THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MedicalReports.PetId must match the related MedicalCheckRequest.PetId';
    END IF;
END$$

DELIMITER ;