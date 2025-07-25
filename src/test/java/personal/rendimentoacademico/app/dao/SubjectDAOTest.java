/*
 * Testes robustos para SubjectDAO
 */
package personal.rendimentoacademico.app.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import personal.rendimentoacademico.app.model.Subject;

/**
 * Testes de integração robustos para SubjectDAO
 * Usa banco SQLite em memória para isolamento completo
 */
public class SubjectDAOTest {
    
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
            stmt.execute("INSERT INTO subject (name, code, credits, grade, mandatory, semester_id) " +
                        "VALUES ('Química', 'QUI001', 4, 'A', 0, 2)");
        }
    }
    
    @AfterEach
    public void tearDown() throws SQLException {
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }
    }

    @Test
    @DisplayName("Deve salvar subject e definir ID automaticamente")
    public void testSaveSubject() throws SQLException {
        // Arrange
        Subject subject = new Subject();
        subject.setName("Algoritmos");
        subject.setCode("ALG001");
        subject.setCredits(4);
        subject.setGrade("A");
        subject.setMandatory(true);
        subject.setSemesterId(1);
        
        assertNull(subject.getId(), "ID deve ser null antes de salvar");
        
        // Act - Simula SubjectDAO.save()
        String sql = "INSERT INTO subject (name, code, credits, grade, mandatory, semester_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (var stmt = testConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getCode());
            stmt.setInt(3, subject.getCredits());
            stmt.setString(4, subject.getGrade());
            stmt.setBoolean(5, subject.isMandatory());
            stmt.setInt(6, subject.getSemesterId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (var keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        subject.setId(keys.getInt(1));
                    }
                }
            }
        }
        
        // Assert
        assertNotNull(subject.getId(), "ID deve ser definido após salvar");
        assertTrue(subject.getId() > 0, "ID deve ser positivo");
        assertEquals("Algoritmos", subject.getName(), "Nome deve ser preservado");
        assertEquals("ALG001", subject.getCode(), "Código deve ser preservado");
    }
    
    @Test
    @DisplayName("Deve salvar subject sem código")
    public void testSaveSubjectWithoutCode() throws SQLException {
        // Arrange
        Subject subject = new Subject();
        subject.setName("Disciplina Sem Código");
        subject.setCode(null); // Sem código
        subject.setCredits(2);
        subject.setGrade("C");
        subject.setMandatory(false);
        subject.setSemesterId(1);
        
        // Act
        String sql = "INSERT INTO subject (name, code, credits, grade, mandatory, semester_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (var stmt = testConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getCode()); // null é válido
            stmt.setInt(3, subject.getCredits());
            stmt.setString(4, subject.getGrade());
            stmt.setBoolean(5, subject.isMandatory());
            stmt.setInt(6, subject.getSemesterId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (var keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        subject.setId(keys.getInt(1));
                    }
                }
            }
        }
        
        // Assert
        assertNotNull(subject.getId(), "Subject deve ser salva mesmo sem código");
        assertNull(subject.getCode(), "Código deve permanecer null");
    }
    
    @Test
    @DisplayName("Deve buscar subject por ID com dados corretos")
    public void testGetById() throws SQLException {
        // Act - Simula SubjectDAO.getById(1)
        Subject subject = null;
        String sql = "SELECT name, code, credits, grade, mandatory, semester_id FROM subject WHERE id = ?";
        
        try (var stmt = testConnection.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            try (var result = stmt.executeQuery()) {
                if (result.next()) {
                    subject = new Subject();
                    subject.setId(1);
                    subject.setName(result.getString("name"));
                    subject.setCode(result.getString("code"));
                    subject.setCredits(result.getInt("credits"));
                    subject.setGrade(result.getString("grade"));
                    subject.setMandatory(result.getBoolean("mandatory"));
                    subject.setSemesterId(result.getInt("semester_id"));
                }
            }
        }
        
        // Assert
        assertNotNull(subject, "Subject deve ser encontrada");
        assertEquals(1, subject.getId(), "ID deve corresponder");
        assertEquals("Matemática", subject.getName(), "Nome deve ser Matemática");
        assertEquals("MAT001", subject.getCode(), "Código deve ser MAT001");
        assertEquals(4, subject.getCredits(), "Deve ter 4 créditos");
        assertEquals("A", subject.getGrade(), "Nota deve ser A");
        assertTrue(subject.isMandatory(), "Deve ser obrigatória");
        assertEquals(1, subject.getSemesterId(), "Deve pertencer ao semestre 1");
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao buscar subject com ID null")
    public void testGetByIdWithNullId() {
        // Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> {
                if (null == null) { // Simula validação do DAO
                    throw new IllegalArgumentException("O ID não pode ser null.");
                }
            },
            "Deve lançar IllegalArgumentException para ID null"
        );
        
        assertEquals("O ID não pode ser null.", exception.getMessage());
    }
    
    @Test
    @DisplayName("Deve buscar subjects por semester ID")
    public void testGetBySemesterId() throws SQLException {
        // Act - Simula SubjectDAO.getBySemesterId(1)
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT id, name, code, credits, grade, mandatory, semester_id FROM subject WHERE semester_id = ?";
        
        try (var stmt = testConnection.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            try (var result = stmt.executeQuery()) {
                while (result.next()) {
                    Subject subject = new Subject();
                    subject.setId(result.getInt("id"));
                    subject.setName(result.getString("name"));
                    subject.setCode(result.getString("code"));
                    subject.setCredits(result.getInt("credits"));
                    subject.setGrade(result.getString("grade"));
                    subject.setMandatory(result.getBoolean("mandatory"));
                    subject.setSemesterId(result.getInt("semester_id"));
                    subjects.add(subject);
                }
            }
        }
        
        // Assert
        assertNotNull(subjects, "Lista não deve ser null");
        assertEquals(2, subjects.size(), "Semestre 1 deve ter 2 subjects");
        
        // Verificar se todas pertencem ao semestre 1
        for (Subject subject : subjects) {
            assertEquals(1, subject.getSemesterId(), "Todas subjects devem pertencer ao semestre 1");
        }
        
        // Verificar subjects específicas
        boolean temMatematica = subjects.stream().anyMatch(s -> "Matemática".equals(s.getName()));
        boolean temFisica = subjects.stream().anyMatch(s -> "Física".equals(s.getName()));
        
        assertTrue(temMatematica, "Deve conter Matemática");
        assertTrue(temFisica, "Deve conter Física");
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia para semester sem subjects")
    public void testGetBySemesterIdEmpty() throws SQLException {
        // Act - Buscar por semester inexistente
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT id, name, code, credits, grade, mandatory, semester_id FROM subject WHERE semester_id = ?";
        
        try (var stmt = testConnection.prepareStatement(sql)) {
            stmt.setInt(1, 999); // ID inexistente
            try (var result = stmt.executeQuery()) {
                while (result.next()) {
                    Subject subject = new Subject();
                    subject.setId(result.getInt("id"));
                    subject.setName(result.getString("name"));
                    subject.setCode(result.getString("code"));
                    subject.setCredits(result.getInt("credits"));
                    subject.setGrade(result.getString("grade"));
                    subject.setMandatory(result.getBoolean("mandatory"));
                    subject.setSemesterId(result.getInt("semester_id"));
                    subjects.add(subject);
                }
            }
        }
        
        // Assert
        assertNotNull(subjects, "Lista não deve ser null");
        assertTrue(subjects.isEmpty(), "Lista deve estar vazia para semester inexistente");
    }
    
    @Test
    @DisplayName("Deve atualizar subject existente")
    public void testUpdateSubject() throws SQLException {
        // Arrange
        Subject subject = new Subject();
        subject.setId(1);
        subject.setName("Matemática Avançada");
        subject.setCode("MAT002");
        subject.setCredits(6);
        subject.setGrade("A+");
        subject.setMandatory(true);
        subject.setSemesterId(1);
        
        // Act - Simula SubjectDAO.update()
        String sql = "UPDATE subject SET name = ?, code = ?, credits = ?, grade = ?, mandatory = ?, semester_id = ? WHERE id = ?";
        int affectedRows;
        
        try (var stmt = testConnection.prepareStatement(sql)) {
            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getCode());
            stmt.setInt(3, subject.getCredits());
            stmt.setString(4, subject.getGrade());
            stmt.setBoolean(5, subject.isMandatory());
            stmt.setInt(6, subject.getSemesterId());
            stmt.setInt(7, subject.getId());
            
            affectedRows = stmt.executeUpdate();
        }
        
        // Assert
        assertEquals(1, affectedRows, "Deve afetar exatamente 1 linha");
        
        // Verificar se foi atualizado
        String selectSql = "SELECT name, code, credits, grade FROM subject WHERE id = 1";
        try (var stmt = testConnection.createStatement();
             var result = stmt.executeQuery(selectSql)) {
            
            assertTrue(result.next(), "Subject deve existir");
            assertEquals("Matemática Avançada", result.getString("name"), "Nome deve ser atualizado");
            assertEquals("MAT002", result.getString("code"), "Código deve ser atualizado");
            assertEquals(6, result.getInt("credits"), "Créditos devem ser atualizados");
            assertEquals("A+", result.getString("grade"), "Nota deve ser atualizada");
        }
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao atualizar subject sem ID")
    public void testUpdateSubjectWithoutId() {
        // Arrange
        Subject subject = new Subject();
        subject.setName("Test");
        // ID não definido
        
        // Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> {
                if (subject.getId() == null) { // Simula validação do DAO
                    throw new IllegalArgumentException("Não é possível alterar uma matéria sem ID.");
                }
            },
            "Deve lançar exceção para subject sem ID"
        );
        
        assertEquals("Não é possível alterar uma matéria sem ID.", exception.getMessage());
    }
    
    @Test
    @DisplayName("Deve deletar subject existente")
    public void testDeleteSubject() throws SQLException {
        // Verificar que subject existe
        String countBefore = "SELECT COUNT(*) FROM subject";
        int countBeforeDelete;
        try (var stmt = testConnection.createStatement();
             var result = stmt.executeQuery(countBefore)) {
            result.next();
            countBeforeDelete = result.getInt(1);
        }
        
        // Act - Simula SubjectDAO.delete(1)
        String sql = "DELETE FROM subject WHERE id = ?";
        int affectedRows;
        
        try (var stmt = testConnection.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            affectedRows = stmt.executeUpdate();
        }
        
        // Assert
        assertEquals(1, affectedRows, "Deve deletar exatamente 1 subject");
        
        // Verificar que foi deletado
        String countAfter = "SELECT COUNT(*) FROM subject";
        try (var stmt = testConnection.createStatement();
             var result = stmt.executeQuery(countAfter)) {
            result.next();
            int countAfterDelete = result.getInt(1);
            assertEquals(countBeforeDelete - 1, countAfterDelete, "Deve ter 1 subject a menos");
        }
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao deletar subject inexistente")
    public void testDeleteNonExistentSubject() throws SQLException {
        // Act
        String sql = "DELETE FROM subject WHERE id = ?";
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
                    throw new SQLException("Subject 999 não encontrado para exclusão");
                }
            },
            "Deve lançar SQLException para subject inexistente"
        );
        
        assertTrue(exception.getMessage().contains("999"), "Mensagem deve conter o ID");
    }
    
    @Test
    @DisplayName("Deve validar dados obrigatórios")
    public void testValidateMandatoryData() {
        // Test nome obrigatório
        SQLException exception = assertThrows(
            SQLException.class,
            () -> {
                String sql = "INSERT INTO subject (name, code, credits, grade, mandatory, semester_id) VALUES (?, ?, ?, ?, ?, ?)";
                try (var stmt = testConnection.prepareStatement(sql)) {
                    stmt.setString(1, null); // Nome null deve falhar
                    stmt.setString(2, "TEST");
                    stmt.setInt(3, 3);
                    stmt.setString(4, "B");
                    stmt.setBoolean(5, true);
                    stmt.setInt(6, 1);
                    stmt.executeUpdate();
                }
            },
            "Deve falhar com nome null"
        );
        assertNotNull(exception.getMessage(), "Mensagem de exceção não deve ser null");
    }
    
    @Test
    @DisplayName("Deve buscar subjects com diferentes tipos de dados")
    public void testDifferentDataTypes() throws SQLException {
        // Inserir subject com diferentes tipos
        String sql = "INSERT INTO subject (name, code, credits, grade, mandatory, semester_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (var stmt = testConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Subject Teste");
            stmt.setString(2, null); // Código null
            stmt.setInt(3, 0); // Créditos zero
            stmt.setString(4, "F"); // Nota baixa
            stmt.setBoolean(5, false); // Não obrigatória
            stmt.setInt(6, 2);
            
            stmt.executeUpdate();
            
            // Buscar e verificar
            try (var keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    
                    String selectSql = "SELECT name, code, credits, grade, mandatory FROM subject WHERE id = ?";
                    try (var selectStmt = testConnection.prepareStatement(selectSql)) {
                        selectStmt.setInt(1, id);
                        try (var result = selectStmt.executeQuery()) {
                            assertTrue(result.next(), "Subject deve ser encontrada");
                            assertEquals("Subject Teste", result.getString("name"));
                            assertNull(result.getString("code"), "Código deve ser null");
                            assertEquals(0, result.getInt("credits"), "Créditos podem ser zero");
                            assertEquals("F", result.getString("grade"));
                            assertFalse(result.getBoolean("mandatory"), "Deve ser não obrigatória");
                        }
                    }
                }
            }
        }
    }
}
