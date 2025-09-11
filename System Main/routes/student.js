// routes/student.js
const express = require('express');
const router = express.Router();
const Course = require('../models/Course');
const Student = require('../models/Student');
const Marks = require('../models/Marks');

// Middleware to check if user is logged in
function isAuthenticated(req, res, next) {
  if (req.session.user) {
    return next();
  }
  res.redirect('/login');
}

// GET page with student list
router.get('/student_main', isAuthenticated, async (req, res) => {
  const searchQuery = req.query.search || '';

  try {
    const students = await Student.find({
      studentID: { $regex: searchQuery, $options: 'i' }  // ← roll number search
    });

    res.render('student_main', {
      user: req.session.user,
      students,
      success: req.flash('success'),
      error: req.flash('error'),
      search: searchQuery
    });
  } catch (err) {
    console.error(err);
    res.render('student_main', {
      user: req.session.user,
      students: [],
      success: [],
      error: ['Could not fetch students.'],
      search: searchQuery
    });
  }
});

/* Duplicate route removed for clarity */
// GET Student-Main page with student list
// router.get('/student_main', isAuthenticated, async (req, res) => {
//   try {
//     const students = await Student.find({});
//     res.render('student_main', { user: req.session.user, students, success: req.flash('success'), error: req.flash('error') });
//   } catch (err) {
//     console.error(err);
//     res.render('student_main', { user: req.session.user, students: [], error: ['Could not fetch students.'] });
//   }
// });

/*

// POST Add new student
router.post('/students/add_student', isAuthenticated, async (req, res) => {
  const { studentID, name, dob, branch } = req.body;
  if (!studentID || !name || !dob || !branch) {
    req.flash('error', 'All student fields are required.');
    return res.redirect('/student_main');
  }
  try {
    const newStudent = new Student({ studentID, name, dob, branch });
    await newStudent.save();
    req.flash('success', 'Student added successfully.');
    res.redirect('/student_main');
  } catch (err) {
    console.error(err);
    req.flash('error', 'Error adding student.');
    res.redirect('/student_main');
  }
});

*/

// Render Add Student Form
router.get('/add_student', (req, res) => {
    res.render('add_student');
});

// Handle Add Student POST
router.post('/add_student', async (req, res) => {
    const { studentID, name, dob, branch } = req.body;
    try {
    const existing = await Student.findOne({ studentID });
    if (existing) {
        return res.send('Student with this code already exists.');
    }
    const newStudent = new Student({ studentID, name, dob, branch });
    await newStudent.save();

    // After adding a new student, initialize marks for all assigned courses
    const courses = await Course.find({});
    const marksToInsert = [];
    courses.forEach(course => {
        if (course.assignedTo.includes(newStudent._id.toString())) {
            marksToInsert.push({
                studentID: newStudent.studentID,
                courseCode: course.courseCode,
                mst: 0,
                est: 0,
                internal: 0,
                total: 0
            });
        }
    });
    if (marksToInsert.length > 0) {
        await Marks.insertMany(marksToInsert);
    }

    res.redirect('/student/student_main');
    } catch (err) {
    console.error(err);
    res.send('Error adding new student');
    res.redirect('/student_main');
    }
});


// Search student
/*
router.post('/students/search', isAuthenticated, async (req, res) => {
  const { studentID } = req.body;
  const student = await Student.findOne({ studentID });
  res.render('student_search_result', { 
    user: req.session.user,
    success: req.flash('success'),
    error: req.flash('error'),
    student 
  });
});
*/


// Search student
router.get('/search_student', async (req, res) => {
    const query = req.query.query;
    try {
    const results = await Student.find({
        $or: [
        { studentID: { $regex: query, $options: 'i' } },
        { name: { $regex: query, $options: 'i' } }
        ]
    });
    res.render('student_search_result', { students: results });
    } catch (err) {
    console.error(err);
    res.send('Search error');
    }
});


// Edit Student
router.get('/edit/:id', isAuthenticated, async (req, res) => {
  const student = await Student.findById(req.params.id);
  res.render('student_search_result', { student });
});


router.post('/update/:id', isAuthenticated, async (req, res) => {
  const { studentID, name, dob, branch } = req.body;
  await Student.findByIdAndUpdate(req.params.id, { studentID, name, dob, branch });
  res.redirect('/student_main');
});


// Render Edit Student Form
router.get('/edit_student/:id', async (req, res) => {
    const student = await Student.findById(req.params.id);
    if (!student) return res.send('Student not found');
    res.render('edit_student', { student });
});

// Handle Edit Course POST
router.post('/edit_student/:id', async (req, res) => {
    const { studentID, name, dob, branch } = req.body;
    try {
    await Student.findByIdAndUpdate(req.params.id, {
        studentID,
        name,
        dob,
        branch
    });
    res.redirect('/student/student_main');
    } catch (err) {
    console.error(err);
    res.send('Error updating student!');
    }
});


// Delete Student
router.post('/delete_student/:id', async (req, res) => {
    try {
    await Student.findByIdAndDelete(req.params.id);
    res.redirect('/student/student_main');
    } catch (err) {
    console.error(err);
    res.send('Error deleting Student!');
    }
});

/*
// POST Update student details
router.post('/students/update/:id', isAuthenticated, async (req, res) => {
  const { name, dob, branch } = req.body;
  if (!name || !dob || !branch) {
    req.flash('error', 'All fields are required for update.');
    return res.redirect('/student_main');
  }
  try {
    await Student.findByIdAndUpdate(req.params.id, { name, dob, branch });
    req.flash('success', 'Student updated successfully.');
    res.redirect('/student_main');
  } catch (err) {
    console.error(err);
    req.flash('error', 'Error updating student.');
    res.redirect('/student_main');
  }
});

// POST Delete a student
router.post('/students/delete/:id', isAuthenticated, async (req, res) => {
  try {
    await Student.findByIdAndDelete(req.params.id);
    req.flash('success', 'Student deleted successfully.');
    res.redirect('/student_main');
  } catch (err) {
    console.error(err);
    req.flash('error', 'Error deleting student.');
    res.redirect('/student_main');
  }
});
*/

// GET page with list of students
/*router.get('/list_student', isAuthenticated, async (req, res) => {
  try {
    const students = await Student.find({});
    res.render('list_student', {
      user: req.session.user,
      students,
      success: req.flash('success'),
      error: req.flash('error')
    });
  } catch (err) {
    console.error(err);
    res.render('list_student', {
      user: req.session.user,
      students: [],
      success: [],
      error: ['Could not fetch students.']
    });
  }
});
*/

router.get('/list_student', isAuthenticated, async (req, res) => {
  try {
    const students = await Student.find({}).sort({ studentID: 1 }); // Sort by studentID in ascending order
    const courses = await Course.find({});

    // Create a mapping from studentID to array of courseCodes
    const studentCoursesMap = {};
    students.forEach(student => {
      studentCoursesMap[student._id.toString()] = [];
    });
    courses.forEach(course => {
      course.assignedTo.forEach(studentId => {
        if (studentCoursesMap[studentId]) {
          studentCoursesMap[studentId].push(course.courseCode);
        }
      });
    });

    res.render('list_student', {
      user: req.session.user,
      students,
      studentCoursesMap,
      success: req.flash('success'),
      error: req.flash('error')
    });
  } catch (err) {
    console.error(err);
    res.render('list_student', {
      user: req.session.user,
      students: [],
      studentCoursesMap: {},
      success: [],
      error: ['Could not fetch students.']
    });
  }
});

// Export the router
module.exports = router;
