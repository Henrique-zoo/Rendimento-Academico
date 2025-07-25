/*
 * Testes unitários para a classe DBUtil
 * Testa funcionalidades de conexão e gerenciamento de banco de dados
 */
package personal.rendimentoacademico.app.utils;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para a classe DBUtil
 * Valida conexões, configurações e comportamentos da classe utilitária de banco
 */
public class DBUtilTest {
    
    private Connection testConnection;
    
    @BeforeEach
    public void setUp() throws SQLException {
        // Usar o utilitário de teste para configurar ambiente isolado
        testConnection = TestDBUtil.createTestConnection();
        TestDBUtil.setupCompleteTestDatabase(testConnection);
    }
    
    @AfterEach
    public void tearDown() {
        TestDBUtil.closeTestConnection(testConnection);
        testConnection = null;
    }

    @Test
    @DisplayName("Deve obter conexão válida do DBUtil")
    public void testGetConnection() throws SQLException {
        // Act
        Connection conn = DBUtil.getConnection();
        
        // Assert
        assertNotNull(conn, "Conexão não deve ser null");
        assertTrue(TestDBUtil.isConnectionValid(conn), "Conexão deve ser válida");
        
        // Cleanup
        DBUtil.close(conn);
    }
    
    @Test
    @DisplayName("Deve fechar conexão de forma segura")
    public void testCloseConnection() throws SQLException {
        // Arrange
        Connection conn = DBUtil.getConnection();
        assertTrue(TestDBUtil.isConnectionValid(conn), "Conexão deve estar ativa inicialmente");
        
        // Act
        DBUtil.close(conn);
        
        // Assert
        assertFalse(TestDBUtil.isConnectionValid(conn), "Conexão deve estar fechada");
    }
    
    @Test
    @DisplayName("Deve lidar com fechamento de conexão null")
    public void testCloseNullConnection() {
        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> {
            DBUtil.close(null);
        }, "Fechar conexão null não deve lançar exceção");
    }
    
    @Test
    @DisplayName("Deve lidar com fechamento de conexão já fechada")
    public void testCloseAlreadyClosedConnection() throws SQLException {
        // Arrange
        Connection conn = DBUtil.getConnection();
        conn.close(); // Fechar manualmente primeiro
        
        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> {
            DBUtil.close(conn);
        }, "Fechar conexão já fechada não deve lançar exceção");
    }
    
    @Test
    @DisplayName("Deve criar múltiplas conexões independentes")
    public void testMultipleConnections() throws SQLException {
        // Act
        Connection conn1 = DBUtil.getConnection();
        Connection conn2 = DBUtil.getConnection();
        
        // Assert
        assertNotNull(conn1, "Primeira conexão não deve ser null");
        assertNotNull(conn2, "Segunda conexão não deve ser null");
        assertNotSame(conn1, conn2, "Conexões devem ser instâncias diferentes");
        
        assertTrue(TestDBUtil.isConnectionValid(conn1), "Primeira conexão deve ser válida");
        assertTrue(TestDBUtil.isConnectionValid(conn2), "Segunda conexão deve ser válida");
        
        // Cleanup
        DBUtil.close(conn1);
        DBUtil.close(conn2);
    }
    
    @Test
    @DisplayName("Deve manter conexão funcional após operações")
    public void testConnectionAfterOperations() throws SQLException {
        // Arrange
        Connection conn = DBUtil.getConnection();
        
        // Act - realizar operações básicas
        try (var stmt = conn.createStatement()) {
            // Verificar se consegue executar queries
            var result = stmt.executeQuery("SELECT 1 as test");
            assertTrue(result.next(), "Query deve retornar resultado");
            assertEquals(1, result.getInt("test"), "Resultado deve ser 1");
        }
        
        // Assert
        assertTrue(TestDBUtil.isConnectionValid(conn), "Conexão deve permanecer válida após operações");
        
        // Cleanup
        DBUtil.close(conn);
    }
    
    @Test
    @DisplayName("Deve validar configuração do banco de dados")
    public void testDatabaseConfiguration() throws SQLException {
        // Arrange
        Connection conn = DBUtil.getConnection();
        
        // Act & Assert - verificar se consegue acessar metadados
        var metaData = conn.getMetaData();
        assertNotNull(metaData, "Metadados não devem ser null");
        
        String url = metaData.getURL();
        assertNotNull(url, "URL da conexão não deve ser null");
        assertTrue(url.toLowerCase().contains("sqlite"), "Deve usar SQLite como esperado");
        
        // Cleanup
        DBUtil.close(conn);
    }
    
    @Test
    @DisplayName("Deve inicializar banco de dados corretamente")
    public void testInitializeDatabase() {
        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> {
            DBUtil.initializeDatabase();
        }, "Inicialização do banco não deve lançar exceção");
    }
    
    @Test
    @DisplayName("Deve validar URL de conexão")
    public void testConnectionURL() throws SQLException {
        // Arrange
        Connection conn = DBUtil.getConnection();
        
        // Act
        String url = conn.getMetaData().getURL();
        
        // Assert
        assertTrue(url.contains("rendimento_academico.db"), "URL deve apontar para o banco correto");
        assertTrue(url.startsWith("jdbc:sqlite:"), "Deve usar protocolo SQLite");
        
        // Cleanup
        DBUtil.close(conn);
    }
    
    @Test
    @DisplayName("Deve permitir múltiplas inicializações")
    public void testMultipleInitializations() {
        // Act & Assert - múltiplas chamadas não devem causar erro
        assertDoesNotThrow(() -> {
            DBUtil.initializeDatabase();
            DBUtil.initializeDatabase();
            DBUtil.initializeDatabase();
        }, "Múltiplas inicializações não devem causar erro");
    }
}
