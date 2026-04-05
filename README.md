# PetAdoptionSystem
A digital platform for managing pet surrender, health check, and adoption workflows.

## Project Overview
This project is a digital pet adoption system that helps shelters and veterinary clinics work together with surrender owners and adopters. The system manages pet surrender requests, medical check processes, and adoption applications in one shared platform.

## Participating Organizations
- Pet Adoption Agency / Shelter
- Veterinary Clinic

## User Roles
- Surrender Owner
- Adopter
- Shelter Admin
- Vet Staff

## Main Workflow

1. Surrender Process
- A surrender owner submits a surrender request
- Shelter admin reviews the request
- If approved, the pet is accepted and a pet record is created

2. Medical Check Process
- Shelter sends the pet to a veterinary clinic
- Vet staff performs a medical check
- Vet updates the health record
- Pet status is updated to "Ready for Adoption"

3. Adoption Process
- Adopter browses available pets
- Adopter submits an adoption application
- Shelter admin reviews the application
- If approved, the pet status is updated to "Adopted"

## Role Definition
- Surrender Owner

Register and manage personal profile
Fill in pet information
Submit a pet surrender request
View surrender request status

- Shelter Admin

Review surrender requests
Accept or reject pet intake
Create pet records
Send pets for medical check
Review adoption applications
Approve or reject adoption requests
Update pet status

- Vet Staff

View medical check requests
Enter examination results
Update pet health status
Submit medical reports back to the shelter

- Adopter

Register and manage personal profile
Set adoption preferences
Browse available pets
Submit adoption applications
View adoption application status

## Status

Pet status
-submitted
-accepted
-under medical check
-ready for adoption
-adoption pending
-adopted

Surrender request status
-submitted
-approved
-rejected

Adoption request status
-submitted 
-approved
-rejected
-completed


## Use Case Diagram

- Surrender Owner
Register/Manage Profile
Submit Surrender Request
Enter Pet Information
View Request Status

- Shelter Admin
Review Surrender Request
Accept/Reject Pet Intake
Create Pet Record
Send Medical Check Request
Review Medical Report
Manage Pet Status
Review Adoption Applications
Approve / Reject Adoption

- Vet Staff
View Medical Check Requests
Enter Examination Results
Update Pet Health Status
Submit Medical Report


- Adopter
Register/Manage Profile
Browse Available Pets
Submit Adoption Application
View Adoption Status


## Class Diagram

1）UI 
MainFrame

Attributes

system: PetAdoptionSystem
cardLayout: CardLayout
mainPanel: JPanel

Methods

MainFrame()
switchPanel(panelName: String)
initComponents()
LoginPanel

Attributes

usernameField: JTextField
passwordField: JPasswordField
system: PetAdoptionSystem
mainFrame: MainFrame

Methods

login()
clearFields()
SurrenderOwnerPanel

Attributes

system: PetAdoptionSystem
currentUser: SurrenderOwner
petTable: JTable

Methods

submitSurrenderRequest()
enterPetInformation()
viewRequestStatus()
AdopterPanel

Attributes

system: PetAdoptionSystem
currentUser: Adopter
petTable: JTable

Methods

browsePets()
submitAdoptionApplication()
viewAdoptionStatus()
ShelterAdminPanel

Attributes

system: PetAdoptionSystem
currentUser: ShelterAdmin
requestTable: JTable

Methods

reviewSurrenderRequests()
createPetRecord()
sendMedicalCheckRequest()
reviewAdoptionApplications()
updatePetStatus()
VetStaffPanel

Attributes

system: PetAdoptionSystem
currentUser: VetStaff
requestTable: JTable

Methods

viewMedicalCheckRequests()
enterExaminationResults()
updatePetHealthStatus()
submitMedicalReport()


2）Core
PetAdoptionSystem <<Singleton>>

Attributes

instance: PetAdoptionSystem
userDAO: UserDAO
organizationDAO: OrganizationDAO
petDAO: PetDAO
surrenderRequestDAO: SurrenderRequestDAO
adoptionApplicationDAO: AdoptionApplicationDAO
medicalCheckRequestDAO: MedicalCheckRequestDAO
medicalReportDAO: MedicalReportDAO

Methods

getInstance(): PetAdoptionSystem
authenticateUser(email: String, password: String): User
getAvailablePets(): List<Pet>
createSurrenderRequest(...)
createAdoptionApplication(...)
createMedicalCheckRequest(...)
createMedicalReport(...)
updateRequestStatus(...)

3）DAO 
UserDAO

Attributes

db: DatabaseManager

Methods

createUser(user: User): void
getUserById(id: int): User
getUserByEmail(email: String): User
updateUser(user: User): void
deleteUser(id: int): void
OrganizationDAO
createOrganization()
getOrganizationById()
getAllOrganizations()
updateOrganization()
deleteOrganization()
PetDAO
createPet()
getPetById()
getAllPets()
getAvailablePets()
updatePet()
deletePet()
SurrenderRequestDAO
createRequest()
getRequestById()
getAllRequests()
updateRequest()
deleteRequest()
AdoptionApplicationDAO
createApplication()
getApplicationById()
getAllApplications()
updateApplication()
deleteApplication()
MedicalCheckRequestDAO
createRequest()
getRequestById()
getAllRequests()
updateRequest()
deleteRequest()
MedicalReportDAO
createReport()
getReportById()
getReportsByPetId()
updateReport()
deleteReport()

4）Database 
DatabaseManager

Attributes

connection: Connection

Methods

connect(): void
closeConnection(): void
executeQuery(sql: String): ResultSet
executeUpdate(sql: String): int

5）Model 
User <<abstract>>

Attributes

userId: int
name: String
email: String
password: String
phone: String
address: String

Methods

login()
updateProfile()
Adopter

Attributes

preference: String
housingType: String

Methods

submitAdoptionApplication()
SurrenderOwner

Attributes

ownerNote: String

Methods

submitSurrenderRequest()
ShelterAdmin

Attributes

employeeId: String

Methods

reviewRequest()
updatePetStatus()
VetStaff

Attributes

staffId: String

Methods

createMedicalReport()
updateHealthStatus()
Organization <<abstract>>

Attributes

organizationId: int
name: String
address: String
phone: String

Methods

updateOrganizationInfo()
Shelter

Methods

acceptPet()
publishPet()
VeterinaryClinic

Methods

performCheckup()
Pet

Attributes

petId: int
name: String
species: String
breed: String
age: int
gender: String
healthStatus: String
adoptionStatus: String
intakeDate: Date

Methods

updateHealthStatus()
updateAdoptionStatus()
WorkRequest <<abstract>>

Attributes

requestId: int
requestDate: Date
status: String
description: String

Methods

createRequest()
updateStatus()
SurrenderRequest

Attributes

reason: String
AdoptionApplication

Attributes

adopterNote: String
MedicalCheckRequest

Attributes

priority: String
MedicalReport

Attributes

reportId: int
examDate: Date
diagnosis: String
vaccinationStatus: String
recommendation: String
resultStatus: String

Methods

generateReport()
submitReport()







