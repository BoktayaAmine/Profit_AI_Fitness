# 💪 Profit: Your Fitness AI Assistant 🏋‍♂

![Profit App Banner](https://github.com/user-attachments/assets/762b9fc0-9051-4b36-93ef-1b734848811a)

Profit is a comprehensive fitness android application designed to help users achieve their fitness goals through personalized AI-driven recommendations. The app combines modern technologies to provide a seamless experience for tracking progress, planning workouts, and staying motivated.

---
## 📑 Table of Contents
- [🛠 Software Architecture](#-software-architecture)
- [🐳 Docker Image](#-docker-image)
- [📱 Frontend](#-frontend)
- [⚙ Backend](#-backend)
- [🤖 AI Service](#-ai-service)
- [🚀 Getting Started](#-getting-started)
- [🎥 Video Demonstration](#-video-demonstration)
- [🤝 Contributing](#-contributing)
- [🙌 Contributors](#-contributors)

---
##  🛠 Software Architecture

![Architecture](https://github.com/user-attachments/assets/cc6c8f05-6937-48c9-bb19-539391eb1467)

The architecture includes:
- *Front Mobile:* Built with Java/Kotlin for Android devices.
- *Front Admin:* Developed with modern web technologies for managing user data.
- *Back End:* Powered by Spring Boot for core business logic.
- *AI Service:* Used for generating personalized fitness recommendations, selecting suitable exercises based on user data, and providing textual advice based on fitness progress and goals.
---
## 🐳 Docker Image

```yaml
version: '3'
services:
  backend:
    build:
      context: ./backend
    ports:
      - "8090:8090"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/pfm_mobile
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=
    depends_on:
      - db
    networks:
      - app-network

  frontend:
    build:
      context: ./front_admin
    ports:
      - "3000:3000"
    depends_on:
      - backend
    networks:
      - app-network

  flask_api:
    build:
      context: ./Profit_flask
    ports:
      - "5000:5000"
    environment:
      - FLASK_ENV=development
      - API_KEY=your_api_key_here
    depends_on:
      - db
    networks:
      - app-network

  db:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: 
      MYSQL_DATABASE: pfm_mobile
    ports:
      - "3306:3306"
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

```
---
## 📱 Frontend

### 📲 Mobile Frontend

- Built with *Java/Kotlin* for native Android functionality.
- Includes features such as:
  - Workout tracking
  - Daily calorie logging
  - Interactive charts and graphs

### 💻 Admin Frontend

- Web-based admin panel for managing user accounts and reviewing data.

---
## ⚙ Backend

### Technologies Used

- *Spring Boot* for robust backend operations.
- *MySQL* for database management.

### Features

- User management
- Workout and calorie tracking
- Integration with the AI service for AI-based recommendations

---
## 🤖 AI Service
The AI microservice in our Flask application is responsible for:

- Handling personalized fitness recommendations: Generates tailored textual advice and exercise suggestions based on user data (e.g., fitness level, health conditions, objectives).
- Providing AI-powered exercise selection: Recommends exercises that align with the user's fitness level and completed objectives.
### Technologies Used
- Flask: Python web framework.
- Flask-CORS: Enables cross-origin requests.
- Google Generative AI: Powers content generation for personalized recommendations.
- dotenv: Manages API keys and environment variables.

---
## 🚀 Getting Started

### Prerequisites

1. Install [Git](https://git-scm.com/).
2. Install [Docker](https://www.docker.com/).
3. Install [Android Studio](https://developer.android.com/studio) for mobile frontend.
4. Install [Node.js](https://nodejs.org/) for admin frontend.

### Setup Steps

1. Clone the repository:
   bash
   git clone <repository_url>
   cd <project_folder>
   

2. Start the application:
   bash
   docker-compose up --build
   

3. Run the mobile app in Android Studio or install the APK on your device and link it with your PC.

4. Access the admin frontend at http://localhost:3000.

5. The AI service runs on http://localhost:5000.

## 🎥 Video Demonstration

🎬Watch a detailed demonstration of Profit in action:

[![Watch Video]](https://github.com/user-attachments/assets/7abab7df-04a6-41c5-a041-5f366a4ac5b1)

---
## 🤝 Contributing

We welcome contributions to improve Profit. To contribute:

1. Fork the repository.
2. Create a new branch for your feature.
3. Submit a pull request.

---
## 🙌 Contributors

- Amine Boktaya ([GitHub Profile](https://github.com/BoktayaAmine))
- Meryem Boukhrais ([GitHub Profile](https://github.com/Bou-Mery))
