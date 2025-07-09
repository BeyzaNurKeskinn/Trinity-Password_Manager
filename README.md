
## Trinity Password Manager

Trinity Password Manager is a modern web application designed to securely store, categorize, and manage user passwords. 
With a user-friendly interface and robust security features, it provides a comprehensive solution for both individual users and administrators.
The project is built with a React and TypeScript-based frontend, a Spring Boot-based backend, and a PostgreSQL database, with Docker support for easy deployment.

Features

User Management:

    --Sign-up, login, and password reset (via email or phone verification code).

    --Support for social media login with Google and Facebook.

    --Profile picture upload and user information updates.

    --Account deactivation/freezing.


Password Management:

    --Add, update, delete, and categorize passwords.

    --Search and filter passwords by category.

    --Mark and highlight featured passwords.

    --Verification code required via email to view passwords.


Category Management (Admin):

    --Create, update, and delete categories.

    --Color-coded categories for visual distinction.


User Management (Admin):

    --Add, update, and deactivate users.

    --Assign user roles (USER/ADMIN).


Dashboard:

    --Matrix-themed interface displaying recently viewed and featured passwords for users.

    --System statistics and recent activities for admins.


Security:

    --JWT-based authentication.

    --Passwords stored encrypted using AES/BCrypt.

    --HttpOnly cookies for enhanced security against XSS attacks.


Responsive Design:

    --TailwindCSS-based interface compatible with mobile, tablet, and desktop devices.

    --Matrix-themed neon green/blue color palette with VT323 font.


Docker Support:

    --Easy deployment with Docker Compose for frontend, backend, and PostgreSQL.



Technology Stack

Frontend

    React: User interface development.
    TypeScript: Type safety and code quality.
    Vite: Fast development and build tool.
    TailwindCSS: Responsive design with custom animations (matrix-rain, pulse-glow).
    Axios: API requests.
    React Router: Page navigation.
    Heroicons: Icon set.
    @greatsumini/react-facebook-login, @react-oauth/google: Social media login integration.

Backend

    Spring Boot: RESTful API development.
    Java: Backend programming language.
    PostgreSQL: Database management.
    JWT: Authentication and authorization.
    AES/BCrypt: Encryption and password hashing.
    Spring Security: Security layer.

DevOps

    Docker: Containerization of services.
    Docker Compose: Orchestration of frontend, backend, and database services.
    Render: Cloud-based deployment (assumed based on prior discussions).

Installation

Prerequisites

    Node.js (v16 or higher)
    Java (JDK 17)
    Docker and Docker Compose
    PostgreSQL (if not using Docker for local development)
    Git

Steps

Clone the Repository:

    git clone <repository-url>
    cd trinity-password-manager


Set Up Environment Variables:

Create a .env file in the root directory and populate it with the following variables:

    DB_USERNAME=postgres
    DB_PASSWORD=your_secure_password
    DB_NAME=trinity_db
    DB_HOST=db
    DB_PORT=5432
    ENCRYPTION_SECRET_KEY=your_encryption_secret_key
    JWT_SECRET=your_jwt_secret
    MAIL_USERNAME=your_email_username
    MAIL_PASSWORD=your_email_password




Run with Docker:

Start all services using Docker Compose:

    docker-compose up --build


Access points:

    Frontend: http://localhost:5173
    Backend: http://localhost:8080
    PostgreSQL: localhost:5432




Local Development (Without Docker):

Backend:
  Navigate to the backend directory.
  
  Install dependencies:
  
      mvn clean install.
  
  Run the backend:
  
    mvn spring-boot:run.


Frontend:

Navigate to the frontend directory.

Install dependencies: 

    npm install.

Start the development server: 

    npm run dev.


Access the Application:

Open http://localhost:5173 in your browser.

Sign up or log in with an existing account.


Restore Database Backup (Optional):

If you have a backup file like trinity_backup_full.sql:

    docker cp trinity_backup_full.sql trinity-db-1:/backup.sql

    docker exec -it trinity-db-1 psql -U postgres -d trinity_db -f /backup.sql





Usage

User Operations

Login/Sign-Up:

    Navigate to /login or /register to create an account or log in.

    Use Google or Facebook for quick login.


Password Management:

    Go to /user/passwords to add, update, delete, or categorize passwords.

    View passwords by entering the verification code sent via email.

    Filter passwords by title, username, or description using the search bar.


Account Management:

    Visit /user/account to upload a profile picture, update user information, or deactivate your account.



Admin Operations

    User Management:

    Access /admin/users to add, update, or deactivate users.


Category Management:

    Go to /admin/categories to create, edit, or delete categories.


Dashboard:

    View recent activities and featured passwords at /admin/dashboard or /user/dashboard.

    The Matrix-themed dashboard includes clickable "Frequently Viewed Passwords" that redirect to /passwords.


API Endpoints (Examples)

The backend runs at https://trinity-backend-szj7.onrender.com/api. 

Example endpoints:

    POST /auth/login: User login (returns JWT token).
    POST /auth/register: Register a new user.
    POST /auth/forgot-password: Send password reset code.
    POST /auth/reset-password: Reset password.
    GET /user/me: Retrieve user information.
    GET /user/passwords: List user passwords.
    POST /user/passwords: Add a new password.
    PUT /user/passwords/{id}: Update a password.
    DELETE /user/passwords/{id}: Delete a password.
    GET /admin/users: List all users (admin).
    POST /admin/users: Add a new user (admin).
    PUT /admin/users/{id}: Update a user (admin).

Development Notes

Frontend:

    Vite provides a fast development environment.

    TailwindCSS implements a Matrix-themed neon green/blue color scheme with VT323 font.

    Responsive design: Sidebar is hidden on mobile, and cards are displayed in a single column.

    ESLint and TypeScript ensure code quality.


Backend:

    Spring Boot powers the RESTful API.

    Passwords are encrypted with AES/BCrypt.

    HttpOnly cookies enhance JWT security (XSS protection).


Docker:

    trinity-network enables communication between services.

    PostgreSQL includes health checks for reliable startup.


Responsive Design:

    Dashboard displays password cards in a 2x2 grid on desktop and a single column on mobile.

    User management and password lists adapt to various screen sizes.


Troubleshooting

Login/Sign-Up Errors:

    Verify MAIL_USERNAME and MAIL_PASSWORD in the .env file.

    Check the spam/junk folder for email verification codes.


Password Viewing Issues:

    If the verification code is not received, inspect backend logs (backend-1 container).


Docker Issues:

    Check for port conflicts (5432, 8080, 5173) using docker ps.

    Verify DB_USERNAME, DB_PASSWORD, and DB_HOST in the .env file for database connection issues.


Deactivate Button Issues:

    Ensure the backend endpoint (PUT /api/admin/users/{id}) is correctly configured in UserManagement.tsx.



Deployment (Render)

Push Docker Images to Docker Hub:

    Build and push Docker images for backend and frontend:docker build -t <dockerhub-username>/trinity-backend ./backend
    docker build -t <dockerhub-username>/trinity-frontend ./frontend
    docker push <dockerhub-username>/trinity-backend
    docker push <dockerhub-username>/trinity-frontend




Deploy on Render:

    Create a new Web Service on Render.

    Link the Docker Hub images and upload .env variables.

    Set up a managed PostgreSQL database on Render and update .env with connection details.


Test:

    Verify the deployment at https://<your-render-url>.



Contributing

    Fork the repository.

    Create a new branch: git checkout -b feature/your-feature.

    Make your changes and commit: git commit -m "Feature: ..."

    Push to the branch: git push origin feature/your-feature.

    Open a Pull Request.

License
    This project is licensed under the MIT License. See the LICENSE file for details.
Contact
For questions, email beyzanurhorasan89@gmail.com or open an issue on GitHub.
