
package com.peertutoring.dao;

import com.peertutoring.model.TutoringSession;
import com.peertutoring.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionDAO {

    public int createSession(TutoringSession session) throws SQLException {
        String sql = """
                INSERT INTO tutoring_sessions
                (learner_id, tutor_id, subject_id, scheduled_at, message)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, session.getLearnerId());
            statement.setInt(2, session.getTutorId());
            statement.setInt(3, session.getSubjectId());

            if (session.getScheduledAt() == null) {
                statement.setNull(4, java.sql.Types.TIMESTAMP);
            } else {
                statement.setTimestamp(
                        4, Timestamp.valueOf(session.getScheduledAt()));
            }

            statement.setString(5, session.getMessage());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    session.setSessionId(id);
                    session.setStatus("PENDING");
                    return id;
                }
            }
        }

        throw new SQLException("Creating the tutoring session failed.");
    }

    public List<TutoringSession> findSessionsForLearner(
            int learnerId) throws SQLException {

        String sql = """
                SELECT session_id, learner_id, tutor_id, subject_id,
                       requested_at, scheduled_at, status, message
                FROM tutoring_sessions
                WHERE learner_id = ?
                ORDER BY requested_at DESC
                """;

        return findSessions(sql, learnerId);
    }

    public List<TutoringSession> findSessionsForTutor(
            int tutorId) throws SQLException {

        String sql = """
                SELECT session_id, learner_id, tutor_id, subject_id,
                       requested_at, scheduled_at, status, message
                FROM tutoring_sessions
                WHERE tutor_id = ?
                ORDER BY requested_at DESC
                """;

        return findSessions(sql, tutorId);
    }

    private List<TutoringSession> findSessions(
            String sql, int studentId) throws SQLException {

        List<TutoringSession> sessions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sessions.add(mapSession(resultSet));
                }
            }
        }

        return sessions;
    }

    public Optional<TutoringSession> findById(
            int sessionId) throws SQLException {

        String sql = """
                SELECT session_id, learner_id, tutor_id, subject_id,
                       requested_at, scheduled_at, status, message
                FROM tutoring_sessions
                WHERE session_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, sessionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapSession(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public boolean updateStatusIfCurrent(
            int sessionId,
            String expectedStatus,
            String newStatus,
            int actingStudentId,
            boolean actingAsTutor) throws SQLException {

        String roleColumn = actingAsTutor ? "tutor_id" : "learner_id";

        String sql = """
                UPDATE tutoring_sessions
                SET status = ?
                WHERE session_id = ?
                  AND status = ?
                  AND %s = ?
                """.formatted(roleColumn);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newStatus);
            statement.setInt(2, sessionId);
            statement.setString(3, expectedStatus);
            statement.setInt(4, actingStudentId);

            return statement.executeUpdate() == 1;
        }
    }

    private TutoringSession mapSession(
            ResultSet resultSet) throws SQLException {

        TutoringSession session = new TutoringSession();

        session.setSessionId(resultSet.getInt("session_id"));
        session.setLearnerId(resultSet.getInt("learner_id"));
        session.setTutorId(resultSet.getInt("tutor_id"));
        session.setSubjectId(resultSet.getInt("subject_id"));
        session.setStatus(resultSet.getString("status"));
        session.setMessage(resultSet.getString("message"));

        Timestamp requestedAt = resultSet.getTimestamp("requested_at");
        if (requestedAt != null) {
            session.setRequestedAt(requestedAt.toLocalDateTime());
        }

        Timestamp scheduledAt = resultSet.getTimestamp("scheduled_at");
        if (scheduledAt != null) {
            session.setScheduledAt(scheduledAt.toLocalDateTime());
        }

        return session;
    }
}
