/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package personal.rendimentoacademico.app.models;

import java.util.List;

/**
 *
 * @author henri
 */
public class Semester {
    private Integer id;
    private int number;
    private int subjectCount;
    private int totalCredits;
    private List<Subject> subjects;
    
    public Semester() {}

    public Semester(Integer id, int number, int subjectCount, int totalCredits, List<Subject> subjects) {
        this.id = id;
        this.number = number;
        this.subjectCount = subjectCount;
        this.totalCredits = totalCredits;
        this.subjects = subjects;
    }

    public Semester(int number, int subjectCount, int totalCredits, List<Subject> subjects) {
        this(null, number, subjectCount, totalCredits, subjects);
    }

    // Getters
    public Integer getId() {
        return id;
    }
    public int getNumber() {
        return number;
    }
    public int getSubjectCount() {
        return subjectCount;
    }
    public int getTotalCredits() {
        return totalCredits;
    }
    public List<Subject> getSubjects() {
        return subjects;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setSubjectCount(int subjectCount) {
        this.subjectCount = subjectCount;
    }
    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }
    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    // Métodos auxiliares
    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }
    public void removeSubject(Subject subject) {
        this.subjects.remove(subject);
    }
}
