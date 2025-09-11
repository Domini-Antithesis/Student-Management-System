const express = require('express');
const router = express.Router();
const Marks = require('../models/Marks');
const Student = require('../models/Student');
const Course = require('../models/Course');

// Route to fetch and display marks data
router.get('/', async (req, res) => {
    try {
        const students = await Student.find({});
        const courses = await Course.find({});
        const marks = await Marks.find({});

        // Create a mapping of studentID to their assigned courses
        const studentCourseMap = students.map(student => {
            const assignedCourses = courses.filter(course => course.assignedTo.includes(student._id.toString()));
            return {
                studentID: student.studentID,
                courses: assignedCourses.map(course => ({ courseCode: course.courseCode }))
            };
        });

        // Combine marks data with student-course mapping
        const formattedMarks = [];
        studentCourseMap.forEach(student => {
            student.courses.forEach(course => {
                const markEntry = marks.find(mark => mark.studentID === student.studentID && mark.courseCode === course.courseCode);
                formattedMarks.push({
                    studentID: student.studentID,
                    courseCode: course.courseCode,
                    internal: markEntry ? markEntry.internal : 'NULL',
                    mst: markEntry ? markEntry.mst : 'NULL',
                    est: markEntry ? markEntry.est : 'NULL',
                    total: markEntry ? markEntry.total : 'NULL'
                });
            });
        });

        // Sort marks by studentID in ascending order
        const sortedMarks = formattedMarks.sort((a, b) => a.studentID.localeCompare(b.studentID));

        res.render('marks_main', { marks: sortedMarks });
    } catch (err) {
        console.error(err);
        res.status(500).send('Error fetching marks data');
    }
});

// Route to render the Enter Marks page
router.get('/enter', async (req, res) => {
    try {
        const students = await Student.find({});
        const courses = await Course.find({});
        const marks = await Marks.find({});

        // Sort students by studentID in ascending order
        const sortedStudents = students.sort((a, b) => a.studentID.localeCompare(b.studentID));

        // Map sorted students to their assigned courses
        const studentData = sortedStudents.map(student => {
            const assignedCourses = courses.filter(course => course.assignedTo.includes(student._id.toString()));
            return {
                studentID: student.studentID,
                courses: assignedCourses.map(course => ({ courseCode: course.courseCode }))
            };
        });

        // Filter out courses with non-empty marks before rendering enter_marks.ejs
        const filteredStudentData = studentData.map(student => {
            const filteredCourses = student.courses.filter(course => {
                const markEntry = marks.find(mark => mark.studentID === student.studentID && mark.courseCode === course.courseCode);
                return !markEntry || (markEntry.internal === 0 && markEntry.mst === 0 && markEntry.est === 0 && markEntry.total === 0);
            });
            return { ...student, courses: filteredCourses };
        }).filter(student => student.courses.length > 0);

        res.render('enter_marks', { students: filteredStudentData });
    } catch (err) {
        console.error(err);
        res.status(500).send('Error loading Enter Marks page');
    }
});

// Route to handle marks submission
router.post('/enter', async (req, res) => {
    const marksData = req.body.marks;

    try {
        for (const studentID in marksData) {
            for (const courseCode in marksData[studentID]) {
                const { mst, est, internal, total } = marksData[studentID][courseCode];

                // Check if marks entry already exists
                let markEntry = await Marks.findOne({ studentID, courseCode });

                if (markEntry) {
                    // Update existing entry
                    markEntry.mst = mst;
                    markEntry.est = est;
                    markEntry.internal = internal;
                    markEntry.total = total;
                    await markEntry.save();
                } else {
                    // Create new entry
                    const newMark = new Marks({ studentID, courseCode, mst, est, internal, total });
                    await newMark.save();
                }
            }
        }

        res.redirect('/marks');
    } catch (err) {
        console.error(err);
        res.status(500).send('Error saving marks');
    }
});

// Route to render the Update Marks page
router.get('/update', (req, res) => {
    res.render('update_marks');
});

// Route to handle search for studentID in Update Marks
router.get('/update/search', async (req, res) => {
    const { studentID } = req.query;
    try {
        const marks = await Marks.find({ studentID });
        res.render('marks_update_search_result', { studentID, marks });
    } catch (err) {
        console.error(err);
        res.status(500).send('Error fetching marks for the student');
    }
});

// Route to render the Edit Marks page
router.get('/update/edit/:studentID', async (req, res) => {
    const { studentID } = req.params;
    try {
        const marks = await Marks.find({ studentID });
        res.render('edit_marks', { studentID, marks });
    } catch (err) {
        console.error(err);
        res.status(500).send('Error loading Edit Marks page');
    }
});

// Route to handle marks update submission
router.post('/update/edit/:studentID', async (req, res) => {
    const { studentID } = req.params;
    const updatedMarks = req.body.marks;

    try {
        for (const courseCode in updatedMarks) {
            const { mst, est, internal, total } = updatedMarks[courseCode];
            await Marks.findOneAndUpdate(
                { studentID, courseCode },
                { mst, est, internal, total },
                { new: true }
            );
        }
        res.redirect('/marks');
    } catch (err) {
        console.error(err);
        res.status(500).send('Error updating marks');
    }
});

router.get('/marks_norm', async (req, res) => {
    if (!req.session.user) {
        return res.redirect('/login_norm');
    }

    try {
        const studentID = req.session.user.studentID;
        const marks = await Marks.find({ studentID });
        const coursesList = await Course.find({});
        // Create a map of courseCode to course object for easy lookup
        const courses = {};
        coursesList.forEach(course => {
            courses[course.courseCode] = course;
        });

        // Add credits to each mark object
        const marksWithCredits = marks.map(mark => {
            return {
                ...mark.toObject(),
                credits: courses[mark.courseCode] ? courses[mark.courseCode].credits : 0
            };
        });

        res.render('marks_norm', { marks: marksWithCredits, courses });
    } catch (err) {
        console.error(err);
        res.render('marks_norm', { marks: [], courses: {} });
    }
});

module.exports = router;
