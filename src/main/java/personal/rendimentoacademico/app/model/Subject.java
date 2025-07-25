/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package personal.rendimentoacademico.app.model;

/**
 *
 * @author henri
 */
public class Subject {
    private Integer id;
    private String name;
    private String code;
    private int credits;
    private String grade;
    private boolean mandatory;
    private Integer semesterId;
    
    public Subject() {}
    
    public Subject(int id, String name, String code, int credits, String grade, boolean mandatory, int semesterId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.credits = credits;
        this.grade = grade;
        this.mandatory = mandatory;
        this.semesterId = semesterId;
    }
    
    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCode() {
        return code;
    }
    public int getCredits() {
        return credits;
    }
    public String getGrade() {
        return grade;
    }
    public boolean isMandatory() {
        return mandatory;
    }
    public Integer getSemesterId() {
        return semesterId;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setCredits(int credits) {
        this.credits = credits;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
    public void setSemesterId(Integer semesterId) {
        this.semesterId = semesterId;
    }
    
    public int getGradeWeight() throws IllegalArgumentException {
        return switch(this.grade) {
            case "SS" -> 5;
            case "MS" -> 4;
            case "MM" -> 3;
            case "MI" -> 2;
            case "II" -> 1;
            case "SR" -> 0;
            default -> throw new IllegalArgumentException(String.format("A menção %s não é válida", this.grade));
        };
    }
}
