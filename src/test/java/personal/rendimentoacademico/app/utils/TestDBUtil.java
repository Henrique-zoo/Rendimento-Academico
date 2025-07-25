/*
 * Utilitário para testes de banco de dados
 * Gerencia conexões de teste com SQLite em memória
 * APENAS métodos utilitários - sem testes próprios
 */
package personal.rendimentoacademico.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitária para configuração e gerenciamento de banco de dados de teste
 * Fornece métodos estáticos para criar, configurar e limpar bancos SQLite em memória
 * Usada pelos testes dos DAOs para isolamento e consistência
 */
public class TestDBUtil {
    
    // Construtor privado - classe utilitária apenas com métodos estáticos
    private TestDBUtil() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada");
    }
    
    /**
     * Cria uma nova conexão de teste com SQLite em memória
     * @return Connection configurada para testes
     * @throws SQLException se não conseguir criar a conexão
     */
    public static Connection createTestConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite::memory:");
    }
    
    /**
     * Configura as tabelas necessárias para os testes
     * @param connection Conexão onde criar as tabelas
     * @throws SQLException se não conseguir criar as tabelas
     */
    public static void setupTestTables(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Criar tabela semester
            stmt.execute("""
                CREATE TABLE semester (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    number INTEGER NOT NULL,
                    subject_count INTEGER NOT NULL,
                    total_credits INTEGER NOT NULL
                )
            """);
            
            // Criar tabela subject
            stmt.execute("""
                CREATE TABLE subject (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    code TEXT,
                    credits INTEGER NOT NULL,
                    grade TEXT NOT NULL,
                    mandatory BOOLEAN NOT NULL,
                    semester_id INTEGER,
                    FOREIGN KEY (semester_id) REFERENCES semester(id)
                )
            """);
        }
    }
    
    /**
     * Insere dados de teste padrão nas tabelas
     * @param connection Conexão onde inserir os dados
     * @throws SQLException se não conseguir inserir os dados
     */
    public static void insertTestData(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Inserir semestres de teste
            stmt.execute("INSERT INTO semester (number, subject_count, total_credits) VALUES (1, 2, 7)");
            stmt.execute("INSERT INTO semester (number, subject_count, total_credits) VALUES (2, 1, 4)");
            stmt.execute("INSERT INTO semester (number, subject_count, total_credits) VALUES (3, 0, 0)");
            
            // Inserir subjects de teste
            stmt.execute("""
                INSERT INTO subject (name, code, credits, grade, mandatory, semester_id) 
                VALUES ('Matemática', 'MAT001', 4, 'A', 1, 1)
            """);
            
            stmt.execute("""
                INSERT INTO subject (name, code, credits, grade, mandatory, semester_id) 
                VALUES ('Física', 'FIS001', 3, 'B', 1, 1)
            """);
            
            stmt.execute("""
                INSERT INTO subject (name, code, credits, grade, mandatory, semester_id) 
                VALUES ('Química', 'QUI001', 4, 'A', 0, 2)
            """);
        }
    }
    
    /**
     * Configura um banco completo para testes (tabelas + dados)
     * @param connection Conexão para configurar
     * @throws SQLException se não conseguir configurar o banco
     */
    public static void setupCompleteTestDatabase(Connection connection) throws SQLException {
        setupTestTables(connection);
        insertTestData(connection);
    }
    
    /**
     * Limpa todas as tabelas do banco de teste
     * @param connection Conexão para limpar
     * @throws SQLException se não conseguir limpar o banco
     */
    public static void cleanTestDatabase(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM subject");
            stmt.execute("DELETE FROM semester");
            // Reset autoincrement
            stmt.execute("DELETE FROM sqlite_sequence WHERE name IN ('subject', 'semester')");
        }
    }
    
    /**
     * Fecha uma conexão de teste de forma segura
     * @param connection Conexão para fechar
     */
    public static void closeTestConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão de teste: " + e.getMessage());
            }
        }
    }
    
    /**
     * Verifica se uma conexão está válida e ativa
     * @param connection Conexão para verificar
     * @return true se a conexão está válida
     */
    public static boolean isConnectionValid(Connection connection) {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Conta o número de registros em uma tabela
     * @param connection Conexão para usar
     * @param tableName Nome da tabela
     * @return Número de registros
     * @throws SQLException se não conseguir contar
     */
    public static int countRecords(Connection connection, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             var result = stmt.executeQuery(sql)) {
            
            if (result.next()) {
                return result.getInt(1);
            }
            return 0;
        }
    }
}
