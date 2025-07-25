/*
 * Testes robustos para a classe Subject
 * Testa validações, comportamentos e regras de negócio
 */
package personal.rendimentoacademico.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe Subject
 * Foca em validações, comportamentos e regras de negócio
 */
public class SubjectTest {
    
    private Subject subject;
    
    @BeforeEach
    public void setUp() {
        subject = new Subject();
    }

    @Test
    @DisplayName("Deve criar subject com valores padrão")
    public void testDefaultConstructor() {
        Subject newSubject = new Subject();
        
        assertNull(newSubject.getId(), "ID deve ser null por padrão");
        assertNull(newSubject.getName(), "Nome deve ser null por padrão");
        assertNull(newSubject.getCode(), "Código deve ser null por padrão");
        assertEquals(0, newSubject.getCredits(), "Créditos devem ser 0 por padrão");
        assertNull(newSubject.getGrade(), "Nota deve ser null por padrão");
        assertFalse(newSubject.isMandatory(), "Deve ser não obrigatória por padrão");
        assertNull(newSubject.getSemesterId(), "Semester ID deve ser null por padrão");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar ID corretamente")
    public void testIdGetterSetter() {
        assertNull(subject.getId(), "ID inicial deve ser null");
        
        subject.setId(1);
        assertEquals(1, subject.getId(), "ID deve ser definido corretamente");
        
        subject.setId(999);
        assertEquals(999, subject.getId(), "ID deve ser atualizado corretamente");
        
        subject.setId(null);
        assertNull(subject.getId(), "ID deve aceitar null");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar nome corretamente")
    public void testNameGetterSetter() {
        assertNull(subject.getName(), "Nome inicial deve ser null");
        
        subject.setName("Matemática");
        assertEquals("Matemática", subject.getName(), "Nome deve ser definido corretamente");
        
        subject.setName("");
        assertEquals("", subject.getName(), "Nome deve aceitar string vazia");
        
        subject.setName(null);
        assertNull(subject.getName(), "Nome deve aceitar null");
        
        // Teste com nomes especiais
        subject.setName("Matemática Aplicada à Engenharia");
        assertEquals("Matemática Aplicada à Engenharia", subject.getName(), "Deve aceitar nomes com acentos e caracteres especiais");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar código corretamente")
    public void testCodeGetterSetter() {
        assertNull(subject.getCode(), "Código inicial deve ser null");
        
        subject.setCode("MAT001");
        assertEquals("MAT001", subject.getCode(), "Código deve ser definido corretamente");
        
        subject.setCode("");
        assertEquals("", subject.getCode(), "Código deve aceitar string vazia");
        
        subject.setCode(null);
        assertNull(subject.getCode(), "Código deve aceitar null");
        
        // Teste com diferentes formatos
        subject.setCode("FIS-101");
        assertEquals("FIS-101", subject.getCode(), "Deve aceitar códigos com hífen");
        
        subject.setCode("QUIM_001");
        assertEquals("QUIM_001", subject.getCode(), "Deve aceitar códigos com underscore");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar créditos corretamente")
    public void testCreditsGetterSetter() {
        assertEquals(0, subject.getCredits(), "Créditos iniciais devem ser 0");
        
        subject.setCredits(4);
        assertEquals(4, subject.getCredits(), "Créditos devem ser definidos corretamente");
        
        subject.setCredits(1);
        assertEquals(1, subject.getCredits(), "Deve aceitar 1 crédito");
        
        subject.setCredits(10);
        assertEquals(10, subject.getCredits(), "Deve aceitar muitos créditos");
        
        // Teste com valores limítrofes
        subject.setCredits(0);
        assertEquals(0, subject.getCredits(), "Deve aceitar 0 créditos");
        
        subject.setCredits(-1);
        assertEquals(-1, subject.getCredits(), "Deve aceitar créditos negativos (para validação posterior)");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar nota corretamente")
    public void testGradeGetterSetter() {
        assertNull(subject.getGrade(), "Nota inicial deve ser null");
        
        subject.setGrade("A");
        assertEquals("A", subject.getGrade(), "Nota deve ser definida corretamente");
        
        // Teste com diferentes tipos de nota
        String[] notas = {"A", "B", "C", "D", "F", "A+", "B-", "10", "7.5", "SS", "MS", "MI", "II"};
        
        for (String nota : notas) {
            subject.setGrade(nota);
            assertEquals(nota, subject.getGrade(), "Deve aceitar nota: " + nota);
        }
        
        subject.setGrade("");
        assertEquals("", subject.getGrade(), "Deve aceitar nota vazia");
        
        subject.setGrade(null);
        assertNull(subject.getGrade(), "Deve aceitar nota null");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar mandatory corretamente")
    public void testMandatoryGetterSetter() {
        assertFalse(subject.isMandatory(), "Deve ser não obrigatória por padrão");
        
        subject.setMandatory(true);
        assertTrue(subject.isMandatory(), "Deve ser definida como obrigatória");
        
        subject.setMandatory(false);
        assertFalse(subject.isMandatory(), "Deve ser definida como não obrigatória");
    }
    
    @Test
    @DisplayName("Deve definir e recuperar semester ID corretamente")
    public void testSemesterIdGetterSetter() {
        assertNull(subject.getSemesterId(), "Semester ID inicial deve ser null");
        
        subject.setSemesterId(1);
        assertEquals(1, subject.getSemesterId(), "Semester ID deve ser definido corretamente");
        
        subject.setSemesterId(8);
        assertEquals(8, subject.getSemesterId(), "Deve aceitar semestres altos");
        
        subject.setSemesterId(null);
        assertNull(subject.getSemesterId(), "Deve aceitar semester ID null");
        
        // Teste com valores extremos
        subject.setSemesterId(0);
        assertEquals(0, subject.getSemesterId(), "Deve aceitar semester ID 0");
        
        subject.setSemesterId(-1);
        assertEquals(-1, subject.getSemesterId(), "Deve aceitar semester ID negativo");
    }
    
    @Test
    @DisplayName("Deve manter consistência após múltiplas alterações")
    public void testMultipleChanges() {
        // Configurar subject completa
        subject.setId(1);
        subject.setName("Cálculo I");
        subject.setCode("MAT101");
        subject.setCredits(6);
        subject.setGrade("A");
        subject.setMandatory(true);
        subject.setSemesterId(1);
        
        // Verificar todos os valores
        assertEquals(1, subject.getId());
        assertEquals("Cálculo I", subject.getName());
        assertEquals("MAT101", subject.getCode());
        assertEquals(6, subject.getCredits());
        assertEquals("A", subject.getGrade());
        assertTrue(subject.isMandatory());
        assertEquals(1, subject.getSemesterId());
        
        // Alterar alguns valores
        subject.setGrade("B+");
        subject.setCredits(4);
        subject.setMandatory(false);
        
        // Verificar que apenas os alterados mudaram
        assertEquals(1, subject.getId(), "ID não deve ter mudado");
        assertEquals("Cálculo I", subject.getName(), "Nome não deve ter mudado");
        assertEquals("MAT101", subject.getCode(), "Código não deve ter mudado");
        assertEquals(4, subject.getCredits(), "Créditos devem ter mudado");
        assertEquals("B+", subject.getGrade(), "Nota deve ter mudado");
        assertFalse(subject.isMandatory(), "Mandatory deve ter mudado");
        assertEquals(1, subject.getSemesterId(), "Semester ID não deve ter mudado");
    }
    
    @Test
    @DisplayName("Deve lidar com strings especiais")
    public void testSpecialStrings() {
        // Teste com strings com espaços
        subject.setName("  Matemática  ");
        assertEquals("  Matemática  ", subject.getName(), "Deve preservar espaços");
        
        subject.setCode("  MAT001  ");
        assertEquals("  MAT001  ", subject.getCode(), "Deve preservar espaços no código");
        
        // Teste com caracteres especiais
        subject.setName("Física - Mecânica & Termodinâmica");
        assertEquals("Física - Mecânica & Termodinâmica", subject.getName(), "Deve aceitar caracteres especiais");
        
        subject.setGrade("A+ (Excelente)");
        assertEquals("A+ (Excelente)", subject.getGrade(), "Deve aceitar notas com descrição");
        
        // Teste com strings longas
        String nomeLongo = "Introdução à Programação Orientada a Objetos com Padrões de Projeto e Arquitetura de Software";
        subject.setName(nomeLongo);
        assertEquals(nomeLongo, subject.getName(), "Deve aceitar nomes longos");
    }
    
    @Test
    @DisplayName("Deve validar tipos primitivos vs wrapper")
    public void testPrimitiveVsWrapper() {
        // Integer (wrapper) - pode ser null
        subject.setId(null);
        assertNull(subject.getId(), "ID pode ser null");
        
        subject.setSemesterId(null);
        assertNull(subject.getSemesterId(), "Semester ID pode ser null");
        
        // int (primitivo) - não pode ser null, tem valor padrão
        assertEquals(0, subject.getCredits(), "Credits deve ter valor padrão 0");
        
        // boolean (primitivo) - não pode ser null, tem valor padrão
        assertFalse(subject.isMandatory(), "Mandatory deve ter valor padrão false");
    }
    
    @Test
    @DisplayName("Deve simular comportamentos de validação")
    public void testValidationBehaviors() {
        // Simular validações que poderiam existir na aplicação
        
        // Validação de créditos positivos
        subject.setCredits(-5);
        assertTrue(subject.getCredits() < 0, "Créditos negativos devem ser detectáveis");
        
        // Validação de nome não vazio
        subject.setName("");
        assertTrue(subject.getName().isEmpty(), "Nome vazio deve ser detectável");
        
        subject.setName("   ");
        assertTrue(subject.getName().trim().isEmpty(), "Nome só com espaços deve ser detectável");
        
        // Validação de nota válida
        subject.setGrade("X");
        assertEquals("X", subject.getGrade(), "Nota inválida deve ser detectável");
        
        // Validação de relacionamento
        subject.setSemesterId(-1);
        assertTrue(subject.getSemesterId() < 0, "Semester ID inválido deve ser detectável");
    }
    
    @Test
    @DisplayName("Deve testar cenários de uso real")
    public void testRealWorldScenarios() {
        // Cenário 1: Subject obrigatória com nota alta
        subject.setName("Algoritmos e Estruturas de Dados");
        subject.setCode("INF110");
        subject.setCredits(6);
        subject.setGrade("A");
        subject.setMandatory(true);
        subject.setSemesterId(2);
        
        assertTrue(subject.isMandatory(), "Subject obrigatória");
        assertEquals("A", subject.getGrade(), "Nota alta");
        assertTrue(subject.getCredits() > 4, "Muitos créditos");
        
        // Cenário 2: Subject optativa com código null
        Subject optativa = new Subject();
        optativa.setName("Tópicos Especiais em IA");
        optativa.setCode(null); // Sem código definido
        optativa.setCredits(2);
        optativa.setGrade("B+");
        optativa.setMandatory(false);
        optativa.setSemesterId(7);
        
        assertFalse(optativa.isMandatory(), "Subject optativa");
        assertNull(optativa.getCode(), "Sem código");
        assertTrue(optativa.getCredits() > 0, "Tem créditos");
        
        // Cenário 3: Subject reprovada
        Subject reprovada = new Subject();
        reprovada.setName("Cálculo Diferencial");
        reprovada.setCode("MAT201");
        reprovada.setCredits(4);
        reprovada.setGrade("F");
        reprovada.setMandatory(true);
        reprovada.setSemesterId(3);
        
        assertEquals("F", reprovada.getGrade(), "Nota de reprovação");
        assertTrue(reprovada.isMandatory(), "Subject obrigatória reprovada");
    }
}
