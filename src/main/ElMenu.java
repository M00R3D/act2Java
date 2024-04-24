package main;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class ElMenu extends JFrame {

    public static String Text = null;
    public static Map<String, Integer> tokenCounts = new HashMap<>();
    public static Map<String, Integer> initialCodes = new HashMap<>();
    public static int tokenCounter = 1;
    public static int identifierCounter = 401; // Iniciar contador de identificadores en 401
    public static int constantCounter = 600; // Iniciar contador de constantes en 600
    public static DefaultTableModel identifierTableModel = new DefaultTableModel();
    public static DefaultTableModel constantTableModel = new DefaultTableModel();

    public ElMenu() {
        setTitle("Escáner DML");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JButton sendButton = new JButton("Analizar");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Text = textArea.getText();
                processAnalysis(Text);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private static void processAnalysis(String inputText) {
        DefaultTableModel tokenTableModel = new DefaultTableModel();
        tokenTableModel.addColumn("No.");
        tokenTableModel.addColumn("Línea");
        tokenTableModel.addColumn("TOKEN");
        tokenTableModel.addColumn("Tipo");
        tokenTableModel.addColumn("Código");

        identifierTableModel = new DefaultTableModel();
        identifierTableModel.addColumn("Identificador");
        identifierTableModel.addColumn("Valor");
        identifierTableModel.addColumn("Línea");

        constantTableModel = new DefaultTableModel();
        constantTableModel.addColumn("No.");
        constantTableModel.addColumn("Constante");
        constantTableModel.addColumn("Tipo");
        constantTableModel.addColumn("Valor");

        String[] lines = inputText.split("\n");
        int lineCount = 1;

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                String[] tokens = line.split("\\b|(?<=[,.='])|(?=[,.='])");
                for (String token : tokens) {
                    if (!token.isEmpty()) { // Avoid adding empty tokens
                        String[] info = getTokenInfo(token);
                        if (!info[0].equals("Inválido")) {
                            int code = getCode(token, info[0]);
                            tokenTableModel.addRow(new Object[]{tokenCounter++, lineCount, token, info[0], code});
                            if (info[0].equals("Identificador")) {
                                identifierTableModel.addRow(new Object[]{token, identifierCounter++, lineCount}); // Asignar valor de identificador y aumentar contador
                            } else if (info[0].equals("Constante")) {
                                if (token.matches("[a-zA-Z]+")) {
                                    constantTableModel.addRow(new Object[]{constantCounter++, token, "String", code});
                                } else if (token.matches("\\d+")) {
                                    constantTableModel.addRow(new Object[]{constantCounter++, token, "Numérico", code});
                                }
                            }
                        }
                    }
                }
                lineCount++;
            }
        }

        JTable tokenTable = new JTable(tokenTableModel);
        JTable identifierTable = new JTable(identifierTableModel);
        JTable constantTable = new JTable(constantTableModel);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tokens", new JScrollPane(tokenTable));
        tabbedPane.addTab("Identificadores", new JScrollPane(identifierTable));
        tabbedPane.addTab("Constantes", new JScrollPane(constantTable));

        JOptionPane.showMessageDialog(null, tabbedPane);
    }

    private static String[] getTokenInfo(String token) {
        switch (token) {
            case "SELECT":
            case "FROM":
            case "WHERE":
            case "AND":
                return new String[]{"Palabras Reservadas", "1"};
            case "ANOMBRE":
            case "CALIFICACION":
            case "TURNO":
            case "ALUMNOS":
            case "INSCRITOS":
            case "MATERIAS":
            case "CARRERAS":
            case "MNOMBRE":
            case "CNOMBRE":
            case "SEMESTRE":
                return new String[]{"Identificador", "4"};
            case ",":
            case "=":
                return new String[]{"Operador", "5"};
            case "'":
                return new String[]{"Delimitadores", "54"};
            case ">=":
            case "<=":
                return new String[]{"Relacionales", "84"};
            case "70":
                return new String[]{"Constante", "604"};
            case "LENAUT2":
            case "TM":
            case "ISC":
            case "2023I":
                return new String[]{"Constante", "61"}; // Tipo 61 para constantes string
            default:
                if (token.matches("\\b\\w+\\b")) {
                    return new String[]{"Palabra", "99"};
                } else {
                    return new String[]{"Inválido", "-1"};
                }
        }
    }

    private static int getCode(String token, String type) {
        if (!tokenCounts.containsKey(token)) {
            initialCodes.put(token, tokenCounter);
            tokenCounts.put(token, 0);
        }
        return initialCodes.get(token) + tokenCounts.merge(token, 1, Integer::sum) - 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ElMenu main = new ElMenu();
            }
        });
    }
}
