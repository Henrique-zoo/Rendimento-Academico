/*
 * Testes robustos para a classe Semester
 * Testa validações, comportamentos e regras de negócio
 */
package personal.rendimentoacademico.app.model;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para a classe Semester
 * Foca em validações, comportamentos e regras de negócio
 */
public class SemesterTest {
    
    private Semester semester;
    
    @BeforeEach
    public void setUp() {
        semester = new Semester();
    }

    @Test
    @DisplayName("Deve criar semester com valores padrão")
    public void testDefaultConstructor() {
        Semester newSemester = new Semester();
        
        assertNull(newSemester.getId(), "ID deve ser null por padrão");
        assertEquals(0, newSemester.getNumber(), "Number deve ser 0 por padrão");
        assertEquals(0, newSemester.getSubjectCount(), "Subject count deve ser 0 por padrão");
        assertEquals(0, newSemester.getTotalCredits(), "Total credits deve ser 0 por padrão");
        assertNull(newSemester.getSubjects(), "Subjects deve ser null por padrão");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar ID corretamente")
    public void testIdGetterSetter() {
        assertNull(semester.getId(), "ID inicial deve ser null");
        
        semester.setId(1);
        assertEquals(1, semester.getId(), "ID deve ser definido corretamente");
        
        semester.setId(999);
        assertEquals(999, semester.getId(), "ID deve ser atualizado corretamente");
        
        semester.setId(null);
        assertNull(semester.getId(), "ID deve aceitar null");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar número do semestre corretamente")
    public void testNumberGetterSetter() {
        assertEquals(0, semester.getNumber(), "Number inicial deve ser 0");
        
        semester.setNumber(1);
        assertEquals(1, semester.getNumber(), "Number deve ser definido corretamente");
        
        semester.setNumber(8);
        assertEquals(8, semester.getNumber(), "Deve aceitar semestres altos");
        
        semester.setNumber(0);
        assertEquals(0, semester.getNumber(), "Deve aceitar número 0");
        
        semester.setNumber(-1);
        assertEquals(-1, semester.getNumber(), "Deve aceitar números negativos (para validação posterior)");
        
        // Teste com números típicos de semestre
        for (int i = 1; i <= 10; i++) {
            semester.setNumber(i);
            assertEquals(i, semester.getNumber(), "Deve aceitar semestre " + i);
        }
    }
    
    @Test
    @DisplayName("Deve definir e recuperar contagem de subjects corretamente")
    public void testSubjectCountGetterSetter() {
        assertEquals(0, semester.getSubjectCount(), "Subject count inicial deve ser 0");
        
        semester.setSubjectCount(5);
        assertEquals(5, semester.getSubjectCount(), "Subject count deve ser definido corretamente");
        
        semester.setSubjectCount(1);
        assertEquals(1, semester.getSubjectCount(), "Deve aceitar 1 subject");
        
        semester.setSubjectCount(15);
        assertEquals(15, semester.getSubjectCount(), "Deve aceitar muitas subjects");
        
        semester.setSubjectCount(0);
        assertEquals(0, semester.getSubjectCount(), "Deve aceitar 0 subjects");
        
        semester.setSubjectCount(-1);
        assertEquals(-1, semester.getSubjectCount(), "Deve aceitar count negativo (para validação posterior)");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar total de créditos corretamente")
    public void testTotalCreditsGetterSetter() {
        assertEquals(0, semester.getTotalCredits(), "Total credits inicial deve ser 0");
        
        semester.setTotalCredits(20);
        assertEquals(20, semester.getTotalCredits(), "Total credits deve ser definido corretamente");
        
        semester.setTotalCredits(1);
        assertEquals(1, semester.getTotalCredits(), "Deve aceitar 1 crédito");
        
        semester.setTotalCredits(50);
        assertEquals(50, semester.getTotalCredits(), "Deve aceitar muitos créditos");
        
        semester.setTotalCredits(0);
        assertEquals(0, semester.getTotalCredits(), "Deve aceitar 0 créditos");
        
        semester.setTotalCredits(-5);
        assertEquals(-5, semester.getTotalCredits(), "Deve aceitar créditos negativos (para validação posterior)");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar lista de subjects corretamente")
    public void testSubjectsGetterSetter() {
        assertNull(semester.getSubjects(), "Subjects inicial deve ser null");
        
        java.util.List<Subject> subjects = new java.util.ArrayList<>();
        semester.setSubjects(subjects);
        assertEquals(subjects, semester.getSubjects(), "Lista deve ser definida corretamente");
        
        // Teste com lista preenchida
        Subject subject1 = new Subject();
        subject1.setName("Matemática");
        subjects.add(subject1);
        
        Subject subject2 = new Subject();
        subject2.setName("Física");
        subjects.add(subject2);
        
        semester.setSubjects(subjects);
        assertEquals(2, semester.getSubjects().size(), "Lista deve ter 2 subjects");
        assertTrue(semester.getSubjects().contains(subject1), "Deve conter subject1");
        assertTrue(semester.getSubjects().contains(subject2), "Deve conter subject2");
        
        semester.setSubjects(null);
        assertNull(semester.getSubjects(), "Deve aceitar lista null");
    }
    
    @Test
    @DisplayName("Deve manter consistência após múltiplas alterações")
    public void testMultipleChanges() {
        // Configurar semester completo
        semester.setId(1);
        semester.setNumber(3);
        semester.setSubjectCount(6);
        semester.setTotalCredits(24);
        
        java.util.List<Subject> subjects = new java.util.ArrayList<>();
        Subject subject = new Subject();
        subject.setName("Algoritmos");
        subjects.add(subject);
        semester.setSubjects(subjects);
        
        // Verificar todos os valores
        assertEquals(1, semester.getId());
        assertEquals(3, semester.getNumber());
        assertEquals(6, semester.getSubjectCount());
        assertEquals(24, semester.getTotalCredits());
        assertNotNull(semester.getSubjects());
        assertEquals(1, semester.getSubjects().size());
        
        // Alterar alguns valores
        semester.setSubjectCount(7);
        semester.setTotalCredits(28);
        
        // Verificar que apenas os alterados mudaram
        assertEquals(1, semester.getId(), "ID não deve ter mudado");
        assertEquals(3, semester.getNumber(), "Number não deve ter mudado");
        assertEquals(7, semester.getSubjectCount(), "Subject count deve ter mudado");
        assertEquals(28, semester.getTotalCredits(), "Total credits deve ter mudado");
        assertNotNull(semester.getSubjects(), "Subjects não deve ter mudado");
    }
    
    @Test
    @DisplayName("Deve testar valores típicos de semestre")
    public void testTypicalSemesterValues() {
        // Semestre inicial típico
        semester.setNumber(1);
        semester.setSubjectCount(5);
        semester.setTotalCredits(20);
        
        assertTrue(semester.getNumber() > 0, "Número deve ser positivo");
        assertTrue(semester.getSubjectCount() >= 0, "Count deve ser não negativo");
        assertTrue(semester.getTotalCredits() >= 0, "Créditos devem ser não negativos");
        
        // Semestre médio típico
        semester.setNumber(4);
        semester.setSubjectCount(6);
        semester.setTotalCredits(24);
        
        assertTrue(semester.getNumber() <= 8, "Número geralmente até 8");
        assertTrue(semester.getSubjectCount() <= 10, "Count geralmente até 10");
        assertTrue(semester.getTotalCredits() <= 30, "Créditos geralmente até 30");
        
        // Semestre final típico
        semester.setNumber(8);
        semester.setSubjectCount(4);
        semester.setTotalCredits(16);
        
        assertEquals(8, semester.getNumber(), "Último semestre");
        assertTrue(semester.getSubjectCount() > 0, "Deve ter subjects");
        assertTrue(semester.getTotalCredits() > 0, "Deve ter créditos");
    }
    
    @Test
    @DisplayName("Deve testar coerência entre contagem e lista de subjects")
    public void testSubjectCountVsListConsistency() {
        java.util.List<Subject> subjects = new java.util.ArrayList<>();
        
        // Lista vazia
        semester.setSubjects(subjects);
        semester.setSubjectCount(0);
        assertTrue(semester.getSubjects().isEmpty(), "Lista deve estar vazia");
        assertEquals(0, semester.getSubjectCount(), "Count deve ser 0");
        
        // Adicionar subjects à lista
        Subject subject1 = new Subject();
        subject1.setName("Matemática");
        subject1.setCredits(4);
        subjects.add(subject1);
        
        Subject subject2 = new Subject();
        subject2.setName("Física");
        subject2.setCredits(3);
        subjects.add(subject2);
        
        semester.setSubjects(subjects);
        semester.setSubjectCount(2);
        
        assertEquals(2, semester.getSubjects().size(), "Lista deve ter 2 items");
        assertEquals(2, semester.getSubjectCount(), "Count deve ser 2");
        
        // Teste de inconsistência intencional (para detectar em validações)
        semester.setSubjectCount(5); // Diferente do tamanho real da lista
        assertNotEquals(semester.getSubjects().size(), semester.getSubjectCount(), 
                       "Inconsistência deve ser detectável");
    }
    
    @Test
    @DisplayName("Deve testar coerência entre total de créditos e subjects")
    public void testTotalCreditsVsSubjectsConsistency() {
        java.util.List<Subject> subjects = new java.util.ArrayList<>();
        
        Subject subject1 = new Subject();
        subject1.setName("Matemática");
        subject1.setCredits(4);
        subjects.add(subject1);
        
        Subject subject2 = new Subject();
        subject2.setName("Física");
        subject2.setCredits(3);
        subjects.add(subject2);
        
        Subject subject3 = new Subject();
        subject3.setName("Química");
        subject3.setCredits(2);
        subjects.add(subject3);
        
        semester.setSubjects(subjects);
        
        // Calcular total esperado
        int expectedTotal = subjects.stream().mapToInt(Subject::getCredits).sum();
        assertEquals(9, expectedTotal, "Total calculado deve ser 9");
        
        // Definir total correto
        semester.setTotalCredits(9);
        assertEquals(9, semester.getTotalCredits(), "Total deve estar correto");
        
        // Teste de inconsistência intencional
        semester.setTotalCredits(15); // Diferente do total real
        assertNotEquals(expectedTotal, semester.getTotalCredits(), 
                       "Inconsistência de créditos deve ser detectável");
    }
    
    @Test
    @DisplayName("Deve validar tipos primitivos vs wrapper")
    public void testPrimitiveVsWrapper() {
        // Integer (wrapper) - pode ser null
        semester.setId(null);
        assertNull(semester.getId(), "ID pode ser null");
        
        // int (primitivos) - não podem ser null, têm valor padrão
        assertEquals(0, semester.getNumber(), "Number deve ter valor padrão 0");
        assertEquals(0, semester.getSubjectCount(), "Subject count deve ter valor padrão 0");
        assertEquals(0, semester.getTotalCredits(), "Total credits deve ter valor padrão 0");
    }
    
    @Test
    @DisplayName("Deve simular comportamentos de validação")
    public void testValidationBehaviors() {
        // Simular validações que poderiam existir na aplicação
        
        // Validação de número de semestre válido
        semester.setNumber(0);
        assertTrue(semester.getNumber() <= 0, "Semestre inválido deve ser detectável");
        
        semester.setNumber(15);
        assertTrue(semester.getNumber() > 10, "Semestre muito alto deve ser detectável");
        
        // Validação de counts negativos
        semester.setSubjectCount(-5);
        assertTrue(semester.getSubjectCount() < 0, "Count negativo deve ser detectável");
        
        semester.setTotalCredits(-10);
        assertTrue(semester.getTotalCredits() < 0, "Créditos negativos devem ser detectáveis");
        
        // Validação de consistência
        semester.setSubjectCount(5);
        semester.setTotalCredits(0);
        assertTrue(semester.getSubjectCount() > 0 && semester.getTotalCredits() == 0, 
                  "Inconsistência subjects sem créditos deve ser detectável");
    }
    
    @Test
    @DisplayName("Deve testar cenários de uso real")
    public void testRealWorldScenarios() {
        // Cenário 1: Primeiro semestre típico
        semester.setNumber(1);
        semester.setSubjectCount(5);
        semester.setTotalCredits(20);
        
        java.util.List<Subject> subjects = new java.util.ArrayList<>();
        
        Subject calculo = new Subject();
        calculo.setName("Cálculo I");
        calculo.setCredits(6);
        calculo.setMandatory(true);
        subjects.add(calculo);
        
        Subject algoritmos = new Subject();
        algoritmos.setName("Algoritmos");
        algoritmos.setCredits(4);
        algoritmos.setMandatory(true);
        subjects.add(algoritmos);
        
        semester.setSubjects(subjects);
        
        assertEquals(1, semester.getNumber(), "Primeiro semestre");
        assertTrue(semester.getSubjectCount() > 0, "Tem subjects");
        assertTrue(semester.getTotalCredits() > 15, "Carga horária adequada");
        assertNotNull(semester.getSubjects(), "Lista de subjects definida");
        
        // Cenário 2: Semestre vazio (trancamento)
        Semester semesterVazio = new Semester();
        semesterVazio.setNumber(5);
        semesterVazio.setSubjectCount(0);
        semesterVazio.setTotalCredits(0);
        semesterVazio.setSubjects(new java.util.ArrayList<>());
        
        assertEquals(0, semesterVazio.getSubjectCount(), "Semestre trancado");
        assertEquals(0, semesterVazio.getTotalCredits(), "Sem créditos");
        assertTrue(semesterVazio.getSubjects().isEmpty(), "Lista vazia");
        
        // Cenário 3: Semestre final com poucas subjects
        Semester semesterFinal = new Semester();
        semesterFinal.setNumber(8);
        semesterFinal.setSubjectCount(3);
        semesterFinal.setTotalCredits(12);
        
        assertTrue(semesterFinal.getNumber() >= 7, "Semestre avançado");
        assertTrue(semesterFinal.getSubjectCount() < 5, "Poucas subjects restantes");
        assertTrue(semesterFinal.getTotalCredits() < 20, "Carga horária menor");
    }
    
    @Test
    @DisplayName("Deve lidar com operações em lista de subjects")
    public void testSubjectsListOperations() {
        java.util.List<Subject> subjects = new java.util.ArrayList<>();
        semester.setSubjects(subjects);
        
        // Lista inicialmente vazia
        assertTrue(semester.getSubjects().isEmpty(), "Lista deve estar vazia");
        
        // Adicionar subject
        Subject subject = new Subject();
        subject.setName("Matemática");
        semester.getSubjects().add(subject);
        
        assertEquals(1, semester.getSubjects().size(), "Lista deve ter 1 subject");
        assertEquals("Matemática", semester.getSubjects().get(0).getName(), "Subject deve ser recuperada");
        
        // Remover subject
        semester.getSubjects().remove(subject);
        assertTrue(semester.getSubjects().isEmpty(), "Lista deve estar vazia novamente");
        
        // Teste com múltiplas subjects
        Subject[] subjectsArray = {
            createSubject("Física", 4),
            createSubject("Química", 3),
            createSubject("Biologia", 2)
        };
        
        semester.getSubjects().addAll(Arrays.asList(subjectsArray));
        
        assertEquals(3, semester.getSubjects().size(), "Lista deve ter 3 subjects");
        
        // Verificar se todas estão presentes
        for (Subject s : subjectsArray) {
            assertTrue(semester.getSubjects().contains(s), "Lista deve conter " + s.getName());
        }
    }
    
    // Método utilitário para criar subjects nos testes
    private Subject createSubject(String name, int credits) {
        Subject subject = new Subject();
        subject.setName(name);
        subject.setCredits(credits);
        return subject;
    }
}
