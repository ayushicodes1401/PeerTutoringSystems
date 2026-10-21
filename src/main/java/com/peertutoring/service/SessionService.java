
package com.peertutoring.service;

import com.peertutoring.dao.SessionDAO;
import com.peertutoring.dao.StudentDAO;
import com.peertutoring.dao.TutorDAO;
import com.peertutoring.model.Student;
import com.peertutoring.model.TutoringSession;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SessionService {

    private final SessionDAO sessionDAO;
    private final StudentDAO studentDAO;
    private final TutorDAO tutorDAO;

    public SessionService() {
        this.sessionDAO = new SessionDAO();
        this.studentDAO = new StudentDAO();
        this.tutorDAO = new TutorDAO();
    }

    public TutoringSession requestSession(
            int learnerId,
            int tutorId,
            int subjectId,
            LocalDateTime scheduledAt,
            String message) throws SQLException {

        if (learnerId <= 0 || tutorId <= 0 || subjectId <= 0) {
            throw new IllegalArgumentException(
                    "Learner, tutor, and subject IDs must be valid.");
        }

        if (learnerId == tutorId) {
            throw new IllegalArgumentException(
                    "You cannot request a session with yourself.");
        }

        if (studentDAO.findById(learnerId).isEmpty()) {
            throw new IllegalArgumentException("Learner does not exist.");
        }

        if (studentDAO.findById(tutorId).isEmpty()) {
            throw new IllegalArgumentException("Tutor does not exist.");
        }

        boolean tutorOffersSubject = tutorDAO
                .getSubjectsForTutor(tutorId)
                .stream()
                .anyMatch(subject -> subject.getSubjectId() == subjectId);

        if (!tutorOffersSubject) {
            throw new IllegalArgumentException(
                    "This tutor does not offer the selected subject.");
        }

        if (scheduledAt != null && !scheduledAt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "The scheduled time must be in the future.");
        }

        if (message != null && message.length() > 500) {
            throw new IllegalArgumentException(
                    "The message cannot exceed 500 characters.");
        }

        TutoringSession session = new TutoringSession();
        session.setLearnerId(learnerId);
        session.setTutorId(tutorId);
        session.setSubjectId(subjectId);
        session.setScheduledAt(scheduledAt);
        session.setMessage(message == null || message.isBlank()
                ? null : message.trim());
        session.setStatus("PENDING");

        sessionDAO.createSession(session);
        return session;
    }

    public List<TutoringSession> getLearnerSessions(
            int learnerId) throws SQLException {

        return sessionDAO.findSessionsForLearner(learnerId);
    }

    public List<TutoringSession> getTutorSessions(
            int tutorId) throws SQLException {

        return sessionDAO.findSessionsForTutor(tutorId);
    }

    public Optional<TutoringSession> getSession(
            int sessionId) throws SQLException {

        return sessionDAO.findById(sessionId);
    }

    public boolean acceptRequest(
            int sessionId, int tutorId) throws SQLException {

        return changeStatus(
                sessionId, tutorId, true, "PENDING", "ACCEPTED");
    }

    public boolean rejectRequest(
            int sessionId, int tutorId) throws SQLException {

        return changeStatus(
                sessionId, tutorId, true, "PENDING", "REJECTED");
    }

    public boolean completeSession(
            int sessionId, int tutorId) throws SQLException {

        return changeStatus(
                sessionId, tutorId, true, "ACCEPTED", "COMPLETED");
    }

    public boolean cancelRequest(
            int sessionId, int learnerId) throws SQLException {

        return changeStatus(
                sessionId, learnerId, false, "PENDING", "CANCELLED");
    }

    private boolean changeStatus(
            int sessionId,
            int actingStudentId,
            boolean actingAsTutor,
            String expectedStatus,
            String newStatus) throws SQLException {

        if (sessionId <= 0 || actingStudentId <= 0) {
            throw new IllegalArgumentException(
                    "Session ID and student ID must be positive.");
        }

        return sessionDAO.updateStatusIfCurrent(
                sessionId,
                expectedStatus,
                newStatus,
                actingStudentId,
                actingAsTutor);
    }
}
