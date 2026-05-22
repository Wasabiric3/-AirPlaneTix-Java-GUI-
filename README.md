# AirPlaneTix (Java GUI)

A desktop application built with Java (Swing) for managing airplane ticket bookings. It interfaces with a MySQL database to store and retrieve booking records, passenger information, and flight details. This is an academic Finals Project.

## Features
- Interactive Graphical User Interface (GUI) built with Java Swing.
- MySQL Database integration using JDBC.
- Booking management (Add, View, Update tickets).
- Seat assignment tracking.

## Prerequisites
- Java Development Kit (JDK) 8 or higher
- MySQL Server

## Setup and Installation
1. Ensure your local MySQL server is running.
2. In the project folder, open `db.properties` and update the database credentials (URL, username, password) to match your local setup.
3. The project includes the MySQL JDBC driver (`mysql-connector-j-8.4.0.jar`) required for the database connection.

## How to Run (Windows)
Double-click or run the `run.bat` file in your terminal:
```cmd
.\run.bat
```
This script will automatically compile the Java source files into the `bin` directory and launch the application with the correct classpath for the MySQL driver.
