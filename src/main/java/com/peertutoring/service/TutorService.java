
package com.peertutoring.service;

import com.peertutoring.dao.StudentDAO;
import com.peertutoring.dao.TutorDAO;
import com.peertutoring.model.Student;
import com.peertutoring.model.Subject;

import java.sql.SQLException;
import java.util.List;

public class TutorService {

    private final TutorDAO tutorDAO;
    private final StudentDAO studentDAO;

    public TutorService() {
        this.tutorDAO = new TutorDAO();
        this.studentDAO = new StudentDAO();
    }

    public List<Subject> getAllSubjects() throws SQLException {
        return tutorDAO.getAllSubjects();
    }

    public List<Subject> getSubjectsForTutor(int tutorId)
            throws SQLException {

        validateStudentId(tutorId);
        return tutorDAO.getSubjectsForTutor(tutorId);
    }

    public List<Student> searchTutorsBySubject(
            int subjectId, int currentStudentId) throws SQLException {

        if (subjectId <= 0) {
            throw new IllegalArgumentException("Select a valid subject.");
        }

        validateStudentId(currentStudentId);

        List<Student> tutors =
                tutorDAO.searchTutorsBySubject(subjectId);

        // A student should not see themselves as a tutor to contact.
        tutors.removeIf(tutor ->
                tutor.getStudentId() == currentStudentId);

        return tutors;
    }

    public void offerTutoring(int studentId, int subjectId)
            throws SQLException {

        validateStudentId(studentId);

        if (subjectId <= 0) {
            throw new IllegalArgumentException("Invalid subject.");
        }

        if (studentDAO.findById(studentId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Student account does not exist.");
        }

        boolean subjectExists = tutorDAO.getAllSubjects().stream()
                .anyMatch(subject ->
                        subject.getSubjectId() == subjectId);

        if (!subjectExists) {
            throw new IllegalArgumentException(
                    "Selected subject does not exist.");
        }

        List<Subject> existingSubjects =
                tutorDAO.getSubjectsForTutor(studentId);

        boolean alreadyOffering = existingSubjects.stream()
                .anyMatch(subject ->
                        subject.getSubjectId() == subjectId);

        if (alreadyOffering) {
            throw new IllegalArgumentException(
                    "You already offer tutoring for this subject.");
        }

        tutorDAO.addTutorSubject(studentId, subjectId);
    }

    public boolean stopOfferingTutoring(int studentId, int subjectId)
            throws SQLException {

        validateStudentId(studentId);

        if (subjectId <= 0) {
            throw new IllegalArgumentException("Invalid subject.");
        }

        return tutorDAO.removeTutorSubject(studentId, subjectId);
    }

    private void validateStudentId(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Invalid student ID.");
        }
    }
}
