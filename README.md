# Patient and Exam Management System (Java + MySQL)

This is a simple console-based CRUD application built in Java that allows users to manage patients and medical exams. It was developed as part of an academic exercise to reinforce concepts such as object-oriented programming, data persistence using relational databases, and the DAO (Data Access Object) design pattern.

## 📌 Purpose

This Toy Project aims to provide a learning environment for students to apply knowledge acquired throughout the course. The application simulates a real-world healthcare management system where users can perform basic operations (Create, Read, Update, Delete) through a Java console interface.

## 🧩 Features

- Register, list, update, and delete **patients**.
- Register, list, update, and delete **exams**.
- Associate exams with patients.
- Display patients with their associated exams.
- Persist all data using **MySQL** (or compatible RDBMS).
- Organized using the **DAO pattern** for clean separation of concerns.

## 💻 Technologies Used

- Java (JDK 17+ recommended)
- JDBC for database connection
- MySQL for data persistence
- Maven (optional, if using for dependency management)

  
## 📂 Project Structure
crud/
├── prontuario/
│ ├── app/ # Application entry point
│ ├── dao/ # Data Access Objects (DAOs)
│ ├── database/ # Database connection classes
│ ├── model/ # Domain models (Paciente, Exame)



## ⚙️ Requirements

- Java 17 or higher
- MySQL installed and running
- Database schema created (see below)
- IDE like Eclipse, IntelliJ, or VSCode (optional)

## 🗄️ Database Schema (MySQL)

```sql
CREATE TABLE pacientes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(255) NOT NULL,
  cpf VARCHAR(14) NOT NULL UNIQUE
);

CREATE TABLE exames (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  descricao VARCHAR(255) NOT NULL,
  data_exame DATETIME NOT NULL,
  paciente_id BIGINT,
  FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE
);
```

## 🚀 How to Run
- Clone the repository.

- Set up your MySQL database and update the connection details in DatabaseConnectionMySQL.java.

- Compile and run Application.java.

- Follow the console prompts to interact with the system.

## 🤝 Contributions
This project is for educational purposes. Contributions, suggestions, or improvements are welcome!

Made with ☕ and 💻 for learning purposes.
