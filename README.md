# Stork Drop
Stork Drop is a miniature distributed file management system. 

##  Overview
### What this project does

 The primary features of this project are : -

  1. File Uploading - A user can upload any file they want to the server to be shared by others
  2. File Downloading - A user with the right permission attributes , that is the ability to read / write into the file can download files into their local computer. 
  3. File Versioning- A user can upload different version of the same file and the system will elegantly handle the naming convention and store the file in the appropriate database and server by indicating that the file is a version. The indication is done by a special key / id - version_ID in the file versions and file repository.
  4. File Syncing - Whenever there is a new version for a file, the system notifies all users that have access to it that a new version exists.
  5. File Permissions - After uploading a file as an owner, the user can allow users to access the file by either using read mode / write mode or restrict access.
  6. User Login/Sign-up - The system implements a proper user login and signup by storing data in the database for a signup and producing a bearer token for authentication everytime the user logs in . Every activity the user does is linked by it's user ID and managed properly . 

### Why it exists

The main reason we decided to build this project is to have a general understanding of how file management works, how Java handles system programming, understand the underlying working mechanism of HTTP requests and SpringBoot. 

### Who it is for

If you are a developer wishing to understand how file management works, we believe this project will give you a little more understanding of the underlying mechanisms.

## Tech Stack
- Language(s): Java, HTML, CSS
- Framework(s): SpringBoot, JavaFx, Hibernate, JPA, Eclipse 

##  Getting Started

### Prerequisites
List of requirements for the system :
- Java 21
- Maven
- Xammp MySQL server or MySQL Workbench 

### How to run the project
```bash
# 1. Run the Xammp Server and Start MySQL service 
# 2. Run the SpringBoot backend (StorkApplication) 
# 3. Run the JavaFx client service (Stork)
# 4. Create an Account 
# 5. Log into the Account 
# 6. Perform any operation you would like . 
```
### How to close the project 

```bash
# 1. Close the JavaFx window
# 2. Stop the springboot server
# 3. Stop the MySQL service form Xammp
```

