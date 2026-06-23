# 🎓 Qubaati | قبعتي

> منصة تعليمية تفاعلية تساعد الأطفال على اكتشاف مهاراتهم واهتماماتهم المستقبلية من خلال عوالم محاكاة، أنشطة تعليمية، مهام تفاعلية، ولوحات متابعة ذكية للمعلمين وأولياء الأمور.

![Java](https://img.shields.io/badge/Java-Spring%20Boot-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-Backend-green)
![MariaDB](https://img.shields.io/badge/MariaDB-Database-blue)
![OpenAI](https://img.shields.io/badge/OpenAI-AI-purple)
![Moyasar](https://img.shields.io/badge/Moyasar-Payments-yellow)
![Spring Security](https://img.shields.io/badge/Spring_Security-Secured-success)

---

# 🇸🇦 نبذة عن المشروع

**قبعتي** هي منصة تعليمية تفاعلية للأطفال تهدف إلى ربط التعلم باكتشاف المهارات والطموحات المستقبلية.

يستطيع الطفل استكشاف عوالم محاكاة مرتبطة بالمهن والاهتمامات، وإكمال أنشطة ومهام تعليمية تساعده على تطوير مهاراته. كما يستطيع المعلم إدارة الفصول ومتابعة تقدم الطلاب، بينما يستطيع ولي الأمر متابعة أبنائه والحصول على ملخصات ذكية عن تقدمهم.

تدعم المنصة التحليلات التعليمية، والملخصات المدعومة بالذكاء الاصطناعي، ونظام اشتراكات ودفع إلكتروني آمن باستخدام Moyasar Sandbox.

---

# 🇬🇧 Project Overview

**Qubaati** is an interactive educational platform designed to help children explore their skills, interests, and future career possibilities.

Students explore simulation worlds, complete learning activities, and progress through missions. Teachers manage classrooms and monitor student performance, while parents follow their children’s progress through dashboards and AI-generated insights.

The platform also includes secure subscription management, Moyasar Sandbox payment integration, payment receipts, and email payment confirmations.

---

# 📐 Class Diagram

![Qubaati Class Diagram](docs/class-diagram.png)

---

# 🎭 Use Case Diagram

> The shared team Use Case Diagram will be added here.

```md
![Qubaati Use Case Diagram](docs/use-case-diagram.png)
```

---

# 👨‍💻 My Contribution

| Module                             | Features                                                                            |
| ---------------------------------- | ----------------------------------------------------------------------------------- |
| 👨‍👩‍👧 Parent & Child Management | Create child accounts, list children, child overview, profile updates               |
| 🏫 Classroom Supervision           | Enroll students, remove students, list classroom students                           |
| 📊 Dashboards                      | Parent dashboard, teacher dashboard, classroom dashboard, student progress          |
| 🤖 AI Insights                     | Classroom AI summary, child learning summary, family dashboard insight              |
| 💳 Subscription & Payment          | Moyasar Sandbox checkout, payment verification, subscription activation and renewal |
| 🧾 Payment Receipt                 | Payment status lookup and full receipt generation                                   |
| 📧 Email Confirmation              | Branded HTML subscription-payment confirmation email                                |
| 🔐 Security                        | Basic Auth protection and payment ownership validation                              |
| 🧪 Testing                         | Controller, service, payment, subscription, security, and regression tests          |
| 🛠️ ModelMapper Fixes              | Safe relationship mapping to prevent DTO relation IDs from overwriting entity IDs   |

---

# 🔗 Git-Proven Endpoints I Implemented

> The following original profile-ID endpoints were implemented as part of my contribution. Some authenticated `/me` alternatives were added later by another team member.

## 👨‍👩‍👧 Parent & Child Management

| Method  | Endpoint                                                   | Description                                                  |
| ------- | ---------------------------------------------------------- | ------------------------------------------------------------ |
| `POST`  | `/api/v1/parents/{parentId}/children`                      | Create a child account linked to a parent                    |
| `GET`   | `/api/v1/parents/{parentId}/children`                      | List all children belonging to a parent                      |
| `GET`   | `/api/v1/parents/{parentId}/children/{studentId}/overview` | Get an overview of a specific child                          |
| `PATCH` | `/api/v1/parents/{parentId}/children/{studentId}/profile`  | Update a child profile, including name, age, and grade       |
| `GET`   | `/api/v1/parents/{parentId}/dashboard`                     | Get a parent dashboard with child cards and mission progress |

---

## 🏫 Classroom Supervision

| Method   | Endpoint                                                       | Description                               |
| -------- | -------------------------------------------------------------- | ----------------------------------------- |
| `POST`   | `/api/v1/classrooms/{classroomId}/students/{studentId}/enroll` | Enroll a student in a classroom           |
| `DELETE` | `/api/v1/classrooms/{classroomId}/students/{studentId}/remove` | Remove a student from a classroom         |
| `GET`    | `/api/v1/classrooms/{classroomId}/students`                    | List students enrolled in a classroom     |
| `GET`    | `/api/v1/classrooms/{classroomId}/dashboard`                   | Get a classroom dashboard summary         |
| `GET`    | `/api/v1/classrooms/{classroomId}/progress`                    | Get student-by-student classroom progress |

---

## 👩‍🏫 Teacher Dashboard

| Method | Endpoint                                 | Description                           |
| ------ | ---------------------------------------- | ------------------------------------- |
| `GET`  | `/api/v1/teachers/{teacherId}/dashboard` | Get a teacher-level dashboard summary |

---

## 🤖 AI Analysis & Insights

| Method | Endpoint                                                     | Description                                   |
| ------ | ------------------------------------------------------------ | --------------------------------------------- |
| `POST` | `/api/v1/ai/classrooms/{classroomId}/summary`                | Generate an AI classroom performance summary  |
| `POST` | `/api/v1/ai/parents/{parentId}/children/{studentId}/summary` | Generate an AI learning summary for a child   |
| `POST` | `/api/v1/ai/parents/{parentId}/dashboard-insight`            | Generate an AI family-level dashboard insight |

---

## 📚 Parent Child History

| Method | Endpoint                                                   | Description                                                          |
| ------ | ---------------------------------------------------------- | -------------------------------------------------------------------- |
| `GET`  | `/api/v1/parents/me/children/{studentId}/activity-results` | Get activity submission results for the authenticated parent’s child |
| `GET`  | `/api/v1/parents/me/children/{studentId}/mission-history`  | Get mission session history for the authenticated parent’s child     |

---

## 💳 Subscription & Payment APIs

| Method | Endpoint                                            | Description                                               |
| ------ | --------------------------------------------------- | --------------------------------------------------------- |
| `POST` | `/api/v1/payments/checkout`                         | Start an authenticated Moyasar payment checkout           |
| `GET`  | `/api/v1/payments/callback`                         | Verify Moyasar payment and activate or renew subscription |
| `GET`  | `/api/v1/payments/status`                           | Get payment status using the local payment reference      |
| `GET`  | `/api/v1/payments/receipt`                          | Get a complete payment receipt with ownership protection  |
| `GET`  | `/api/v1/subscriptions/plans`                       | List active subscription plans                            |
| `GET`  | `/api/v1/subscriptions/parents/{parentId}/status`   | Get parent subscription status                            |
| `GET`  | `/api/v1/subscriptions/teachers/{teacherId}/status` | Get teacher subscription status                           |

> The profile-ID subscription-status routes were later superseded by authenticated `/me` alternatives.

---

# 🧪 Postman Collection Endpoints (`ab`)

> This section lists every unique request whose name starts with `ab -` in the current Postman collection. It documents the full shared collection flow and does not change the Git-based ownership listed above.

**Collection result:** 87 `ab` requests were found, representing **43 unique method-and-route combinations**.

The following three already appear in **Git-Proven Endpoints I Implemented**, so they are not repeated below:

- `GET /api/v1/subscriptions/plans`
- `POST /api/v1/payments/checkout`
- `GET /api/v1/payments/callback`

## ⚙️ Setup & Core Resources

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/v1/activities/get-all` | List activities; used to initialize collection run variables. |
| `POST` | `/api/v1/teachers/add` | Create a teacher account. |
| `POST` | `/api/v1/parents/add` | Create a parent account. |
| `POST` | `/api/v1/classrooms/add` | Create a classroom. |
| `GET` | `/api/v1/classrooms/get-all` | List classrooms and capture a classroom ID. |
| `POST` | `/api/v1/parents/me/children` | Create a child for the authenticated parent. |
| `POST` | `/api/v1/activities/add` | Create a teacher-owned activity for the review flow. |

## 📝 Activity Review Flow

| Method | Endpoint | Description |
| --- | --- | --- |
| `PATCH` | `/api/v1/activities/approve` | Approve an activity through the review flow. |
| `POST` | `/api/v1/activities/details` | Return teacher activity details including questions and options. |
| `GET` | `/api/v1/activities/review-queue` | List activities waiting for review. |
| `POST` | `/api/v1/activities/review-history` | Return review history for an activity. |
| `PATCH` | `/api/v1/activities/request-revision` | Request changes to an activity. |
| `PATCH` | `/api/v1/activities/reject` | Reject an activity. |

## 👩‍🏫 Teacher & Parent Dashboard Flow

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/v1/teachers/me/dashboard` | Return the authenticated teacher dashboard. |
| `POST` | `/api/v1/ai/teachers/me/dashboard-insight` | Generate an AI insight for the teacher dashboard. |
| `GET` | `/api/v1/teachers/me/classrooms` | List classrooms belonging to the authenticated teacher. |
| `GET` | `/api/v1/teachers/me/students` | List students available to the authenticated teacher. |
| `GET` | `/api/v1/teachers/me/activities` | List activities available to the authenticated teacher. |
| `GET` | `/api/v1/parents/me/children` | List children belonging to the authenticated parent. |
| `POST` | `/api/v1/parents/me/children/overview` | Return an overview for one of the authenticated parent’s children. |
| `POST` | `/api/v1/parents/me/children/learning-profile` | Return a child learning profile for the authenticated parent. |
| `POST` | `/api/v1/parents/me/children/activity-results` | Return child activity results without exposing correct answers. |
| `POST` | `/api/v1/parents/me/children/mission-history` | Return child mission history without internal decision details. |
| `POST` | `/api/v1/ai/classrooms/summary` | Generate an AI classroom summary. |
| `POST` | `/api/v1/ai/parents/children/summary` | Generate an AI learning summary for a child. |
| `POST` | `/api/v1/ai/parents/me/dashboard-insight` | Generate an AI family dashboard insight. |
| `GET` | `/api/v1/parents/me/dashboard` | Return the authenticated parent dashboard. |

## 💳 Subscription, Payment & Weekly Reports

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/v1/subscriptions/parents/me/status` | Return the authenticated parent subscription status. |
| `GET` | `/api/v1/subscriptions/teachers/me/status` | Return the authenticated teacher subscription status. |
| `POST` | `/api/v1/parents/me/weekly-report/generate` | Generate a weekly report for the authenticated parent. |
| `POST` | `/api/v1/parents/weekly-reports/generate-all` | Generate weekly reports for all parents. |
| `GET` | `/api/v1/parents/me/weekly-reports` | List weekly reports for the authenticated parent. |
| `GET` | `/api/v1/parents/me/weekly-reports/latest` | Return the latest weekly report for the authenticated parent. |
| `POST` | `/api/v1/parents/weekly-reports/get` | Return a weekly report by ID. |

## 🔐 Security & Access-Control Checks

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/v1/audit-logs/get-all` | List audit logs for an authorized admin. |
| `GET` | `/api/v1/users/me/notifications` | Security test: unauthenticated access should be rejected. |
| `POST` | `/api/v1/ai/activity-submissions/evaluate` | Security test: a parent must not evaluate a submission. |
| `POST` | `/api/v1/ai/activity-submissions/generate-feedback` | Security test: a student must not generate protected feedback. |
| `PATCH` | `/api/v1/recommendations/accept` | Security test: a student cannot accept another student’s recommendation. |
| `POST` | `/api/v1/activity-assignments/assign-student` | Security test: a parent cannot assign an activity. |

---

# 💳 Payment & Subscription Feature

The payment system was implemented using **Moyasar Sandbox** with secure backend verification.

### Main Features

* Parent Plus subscription for parents who need to add more children.
* Teacher Plus subscription for teachers who need to create more classrooms.
* Secure payment checkout with authenticated user identity.
* Backend-only verification with Moyasar after payment callback.
* Subscription activation after successful payment.
* Subscription renewal by extending the existing subscription end date.
* Idempotency protection to prevent repeated callbacks from activating a subscription twice.
* Local payment receipt generation.
* Ownership-protected payment status and receipt access.
* Branded HTML email confirmation after successful payment.

---

# 🤖 AI Features

### Classroom Summary

Generates a classroom-level learning summary based on student activity and progress.

### Child Learning Summary

Generates a parent-friendly summary of a child’s points, missions, strengths, concerns, and recommended actions.

### Family Dashboard Insight

Generates an AI insight that helps parents understand the overall learning progress of their children.

---

# 🔐 Security Features

* Stateless HTTP Basic Authentication.
* Authenticated payment checkout.
* Parent and teacher identity derived from the authenticated account.
* Payment status and receipt ownership validation.
* Public Moyasar callback endpoint for payment redirect handling.
* Custom authentication response to avoid browser Basic Auth popups in the local payment tester.

---

# 🧪 Testing & Quality

Focused tests were added for important business flows, including:

* Parent and classroom supervision endpoints.
* Teacher, parent, and classroom dashboard logic.
* Payment verification and callback handling.
* Subscription activation and renewal.
* Duplicate payment callback protection.
* Email failure safety without breaking payment activation.
* Payment ownership validation.
* ModelMapper regression protection for relationship ID mapping.
* Question mapping protection to ensure `activityId` never overwrites `Question.id`.

---

# 🛠️ Technologies Used

* Java
* Spring Boot
* Spring Data JPA
* Hibernate
* MariaDB / MySQL
* Spring Security
* Spring Mail
* OpenAI API
* Moyasar Sandbox API
* Maven
* JUnit 5
* Mockito

---

# 🎯 Key Achievement

Built the parent supervision, classroom monitoring, dashboards, AI insight, and subscription-payment layer of Qubaati.

The contribution includes secure Moyasar payment verification, subscription renewal, payment receipts, email confirmation, ownership protection, and AI-powered educational summaries for parents and teachers.
