Users
UserId (PK)
Name
Gender
Age
Email
Password
Phone
Address
Role
OrganizationId (FK, nullable)

AdopterProfiles
AdopterProfileId (PK)
UserId (FK, unique)
MonthlyIncome
LivingArea
NumberOfPets
PetRaisingExperience
NumberOfPeople
PreferredPetType

Organizations
OrganizationId (PK)
Name
Phone
Address
OrganizationType

Pets
PetId (PK)
Name
Species
Breed
Age
Gender
Weight
Color
HealthStatus
HealthCheckStatus
AdoptionStatus
IntakeDate
ShelterId (FK)

SurrenderRequests
SurrenderRequestId (PK)
UserId (FK)
PetId (FK)
RequestDate
Status
Description
Reason

AdoptionApplications
ApplicationId (PK)
UserId (FK)
PetId (FK)
ApplicationDate
Status
Description

MedicalCheckRequests
MedicalCheckRequestId (PK)
PetId (FK)
ShelterId (FK)
ClinicId (FK)
RequestDate
Status
Description

MedicalReports
ReportId (PK)
PetId (FK)
VetStaffUserId (FK)
MedicalCheckRequestId (FK, nullable)
ExamDate
Diagnosis
VaccinationStatus
Recommendation
ResultStatus
