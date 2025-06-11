-- Create database
CREATE DATABASE StudentRegistration;

-- Use the newly created database
USE StudentRegistration;

-- Table: Careers
CREATE TABLE Careers (
    CareerID INT PRIMARY KEY IDENTITY(1,1),
    CareerName NVARCHAR(100) NOT NULL
);

-- Table: Students
CREATE TABLE Students (
    StudentID INT PRIMARY KEY IDENTITY(1,1),
    Code NVARCHAR(20) NOT NULL UNIQUE,  -- Student unique code (e.g., enrollment number)
    FullName NVARCHAR(100),
    Age INT,
    CareerID INT,
    FOREIGN KEY (CareerID) REFERENCES Careers(CareerID)
);

-- Table: Users (for system login)
CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY(1,1),
    FullName VARCHAR(100) NOT NULL,
    PasswordHash VARCHAR(64) NOT NULL,
    Email VARCHAR(200) NOT NULL UNIQUE,
    Status TINYINT NOT NULL
);