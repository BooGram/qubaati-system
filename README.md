# 🎓 Qubaati System | نظام قبعاتي

<p align="center">
  <b>AI-powered adaptive learning platform for children</b><br/>
  <b>منصة تعليمية ذكية وتفاعلية للأطفال مدعومة بالذكاء الاصطناعي</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen?style=for-the-badge&logo=springboot" />
  <img src="https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql" />
  <img src="https://img.shields.io/badge/Spring%20Security-Basic%20Auth-green?style=for-the-badge&logo=springsecurity" />
  <img src="https://img.shields.io/badge/Spring%20AI-OpenAI-purple?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Moyasar-Payments-lightgrey?style=for-the-badge" />
  <img src="https://img.shields.io/badge/n8n-Automation-orange?style=for-the-badge&logo=n8n" />
</p>

---

## 📌 Project Summary

### العربية

**نظام قبعاتي** هو منصة تعليمية ذكية وتفاعلية للأطفال، تساعد الطلاب على التعلم من خلال عوالم مهنية افتراضية مثل الطب، الهندسة، العلوم، التعليم وغيرها. يدخل الطالب إلى هذه العوالم وينفذ مهام وأنشطة تفاعلية وأسئلة تعليمية مبنية على القرارات.

يقوم النظام بتحليل أداء الطالب وسلوكه التعليمي وسرعة اتخاذ القرار ونقاط القوة والضعف والمهارات ونمط التعلم. يستطيع المعلم إنشاء أنشطة باستخدام الذكاء الاصطناعي، تعديلها، اعتمادها، ثم تعيينها للطلاب أو الفصول. كما يستطيع ولي الأمر متابعة تقدم أبنائه من خلال لوحات تحكم وتقارير أسبوعية يتم إنشاؤها بمساعدة n8n والذكاء الاصطناعي.

هدف قبعاتي هو تقديم تجربة تعليمية أعمق من مجرد الدرجات، تساعد الطفل على اكتشاف مهاراته وميوله واهتماماته المستقبلية.


### English

**Qubaati System** is an AI-assisted educational platform designed for children, teachers, and parents. Students explore interactive career worlds such as medicine, engineering, science, teaching, and more. Inside each world, they complete missions, activities, questions, and decision-based learning experiences.

The system analyzes student performance, learning behavior, decision speed, strengths, weaknesses, skills, and learning style. Teachers can generate and refine AI-powered activities, assign them to students or classrooms, review progress, and provide feedback. Parents can monitor their children’s progress through dashboards and AI/n8n-generated weekly reports.

The goal of Qubaati is to move beyond traditional grades and provide a deeper, personalized learning experience that helps children discover their interests, skills, and future career tendencies.


---

## ✨ Key Features

* 👨‍👩‍👧 **Parent flow**

  * Parent creates child account.
  * Parent views child progress, activity results, mission history, learning profile, and weekly reports.
  * Parent receives AI/n8n-generated weekly summaries.

* 👨‍🏫 **Teacher flow**

  * Teacher creates classrooms.
  * Teacher enrolls students into classrooms.
  * Teacher generates AI activities.
  * Teacher refines AI activities.
  * Teacher approves/rejects activities.
  * Teacher assigns activities to students or classrooms.
  * Teacher grades, reviews, reopens, and gives feedback.

* 👧 **Student flow**

  * Student starts assigned activities.
  * Student submits answers.
  * Student receives AI/system feedback.
  * Student plays missions inside career worlds.
  * Student receives recommendations.
  * Student skills and learning style are updated automatically.

* 🤖 **AI-powered learning**

  * AI activity generation.
  * AI activity refinement.
  * AI submission feedback.
  * AI answer grading.
  * AI teacher dashboard insight.
  * AI parent dashboard insight.
  * AI classroom summaries.
  * AI learning analysis.

* 💳 **Subscription and payments**

  * Moyasar checkout.
  * Parent and teacher subscriptions.
  * Subscription plans.
  * Payment callback, status, and receipt.

* 🔐 **Security**

  * HTTP Basic Auth.
  * `@AuthenticationPrincipal User user`.
  * Role-based authorization.
  * Service-layer ownership checks.
  * No path-variable IDs.
  * Body-based target IDs.
  * Thin controllers.

---

## 🛠️ Technologies and Tools Used

### Backend

| Technology            | Purpose                          |
| --------------------- | -------------------------------- |
| Java 17               | Main programming language        |
| Spring Boot 4.x       | Backend framework                |
| Spring Web            | REST API development             |
| Spring Data JPA       | Database access layer            |
| Hibernate             | ORM                              |
| MySQL                 | Relational database              |
| Spring Security       | Authentication and authorization |
| Basic Auth            | API authentication style         |
| BCrypt                | Password hashing                 |
| Spring Validation     | DTO validation                   |
| Lombok                | Boilerplate reduction            |
| ModelMapper           | Entity/DTO mapping               |
| Jackson               | JSON parsing and serialization   |
| Maven / Maven Wrapper | Build and dependency management  |

### AI and Automation

| Tool              | Purpose                                    |
| ----------------- | ------------------------------------------ |
| Spring AI         | AI integration layer                       |
| OpenAI ChatClient | AI activity generation/refinement/feedback |
| n8n               | Parent weekly report automation            |
| Webhooks          | Integration between Spring Boot and n8n    |

### Payments

| Tool               | Purpose                                         |
| ------------------ | ----------------------------------------------- |
| Moyasar            | Payment checkout, status, callback, and receipt |
| Subscription plans | Parent/teacher subscription management          |

### Development and Testing

| Tool                    | Purpose                       |
| ----------------------- | ----------------------------- |
| Postman                 | API testing collection        |
| Postman lint scripts    | Route and actor-ID validation |
| Git / GitHub            | Version control               |
| IntelliJ IDEA / VS Code | Development environment       |
| Mermaid                 | README diagrams               |

---




### Roles

| Role      | Main Capabilities                                                      |
| --------- | ---------------------------------------------------------------------- |
| `ADMIN`   | Manage system data, generic CRUD, plans, worlds, missions, skills      |
| `TEACHER` | Manage classrooms, activities, assignments, grading, dashboards        |
| `PARENT`  | Create children, view child progress, reports, subscriptions           |
| `STUDENT` | Start assignments, submit answers, play missions, view recommendations |

---

## 🧩 Class Diagram
```mermaid
classDiagram

class User {
    Integer id
    String username
    String email
    String password
    UserRole role
    Boolean enabled
}

class Teacher {
    Integer id
    String fullName
    String specialization
}

class Parent {
    Integer id
    String fullName
    String phoneNumber
}

class Student {
    Integer id
    String fullName
    Integer age
    String grade
    Integer totalPoints
    Integer completedMissionsCount
}

class Classroom {
    Integer id
    String name
    Integer gradeLevel
    String section
}

class CareerWorld {
    Integer id
    String name
    String description
}

class Mission {
    Integer id
    String title
    String description
    Integer maxScore
    MissionSource source
    Boolean active
}

class MissionStep {
    Integer id
    Integer stepOrder
    String content
}

class MissionChoice {
    Integer id
    String content
    Boolean correct
    Integer scoreImpact
}

class MissionSession {
    Integer id
    MissionSessionStatus status
    Integer score
    LocalDateTime startedAt
    LocalDateTime completedAt
}

class Decision {
    Integer id
    Integer responseTimeSeconds
    Integer scoreImpact
}

class Activity {
    Integer id
    String title
    String description
    ActivityStatus status
    Difficulty difficulty
    Integer maxScore
}

class Question {
    Integer id
    String content
    QuestionType type
    Integer points
}

class Option {
    Integer id
    String content
    Boolean isCorrect
}

class ActivityAssignment {
    Integer id
    LocalDateTime assignedAt
    LocalDateTime dueDate
    AssignmentStatus status
}

class ActivitySubmission {
    Integer id
    LocalDateTime startedAt
    LocalDateTime submittedAt
    Integer score
    SubmissionStatus status
    String aiFeedback
    String feedbackSource
}

class StudentAnswer {
    Integer id
    String answerText
    Boolean correct
    Integer earnedPoints
}

class Skill {
    Integer id
    String name
    String description
}

class StudentSkill {
    Integer id
    Double score
    Integer level
}

class LearningStyle {
    Integer id
    LearningStyleType primaryStyle
    LearningStyleType secondaryStyle
    Double confidence
}

class Recommendation {
    Integer id
    String title
    String description
    RecommendationStatus status
}

class Notification {
    Integer id
    String title
    String message
    Boolean read
}

class ParentWeeklyReport {
    Integer id
    Boolean success
    String reportType
    String reportTitle
    String reportJson
}

class Payment {
    Integer id
    String reference
    PaymentStatus status
    Double amount
}

class Subscription {
    Integer id
    SubscriptionStatus status
    LocalDateTime startsAt
    LocalDateTime endsAt
}

class SubscriptionPlan {
    Integer id
    String code
    String name
    Double price
    PlanAudience audience
}

User "1" --> "0..1" Teacher
User "1" --> "0..1" Parent
User "1" --> "0..1" Student

Teacher "1" --> "0..*" Classroom
Classroom "1" --> "0..*" Student
Parent "1" --> "0..*" Student

CareerWorld "1" --> "0..*" Mission
Mission "1" --> "0..*" MissionStep
MissionStep "1" --> "0..*" MissionChoice

Student "1" --> "0..*" MissionSession
Mission "1" --> "0..*" MissionSession
MissionSession "1" --> "0..*" Decision
Decision "*" --> "1" MissionChoice

Teacher "1" --> "0..*" Activity
Activity "1" --> "0..*" Question
Question "1" --> "0..*" Option

Activity "1" --> "0..*" ActivityAssignment
ActivityAssignment "*" --> "1" Student
ActivityAssignment "*" --> "1" Teacher
ActivityAssignment "1" --> "0..1" ActivitySubmission

ActivitySubmission "1" --> "0..*" StudentAnswer
StudentAnswer "*" --> "1" Question
StudentAnswer "*" --> "0..1" Option

Student "1" --> "0..*" StudentSkill
Skill "1" --> "0..*" StudentSkill
Student "1" --> "0..1" LearningStyle

Student "1" --> "0..*" Recommendation
User "1" --> "0..*" Notification
Parent "1" --> "0..*" ParentWeeklyReport

User "1" --> "0..*" Payment
User "1" --> "0..*" Subscription
Subscription "*" --> "1" SubscriptionPlan
```

---

## 🎭 Use Case Diagram

## 🎭 Use Case Diagram

```mermaid id="use-case-diagram"
flowchart LR

%% Actors - Left Side
Parent([Parent])
Student([Student])

%% System Boundary
subgraph Qubaati["Qubaati System"]
    UC1((Manage child profile<br/>and reports))
    UC2((Manage classrooms<br/>and enrollment))
    UC3((Generate and refine<br/>AI activities))
    UC4((Assign, submit,<br/>and grade activities))
    UC5((Play career-world<br/>missions))
    UC6((Analyze skills,<br/>learning style, and progress))
    UC7((Manage payments<br/>and subscriptions))
    UC8((Manage system data))
end

%% Actors - Right Side
Teacher([Teacher])
Admin([Admin])

%% External Systems
AI[[AI Provider]]
N8N[[n8n]]
Moyasar[[Moyasar]]

%% Parent Use Cases
Parent --> UC1
Parent --> UC6
Parent --> UC7

%% Student Use Cases
Student --> UC4
Student --> UC5
Student --> UC6

%% Teacher Use Cases
Teacher --> UC2
Teacher --> UC3
Teacher --> UC4
Teacher --> UC6

%% Admin Use Cases
Admin --> UC8
Admin --> UC7

%% External Integrations
AI -.-> UC3
AI -.-> UC6
N8N -.-> UC1
Moyasar -.-> UC7
```

---

## 🧠 AI Features

Qubaati uses AI to make learning more personalized.

| AI Feature                | Description                                             |
| ------------------------- | ------------------------------------------------------- |
| Activity generation       | Teacher generates activities using AI                   |
| Activity refinement       | Teacher refines an activity using instructions          |
| AI feedback               | Student receives personalized feedback after submission |
| AI grading support        | Free-text answers can be graded with AI support         |
| Parent dashboard insight  | Parent receives AI-powered child progress analysis      |
| Teacher dashboard insight | Teacher receives AI-powered classroom insights          |
| Classroom summary         | AI summarizes classroom performance                     |
| Mission recommendations   | Student receives learning recommendations               |

AI is implemented using **Spring AI ChatClient**.

---

## 💳 Payment and Subscription

The system integrates with **Moyasar** for payments.

| Feature  | Description                                   |
| -------- | --------------------------------------------- |
| Checkout | Authenticated user starts checkout            |
| Callback | Moyasar redirects/calls backend after payment |
| Status   | User checks payment status                    |
| Receipt  | User views payment receipt                    |
| Plans    | Parent/teacher subscription plans             |
| Limits   | Free/paid limits for children and classrooms  |

Public payment endpoints:

```http
GET /api/v1/payments/callback
GET /api/v1/subscriptions/plans
```

Authenticated payment endpoints:

```http
POST /api/v1/payments/checkout
GET  /api/v1/payments/status
GET  /api/v1/payments/receipt
```

---

## 🔄 n8n Weekly Reports

Qubaati integrates with **n8n** to generate parent weekly reports.

Flow:

```text
Parent request
    ↓
Spring Boot backend
    ↓
n8n webhook
    ↓
Report generation
    ↓
Spring Boot saves report
    ↓
Parent views report
```

Main endpoints:

```http
POST /api/v1/parents/me/weekly-report/generate
GET  /api/v1/parents/me/weekly-reports
GET  /api/v1/parents/me/weekly-reports/latest
POST /api/v1/parents/weekly-reports/generate-all
```

---

## 📡 Non-CRUD Endpoint Catalog

This section lists the important business endpoints that are not simple CRUD.

> Note: The project follows a body-based ID style. Resource IDs such as `studentId`, `activityId`, `assignmentId`, and `submissionId` are sent in the request body, not in the URL path.

---

### 🔐 AI Endpoints

| Method | Endpoint                                            | Role          | Description                           |
| ------ | --------------------------------------------------- | ------------- | ------------------------------------- |
| `GET`  | `/api/v1/ai/health`                                 | Authenticated | Check AI provider status              |
| `POST` | `/api/v1/ai/activities/generate`                    | Teacher/Admin | Generate an activity using AI         |
| `POST` | `/api/v1/ai/activities/refine`                      | Teacher/Admin | Refine full activity content using AI |
| `POST` | `/api/v1/ai/activity-submissions/evaluate`          | Teacher/Admin | AI-evaluate a submission              |
| `POST` | `/api/v1/ai/activity-submissions/generate-feedback` | Teacher/Admin | Generate AI feedback for submission   |
| `POST` | `/api/v1/ai/classrooms/summary`                     | Teacher/Admin | Generate classroom AI summary         |
| `POST` | `/api/v1/ai/parents/me/dashboard-insight`           | Parent        | Generate parent dashboard insight     |
| `POST` | `/api/v1/ai/parents/me/children/summary`            | Parent        | Generate AI child summary             |
| `POST` | `/api/v1/ai/teachers/me/dashboard-insight`          | Teacher       | Generate teacher dashboard insight    |

---

### 👨‍👩‍👧 Parent Endpoints

| Method | Endpoint                                       | Role   | Description                             |
| ------ | ---------------------------------------------- | ------ | --------------------------------------- |
| `POST` | `/api/v1/parents/me/children`                  | Parent | Create child account                    |
| `GET`  | `/api/v1/parents/me/children`                  | Parent | List own children                       |
| `GET`  | `/api/v1/parents/me/dashboard`                 | Parent | Parent dashboard                        |
| `POST` | `/api/v1/parents/me/children/overview`         | Parent | View child overview                     |
| `POST` | `/api/v1/parents/me/children/learning-profile` | Parent | View child learning profile             |
| `POST` | `/api/v1/parents/me/children/activity-results` | Parent | View child activity results             |
| `POST` | `/api/v1/parents/me/children/mission-history`  | Parent | View child mission history              |
| `POST` | `/api/v1/parents/me/weekly-report/generate`    | Parent | Generate weekly report                  |
| `GET`  | `/api/v1/parents/me/weekly-reports`            | Parent | List weekly reports                     |
| `GET`  | `/api/v1/parents/me/weekly-reports/latest`     | Parent | Get latest weekly report                |
| `POST` | `/api/v1/parents/weekly-reports/generate-all`  | Admin  | Generate weekly reports for all parents |

---

### 👨‍🏫 Teacher Endpoints

| Method | Endpoint                         | Role    | Description        |
| ------ | -------------------------------- | ------- | ------------------ |
| `GET`  | `/api/v1/teachers/me/dashboard`  | Teacher | Teacher dashboard  |
| `GET`  | `/api/v1/teachers/me/classrooms` | Teacher | Teacher classrooms |
| `GET`  | `/api/v1/teachers/me/students`   | Teacher | Teacher students   |
| `GET`  | `/api/v1/teachers/me/activities` | Teacher | Teacher activities |

---

### 🏫 Classroom Endpoints

| Method | Endpoint                             | Role          | Description                   |
| ------ | ------------------------------------ | ------------- | ----------------------------- |
| `POST` | `/api/v1/classrooms/students/enroll` | Teacher/Admin | Enroll student into classroom |
| `POST` | `/api/v1/classrooms/students/remove` | Teacher/Admin | Remove student from classroom |
| `POST` | `/api/v1/classrooms/dashboard`       | Teacher/Admin | Classroom dashboard           |
| `POST` | `/api/v1/classrooms/progress`        | Teacher/Admin | Classroom progress            |

---

### 🧪 Activity Review and Assignment Endpoints

| Method | Endpoint                                        | Role          | Description                    |
| ------ | ----------------------------------------------- | ------------- | ------------------------------ |
| `POST` | `/api/v1/activities/approve`                    | Teacher/Admin | Approve activity               |
| `POST` | `/api/v1/activities/reject`                     | Teacher/Admin | Reject activity                |
| `POST` | `/api/v1/activities/request-revision`           | Teacher/Admin | Request activity revision      |
| `POST` | `/api/v1/activity-assignments/assign-student`   | Teacher/Admin | Assign activity to one student |
| `POST` | `/api/v1/activity-assignments/assign-classroom` | Teacher/Admin | Assign activity to classroom   |
| `POST` | `/api/v1/activity-assignments/bulk`             | Teacher/Admin | Bulk assign activity           |
| `POST` | `/api/v1/activity-assignments/by-activity`      | Teacher/Admin | List assignments by activity   |
| `POST` | `/api/v1/activity-assignments/cancel`           | Teacher/Admin | Cancel assignment              |
| `POST` | `/api/v1/activity-assignments/extend`           | Teacher/Admin | Extend assignment due date     |
| `POST` | `/api/v1/activity-assignments/expire-overdue`   | Teacher/Admin | Mark overdue assignments       |
| `POST` | `/api/v1/activity-assignments/due-soon`         | Teacher/Admin | Send due-soon notifications    |

---

### 📝 Activity Submission Endpoints

| Method | Endpoint                                         | Role          | Description                      |
| ------ | ------------------------------------------------ | ------------- | -------------------------------- |
| `POST` | `/api/v1/activity-assignments/start`             | Student/Admin | Start assignment                 |
| `POST` | `/api/v1/activity-submissions/submit`            | Student/Admin | Submit assignment                |
| `POST` | `/api/v1/activity-submissions/result`            | Student/Admin | View submission result           |
| `POST` | `/api/v1/activity-submissions/current`           | Student/Admin | View current submission          |
| `POST` | `/api/v1/activity-submissions/feedback`          | Student/Admin | View submission feedback         |
| `POST` | `/api/v1/activity-submissions/by-activity`       | Teacher/Admin | List submissions by activity     |
| `POST` | `/api/v1/activity-submissions/teacher-details`   | Teacher/Admin | View teacher submission details  |
| `POST` | `/api/v1/activity-submissions/teacher-feedback`  | Teacher/Admin | Add teacher feedback             |
| `POST` | `/api/v1/activity-submissions/return-to-student` | Teacher/Admin | Return submission to student     |
| `POST` | `/api/v1/activity-submissions/reopen`            | Teacher/Admin | Reopen submission                |
| `POST` | `/api/v1/activity-submissions/pending-grading`   | Teacher/Admin | List pending grading submissions |

---

### ✍️ Student Answer Endpoints

| Method  | Endpoint                        | Role          | Description           |
| ------- | ------------------------------- | ------------- | --------------------- |
| `POST`  | `/api/v1/student-answers/batch` | Student/Admin | Save batch answers    |
| `PATCH` | `/api/v1/student-answers/grade` | Teacher/Admin | Manually grade answer |

---

### 👧 Student Self-Service Endpoints

| Method | Endpoint                                         | Role    | Description                |
| ------ | ------------------------------------------------ | ------- | -------------------------- |
| `GET`  | `/api/v1/students/me`                            | Student | Student profile            |
| `GET`  | `/api/v1/students/me/activity-dashboard`         | Student | Student activity dashboard |
| `GET`  | `/api/v1/students/me/career-worlds/available`    | Student | Available career worlds    |
| `GET`  | `/api/v1/students/me/missions/available`         | Student | Available missions         |
| `GET`  | `/api/v1/students/me/recommendations`            | Student | Student recommendations    |
| `POST` | `/api/v1/students/me/recommendations/regenerate` | Student | Regenerate recommendations |
| `GET`  | `/api/v1/students/me/skills`                     | Student | Student skills             |

---

### 🎮 Mission Flow Endpoints

| Method   | Endpoint                                      | Role          | Description                 |
| -------- | --------------------------------------------- | ------------- | --------------------------- |
| `POST`   | `/api/v1/mission-sessions/start`              | Student/Admin | Start mission session       |
| `POST`   | `/api/v1/mission-sessions/decision`           | Student/Admin | Submit mission decision     |
| `POST`   | `/api/v1/mission-sessions/complete`           | Student/Admin | Complete mission session    |
| `POST`   | `/api/v1/mission-sessions/current`            | Student/Admin | Get current mission session |
| `POST`   | `/api/v1/mission-sessions/insight/regenerate` | Student/Admin | Regenerate mission insight  |
| `POST`   | `/api/v1/missions/steps/batch`                | Admin         | Add mission steps in batch  |
| `POST`   | `/api/v1/missions/steps/get`                  | Admin         | Get mission steps           |
| `DELETE` | `/api/v1/missions/steps/delete`               | Admin         | Delete mission steps        |

---

### 💡 Recommendation and Notification Endpoints

| Method | Endpoint                                  | Role          | Description                    |
| ------ | ----------------------------------------- | ------------- | ------------------------------ |
| `POST` | `/api/v1/recommendations/accept`          | Student/Admin | Accept recommendation          |
| `POST` | `/api/v1/recommendations/dismiss`         | Student/Admin | Dismiss recommendation         |
| `POST` | `/api/v1/recommendations/complete`        | Student/Admin | Complete recommendation        |
| `GET`  | `/api/v1/users/me/notifications`          | Authenticated | Get user notifications         |
| `GET`  | `/api/v1/users/me/notifications/unread`   | Authenticated | Get unread notifications       |
| `POST` | `/api/v1/users/me/notifications/read`     | Authenticated | Mark notification as read      |
| `POST` | `/api/v1/users/me/notifications/read-all` | Authenticated | Mark all notifications as read |

---

### 💳 Payment and Subscription Endpoints

| Method | Endpoint                                   | Role          | Description                 |
| ------ | ------------------------------------------ | ------------- | --------------------------- |
| `POST` | `/api/v1/payments/checkout`                | Authenticated | Start Moyasar checkout      |
| `GET`  | `/api/v1/payments/callback`                | Public        | Moyasar payment callback    |
| `GET`  | `/api/v1/payments/status`                  | Authenticated | Get payment status          |
| `GET`  | `/api/v1/payments/receipt`                 | Authenticated | Get payment receipt         |
| `GET`  | `/api/v1/subscriptions/plans`              | Public        | List subscription plans     |
| `GET`  | `/api/v1/subscriptions/parents/me/status`  | Parent        | Parent subscription status  |
| `GET`  | `/api/v1/subscriptions/teachers/me/status` | Teacher       | Teacher subscription status |

---

## 📁 Suggested Project Structure

```text
src/main/java/com/example/qubaatisystem
│
├── Api
│   ├── ApiException.java
│   ├── ApiResponse.java
│   └── ControllerAdvice.java
│
├── Config
│   ├── SecurityConfig.java
│   ├── CustomUserDetailsService.java
│   ├── SecurityOwnershipService.java
│   ├── AdminSeeder.java
│   ├── PlanSeeder.java
│   └── BeanConfig.java
│
├── Controller
│   ├── ActivityController.java
│   ├── ActivityAssignmentController.java
│   ├── ActivitySubmissionController.java
│   ├── AiController.java
│   ├── ClassroomController.java
│   ├── MissionController.java
│   ├── MissionFlowController.java
│   ├── ParentController.java
│   ├── StudentController.java
│   ├── TeacherController.java
│   └── ...
│
├── DTO
│   ├── In
│   └── Out
│
├── Model
│
├── Repository
│
├── Service
│
└── Enum
```

---

## ⚙️ Environment Variables

Create environment variables or configure `application.properties`.

```properties
# Database
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/qubaati}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}

# OpenAI / Spring AI
spring.ai.openai.api-key=${OPENAI_API_KEY:}
spring.ai.openai.chat.options.model=${OPENAI_MODEL:gpt-5.4-mini}

# Admin seeder
app.admin.username=${APP_ADMIN_USERNAME:admin}
app.admin.password=${APP_ADMIN_PASSWORD:Admin123!}
app.admin.email=${APP_ADMIN_EMAIL:admin@qubaati.test}

# Moyasar
moyasar.secret-key=${MOYASAR_SECRET_KEY:}
moyasar.publishable-key=${MOYASAR_PUBLISHABLE_KEY:}
moyasar.api-base-url=${MOYASAR_API_BASE_URL:https://api.moyasar.com}
app.base-url=${APP_BASE_URL:http://localhost:8080}

# n8n
n8n.parent-report-webhook-url=${N8N_PARENT_REPORT_WEBHOOK_URL:}
n8n.basic-auth-username=${N8N_BASIC_AUTH_USERNAME:}
n8n.basic-auth-password=${N8N_BASIC_AUTH_PASSWORD:}
n8n.parent-weekly-report.enabled=${N8N_PARENT_WEEKLY_REPORT_ENABLED:true}
n8n.parent-weekly-report.scheduler-enabled=${N8N_PARENT_WEEKLY_REPORT_SCHEDULER_ENABLED:false}
n8n.parent-weekly-report.cron=${N8N_PARENT_WEEKLY_REPORT_CRON:0 0 8 * * MON}
```

---

## 🚀 How to Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/qubaati-system.git
cd qubaati-system
```

### 2. Configure MySQL

Create a database:

```sql
CREATE DATABASE qubaati;
```

### 3. Configure environment variables

Set your database, AI, Moyasar, and n8n values.

Example:

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
export OPENAI_API_KEY=your_openai_key
```

### 4. Build the project

```bash
./mvnw clean compile
```

On Windows:

```bash
mvnw.cmd clean compile
```

### 5. Run the project

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The backend will run on:

```text
http://localhost:8080
```

---

## 🧪 API Testing

The project includes a Postman collection.

Current API testing standards:

* Each request uses role-specific Basic Auth.
* No path variables are used.
* IDs are sent in request bodies.
* No actor IDs are accepted from the body.
* Parent/teacher/student identity comes from Basic Auth.
* Postman lint validates route correctness.

Run the Postman collection in this order:

```text
01 - Setup / Seed
02 - AI Activity Generation
03 - Activity Review
04 - Classroom and Enrollment
05 - Assignment
06 - Student Submission
07 - Grading and Feedback
08 - Mission Flow
09 - Short Answer / AI Grading
10 - Dashboards
11 - Parent Reports
12 - Payments / Subscriptions
13 - n8n Weekly Reports
14 - Security Negative Tests
```

---

## 🧭 Main Business Flow

```mermaid
sequenceDiagram
    participant Parent
    participant Teacher
    participant Student
    participant Backend
    participant AI
    participant n8n
    participant Moyasar

    Parent->>Backend: Create child account
    Teacher->>Backend: Create classroom
    Teacher->>Backend: Enroll child into classroom
    Teacher->>Backend: Request AI activity generation
    Backend->>AI: Generate activity
    AI-->>Backend: Activity with questions/options
    Teacher->>Backend: Refine activity
    Backend->>AI: Refine full activity content
    Teacher->>Backend: Approve activity
    Teacher->>Backend: Assign activity to student
    Student->>Backend: Start assignment
    Student->>Backend: Submit answers
    Backend->>AI: Generate feedback
    Backend-->>Student: Result + feedback
    Backend->>Backend: Update skills and learning style
    Parent->>Backend: Generate weekly report
    Backend->>n8n: Send report payload
    n8n-->>Backend: Weekly report
    Parent->>Moyasar: Subscribe / Pay
```

---

## 🤍 Acknowledgment

Qubaati was built as an educational backend system to explore how AI, gamification, learning analytics, and secure backend design can improve personalized education for children.

> "Learning is not only about grades — it is about discovering potential."
