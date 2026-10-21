
package com.peertutoring.dao;

import com.peertutoring.model.Student;
import com.peertutoring.model.Subject;
import com.peertutoring.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TutorDAO {

    public void addTutorSubject(int tutorId, int subjectId)
            throws SQLException {

        String sql = """
                INSERT INTO tutor_subjects (tutor_id, subject_id)
                VALUES (?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, tutorId);
            statement.setInt(2, subjectId);
            statement.executeUpdate();
        }
    }

    public boolean removeTutorSubject(int tutorId, int subjectId)
            throws SQLException {

        String sql = """
                DELETE FROM tutor_subjects
                WHERE tutor_id = ? AND subject_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, tutorId);
            statement.setInt(2, subjectId);

            return statement.executeUpdate() > 0;
        }
    }

    public List<Student> searchTutorsBySubject(int subjectId)
            throws SQLException {

        String sql = """
                SELECT s.student_id, s.full_name, s.email, s.semester
                FROM students s
                JOIN tutor_subjects ts ON s.student_id = ts.tutor_id
                WHERE ts.subject_id = ?
                ORDER BY s.full_name
                """;

        List<Student> tutors = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    tutors.add(new Student(
                            result.getInt("student_id"),
                            result.getString("full_name"),
                            result.getString("email"),
                            result.getInt("semester")
                    ));
                }
            }
        }

        return tutors;
    }

    public List<Subject> getSubjectsForTutor(int tutorId)
            throws SQLException {

        String sql = """
                SELECT sub.subject_id, sub.subject_name
                FROM subjects sub
                JOIN tutor_subjects ts ON sub.subject_id = ts.subject_id
                WHERE ts.tutor_id = ?
                ORDER BY sub.subject_name
                """;

        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, tutorId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    subjects.add(new Subject(
                            result.getInt("subject_id"),
                            result.getString("subject_name")
                    ));
                }
            }
        }

        return subjects;
    }

    public List<Subject> getAllSubjects() throws SQLException {
        String sql = """
                SELECT subject_id, subject_name
                FROM subjects
                ORDER BY subject_name
                """;

        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                subjects.add(new Subject(
                        result.getInt("subject_id"),
                        result.getString("subject_name")
                ));
            }
        }

        return subjects;
    }
}
