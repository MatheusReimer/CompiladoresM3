package compiladoresM2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class mainFrame extends JFrame {
    private JTextArea taInput;
    private JButton compileButton;
    private JTextArea tfOutput;
    public JPanel mainPanel;
    private JTable tSymbol;
    private JTextArea tAssembly;
    private JButton assemblyButton;


    String code = "";

    public mainFrame() {

        setContentPane(mainPanel);
        setTitle("Compile");
        setSize(1800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        try {
            BufferedReader reader = new BufferedReader(new FileReader("savedInput.txt"));

            List<String> stringSaved = new ArrayList<>();

            String line;
            while((line=reader.readLine()) != null){
                line = line + "  \n";
                stringSaved.add(line);
            }
            for(String string : stringSaved){
                code = code + string;
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        taInput.append(code);
        compileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfOutput.setText("");
                String warningOutput = new String();
                code = taInput.getText();
                String output= Main.compile(code);
                Object[][] data = Main.fillTable();
                warningOutput = Main.warningOutput();
                if(warningOutput.contains("O resultado desta operacao") || warningOutput.contains("Uso de variavel nao inicializada")){
                    output += "Compilado com Warning!\n";
                    output += warningOutput;
                }
                else
                    output = "Compilado com sucesso!\n";
                tfOutput.setText(output);
                String assemblyString = Main.getAssemblyString();
                tAssembly.setText(assemblyString);
                createTable(data);
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("savedInput.txt"));
                    writer.write(code);
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        assemblyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String assemblyString = Main.getAssemblyString();
                    String path="assembly.asm";
                    File file = new File(path);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(assemblyString);
                    bw.close();
                    JOptionPane.showMessageDialog(null, "Arquivo gerado com sucesso");
                }
                catch(Exception a){
                    System.out.println(a);
                }
            }
        });
    }


    private void createTable(Object[][] data){
        tSymbol.setModel(new DefaultTableModel(
                data,
                new String[]{"Nome","Tipo", "Func", "Vet", "Inic", "Param", "Pos", "Ref", "Escopo", "Usado"}
        ));
    }


}