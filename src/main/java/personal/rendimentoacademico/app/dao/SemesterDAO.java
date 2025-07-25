/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package personal.rendimentoacademico.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import personal.rendimentoacademico.app.model.Semester;
import personal.rendimentoacademico.app.model.Subject;
import personal.rendimentoacademico.app.utils.DBUtil;

/**
 *
 * @author henri
 */
public class SemesterDAO {
    public static void save(Semester semester) throws SQLException {
        String sql = "INSERT INTO semester (number, subject_count, total_credits) VALUES (?, ?, ?)";
        String[] returnedColumns = {"id"};
        
        try (
            Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, returnedColumns)
        ) {
            statement.setInt(1, semester.getNumber());
            statement.setInt(2, semester.getSubjectCount());
            statement.setInt(3, semester.getTotalCredits());
            
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Falha ao inserir semestre.");
            }
            
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    semester.setId(keys.getInt("id"));
                } else {
                    throw new SQLException("Falha ao obter ID gerado para o semestre.");
                }
            }
            
        }
    }
    
    public static Semester getById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("O ID não pode ser null.");
        }
        
        Semester semester = new Semester();
        String sql = """
            SELECT
                number, subjectCount, totalCredits
            FROM semester WHERE id = ?
        """;
        
        try (
            Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            try(ResultSet result = statement.executeQuery()) {
                semester.setNumber(result.getInt("number"));
                semester.setSubjectCount(result.getInt("subject_count"));
                semester.setTotalCredits(result.getInt("total_credits"));
                semester.setSubjects(SubjectDAO.getBySemesterId(id));
            }
            
        }
        
        return semester;
    }
    
    public static List<Semester> getAll() throws SQLException {
        List<Semester> semesters = new ArrayList<>();
        try (
            Connection connection = DBUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM semester")
        ) {
            while (result.next()) {
                int id = result.getInt("id");
                int number = result.getInt("number");
                int subject_count = result.getInt("subject_count");
                int total_credits = result.getInt("total_credits");
                List<Subject> subjects = SubjectDAO.getBySemesterId(id);
                Semester semester = new Semester(id, number, subject_count, total_credits, subjects);
                semesters.add(semester);
            }
        }
        
        return semesters;
    }
    
    public static void update(Semester semester) throws SQLException {
        if (semester.getId() == null) {
            throw new IllegalArgumentException("Não é possível alterar um semestre sem ID.");
        }
        
        String sql = """
            UPDATE semester SET
               number = ?,
               subject_count = ?,
               total_credits = ?
            WHERE id = ?
        """;
        
        try (
            Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, semester.getNumber());
            statement.setInt(2, semester.getSubjectCount());
            statement.setInt(3, semester.getTotalCredits());
            statement.setInt(4, semester.getId());
            
            if (statement.executeUpdate() == 0) {
                throw new SQLException(String.format("Falha ao atualizar semestre de ID %d", semester.getId()));
            }
        }
    }
    
    public static void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("O ID não pode ser null");
        }
        
        try (
            Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM semester WHERE id = ?")
        ) {
            statement.setInt(1, id);
            
            if (statement.executeUpdate() == 0) {
                throw new SQLException(String.format("Semestre %d não encontrado para exclusão", id));
            }
        }
    }
}
