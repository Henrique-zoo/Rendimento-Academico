/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package personal.rendimentoacademico.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author henri
 */
public class DBUtil {
    private static final String URL = "jdbc:sqlite:database/rendimento_academico.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Métodos utilitários para fechar recursos (opcional)
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão! " + e.getMessage());
            }
        }
    }
    
    public static void initializeDatabase() {
        String sqlSemester = """
            CREATE TABLE IF NOT EXISTS semester (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                number INTEGER NOT NULL,
                subject_count INTEGER NOT NULL,
                total_credits INTEGER NOT NULL
            );
        """;
        
        String sqlSubject = """
            CREATE TABLE IF NOT EXISTS subject (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                code TEXT,
                credits INTEGER NOT NULL,
                grade TEXT NOT NULL,
                mandatory BOOLEAN NOT NULL,
                semester_id INTEGER,
                FOREIGN KEY (semester_id) REFERENCES semester(id)
            );
        """;
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()
        ) {
            stmt.execute(sqlSemester);
            stmt.execute(sqlSubject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}