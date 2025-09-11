const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const CourseSchema = new Schema({
    courseCode: { type: String, required: true, unique: true },
    courseName: { type: String, required: true },
    description: { type: String },
    credits: { type: Number, required: true },
    assignedTo: [{ type: String }] // studentIDs
});

module.exports = mongoose.model('Course', CourseSchema, 'courses');
