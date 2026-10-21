
package com.peertutoring.dao;

import com.peertutoring.model.Student;
import com.peertutoring.util.DBConnection;
import com.peertutoring.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class StudentDAO {

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public int registerStudent(
            Student student, String plainPassword) throws SQLException {

        String sql = """
                INSERT INTO students
                    (full_name, email, semester, password_hash)
                VALUES (?, ?, ?, ?)
                """;

        String passwordHash = PasswordUtil.hashPassword(plainPassword);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, student.getFullName().trim());
            statement.setString(2, student.getEmail().trim().toLowerCase());
            statement.setInt(3, student.getSemester());
            statement.setString(4, passwordHash);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Student registration failed.");
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int studentId = keys.getInt(1);
                    student.setStudentId(studentId);
                    return studentId;
                }
            }

            throw new SQLException(
                    "Registration succeeded, but no student ID was returned."
            );
        }
    }

    public Optional<Student> findByEmail(String email) throws SQLException {
        String sql = """
                SELECT student_id, full_name, email, semester
                FROM students
                WHERE email = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email.trim().toLowerCase());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapStudent(result));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Student> findById(int studentId) throws SQLException {
        String sql = """
                SELECT student_id, full_name, email, semester
                FROM students
                WHERE student_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapStudent(result));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<String> findPasswordHashByEmail(String email)
            throws SQLException {

        String sql = "SELECT password_hash FROM students WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email.trim().toLowerCase());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(result.getString("password_hash"));
                }
            }
        }

        return Optional.empty();
    }

    private Student mapStudent(ResultSet result) throws SQLException {
        return new Student(
                result.getInt("student_id"),
                result.getString("full_name"),
                result.getString("email"),
                result.getInt("semester")
        );
    }
}
