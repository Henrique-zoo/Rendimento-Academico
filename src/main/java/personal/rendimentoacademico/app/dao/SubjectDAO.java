/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package personal.rendimentoacademico.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import personal.rendimentoacademico.app.models.Subject;
import personal.rendimentoacademico.app.utils.DBUtil;

/**
 *
 * @author henri
 */
public class SubjectDAO {
    public static void save(Subject subject) throws SQLException {
        String sql = """
            INSERT INTO subject
                (name, code, credits, grade, mandatory, semester_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        String[] returnedColumns = {"id"};
        
        try (
            Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, returnedColumns)
        ) {
            statement.setString(1, subject.getName());
            statement.setString(2, subject.getCode());
            statement.setInt(3, subject.getCredits());
            statement.setString(4, subject.getGrade());
            statement.setBoolean(5, subject.isMandatory());
            statement.setInt(6, subject.getSemesterId());

            if (statement.executeUpdate() == 0) {
                throw new SQLException("Falha ao inserir matéria.");
            }
            
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    subject.setId(keys.getInt("id"));
                } else {
                    throw new SQLException("Falha ao obter ID gerado para a matéria");
                }
            }
        }
    }
    
    public static Subject getById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("O ID não pode ser null.");
        }
        
        Subject subject = new Subject();
        String sql = """
            SELECT 
                name, code, credits,
                grade, mandatory, semester_id
            FROM subject
            WHERE id = ?
        """;
        
        try(
            Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                subject.setId(id);
                subject.setName(result.getString("name"));
                subject.setCode(result.getString("code"));
                subject.setCredits(result.getInt("credits"));
                subject.setGrade(result.getString("grade"));
                subject.setMandatory(result.getBoolean("mandatory"));
                subject.setSemesterId(result.getInt("semester_id"));
            }
        }
        
        return subject;
    }
    
    public static List<Subject> getBySemesterId(Integer semesterId) throws SQLException {
        if (semesterId == null) {
            throw new IllegalArgumentException("ID não pode ser null");
        }
        
        List<Subject> subjects = new ArrayList<>();
        String sql = """
            SELECT
                number, subjectCount, totalCredits
            FROM subject
            WHERE semester_id = ?
        """;
        
        try (
            Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, semesterId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String name = result.getString("name");
                    String code = result.getString("code");
                    int credits = result.getInt("credits");
                    String grade = result.getString("grade");
                    boolean mandatory = result.getBoolean("mandatory");
                    int semester_id = result.getInt("semester_id");
                    subjects.add(new Subject(semesterId, name, code, credits, grade, mandatory, semester_id));
                }
            }
        }
        
        return subjects;
    }
    
    public static void update(Subject subject) throws SQLException {
        if (subject.getId() == null) {
            throw new IllegalArgumentException("Não é possível alterar uma matéria sem ID.");
        }
        
        String sql = """
            UPDATE subject SET
                name = ?, %s
                credits = ?,
                grade = ?,
                mandatory = ?,
                semester_id = ?
            WHERE id = ?
            """.formatted(subject.getCode() != null ? "code = ?," : "");
        
        try(
            Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            int pos = 1;
            statement.setString(pos++, subject.getName());
            if (subject.getCode() != null) { statement.setString(pos++, subject.getCode()); }
            statement.setInt(pos++, subject.getCredits());
            statement.setString(pos++, subject.getGrade());
            statement.setBoolean(pos++, subject.isMandatory());
            statement.setInt(pos++, subject.getSemesterId());
            statement.setInt(pos++, subject.getId());
            
            if (statement.executeUpdate() == 0) {
                throw new SQLException(String.format("Falha ao atualizar matéria de ID %d.", subject.getId()));
            }
        }
    }
}
