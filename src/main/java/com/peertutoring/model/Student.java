
package com.peertutoring.model;

public class Student {
    private int studentId;
    private String fullName;
    private String email;
    private int semester;

    public Student() {
    }

    public Student(int studentId, String fullName,
                   String email, int semester) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.semester = semester;
    }

    public Student(String fullName, String email, int semester) {
        this.fullName = fullName;
        this.email = email;
        this.semester = semester;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }
}
