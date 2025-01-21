package com.example.mytest.auth;

import com.example.mytest.model.Admin;
import com.example.mytest.model.Student;
import com.example.mytest.model.Teacher;

public class Authentication {
    static public Student student;
    static public Teacher teacher;
    static public Admin admin;

    public static Student getStudent() {
        return student;
    }

    public static void setStudent(Student student) {
        Authentication.student = student;
    }

    public static Teacher getTeacher() {
        return teacher;
    }

    public static void setTeacher(Teacher teacher) {
        Authentication.teacher = teacher;
    }

    public static Admin getAdmin() {
        return admin;
    }

    public static void setAdmin(Admin admin) {
        Authentication.admin = admin;
    }

    public static String getStudentId() {
        if (student != null) {
            return student.getId(); // Предполагается, что метод getId() возвращает ID студента
        }
        return null; // Если студент не авторизован
    }

    public static String getTeacherId() {
        if (teacher != null) {
            return teacher.getId(); // Убедитесь, что в модели Teacher есть метод getId(), который возвращает ID
        }
        return null; // Если преподаватель не авторизован
    }
}
