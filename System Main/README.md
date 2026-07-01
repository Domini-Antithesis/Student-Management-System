# 🌐 System Main — Web Application

The **System Main** module is the Node.js / Express web application at the heart of the Student Management System. It serves the UI (EJS), handles authentication, and persists data to MongoDB via Mongoose.

> 📚 For the full project overview, see the [root README](../README.md).

---

## 🧰 Tech Stack

- **Node.js** + **Express** `4.21.2`
- **MongoDB** via **Mongoose** `8.10.1`
- **EJS** `3.1.10` templating
- **bcryptjs** — password hashing
- **express-session** + **connect-flash** — sessions & flash messages
- **body-parser** — form parsing

---

## 📁 Structure

```
System Main/
├── server.js          # App entry point & middleware wiring
├── package.json       # Dependencies & metadata
├── models/            # Mongoose schemas
│   ├── User.js        # Admin accounts (email + hashed password)
│   ├── Student.js     # Student records
│   ├── Course.js      # Course catalog
│   └── Marks.js       # Marks per student/course
├── routes/            # Express routers
│   ├── auth.js        # Register / login / student login / logout
│   ├── student.js     # Student CRUD, search, list
│   ├── courses.js     # Course CRUD, assignment, student view
│   └── marks.js       # Enter / update / view marks
├── views/             # EJS templates (admin + student pages)
├── public/            # Static assets (css/, js/)
└── images/            # Logo & backgrounds
```

---

## 🚀 Getting Started

### Prerequisites
- Node.js (LTS) & npm
- A running local MongoDB at `mongodb://localhost:27017`

### Install & Run

```bash
# From the "System Main" folder
npm install
node server.js
```

The server starts on **http://localhost:3000** and connects to the `studentDB` database.

> 💡 Tip: add a script to `package.json` such as `"start": "node server.js"` so you can run `npm start`.

---

## 🔐 Authentication Flows

| Role | Route | Credentials | Lands On |
|------|-------|-------------|----------|
| **Admin** | `/login` | Registered **email** + **password** (bcrypt-checked) | `/admin_main` |
| **Student** | `/login_norm` | **Student ID** | `/norm_main` |

**Registration** (`/register`) requires a username, email, password, and DOB. Passwords must be **≥ 8 alphanumeric characters and contain at least one number**, and emails are format-validated.

---

## 🗺 Key Routes

### Auth (`routes/auth.js`)
- `GET/POST /login` — admin login
- `GET/POST /login_norm` — student login
- `GET/POST /register` — create admin account
- `GET /norm_main` — student dashboard
- `GET /logout` — end session

### Students (`routes/student.js`)
- `GET /student/student_main` — searchable student list
- `GET/POST /student/add_student` — create student
- `GET /student/search_student` — search by ID or name
- `GET/POST /student/edit_student/:id` — edit student
- `POST /student/delete_student/:id` — delete student
- `GET /student/list_student` — full list with assigned courses

### Courses (`routes/courses.js`)
- `GET /courses/courses_main` — list all courses
- `GET/POST /courses/add_course` — create course
- `GET/POST /courses/assign_course` — assign course to a student
- `GET /courses/search_course` — search courses
- `GET/POST /courses/edit_course/:id` — edit course
- `POST /courses/delete_course/:id` — delete course
- `GET /courses/course_norm` — courses assigned to logged-in student

### Marks (`routes/marks.js`)
- `GET /marks` — consolidated marks dashboard
- `GET/POST /marks/enter` — enter new marks
- `GET /marks/update` + `/marks/update/search` — find a student's marks
- `GET/POST /marks/update/edit/:studentID` — edit marks
- `GET /marks/marks_norm` — marks for the logged-in student

---

## 🗃 Data Models (Quick Reference)

| Model | Key Fields |
|-------|-----------|
| **User** | `username`, `email` (unique), `password` (hashed), `dob` |
| **Student** | `studentID` (unique), `name`, `dob`, `branch`, `courses[]` |
| **Course** | `courseCode` (unique), `courseName`, `description`, `credits`, `assignedTo[]` |
| **Marks** | `studentID`, `courseCode`, `mst` (≤30), `est` (≤40), `internal` (≤30), `total` (≤100) |

---

## ⚙️ Configuration Notes

The following values are currently **hard-coded** in `server.js` — consider moving them to environment variables:

- MongoDB URI: `mongodb://localhost:27017/studentDB`
- Session secret: `your_secret_key`
- Port: `3000` (falls back to `process.env.PORT` if set)

---

## 🧪 Testing

End-to-end tests for this app live in the sibling [`Automated Tests`](../Automated%20Tests/README.md) module. Start this server **before** running them, as they drive the live UI at `http://127.0.0.1:3000/`.
