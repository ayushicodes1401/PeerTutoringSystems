
package com.peertutoring.service;

import com.peertutoring.dao.StudentDAO;
import com.peertutoring.model.Student;
import com.peertutoring.util.PasswordUtil;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

public class AuthService {

    private final StudentDAO studentDAO;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public AuthService() {
        this.studentDAO = new StudentDAO();
    }

    public Student register(
            String fullName,
            String email,
            int semester,
            String password,
            String confirmPassword) throws SQLException {

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException(
                    "Enter a valid email address.");
        }

        if (semester < 1 || semester > 12) {
            throw new IllegalArgumentException(
                    "Semester must be between 1 and 12.");
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters.");
        }

        if (confirmPassword == null
                || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException(
                    "Passwords do not match.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (studentDAO.emailExists(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists.");
        }

        Student student = new Student(
                fullName.trim(),
                normalizedEmail,
                semester
        );

        studentDAO.registerStudent(student, password);

        return student;
    }

    public Optional<Student> login(String email, String password)
            throws SQLException {

        if (email == null || email.isBlank()
                || password == null || password.isEmpty()) {
            return Optional.empty();
        }

        Optional<String> storedHash =
                studentDAO.findPasswordHashByEmail(email);

        if (storedHash.isEmpty()
                || !PasswordUtil.verifyPassword(
                        password, storedHash.get())) {
            return Optional.empty();
        }

        return studentDAO.findByEmail(email);
    }
}
