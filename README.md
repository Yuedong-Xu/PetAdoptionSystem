# Pet Adoption System

## Project Overview
The Pet Adoption System is a Java Swing application designed to support the pet surrender, medical check, and adoption process in a more organized and efficient way.

This system connects multiple parties involved in pet rescue and adoption, including surrender owners, adopters, shelter administrators, and veterinary staff. It helps reduce communication problems, improves workflow efficiency, and supports better protection and placement for pets.

## Problem Statement
Without a centralized system, pet rescue and adoption can be inconvenient and inefficient. People who want to adopt may not know where to find pets, people who need to surrender pets may not know where to send them, and rescue organizations may struggle to coordinate screening and health checks.

This system solves these problems by providing one platform that supports:
- pet surrender request management
- medical check request workflow
- adoption application workflow
- multi-role communication and status tracking

Overall, it makes the rescue and adoption process more organized, reliable, and efficient.

## Organizations and Roles
This project includes 2 organizations:
- Shelter
- Veterinary Clinic

This project includes 4 main roles:
- Surrender Owner
- Adopter
- Shelter Admin
- Vet Staff

## Main Features
### Surrender Owner
- Submit surrender request
- Enter pet information
- View surrender request status
- Withdraw surrender request

### Adopter
- Register / manage adopter profile
- Browse available pets
- Submit adoption application
- View adoption application status
- Withdraw adoption application

### Shelter Admin
- Review surrender requests
- Approve or reject pet intake
- Send medical check request
- Review adoption applications
- Approve or reject adoption applications
- Review medical check requests
- Cancel medical check requests

### Vet Staff
- View medical check requests
- Submit medical report
- Update pet health status through report processing

## System Design
The system follows a layered design:
- **UI Layer**: Java Swing frames for each role
- **Service Layer**: `PetAdoptionSystem` singleton for business logic
- **DAO Layer**: DAO classes for database operations
- **Model Layer**: Java model classes representing entities
- **Database Layer**: MySQL database with JDBC connectivity

## Technologies Used
- Java
- Java Swing
- MySQL
- JDBC
- MySQL Workbench
- NetBeans

## Database
The system uses MySQL as the backend database with JDBC connection support.

Main database tables include:
- Organizations
- Users
- AdopterProfiles
- Pets
- SurrenderRequests
- AdoptionApplications
- MedicalCheckRequests
- MedicalReports

## Diagrams
This project includes:
- Class Diagram
- Use Case Diagram

## Demo Video
Presentation + Demo Video Link:  
(https://drive.google.com/file/d/1tV_tyGFngmxsXqkBDvl726vneMH-5ufh/view?usp=sharing)

## Screenshots
This repository includes:
- screenshots of all major application windows
- screenshots of database CRUD activity in MySQL Workbench

## How to Run
1. Open MySQL Workbench and run `schema.sql`
2. Insert demo data into the database
3. Update database connection settings in `DBConfig.java` if needed
4. Open the project in NetBeans
5. Run `App.java`
6. Log in using demo accounts

## Demo Accounts
- Adopter 1: `adopter1@test.com` / `123456`
- Adopter 2: `adopter2@test.com` / `123456`
- Surrender Owner: `owner@test.com` / `123456`
- Shelter Admin: `shelter@test.com` / `123456`
- Vet Staff: `vet@test.com` / `123456`

## Repository Contents
- Java source code
- `schema.sql`
- Class Diagram
- Use Case Diagram
- Application screenshots
- Database CRUD screenshots
- Demo video link

## Author
Yuedong Xu

## Version
demo final version


