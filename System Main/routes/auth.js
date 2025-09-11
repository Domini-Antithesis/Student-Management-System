// routes/auth.js
const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const User = require('../models/User');
const Course = require('../models/Course');
const Student = require('../models/Student');
const Marks = require('../models/Marks');

// GET Login page
router.get('/login', (req, res) => {
  res.render('login', { error: req.flash('error') });
});

// POST Login
router.post('/login', async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    req.flash('error', 'Please enter both email and password.');
    return res.redirect('/login');
  }
  try {
    const user = await User.findOne({ email });
    if (!user) {
      req.flash('error', 'Email not found.');
      return res.redirect('/login');
    }
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      req.flash('error', 'Incorrect password.');
      return res.redirect('/login');
    }
    req.session.user = user;
    res.redirect('/admin_main');
  } catch (err) {
    console.error(err);
    req.flash('error', 'Something went wrong.');
    res.redirect('/login');
  }
});

// GET Login as Student page
router.get('/login_norm', (req, res) => {
  res.render('login_norm', { error: req.flash('error') });
});

// POST Login as Student
router.post('/login_norm', async (req, res) => {
  const { studentID, password } = req.body;
  if (!studentID || !password) {
    req.flash('error', 'Please enter both Student ID and Password.');
    return res.redirect('/login_norm');
  }
  try {
    const student = await Student.findOne({ studentID });
    if (!student) {
      req.flash('error', 'Student ID not found.');
      return res.redirect('/login_norm');
    }
    const dobYear = student.dob.getFullYear().toString(); // Extract only the year from DOB
    if (password !== dobYear) {
      req.flash('error', 'Incorrect password.');
      return res.redirect('/login_norm');
    }
    req.session.user = { username: student.name, studentID: student.studentID };
    res.redirect('/norm_main');
  } catch (err) {
    console.error(err);
    req.flash('error', 'Something went wrong.');
    res.redirect('/login_norm');
  }
});

// GET Norm Main page
router.get('/norm_main', (req, res) => {
  if (!req.session.user) {
    return res.redirect('/login_norm');
  }
  res.render('norm_main', { user: req.session.user });
});

// GET Registration page
router.get('/register', (req, res) => {
  res.render('register', { error: req.flash('error') });
});

// POST Registration
router.post('/register', async (req, res) => {
  const { username, email, password, dob } = req.body;
  if (!username || !email || !password || !dob) {
    req.flash('error', 'All fields are required.');
    return res.redirect('/register');
  }
  const emailRegex = /^\S+@\S+\.\S+$/;
  if (!emailRegex.test(email)) {
    req.flash('error', 'Invalid email format.');
    return res.redirect('/register');
  }
  // Password must be at least 8 alphanumeric characters with at least 1 number.
  const passwordRegex = /^(?=.*\d)[A-Za-z\d]{8,}$/;
  if (!passwordRegex.test(password)) {
    req.flash('error', 'Password must be at least 8 characters long and include at least one number.');
    return res.redirect('/register');
  }
  try {
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      req.flash('error', 'Email already registered.');
      return res.redirect('/register');
    }
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    const newUser = new User({
      username,
      email,
      password: hashedPassword,
      dob
    });
    await newUser.save();
    req.flash('error', 'Registration successful! Please login.');
    res.redirect('/login');
  } catch (err) {
    console.error(err);
    req.flash('error', 'Registration failed.');
    res.redirect('/register');
  }
});

// GET Logout
router.get('/logout', (req, res) => {
  req.session.destroy();
  res.redirect('/login');
});

module.exports = router;
