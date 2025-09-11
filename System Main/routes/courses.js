const express = require('express');
const router = express.Router();
const Course = require('../models/Course');
const Marks = require('../models/Marks'); // to assign marks
const Student = require('../models/Student'); // to assign courses

// Show all courses
router.get('/courses_main', async (req, res) => {
    try {
    const courses = await Course.find({});
    res.render('courses_main', { courses });
    } catch (err) {
    console.error(err);
    res.send('Error loading courses');
    }
});

// Render Add Course Form
router.get('/add_course', (req, res) => {
    res.render('add_course');
});

// Handle Add Course POST
router.post('/add_course', async (req, res) => {
    const { courseCode, courseName, description, credits } = req.body;
    try {
    const existing = await Course.findOne({ courseCode });
    if (existing) {
        return res.send('Course with this code already exists.');
    }
    const newCourse = new Course({ courseCode, courseName, description, credits });
    await newCourse.save();
    res.redirect('/courses/courses_main');
    } catch (err) {
    console.error(err);
    res.send('Error adding course');
    }
});

// Render Assign Course Form
router.get('/assign_course', async (req, res) => {
    const courses = await Course.find({});
    const students = await Student.find({});
    res.render('assign_course', { courses, students });
});

// Handle Assign Course POST
router.post('/assign_course', async (req, res) => {
    const { studentID, courseCode } = req.body;
    try {
    const course = await Course.findById(courseCode);
    if (!course.assignedTo.includes(studentID)) {
        course.assignedTo.push(studentID);
        await course.save();
    }

    // After assigning a course to a student, initialize marks for the course
    const student = await Student.findById(studentID);
    if (student && course) {
        const existingMark = await Marks.findOne({ studentID: student.studentID, courseCode: course.courseCode });
        if (!existingMark) {
            const newMark = new Marks({
                studentID: student.studentID,
                courseCode: course.courseCode,
                mst: 0,
                est: 0,
                internal: 0,
                total: 0
            });
            await newMark.save();
        }
    }

    res.redirect('/courses/courses_main');
    } catch (err) {
    console.error(err);
    res.send('Error assigning course!');
    }
});

// Search for courses
router.get('/search_course', async (req, res) => {
    const query = req.query.query;
    try {
    const results = await Course.find({
        $or: [
        { courseCode: { $regex: query, $options: 'i' } },
        { courseName: { $regex: query, $options: 'i' } }
        ]
    });
    res.render('courses_search_result', { courses: results });
    } catch (err) {
    console.error(err);
    res.send('Search error');
    }
});

// Render Edit Course Form
router.get('/edit_course/:id', async (req, res) => {
    const course = await Course.findById(req.params.id);
    if (!course) return res.send('Course not found');
    res.render('edit_course', { course });
});

// Handle Edit Course POST
router.post('/edit_course/:id', async (req, res) => {
    const { courseCode, courseName, description, credits } = req.body;
    try {
    await Course.findByIdAndUpdate(req.params.id, {
        courseCode,
        courseName,
        description,
        credits
    });
    res.redirect('/courses/courses_main');
    } catch (err) {
    console.error(err);
    res.send('Error updating course');
    }
});

// Delete Course
router.post('/delete_course/:id', async (req, res) => {
    try {
    await Course.findByIdAndDelete(req.params.id);
    res.redirect('/courses/courses_main');
    } catch (err) {
    console.error(err);
    res.send('Error deleting course!');
    }
});

// New: query by assignedTo containing the student's ObjectId string
router.get('/course_norm', async (req, res) => {
    if (!req.session.user) {
      return res.redirect('/login_norm');
    }
  
    try {
      // Look up the student document
      const student = await Student.findOne({ studentID: req.session.user.studentID });
      if (!student) {
        return res.render('course_norm', { courses: [] });
      }
  
      // assignedTo stores String versions of ObjectId, so convert:
      const sid = student._id.toString();
  
      // Fetch all courses whose assignedTo array includes this sid
      const assignedCourses = await Course.find({ assignedTo: sid });
  
      res.render('course_norm', { courses: assignedCourses });
    } catch (err) {
      console.error(err);
      res.render('course_norm', { courses: [] });
    }
  });
  

module.exports = router;
