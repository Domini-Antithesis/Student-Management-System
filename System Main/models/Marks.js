const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const MarksSchema = new Schema({
    studentID: {
        type: Schema.Types.String,
        ref: 'Student',
        required: true
    },
    courseCode: {
        type: Schema.Types.String,
        ref: 'Course',
        required: true
    },
    mst: {
        type: Number,
        default: 0,
        max: 30
    },
    est: {
        type: Number,
        default: 0,
        max: 40
    },
    internal: {
        type: Number,
        default: 0,
        max: 30
    },
    total: {
        type: Number,
        default: 0,
        max: 100
    }
});

module.exports = mongoose.model('Marks', MarksSchema, 'marks');
