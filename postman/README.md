# Qubaati System — Full Flow Postman Check

End-to-end Postman collection that verifies the complete activity flow: data setup → AI generation/refinement → review/approval → assignment → student submission → AI evaluation/feedback → return/reopen → extra availability/history endpoints.

Collection file: `Qubaati_System_Full_Flow_Check.postman_collection.json`

## 1. How to import
1. Open Postman → **Import** → choose `postman/Qubaati_System_Full_Flow_Check.postman_collection.json`.
2. The collection ships with all its variables (including `baseUrl`) as **collection variables**, so no separate environment is required.

## 2. Required variable
- `baseUrl` (collection variable) — default `http://localhost:8080/api/v1`. Change it only if the app runs on a different host/port/context path.

All other variables are **filled automatically** by the test scripts as the requests run — you don't set them by hand. They include the Student-1/2/3 setup ids (`teacherId`, `teacherUserId`, `parentId`, `parentUserId`, `studentId`, `studentUserId`, `student2Id`, `classroomId`, `careerWorldId`, `skillId`, `teacherOwnedActivityId`), the activity-flow ids (`activityId`, `revisionActivityId`, `thirdActivityId`, `assignmentId`, `submissionId`, `questionId`, `optionId`, `s2ActivityId`, `s2QuestionId`, `s2AssignmentId`, `s2SubmissionId`, `s2AnswerId`, `overdueDate`, `dueSoonDate`), and the mission-flow ids (`mission1Id`..`mission4Id`, `sessionId`, `choiceId`, `generatedMissionId`, `genSessionId`, …).

## 2b. Folder structure & request prefixes (ab / am / ah)
The collection is organized into numbered folders, grouped by owner. **Folder `01 - Data Injection` seeds all the cross-cutting data** (users/teacher/parent/classroom/students, career world, skill, learning style, a teacher-owned seed activity) and captures every id into collection variables, so the later folders can run against it. Run the folders **top to bottom**.

**Run order rationale — Student 1 is LAST on purpose.** Student-1 dashboards and AI analysis (teacher/parent dashboards, learning profile, AI insights) **aggregate the results of activities and missions**. So the order is: **Data Injection → Student 2 (activities) → Student 3 (missions) → Student 1 (dashboards/analysis) → Optional/debug**. When the Student-1 folder finally runs, the teacher/parent dashboards reflect real submissions, grades, completed missions, insights and recommendations rather than empty data.

Every request name is prefixed to show which student's work it exercises:
- **`ab -`** = **Student 1** (teacher/parent/classroom management, teacher dashboard + AI insight, teacher-owned activity listing, the activity **review queue / approve / reject / request-revision / review-history / teacher activity-details**, parent dashboard + child overview/learning-profile, AI classroom/family analysis).
- **`am -`** = **Student 2** (activity create/questions/options, AI activity generation/refine, assignment, submission, student answers, grading, teacher feedback, return/reopen, activity notifications, due-soon/overdue, student activity dashboard).
- **`ah -`** = **Student 3** (career worlds, skills, missions, mission steps/choices, mission sessions, decisions, insights, recommendations, mission notifications, skill/learning-style setup rows).

Folders (current order):
1. `01 - Data Injection`
2. `02 - Student 2 - AI Activity Generation`
3. `03 - Student 2 - Approval & Review Flow`
4. `04 - Student 2 - Revision & Reject Flow`
5. `05 - Student 2 - Assignment Flow`
6. `06 - Student 2 - Submission Flow`
7. `07 - Student 2 - AI Evaluation & Feedback`
8. `08 - Student 2 - Teacher Feedback Return Reopen`
9. `09 - Student 2 - Activity Enhancements`
10. `10 - Student 3 - Mission Flow`
11. `11 - Student 1 - Teacher Parent Classroom & Dashboard` ← **Student 1 runs last** (dashboards aggregate the data created above)
12. `12 - Optional / Manual Debug Checks`

**Ownership note:** activities are mostly **Student 2** work — activity creation, questions, options, assignment, submission, grading, feedback, due-soon/overdue and the student activity dashboard are all `am -`. Only the teacher-visibility/review/dashboard side (review queue, approve/reject, teacher-owned activity list, teacher dashboard + AI insight, parent dashboards) is `ab -`. Student 1's changes to the Activity entity were **additive ownership only** (a `createdByTeacher` link); Student 2's assignment/submission/grading flow was **not** rewritten.

## 3. Required app setup
- **MySQL** running and reachable using the credentials in `src/main/resources/application.properties`.
- The Spring Boot app running on **`localhost:8080`** (`./mvnw spring-boot:run`, or run `QubaatiSystemApplication`). Build needs **JDK 17+**.
- **`OPENAI_API_KEY`** environment variable set if you want *real* OpenAI text/translation. The application reads it through the single property `spring.ai.openai.api-key=${OPENAI_API_KEY:}`, used by **all** AI services (`AiActivityService`, the mission `AiService`, and the dashboard `OpenAiService`/`AiAnalysisService`) via Spring AI's `ChatClient`. The empty default means the app **boots fine without it** — set it to a real key for live AI, or leave it unset for placeholder/fallback content. Optional model override: **`OPENAI_MODEL`**, read through `spring.ai.openai.chat.options.model=${OPENAI_MODEL:gpt-5.4-mini}`.

## 4. AI key note
If `OPENAI_API_KEY` is **missing/blank**, the AI endpoints still work using **deterministic placeholder/fallback** content (English canonical storage; for `language=ar` the responses fall back to stored English since live translation needs the key). With a valid key, AI text and Arabic translation are produced by OpenAI. Either way the flow passes.

## 5. Recommended run order
Run the folders **top to bottom** (use Postman's **Collection Runner** for the whole collection, or run each folder in order). The order is **Data Injection → Student 2 → Student 3 → Student 1 → Optional**, because IDs created earlier are reused later and **Student-1 dashboards/analysis only become meaningful after the activity and mission results exist**:
1. **`01 - Data Injection`** — first stamps a per-run `runSuffix` (idempotency, see §18), then creates teacher, parent, classroom, 2 students, career world, a **PROBLEM_SOLVING** skill, a LearningStyle, seeded skill/learning-style-history rows, and a teacher-owned seed activity (**created PENDING_REVIEW, then approved via the review flow** — generic create can no longer set a status directly); captures every id (incl. `teacherUserId`/`parentUserId`/`studentUserId`).
2. **`02`–`09` Student 2 — activity flow** — AI activity generation/refine → approval/review (queue → approve/reject/request-revision, history in the body) → assignment (student/classroom/bulk) → submission (start → save → submit → auto-grade) → AI evaluation/feedback (manual/optional) → teacher feedback/return/reopen → **Activity Enhancements** (AI-assisted text grading, manual grading, teacher submission lists, due-soon/overdue, student activity dashboard, activity notifications).
3. **`10 - Student 3 - Mission Flow`** — seed 4 default multi-step missions → play each (start → decide per step → complete) → unlock 2 personalized missions → play + regenerate a generated mission → insight/recommendations/notifications/skills.
4. **`11 - Student 1 - Teacher Parent Classroom & Dashboard` (LAST)** — teacher dashboard + AI insight, teacher classrooms/students/owned-activities, activities review-queue filter, parent dashboard + child overview/learning-profile, AI classroom/child/family analyses. Runs **last** so every dashboard reflects the real submissions, grades, completed missions, insights and recommendations produced above.
5. **`12 - Optional / Manual Debug Checks`** — available career worlds, skill/learning-style history, and an invalid-language failure check.

## 6. IDs saved automatically
- `teacherId`, `parentId`, `studentId`, `student2Id` — from the create responses (Teacher/Parent/Student return their full DTO with `id`).
- `classroomId`, `careerWorldId`, `skillId` — these create endpoints return only an `ApiResponse` message (no body id), so the collection issues a follow-up `GET` list request and saves the id by matching the unique name/title.
- `activityId`, `revisionActivityId`, `thirdActivityId` — from the AI generate response (`ActivityDetailsOutDTO.id`).
- `questionId`, `optionId` — from the **nested** `questions[0]` / its correct option in the AI generate response.
- `assignmentId` — from the assign-to-student response (`ActivityAssignmentOutDTO.id`).
- `submissionId` — from the start-assignment response (`StudentActivityAttemptOutDTO.submissionId`).
- `questionId1`–`questionId3`, `optionId1`–`optionId3` — from the start-assignment **student-safe** `questions[]` (each question's `questionId` and its first `options[0].optionId`). These build the answer-batch requests.

## 7. Security note
**Security is NOT enabled.** There is no login/token. Every actor id is passed **explicitly** — `assignedByTeacherId`/`teacherId` in bodies or query params, `studentId` as a query param on `start`, etc. When auth is added later, these explicit ids will be replaced by the authenticated principal.

## 8. Expected failures (these are correct, tested as 400)
- **Assigning a DRAFT activity** (`Approval Flow → Assign DRAFT activity`) → **400**: an activity must be `APPROVED` before assignment.
- **Invalid language** (`Extra → Invalid language fails`) → **400**: AI endpoints accept only `en`/`ar`; `language=fr` throws `ApiException("Language must be either en or ar")`.

All errors in this project are returned as HTTP **400** with `{"message": "..."}` (the global `ControllerAdvice` maps everything to 400).

## 9. Endpoints that may return empty lists
- `GET /students/{studentId}/skills/history` and `GET /students/{studentId}/learning-style/history` return rows from both the seeded Setup data **and** the **automatic** updates written after grading (see §12). `changedAt` is **server-assigned** (`LocalDateTime.now()`), never taken from the request body. Run standalone without Setup → expect empty arrays.
- `GET /teachers/{teacherId}/activity-submissions/pending-grading` returns only **SUBMITTED** submissions. Because **submit now auto-evaluates to GRADED**, the submission does not linger in pending-grading — this is expected.

## 10. Data / DTO notes
- `Classroom.gradeLevel` is an **integer** (`6`), not a string — the DTO requires `@NotNull @Positive Integer`.
- `Student.grade` is a **string** (`"Grade 6"`).
- Future dates (`dueDate`) use fixed ISO values in 2027 so they remain valid `@Future` values during the project window.
- Realistic names are used throughout (Noura Alharbi, Faisal Alqahtani, Sarah Alqahtani, Lina Alqahtani, "Grade 6 Physics Explorers", "Speed, Distance, and Time for Grade 6").

## 11. API design notes (recent changes)
- **AI refine instruction is in the body.** `POST /ai/activities/{activityId}/refine?language=ar` with body `{"instruction":"..."}`. The instruction is **optional** — an empty body `{}` or no body at all is valid (a safe default instruction is applied). It is no longer a query parameter.
- **Review comments are in the body.** `approve`, `reject`, and `request-revision` take `{"teacherId":..,"reviewComment":".."}`. `teacherId` is required; `reviewComment` is optional and defaults to `"Approved"` / `"Rejected"` / `"Revision requested"` respectively.
- **submit-for-review was removed from the public API.** AI generation now **automatically** sends the new activity to review, so its status is **PENDING_REVIEW** straight after `generate` (not DRAFT). **A manual `POST /activities` is also forced to PENDING_REVIEW** — the client-supplied `status` is accepted by the DTO but **ignored**, and a generic `PUT /activities/{id}` **never changes status** (see §18). It is still not assignable until **APPROVED**. (`ActivityService.submitForReview(...)` remains an internal helper.)
- **Refine returns the activity to PENDING_REVIEW.** Refine is allowed for DRAFT/REJECTED/PENDING_REVIEW and rejected for APPROVED/ARCHIVED.
- **Start assignment returns student-safe content.** The response (`StudentActivityAttemptOutDTO`) includes the activity info and each question with its options, but **never** `correctAnswer` nor which option `isCorrect`.
- **Submission requires all questions answered.** Submitting before answering every question fails with **400** and lists the missing question ids. Submit is only allowed from IN_PROGRESS (a RETURNED submission must be reopened first).
- **Evaluation uses the activity's total questions** (not the number of submitted answers) and reports the score out of the activity's `maxScore`, e.g. `Total questions: 3. Answered: 3/3. Correct: 1/3. Score: 3/10.`
- **`ActivitySubmissionOutDTO` now includes `activityMaxScore`** so the frontend can show `score / activityMaxScore`.

## 12. API design notes (this batch)
- **return-to-student uses a body.** `PATCH /activity-submissions/{id}/return-to-student` with `{"teacherId":..,"teacherFeedback":".."}`. `teacherId` required; `teacherFeedback` optional (defaults to *"Please review your answers and try again."*). No more free-text query params anywhere (refine/approve/reject/request-revision/return were all moved to bodies).
- **Submit auto-evaluates.** `POST /activity-submissions/{id}/submit?language=en` validates all questions are answered, marks answers SUBMITTED, grades, computes the score out of `maxScore`, writes `aiFeedback`, sets **GRADED**, and returns the full DTO. The **normal flow no longer calls** the public AI evaluate endpoint.
- **Public `evaluate` / `generate-feedback` remain but are manual/optional.** `POST /ai/activity-submissions/{id}/evaluate` is a manual **re-grade** (delegates to the same single grading method; allowed on SUBMITTED/GRADED). `generate-feedback` regenerates audience-specific feedback. Neither is required by the normal flow; both live in folder 7 labelled *manual / optional*. Grading logic is **not** duplicated — it lives once in `ActivitySubmissionService`.
- **Answer status lifecycle:** save → **SAVED**; submit → **SUBMITTED**; evaluation → **CORRECT/INCORRECT** (per answer). Saved draft answers are SAVED so the student can still change them before final submit.
- **Teacher full activity details:** `GET /activities/{id}/details?language=en` → `ActivityDetailsOutDTO` **including** `correctAnswer` and each option's `isCorrect`. This is a teacher/reviewer view and is intentionally NOT student-safe (the student start/reopen views hide those).
- **Teacher feedback endpoint:** `PATCH /activity-submissions/{id}/teacher-feedback` with `{"teacherId":..,"teacherFeedback":".."}` (both required). Adds feedback only — it does **not** change status. Allowed on SUBMITTED/GRADED/RETURNED.
- **Reopen enables resubmission:** `PATCH /activity-submissions/{id}/reopen` (only on RETURNED) sets IN_PROGRESS, clears `submittedAt`/`score`/`aiFeedback`, **keeps** `teacherFeedback`, resets each answer to `earnedPoints=null` + status `SAVED`, and returns the student-safe `StudentActivityAttemptOutDTO` (no correct answers). The student can then re-save and resubmit.
- **Skill / learning-style history `changedAt` is server-assigned** and any client value is ignored. **Automatic, heuristic, documented updates after grading:** (1) skill progress is recorded against the first `PROBLEM_SOLVING` skill (no Activity→Skill mapping exists yet) using the activity percentage as a 0–100 score and a 1–5 level band — **skipped** (no fake data) if no such skill exists; (2) if the student has a `LearningStyle` record and completed a `QUIZ`, its confidence is nudged +0.05 (capped at 1.0) and a history row is written — **skipped** if there is no learning-style record (full detection needs mission/session behavioural signals). Auto rows have a `reason` containing *"automatically"*.

## 13. Student 3 — Multi-step mission flow
The personalized **multi-step** mission flow (Student 3). Folder **10. Student 3 - Mission Flow** now runs the **whole** cycle end-to-end: it seeds 4 DEFAULT missions **with steps** via the authoring endpoint, plays each mission step-by-step, unlocks the personalized missions, plays + regenerates a generated mission in place, and checks the AI-first insight/recommendations, notifications and skills.

**Multi-step model:**
- A mission is now a chain of **`MissionStep`** rows (`stepOrder`, `scenario`, `finalStep`). Each **`MissionChoice`** belongs to a step and carries an internal `scoreImpact` and an optional **`nextStepOrder`** (branch target).
- A session tracks `currentStep` / `currentStepOrder` and a `missionCompleteReady` flag. After each decision the backend returns the **next step** (scenario + choices) or, at the terminal step, `missionCompleteReady=true` with `nextStep=null`.
- **Branching:** the next step is `choice.nextStepOrder` when set, else `currentStep.stepOrder + 1`; when no such step exists the mission becomes complete-ready.

**Unlock rule (per career world, never by grade):**
- A student sees the shared **DEFAULT** missions of a career world. After completing **4 DEFAULT missions in that specific career world**, the system creates up to **2 active `AI_GENERATED`** missions personalized for that student in **that** career world. Career worlds unlock independently.
- `GET /api/v1/students/{studentId}/missions/available?careerWorldId=…` reports `completedDefaultMissions`, `personalizedMissionsUnlocked`, `remainingDefaultMissionsToUnlock`, and the mission list (each with `source` DEFAULT/AI_GENERATED, `completed`, `active`).
- `GET /api/v1/career-worlds/{careerWorldId}/missions` returns DEFAULT missions only (never another student's generated ones).

**Generation rules (multi-step):**
- No generated missions before 4 DEFAULT completions; exactly up to **2 active** generated missions afterwards (never duplicated).
- Generated missions are **multi-step**: the AI returns `steps:[{stepOrder, scenario, choices:[{content, scoreImpact, nextStepOrder}]}]`, saved as `MissionStep` + `MissionChoice` rows. The **fallback** (no key / call failed) also builds a **2-step** mission so the flow always works.
- **Manual regenerate** (`PATCH /students/{studentId}/missions/{missionId}/regenerate`, body `{"reason":"…"}`) rebuilds an **uncompleted active** generated mission's steps **in place** (same row id) — only `AI_GENERATED`, owned by the student, not completed, and only after the 4-completion unlock.
- **Completing an `AI_GENERATED` mission** keeps it as history (set inactive) and creates **one new** generated mission (cap 2).

**Step authoring / seeding (teacher/admin):**
- `POST /api/v1/missions/{missionId}/steps/batch` body `{"steps":[{"stepOrder":1,"scenario":"…","choices":[{"content":"…","scoreImpact":20,"nextStepOrder":2}, …]}, …]}` — **replaces** the mission's steps + choices and returns the full mission with steps (teacher view, `scoreImpact` + `choiceKey` included). `choiceKey` (A/B/C…) is auto-assigned; the last step is marked `finalStep`.
- `GET /api/v1/missions/{missionId}/steps` → the ordered steps (teacher view). `DELETE /api/v1/missions/{missionId}/steps` → clears them.

**Session lifecycle (multi-step):**
- `POST /missions/{missionId}/sessions/start?studentId=…` → student-safe attempt DTO; `currentStep` is **step 1** (scenario + choices `{id, content}`; **never** `scoreImpact`/correctness).
- `GET /mission-sessions/{sessionId}/current` → current attempt view (current step).
- `POST /mission-sessions/{sessionId}/decisions` body `{"choiceId":…,"reason":"…"}` — the choice must belong to the **current step**; **`responseTimeSeconds` is backend-calculated** from `currentStepStartedAt` (never accepted from the client). Returns `missionCompleteReady` + the **next step** (or `null` at the end).
- `PATCH /mission-sessions/{sessionId}/complete` → **blocked with 400 unless `missionCompleteReady`**. On success: COMPLETED + score, then **internally** generates the insight (AI-first), updates the mission's skill + skill history, nudges learning-style + history, generates recommendations (AI-first), fires notifications, and applies the generation rules. Returns a completion summary (insight + updated skills + unlock/new-mission flags).
- `PATCH /mission-sessions/{sessionId}/abandon` → ABANDONED, **no** insight/skills/generation.
- `GET /mission-sessions/{sessionId}/insight` → the insight created at completion.

**AI-first analytics (rule-based fallback):**
- **Insight** — completion calls `AiService.generateMissionInsight(...)` (Spring AI `ChatClient`) which returns strict JSON mapped to the `Insight` entity (5 numeric 0–100 scores + summary + recommendation). On missing key / failure / blank / invalid JSON it falls back to the deterministic rule-based insight. No public AI endpoint is required by the normal flow.
- **Recommendations** — completion calls `AiService.generateRecommendations(...)` returning `{recommendations:[{title,description,type,priority}]}` mapped to `Recommendation` rows; on failure it falls back to the deterministic mission recommendation.
- **Optional manual re-runs** (teacher/admin convenience, also AI-first): `PATCH /mission-sessions/{sessionId}/insight/regenerate` (updates the insight in place) and `POST /students/{studentId}/recommendations/regenerate`. These are **optional** — the normal flow already generates both at completion.

**Other student endpoints:** `GET /students/{studentId}/recommendations` + `PATCH /recommendations/{id}/accept|dismiss|complete`; `GET /users/{userId}/notifications` (+ `/unread`), `PATCH /notifications/{id}/read`, `PATCH /users/{userId}/notifications/read-all`; `GET /students/{studentId}/skills`.

**Backward compatibility (no data migration):** legacy missions that have **no `MissionStep` rows** still work — a session on such a mission keeps `currentStep=null`, shows the mission's own scenario + choices as a single step, and becomes complete-ready after one decision. Nothing is created or rewritten for legacy rows (the safest option: no synthetic step is persisted and no existing `MissionChoice` is moved). `GET /api/v1/mission/available/{studentId}` (old) still returns **DEFAULT missions only** and is kept for compatibility — prefer the per-career-world endpoint above.

**Postman coverage note:** folder 10 now runs the **complete** flow automatically using the `POST /missions/{missionId}/steps/batch` seeding endpoint (Option A) — no SQL or OpenAI key required. With no key, generated missions use the deterministic 2-step fallback and insight/recommendations use the rule-based fallback, so every assertion still passes. A SQL seed (`postman/sql/student3_mission_flow_seed.sql`, Option B) is **not** needed but could be added for DB-level seeding.

## 14. Student 2 — Activity enhancements
Folder **09. Student 2 - Activity Enhancements** runs the whole enhancement set end-to-end. It seeds its own short-answer activity by **creating it (PENDING_REVIEW) and then approving it via the review flow** (`PATCH /activities/{id}/approve`) — generic activity CRUD no longer accepts a client `status`, so the explicit approve step is required before assignment — then assigns it, plays it, and exercises every feature. Reuses `teacherId / studentId / studentUserId / classroomId / parentId` from folder 1. No OpenAI key is required (AI-assisted grading falls back deterministically).

**Activity notifications** — every event creates a notification for the student's linked `User` (skipped safely if a student has no user):
- **Assignment** → `ACTIVITY_ASSIGNED` — to the one student (`/assign/students/{id}`), to **every** classroom student (`/assign/classrooms/{id}`), and to each student in **bulk** assign.
- **Graded** → `ACTIVITY_GRADED` — on auto-grade at submit and on teacher manual grade.
- **Teacher feedback added** → `TEACHER_FEEDBACK`. **Returned** → `ACTIVITY_RETURNED`. **Reopened** → `SUBMISSION_REOPENED`.
- **Due soon** → `ACTIVITY_DUE_SOON`. **Overdue** → `ACTIVITY_OVERDUE`.
- New enum values added: `ACTIVITY_OVERDUE, ACTIVITY_GRADED, ACTIVITY_RETURNED, SUBMISSION_REOPENED` (existing `ACTIVITY_ASSIGNED / ACTIVITY_DUE_SOON / TEACHER_FEEDBACK` reused). **Limitation:** there is no per-event de-dup flag (submission history/audit is out of scope), so re-calling an automation re-notifies.

**Manual grading / score override** — `PATCH /api/v1/student-answers/{answerId}/grade` body `{"teacherId":…,"earnedPoints":…,"status":"CORRECT|INCORRECT|PARTIAL","feedback":"…"}` (`StudentAnswerManualGradeInDTO`). Validates: answer + submission + teacher exist; the assigning teacher (when known); submission is **SUBMITTED/GRADED/RETURNED** (never IN_PROGRESS); `earnedPoints ≤ question.points`. It then **recalculates the submission score** (sum of earnedPoints normalized to the activity maxScore), sets **GRADED**, notifies the student, and returns the full `ActivitySubmissionOutDTO`.

**AI-assisted text grading** — during submit/evaluate, `MULTIPLE_CHOICE / TRUE_FALSE` use deterministic grading; `SHORT_ANSWER / OPEN_ENDED` use `AiAnswerGradingService.gradeTextAnswer(...)` (Spring AI `ChatClient`) which returns strict JSON `{earnedPoints,status,feedback}`. The score is **clamped to `[0, question.points]`** and the status reconciled with it; on any AI failure it **falls back** to exact/contains matching against the reference answer (never crashes submit). Per-answer feedback is stored on the new `StudentAnswer.feedback`. The reference answer is sent to the model only for comparison and is **never** exposed to the student — a deterministic guard strips it from the feedback if the model ever echoes it. *(`AiAnswerGradingService` is a separate, dependency-light bean — `AiActivityService` already injects `ActivitySubmissionService`, so injecting it there would have created a cycle.)*

**Teacher submission lists / details** — `GET /api/v1/activity-assignments/{assignmentId}/submissions` and `GET /api/v1/activities/{activityId}/submissions` return `ActivitySubmissionOutDTO` summaries (id, studentId, studentName, status, score, activityMaxScore, startedAt, submittedAt, teacherFeedback, aiFeedback) — **no answers, no correct answers**. `GET /api/v1/activity-submissions/{submissionId}/teacher-details` is a **teacher-only** view that additionally includes each answer (earnedPoints, status, feedback, question text) **and the correct answer** — mirrors `/activities/{id}/details`, never returned by a student endpoint.

**Due-soon / overdue automation** — `PATCH /api/v1/activity-assignments/expire-overdue` flips every **ASSIGNED** assignment whose `dueDate` has passed to **EXPIRED**, notifies the student(s), and returns `{"expiredCount":N}`. `POST /api/v1/activity-assignments/due-soon-notifications?hours=24` notifies students of **ASSIGNED** assignments due within the next N hours and returns `{"notifiedCount":N}` (`hours` is a small numeric filter, not free text). The deadline is also enforced **dynamically** on both **start** and **submit** (an overdue assignment can't be opened or submitted). The Postman folder seeds an overdue and a due-soon assignment via the assignment **CRUD** (which allows a past/near `dueDate`).

**Student activity dashboard** — `GET /api/v1/students/{studentId}/activity-dashboard` (`StudentActivityDashboardOutDTO`): counts `assigned / inProgress / submitted / graded / returned / overdue / dueSoon`, `averageScore` (mean of GRADED scores, null when none), `latestFeedback`, plus `dueSoonAssignments`, `returnedSubmissions`, and `recentGradedSubmissions` (latest 5). No correct answers are exposed.

**Excluded:** submission history / grading history / audit-log integration were intentionally **not** implemented (not useful in the current project state).

## 15. Student 1 — Dashboard & analysis coverage
The Student-1 AI analysis + parent dashboard endpoints (`POST /ai/classrooms/{classroomId}/summary`, `POST /ai/parents/{parentId}/children/{studentId}/summary`, `POST /ai/parents/{parentId}/dashboard-insight`, `GET /parents/{parentId}/dashboard`) live in folder **11 - Student 1 - Teacher Parent Classroom & Dashboard** (the second-to-last folder). They use Spring AI `ChatClient` with a deterministic rule-based fallback, so they return **200** even without an OpenAI key.

## 16. Student 1 — Teacher ownership, dashboards & review queue
New Student-1 business-flow work, all under folder **11 - Student 1 - …** (`ab -` prefix), which now runs **last** so its dashboards aggregate the activity + mission results produced earlier:

**Activity teacher ownership.** `Activity` gained an optional `createdByTeacher` (nullable for legacy rows). `ActivityInDTO` and `AiGenerateActivityInDTO` accept an optional `teacherId`; `ActivityOutDTO` now returns `createdByTeacherId` + `createdByTeacherName`. Folder 1 seeds a teacher-owned activity (`POST /activities` with `teacherId` → PENDING_REVIEW, then `PATCH /activities/{id}/approve` → APPROVED) to prove ownership end-to-end. **No Student-2 flow was changed** — ownership is purely additive.

**Teacher activity listing.** `GET /api/v1/teachers/{teacherId}/activities` (optional `?status=DRAFT|PENDING_REVIEW|APPROVED|REJECTED` — an enum filter, not free text) lists a teacher's own activities.

**Activity review queue / status filter.** `GET /api/v1/activities?status=PENDING_REVIEW` (optional enum filter on the existing list endpoint; no param = all). The existing `GET /activities/review-queue` and the body-based approve/reject/request-revision endpoints are unchanged; review history is still stored in `ActivityReview`; AI-generated activities still move to PENDING_REVIEW internally.

**Teacher dashboard upgrade.** `GET /api/v1/teachers/{teacherId}/dashboard` now integrates: Student-1 classrooms + students lists; Student-2 `activitySummary` (owned activities by status, assigned/submissions/pending-grading/returned/graded counts, average score, due-soon/overdue); Student-3 `missionSummary` (completed mission sessions, recent insight summaries, common weak skills, top recommendations). Same endpoint, richer payload. Also added: `GET /teachers/{teacherId}/classrooms` and `GET /teachers/{teacherId}/students`.

**Teacher AI dashboard insight.** `POST /api/v1/ai/teachers/{teacherId}/dashboard-insight` returns a `{summary, strengths, concerns, recommendedActions, source}` insight built from the teacher dashboard data via Spring AI `ChatClient`, with an Arabic rule-based fallback (`source: "fallback"`) when no key is set.

**Parent dashboard / learning profile upgrade.** `GET /api/v1/parents/{parentId}/dashboard` child cards now include `gradedActivitiesCount`, `averageActivityScore`, `completedMissionSessionsCount`, `latestInsightSummary`. New `GET /api/v1/parents/{parentId}/children/{studentId}/learning-profile` combines skills, learning style, recent activity performance, recent mission insight, recommendations, and activity/mission completion. The parent-child relationship is validated in service logic (no security layer). No correct answers or mission `scoreImpact` are exposed.

## 17. How to run
1. Start MySQL + the app on `localhost:8080` (`./mvnw spring-boot:run`).
2. Import the collection; optionally set `OPENAI_API_KEY` for live AI (fallbacks keep every assertion green without it).
3. Run the whole collection top-to-bottom with the **Collection Runner**, or run folders in order starting with `01 - Data Injection`.

## 18. Guarded flows & idempotency (latest audit fixes)
This batch closes the remaining "open CRUD can bypass the guarded flow" gaps. None of it adds security/roles — the guards are flow-integrity checks that return the standard **400 `{"message": "..."}`**.

**18.1 Re-runnable seeds (`runSuffix`).** The first request of folder 01, **`ab - Initialize Run Variables`**, stamps `runSuffix = String(Date.now())` into a collection variable in its pre-request script. Every seeded **unique** field then appends `{{runSuffix}}`:
- teacher / parent / student / second-student **usernames and emails** (the only DB-unique columns: `User.username`, `User.email`).
- the folder-12 status-guard activity title (`Guard Status Check {{runSuffix}}`).

So the collection can be run repeatedly against a **persistent** MySQL (`ddl-auto=update`) without 400s on the second run. (With the default `ddl-auto=create-drop` the schema is wiped each boot, so this is belt-and-suspenders.) Name-matched id captures (classroom / career world / skill / default missions) were also switched from `.find()` (oldest) to `.filter(...).pop()` (**newest wins**), so a rerun resolves the ids it just created, not a stale earlier row.

**18.2 Generic CRUD on guarded entities is blocked.** The generic `POST/PUT/DELETE` on **`StudentAnswer`**, **`MissionSession`**, and **`Decision`** now throw a 400 that names the official endpoint to use. Safe reads (`GET` list / by id) are kept. Folder 12 asserts the block:
- `POST /student-answers`, `DELETE /student-answers/{id}` → 400 *(use `POST /activity-submissions/{submissionId}/answers/batch` + submit + `PATCH /student-answers/{answerId}/grade`)*.
- `POST /mission-sessions` → 400 *(use `POST /missions/{missionId}/sessions/start` → decisions → complete/abandon)*.
- `POST /decisions` → 400 *(use `POST /mission-sessions/{sessionId}/decisions`)*.

The **official flow endpoints are untouched** — they use the repositories directly, not the generic service `create/update/delete`.

**18.3 Activity status transition rule.** A generic `POST /activities` always lands the activity in **PENDING_REVIEW** (the client `status` is ignored), and a generic `PUT /activities/{id}` **preserves** the existing status. Status only changes through the review flow (`approve` / `reject` / `request-revision`) and AI generation's internal auto-submit. Folder 12 (`am - Create Activity With status=APPROVED (guard test)` → `am - Verify Activity Not APPROVED`) proves a create that *asks* for `APPROVED` ends up `PENDING_REVIEW`.

**18.4 Legacy mission generate obeys the Student-3 rules.** The legacy `POST /api/v1/mission/generate/{studentId}/{worldId}` no longer bypasses personalization rules — it enforces the **same** unlock (4 completed DEFAULT missions per career world) and **2-active-generated cap** as the available/regenerate flow. Folder 10 asserts both:
- `ah - Legacy Generate Blocked Before Unlock (expect 400)` (before any completion) → 400 with *"locked"*.
- `ah - Legacy Generate At Cap (expect 400)` (right after unlock, when 2 active generated already exist) → 400 with *"active personalized"*.

**18.5 (Code-only) AI JSON fence stripping.** `OpenAiService.analyze(...)` (Student-1 dashboard analysis) now strips ```` ```json ````/```` ``` ```` fences and narrows to the first `{…}` object before parsing, matching the other AI services; on any failure it still falls back to rule-based analysis. There is no HTTP surface to assert this from Postman — it is verified by code and by the existing dashboard endpoints still returning 200.
