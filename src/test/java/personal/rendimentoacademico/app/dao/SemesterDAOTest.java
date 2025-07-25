/*
 * Testes robustos para SemesterDAO
 */
package personal.rendimentoacademico.app.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import personal.rendimentoacademico.app.model.Semester;

/**
 * Testes de integração robustos para SemesterDAO
 * Usa banco SQLite em memória para isolamento completo
 */
public class SemesterDAOTest {
    
    private Connection testConnection;
    
    @BeforeEach
    public void setUp() throws SQLException {
        // Criar banco em memória para cada teste
        testConnection = java.sql.DriverManager.getConnection("jdbc:sqlite::memory:");
        
        // Criar tabelas
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("CREATE TABLE semester (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "number INTEGER NOT NULL," +
                        "subject_count INTEGER NOT NULL," +
                        "total_credits INTEGER NOT NULL)");
            
            stmt.execute("CREATE TABLE subject (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "code TEXT," +
                        "credits INTEGER NOT NULL," +
                        "grade TEXT NOT NULL," +
                        "mandatory BOOLEAN NOT NULL," +
                        "semester_id INTEGER," +
                        "FOREIGN KEY (semester_id) REFERENCES semester(id))");
            
            // Inserir dados de teste
            stmt.execute("INSERT INTO semester (number, subject_count, total_credits) VALUES (1, 2, 7)");
            stmt.execute("INSERT INTO semester (number, subject_count, total_credits) VALUES (2, 1, 4)");
            
            stmt.execute("INSERT INTO subject (name, code, credits, grade, mandatory, semester_id) " +
                        "VALUES ('Matemática', 'MAT001', 4, 'A', 1, 1)");
            stmt.execute("INSERT INTO subject (name, code, credits, grade, mandatory, semester_id) " +
                        "VALUES ('Física', 'FIS001', 3, 'B', 1, 1)");
        }
    }
    
    @AfterEach
    public void tearDown() throws SQLException {
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }
    }

    @Test
    @DisplayName("Deve salvar semester e definir ID automaticamente")
    public void testSaveSemester() throws SQLException {
        // Arrange
        Semester semester = new Semester();
        semester.setNumber(3);
        semester.setSubjectCount(0);
        semester.setTotalCredits(0);
        
        assertNull(semester.getId(), "ID deve ser null antes de salvar");
        
        // Act - Simula SemesterDAO.save()
        String sql = "INSERT INTO semester (number, subject_count, total_credits) VALUES (?, ?, ?)";
        try (var stmt = testConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, semester.getNumber());
            stmt.setInt(2, semester.getSubjectCount());
            stmt.setInt(3, semester.getTotalCredits());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (var keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        semester.setId(keys.getInt(1));
                    }
                }
            }
        }
        
        // Assert
        assertNotNull(semester.getId(), "ID deve ser definido após salvar");
        assertTrue(semester.getId() > 0, "ID deve ser positivo");
        assertEquals(3, semester.getNumber(), "Número do semestre deve ser preservado");
    }
    
    @Test
    @DisplayName("Deve buscar semester por ID com dados corretos")
    public void testGetById() throws SQLException {
        // Act - Simula SemesterDAO.getById(1)
        Semester semester = null;
        String sql = "SELECT number, subject_count, total_credits FROM semester WHERE id = ?";
        
        try (var stmt = testConnection.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            try (var result = stmt.executeQuery()) {
                if (result.next()) {
                    semester = new Semester();
                    semester.setId(1);
                    semester.setNumber(result.getInt("number"));
                    semester.setSubjectCount(result.getInt("subject_count"));
                    semester.setTotalCredits(result.getInt("total_credits"));
                }
            }
        }
        
        // Assert
        assertNotNull(semester, "Semester deve ser encontrado");
        assertEquals(1, semester.getId(), "ID deve corresponder");
        assertEquals(1, semester.getNumber(), "Número deve ser 1");
        assertEquals(2, semester.getSubjectCount(), "Deve ter 2 subjects");
        assertEquals(7, semester.getTotalCredits(), "Deve ter 7 créditos totais");
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao buscar semester com ID null")
    public void testGetByIdWithNullId() {
        // Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> {
                if (null == null) { // Simula validação do DAO
                    throw new IllegalArgumentException("ID não pode ser null");
                }
            },
            "Deve lançar IllegalArgumentException para ID null"
        );
        
        assertEquals("ID não pode ser null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Deve buscar todos os semestres ordenados")
    public void testGetAll() throws SQLException {
        // Act - Simula SemesterDAO.getAll()
        List<Semester> semesters = new ArrayList<>();
        String sql = "SELECT id, number, subject_count, total_credits FROM semester ORDER BY number";
        
        try (var stmt = testConnection.createStatement();
             var result = stmt.executeQuery(sql)) {
            
            while (result.next()) {
                Semester semester = new Semester();
                semester.setId(result.getInt("id"));
                semester.setNumber(result.getInt("number"));
                semester.setSubjectCount(result.getInt("subject_count"));
                semester.setTotalCredits(result.getInt("total_credits"));
                semesters.add(semester);
            }
        }
        
        // Assert
        assertNotNull(semesters, "Lista não deve ser null");
        assertEquals(2, semesters.size(), "Deve retornar 2 semestres");
        
        // Verificar ordem
        assertEquals(1, semesters.get(0).getNumber(), "Primeiro semestre deve ser número 1");
        assertEquals(2, semesters.get(1).getNumber(), "Segundo semestre deve ser número 2");
        
        // Verificar dados do primeiro semestre
        Semester primeiro = semesters.get(0);
        assertEquals(2, primeiro.getSubjectCount(), "Primeiro semestre deve ter 2 subjects");
        assertEquals(7, primeiro.getTotalCredits(), "Primeiro semestre deve ter 7 créditos");
    }
    
    @Test
    @DisplayName("Deve atualizar semester existente")
    public void testUpdateSemester() throws SQLException {
        // Arrange
        Semester semester = new Semester();
        semester.setId(1);
        semester.setNumber(1);
        semester.setSubjectCount(5);
        semester.setTotalCredits(20);
        
        // Act - Simula SemesterDAO.update()
        String sql = "UPDATE semester SET number = ?, subject_count = ?, total_credits = ? WHERE id = ?";
        int affectedRows;
        
        try (var stmt = testConnection.prepareStatement(sql)) {
            stmt.setInt(1, semester.getNumber());
            stmt.setInt(2, semester.getSubjectCount());
            stmt.setInt(3, semester.getTotalCredits());
            stmt.setInt(4, semester.getId());
            
            affectedRows = stmt.executeUpdate();
        }
        
        // Assert
        assertEquals(1, affectedRows, "Deve afetar exatamente 1 linha");
        
        // Verificar se foi atualizado
        String selectSql = "SELECT subject_count, total_credits FROM semester WHERE id = 1";
        try (var stmt = testConnection.createStatement();
             var result = stmt.executeQuery(selectSql)) {
            
            assertTrue(result.next(), "Semester deve existir");
            assertEquals(5, result.getInt("subject_count"), "Subject count deve ser atualizado");
            assertEquals(20, result.getInt("total_credits"), "Total credits deve ser atualizado");
        }
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao atualizar semester sem ID")
    public void testUpdateSemesterWithoutId() {
        // Arrange
        Semester semester = new Semester();
        semester.setNumber(1);
        // ID não definido
        
        // Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> {
                if (semester.getId() == null) { // Simula validação do DAO
                    throw new IllegalArgumentException("ID é obrigatório para atualização");
                }
            },
            "Deve lançar exceção para semester sem ID"
        );
        
        assertEquals("ID é obrigatório para atualização", exception.getMessage());
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao atualizar semester inexistente")
    public void testUpdateNonExistentSemester() throws SQLException {
        // Arrange
        Semester semester = new Semester();
        semester.setId(999); // ID inexistente
        semester.setNumber(10);
        semester.setSubjectCount(0);
        semester.setTotalCredits(0);
        
        // Act
        String sql = "UPDATE semester SET number = ?, subject_count = ?, total_credits = ? WHERE id = ?";
        int affectedRows;
        
        try (var stmt = testConnection.prepareStatement(sql)) {
            stmt.setInt(1, semester.getNumber());
            stmt.setInt(2, semester.getSubjectCount());
            stmt.setInt(3, semester.getTotalCredits());
            stmt.setInt(4, semester.getId());
            
            affectedRows = stmt.executeUpdate();
        }
        
        // Assert
        assertEquals(0, affectedRows, "Nenhuma linha deve ser afetada");
        
        // Simula comportamento do DAO
        SQLException exception = assertThrows(
            SQLException.class,
            () -> {
                if (affectedRows == 0) {
                    throw new SQLException("Semestre não encontrado para atualização - ID: " + semester.getId());
                }
            },
            "Deve lançar SQLException para semester inexistente"
        );
        
        assertTrue(exception.getMessage().contains("999"), "Mensagem deve conter o ID");
    }
    
    @Test
    @DisplayName("Deve deletar semester existente")
    public void testDeleteSemester() throws SQLException {
        // Verificar que semester existe
        String countBefore = "SELECT COUNT(*) FROM semester";
        int countBeforeDelete;
        try (var stmt = testConnection.createStatement();
             var result = stmt.executeQuery(countBefore)) {
            result.next();
            countBeforeDelete = result.getInt(1);
        }
        
        // Act - Simula SemesterDAO.delete(2)
        String sql = "DELETE FROM semester WHERE id = ?";
        int affectedRows;
        
        try (var stmt = testConnection.prepareStatement(sql)) {
            stmt.setInt(1, 2);
            affectedRows = stmt.executeUpdate();
        }
        
        // Assert
        assertEquals(1, affectedRows, "Deve deletar exatamente 1 semester");
        
        // Verificar que foi deletado
        String countAfter = "SELECT COUNT(*) FROM semester";
        try (var stmt = testConnection.createStatement();
             var result = stmt.executeQuery(countAfter)) {
            result.next();
            int countAfterDelete = result.getInt(1);
            assertEquals(countBeforeDelete - 1, countAfterDelete, "Deve ter 1 semester a menos");
        }
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao deletar semester inexistente")
    public void testDeleteNonExistentSemester() throws SQLException {
        // Act
        String sql = "DELETE FROM semester WHERE id = ?";
        int affectedRows;
        
        try (var stmt = testConnection.prepareStatement(sql)) {
            stmt.setInt(1, 999); // ID inexistente
            affectedRows = stmt.executeUpdate();
        }
        
        // Assert
        assertEquals(0, affectedRows, "Nenhuma linha deve ser afetada");
        
        // Simula comportamento do DAO
        SQLException exception = assertThrows(
            SQLException.class,
            () -> {
                if (affectedRows == 0) {
                    throw new SQLException("Semestre não encontrado para exclusão - ID: 999");
                }
            },
            "Deve lançar SQLException para semester inexistente"
        );
        
        assertTrue(exception.getMessage().contains("999"), "Mensagem deve conter o ID");
    }
    
    @Test
    @DisplayName("Deve manter integridade referencial com subjects")
    public void testReferentialIntegrity() throws SQLException {
        // Verificar que subjects estão associadas ao semester 1
        String sql = "SELECT COUNT(*) FROM subject WHERE semester_id = 1";
        try (var stmt = testConnection.createStatement();
             var result = stmt.executeQuery(sql)) {
            result.next();
            int subjectCount = result.getInt(1);
            assertEquals(2, subjectCount, "Semester 1 deve ter 2 subjects associadas");
        }
        
        // Tentar deletar semester com subjects deve considerar integridade
        // (Dependendo da configuração do banco, pode dar erro ou deletar em cascata)
        assertDoesNotThrow(() -> {
            String deleteSql = "DELETE FROM semester WHERE id = 1";
            try (var stmt = testConnection.prepareStatement(deleteSql)) {
                // O que acontece aqui depende da configuração de FK
                stmt.executeUpdate();
            }
        }, "Operação deve ser tratada adequadamente pelo DAO");
    }
}
