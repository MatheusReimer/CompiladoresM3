package compiladoresM2;

import gals.*;

import javax.swing.*;
import java.util.ArrayList;

public class Main {


    public static Semantico sem = new Semantico();
    public static Branches branch = new Branches();
    public static FunctionsAndProcedures func = new FunctionsAndProcedures();


    public static void getDotData(){
        for (Semantico.Simbolo element : sem.symbolTable) {

            // If this element is not present in newList
            // then add it
            if (!sem.assembly.dataArray.contains(element.name)) {
                sem.assembly.dataArray.add("    "+element.name+","+element.scope+","+element.size);
            }
        }
    }

    public static void createFrame(){
        mainFrame mf = new mainFrame();

    }
    public static void main(String[] args) {
        createFrame();

    }

    public static Object[][] fillTable(){
        Object[][] data = {};
        data = sem.fillTable();


        return data;
    }

    public static String warningOutput() {
        String warningOutput = sem.getWarningOutput();

        return warningOutput;
    }

    public static String getAssemblyString(){
        String assemblyString = sem.assembly.getAssemblyString(sem.assembly);

        return assemblyString;
    }


    public static String compile(String code){
        sem.assembly.dataArray.clear();
        sem.assembly.textArray.clear();
        sem.assembly.dataArray.add("    .data\n");
        sem.assembly.textArray.add("    .text\n");
        sem.clearTable();
        Lexico lex = new Lexico();
        Sintatico sint = new Sintatico();
        String output = new String();
        sem.clearTable();
        lex.setInput(code);

        try {
            Token token = new Token(-2,"Program Initialized",-1);
            sem.executeAction(-2,token);
            sint.parse(lex,sem);
            //output += "Compilado com sucesso, tabela de tamanho "+ sem.symbolTable.size() + "\n";
            Token token2 = new Token(-1,"Program Terminated",-1);

            sem.executeAction(-1,token2);
            sem.printTable();


        } catch (LexicalError e) {
            output += e.getMessage();
            e.printStackTrace();
        } catch (SyntaticError e) {
            output += e.getMessage();
            e.printStackTrace();
        } catch (SemanticError e) {
            output += e.getMessage();
            e.printStackTrace();



        }
        sem.assembly.textArray.add("        HLT  " +"0"+"\n");
        getDotData();
        sem.assembly.printAssemblyDataTable(sem.assembly);
        sem.assembly.printAssemblyTextTable(sem.assembly);

        return output;
    }
}