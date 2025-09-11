// server.js
const express = require('express');
const mongoose = require('mongoose');
const session = require('express-session');
const flash = require('connect-flash');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();

const Student = require('./models/Student');  // Import Student model

// --- Connect to MongoDB (StudentDB) ---
mongoose.connect('mongodb://localhost:27017/studentDB', {
  useNewUrlParser: true,
  useUnifiedTopology: true
})
  .then(() => console.log('Connected to StudentDB'))
  .catch(err => console.error(err));

// --- Middleware ---
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));
app.use('/images', express.static(path.join(__dirname, 'images')));
app.set('view engine', 'ejs');

// Session & Flash
app.use(session({
  secret: 'your_secret_key',
  resave: false,
  saveUninitialized: false
}));
app.use(flash());

// Make flash messages available in all templates
app.use((req, res, next) => {
  res.locals.success = req.flash('success');
  res.locals.error = req.flash('error');
  next();
});

// Routes
const authRoutes = require('./routes/auth');
const studentRoutes = require('./routes/student');
const courseRoutes = require('./routes/courses');
const marksRoutes = require('./routes/marks');

// Main welcome page route
app.get('/', (req, res) => {
  res.render('main');
});

// Admin main page route
app.get('/admin_main', (req, res) => {
  if (!req.session.user) {
    return res.redirect('/login');
  }
  Student.find({})
    .then(students => {
      res.render('admin_main', { user: req.session.user, students });
    })
    .catch(err => {
      console.error(err);
      res.render('admin_main', { user: req.session.user, students: [] });
    });
});

// Library page route
app.get('/library', (req, res) => {
  if (!req.session.user) {
    return res.redirect('/login');
  }
  res.render('library', { user: req.session.user });
});

// Use other routes
app.use('/', authRoutes);
app.use('/student', studentRoutes);
app.use('/courses', courseRoutes);
app.use('/marks', marksRoutes);


// Start the server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server started on port ${PORT}`);
  console.log(`http://localhost:${PORT}`);
});
