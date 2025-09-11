// models/Student.js
const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const StudentSchema = new Schema({
  studentID: { type: String, required: true, unique: true },
  name: { type: String, required: true },
  dob: { type: Date, required: true },
  branch: { type: String, required: true },
  courses: [
    {
      courseCode: { type: String, required: true },
    }
  ]
});

// Specify the collection name as 'students'
module.exports = mongoose.model('Student', StudentSchema, 'students');
