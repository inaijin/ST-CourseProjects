# Software Testing Course Projects - FALL 2024

This repository contains a collection of projects completed as part of the **Software Testing Course** at the University of Tehran (CEUT) during FALL 2024. Each project focuses on distinct software testing methodologies, tools, and best practices, ranging from unit testing to GUI automation. Below is a detailed overview of each project, along with key features and tools used.

---

## 📂 Project Overview

| Project | Topic | Tools & Frameworks | Key Concepts |
|---------|-------|--------------------|--------------|
| [CA1](#ca1-unit-testing) | Unit Testing | JUnit 5, Parameterized Tests | Boundary Testing, Assertions |
| [CA2](#ca2-mockito--test-spies) | Mockito & Test Spies | Mockito, JUnit | Behavior/State Verification, Fixture Management |
| [CA3](#ca3-graph-coverage--cfg-analysis) | Graph Coverage & CFG Analysis | JaCoCo, Maven | Prime Paths, DU-Pairs, Mutation Testing |
| [CA4](#ca4-api--logic-based-testing) | API & Logic-Based Testing | Spring Boot, REST Assured | Input Space Partitioning, @WebMvcTest |
| [CA5](#ca5-mutation-testing--pipeline) | Mutation Testing & CI/CD Pipeline | PITest, GitHub Actions | Mutation Coverage, Git Workflows |
| [CA6](#ca6-bdd--gui-testing) | BDD & GUI Testing | Katalon Recorder, Selenium, Cucumber | Behavior-Driven Development, Swagger UI |

---

### 🧪 [CA1: Unit Testing](https://github.com/inaijin/ST-CourseProjects/tree/main/CA1-UnitTesting)
**Objective**: Implement unit tests for core classes (`User`, `Restaurant`, `Table`, `Rating`) using JUnit 5, focusing on parameterized tests and edge cases.

**Key Tasks**:
- Write tests for methods in `User`, `Restaurant`, `Table`, and `Rating` classes.
- Use `@ParameterizedTest` for data-driven testing.
- Validate edge cases (e.g., invalid inputs, boundary values).

**Tools**:  
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=junit5&logoColor=white)

---

### 🎭 [CA2: Mockito & Test Spies](https://github.com/inaijin/ST-CourseProjects/tree/main/CA2-Mockito)
**Objective**: Test controllers (`AuthenticationController`, `ReviewController`, `ReservationController`) using Mockito for mocking dependencies and verifying interactions.

**Key Tasks**:
- Mock dependencies for `AuthenticationController`, `ReviewController`, and `ReservationController`.
- Compare `State Verification` vs. `Behavior Verification`.
- Address shared fixture challenges in test suites.

**Tools**:  
![Mockito](https://img.shields.io/badge/Mockito-78C6A3?style=flat&logo=java&logoColor=white)  
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=junit5&logoColor=white)

---

### 📊 [CA3: Graph Coverage & CFG Analysis](https://github.com/inaijin/ST-CourseProjects/tree/main/CA3-GraphCoverage)
**Objective**: Analyze Control Flow Graphs (CFGs) and achieve 90%+ branch/statement coverage using JaCoCo.

**Key Tasks**:
- Generate CFGs for code snippets.
- Identify Prime Paths and DU-Pairs.
- Write tests to achieve 100% coverage for critical paths.

**Tools**:  
![JaCoCo](https://img.shields.io/badge/JaCoCo-4C4C4C?style=flat&logo=coverage&logoColor=white)  
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)

---

### 🔗 [CA4: API & Logic-Based Testing](https://github.com/inaijin/ST-CourseProjects/tree/main/CA4-APITesting)
**Objective**: Test REST APIs (`TableController`, `RestaurantController`) using logic-based and input-space partitioning techniques.

**Key Tasks**:
- Validate API endpoints with `@WebMvcTest`.
- Implement tests for `calculateDiscountedPrice` using boundary values.
- Address edge cases (e.g., invalid discounts, minimum purchase rules).

**Tools**:  
![SpringBoot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white)  
![RESTAssured](https://img.shields.io/badge/REST_Assured-66CC00?style=flat&logo=rest&logoColor=white)

---

### 🧬 [CA5: Mutation Testing & Pipeline](https://github.com/inaijin/ST-CourseProjects/tree/main/CA5-MutationCoverage)
**Objective**: Evaluate test suite quality using PITest and automate testing via GitHub Actions.

**Key Tasks**:
- Measure mutation coverage for `Transaction` and `TransactionEngine` classes.
- Set up CI/CD pipeline with GitHub Workflows.
- Analyze `Killed`, `Survived`, and `Lived` mutants.

**Tools**:  
![PITest](https://img.shields.io/badge/PITest-000000?style=flat&logo=pitest&logoColor=white)  
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat&logo=githubactions&logoColor=white)

---

### 🖥️ [CA6: BDD & GUI Testing](https://github.com/inaijin/ST-CourseProjects/tree/main/CA6-BDD&GUITesting)
**Objective**: Automate GUI tests using Katalon Recorder and implement BDD scenarios with Cucumber.

**Key Tasks**:
- Record test scenarios for user registration, restaurant listing, and reservation flows.
- Generate Selenium WebDriver scripts from Katalon Recorder.
- Write BDD tests for `addReservation`, `addReview`, and `getAverageRating`.

**Tools**:  
![Selenium](https://img.shields.io/badge/Selenium-43B02A?style=flat&logo=selenium&logoColor=white)  
![Cucumber](https://img.shields.io/badge/Cucumber-23D96C?style=flat&logo=cucumber&logoColor=white)

---

## 🤝 Contributing
Contributions are welcome! Fork the repository, open issues, or submit pull requests. Ensure your code follows the project’s testing standards.
