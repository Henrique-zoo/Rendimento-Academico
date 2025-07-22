/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package personal.rendimentoacademico.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import personal.rendimentoacademico.app.models.Subject;
import personal.rendimentoacademico.app.models.Semester;

/**
 *
 * @author henri
 */
public class RendimentoAcademico {
    /*private static final String FILE_PATH = "semestres.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Semester> semestres = lerArquivo();

        boolean executando = true;
        while (executando) {
            exibirMenu(semestres);
            int op = scanner.nextInt();
            scanner.nextLine();

            if (semestres.isEmpty() && op > 2)
                op = -1;
            
            switch (op) {
                case 1 -> {
                    if (semestres.isEmpty())
                        adicionarSemestre(semestres, scanner);
                    else
                        exibirSemestre(semestres);
                }
                case 2 -> {
                    if (semestres.isEmpty())
                        executando = false;
                    else
                        adicionarSemestre(semestres, scanner);
                }
                case 3 -> {
                    float ira = calculaIRA(semestres);
                    float mp = calculaMP(semestres);     
                    System.out.printf("IRA: %.4f\nMP: %.4f\n", ira, mp);
                }
                case 4 -> executando = false;
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
        scanner.close();
    }

    private static void exibirMenu(List<Semester> semestres) {
        if (semestres.isEmpty()) {
            System.out.printf("%s\n%s\n%s\n",
            "Escolha uma opção:",
            "\t1. Adicionar semestre",
            "\t2. Sair");
        } else {
            System.out.printf("%s\n%s\n%s\n%s\n%s\n",
            "Escolha uma opção:",
            "\t1. Exibir semestres",
            "\t2. Adicionar semestre",
            "\t3. Calcular índices acadêmicos",
            "\t4. Sair");
        }
    }

    private static List<Semester> lerArquivo() {
        List<String> linhas = new ArrayList<>();
        List<Semester> semestres = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String linha;
            StringBuilder builder = new StringBuilder();
            while ((linha = reader.readLine()) != null) {
                for (char ch : linha.toCharArray()) {
                    if (ch == ';') {
                        linhas.add(builder.toString());
                        builder.setLength(0);
                    } else {
                        builder.append(ch);
                    }
                }
            }
            if (builder.length() > 0) {
                linhas.add(builder.toString());
            } else {
                return semestres;
            }

            for (String conteudo : linhas) {
                List<Subject> subjects = new ArrayList<>();
                String[] partes = conteudo.split("\\.");
                int numeroSemestre = Integer.parseInt(partes[0]);
                int qtdSubjects = Integer.parseInt(partes[1]);
                int creditsTotais = Integer.parseInt(partes[2]);
                String[] subjectsInfo = partes[3].split(",");

                for (String texto : subjectsInfo) {
                    String[] subjectInfo = texto.split("-");
                    String name = subjectInfo[0];
                    int credits = Integer.parseInt(subjectInfo[1]);
                    String grade = subjectInfo[2];
                    boolean mandatory = Boolean.parseBoolean(subjectInfo[3]);
                    Subject subject = new Subject(null, name, null, credits, grade, mandatory);
                    subjects.add(subject);
                }

                Semester semestre = new Semester(numeroSemestre, qtdSubjects, creditsTotais, subjects);
                semestres.add(semestre);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        
        return semestres;
    }

    private static void exibirSemestre(List<Semester> semestres) {
        if (semestres.isEmpty())
            System.out.printf("\nVocê não cadastrou nenhum semestre aqui!");
        else
            for (Semester semestre : semestres) {
                System.out.printf("%dº semestre - %d matérias (%d credits):\n", semestre.getNumero(), semestre.getQtdDeSubjects(), semestre.getCredits());
                System.out.println("-".repeat(85));
                System.out.printf("%-40s%s%s%s\n",
                    "Nome",
                    centralizar("Credits", 10),
                    centralizar("Menção", 10),
                    centralizar("Obrigatória/Optativa", 25)
                );
                System.out.println("-".repeat(85));
                for (Subject subject : semestre.getSubjects()) {
                    System.out.printf("%-40s%s%s%s\n",
                        subject.getNome(),
                        centralizar(String.valueOf(subject.getCredits()), 10),
                        centralizar(subject.getGrade(), 10),
                        centralizar(subject.isMandatory() ? "Nao" : "Sim", 25)
                    );
                }
                System.out.println("-".repeat(85));
            }
    }

    private static String centralizar(String texto, int larguraCampo) {
        if (texto.length() >= larguraCampo)
            return texto.substring(0, larguraCampo);

        int espacosEsquerda = (larguraCampo - texto.length()) / 2;
        int espacosDireita = larguraCampo - texto.length() - espacosEsquerda;

        return " ".repeat(espacosEsquerda) + texto + " ".repeat(espacosDireita);
    }

    private static float calculaIRA(List<Semester> semestres) {
        float divisor = 0, dividendo = 0;
        for (Semester semestre : semestres) {
            int se = Math.min(semestre.getNumero(), 6);

            for (Subject subject : semestre.getSubjects()) {
                float cr = subject.getCredits() / 15;
                int eqGrade = converterGrade(subject.getGrade());
                dividendo += eqGrade * cr * se;
                divisor += cr * se;
            }
        }
        return dividendo/divisor;
    }

    private static float calculaMP(List<Semester> semestres) {
        float divisor = 0, dividendo = 0;
        for (Semester semestre : semestres) {
            for (Subject subject : semestre.getSubjects()) {
                if (!subject.isMandatory()) {
                    float creditos = subject.getCredits() / 15;
                    dividendo += creditos * converterGrade(subject.getGrade());
                    divisor += creditos;
                }
            }
        }
        return dividendo/divisor;
        
    }

    private static List<Semester> adicionarSemestre(List<Semester> semestres, Scanner scanner) {
        List<Subject> subjects = new ArrayList<>();
        int creditsTotais = 0;
        System.out.println("Esse semestre conta como qual pelo SIGAA? (matérias de verão contam como do semestre anterior)");
        int numero = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Quantas matérias você fez nesse semestre?");
        int qtdSubjects = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < qtdSubjects; i++) {
            System.out.println("Qual o name da matéria?");
            String name = scanner.nextLine();

            System.out.println("Quantas credits a matéria " + name + " possui?");
            int credits = scanner.nextInt();
            creditsTotais += credits;
            scanner.nextLine();

            System.out.println("Qual a menção que você recebeu em " + name + "?");
            String grade = scanner.nextLine();

            System.out.println(name + " é uma matéria mandatory? (true para mandatory, false para obrigatória)");
            boolean mandatory = scanner.nextBoolean();
            scanner.nextLine();

            Subject subject = new Subject(name, credits, grade, mandatory);
            subjects.add(subject);
        }
        Semester semestre = new Semester(numero, qtdSubjects, creditsTotais, subjects);
        semestres.add(semestre);
        salvarSemestresNoArquivo(semestres);
        return semestres;
    }
    
    private static void salvarSemestresNoArquivo(List<Semester> semestres) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Semester semestre : semestres) {
                writer.write(semestre.getNumero() + '.' + semestre.getQtdDeSubjects() + '.' + semestre.getCredits() + ".\n");
                int i = 0;
                for (Subject subject : semestre.getSubjects()) {
                    i++;
                    writer.write(subject.getNome() + '-' + subject.getCredits() + '-' + subject.getGrade() + '-' + subject.isMandatory());
                    if (i < semestre.getQtdDeSubjects())
                        writer.write(",\n");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    private static int converterGrade(String grade) {
        Map<String, Integer> gradePesoMap = new HashMap<>();
        gradePesoMap.put("SS", 5);
        gradePesoMap.put("MS", 4);
        gradePesoMap.put("MM", 3);
        gradePesoMap.put("MI", 2);
        gradePesoMap.put("II", 1);
        gradePesoMap.put("SR", 0);
        return gradePesoMap.getOrDefault(grade, 0);
    }

    /*private static List<Semestre> atualizarSemestres(List<Semestre> semestres, Scanner scanner) {
        System.out.printf("Qual semestre você quer atualizar?\n");
        int numeroSemestre = scanner.nextInt();
        scanner.nextLine();
        Semester semestreAtualizado;

        for (Semester semestre : semestres) {
            if (semestre.getNumero() == numeroSemestre) {
                semestreAtualizado = semestre;
            }
        }
        System.out.printf("Qual atributo do %dº semestre você quer atualizar?\n\t%s\n\t%s\n",
        numeroSemestre,
        "1. Quantidade de matérias",
        "2. Número do Semester");
        int opcao = scanner.nextInt();
        scanner.nextLine();
        switch (opcao) {
            case 1:
                System.out.printf("Quantas matérias você fez no %dº semestre?\n", numeroSemestre);
                int qtdSubjects = scanner.nextInt();
                scanner.nextLine();
                
                semestreAtualizado.setQtdDeSubjects(qtdSubjects);
                break;
        
            default:
                break;
        }


        return semestres;
    }*/
}