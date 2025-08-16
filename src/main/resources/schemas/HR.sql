-- ======================================================
-- FILE        : HR.sql
-- DESCRIPTION : SQL Generic Script to create HR Schema
-- VERSION     : 1.0
-- AUTHOR      : OpenAI adapted by Andressc19
-- DATE        : 2025-08-07
-- COMPATIBLE  : PostgreSQL, MySQL, SQLite, SQL Server, Oracle
-- ======================================================

-- ==============================
-- TABLE: regions
-- ==============================
CREATE TABLE regions (
    region_id      INT PRIMARY KEY,
    region_name    VARCHAR(25)
);

-- ==============================
-- TABLE: countries
-- ==============================
CREATE TABLE countries (
    country_id     CHAR(2) PRIMARY KEY,
    country_name   VARCHAR(60),
    region_id      INT,
    FOREIGN KEY (region_id) REFERENCES regions(region_id)
);

-- ==============================
-- TABLE: locations
-- ==============================
CREATE TABLE locations (
    location_id     INT PRIMARY KEY,
    street_address  VARCHAR(40),
    postal_code     VARCHAR(12),
    city            VARCHAR(30) NOT NULL,
    state_province  VARCHAR(25),
    country_id      CHAR(2),
    FOREIGN KEY (country_id) REFERENCES countries(country_id)
);

-- ==============================
-- TABLE: departments
-- ==============================
CREATE TABLE departments (
    department_id    INT PRIMARY KEY,
    department_name  VARCHAR(30) NOT NULL,
    manager_id       INT,
    location_id      INT,
    FOREIGN KEY (location_id) REFERENCES locations(location_id)
);

-- ==============================
-- TABLE: jobs
-- ==============================
CREATE TABLE jobs (
    job_id      VARCHAR(10) PRIMARY KEY,
    job_title   VARCHAR(35) NOT NULL,
    min_salary  DECIMAL(10,2),
    max_salary  DECIMAL(10,2)
);

-- ==============================
-- TABLE: employees
-- ==============================
CREATE TABLE employees (
    employee_id     INT PRIMARY KEY,
    first_name      VARCHAR(20),
    last_name       VARCHAR(25) NOT NULL,
    email           VARCHAR(25) NOT NULL UNIQUE,
    phone_number    VARCHAR(20),
    hire_date       DATE NOT NULL,
    job_id          VARCHAR(10) NOT NULL,
    salary          DECIMAL(10,2) CHECK (salary > 0),
    commission_pct  DECIMAL(5,2),
    manager_id      INT,
    department_id   INT,
    FOREIGN KEY (job_id) REFERENCES jobs(job_id),
    FOREIGN KEY (manager_id) REFERENCES employees(employee_id),
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- ==============================
-- TABLE: job_history
-- ==============================
CREATE TABLE job_history (
    employee_id     INT NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    job_id          VARCHAR(10) NOT NULL,
    department_id   INT,
    PRIMARY KEY (employee_id, start_date),
    CHECK (end_date > start_date),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    FOREIGN KEY (job_id) REFERENCES jobs(job_id),
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- ==============================
-- ALTER: Add circular FK
-- ==============================
ALTER TABLE departments
    ADD FOREIGN KEY (manager_id) REFERENCES employees(employee_id);

