package es.ugr.giw.p3.searcher;

import es.ugr.giw.p3.common.News;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.queryparser.classic.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Class that handle the GUI of the searcher, it allow the user to import an index, make queries over it and see the results
 *
 * @see {@link https://www.jetbrains.com/help/idea/2017.1/designing-gui-major-steps.html}
 */
public class GUI_Searcher {

    private static Logger LOGGER = Logger.getLogger(GUI_Searcher.class.getName());

    private JTextField searchField;
    private JButton searchButton;
    private JButton importIndexButton;
    private JList<String> listNews;
    private JPanel panel;
    private static Level logLvl = Level.INFO;
    private List<News> searchResults = null;


    public static void main(String[] args) {
        JFrame frame = new JFrame("Noticias94: Buscador de noticias de 1994");
        frame.setContentPane(new GUI_Searcher().panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 200));
        frame.setPreferredSize(new Dimension(800, 400));
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @see {@link http://stackoverflow.com/a/2469673/6441806}
     */
    public GUI_Searcher() {
        setupLogger();
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });

        importIndexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importIndex();
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    search();
                } else {
                    super.keyPressed(e);
                }
            }
        });
        listNews.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectNews();
            }
        });

        //Close the index when the program ends
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Searcher.closeIndex();
            }
        });
    }

    /**
     * Method called when the user select an item of the listNews
     */
    private void selectNews() {
        if (searchResults != null && searchResults.size() > 0) {
            int index = listNews.getSelectedIndex();
            News n = searchResults.get(index);
            LOGGER.info("Selecionada noticia: " + n.getTitle());

            GUI_News guiNews = new GUI_News(n);
        } else {
            LOGGER.info("Selecionado item de lista sin haber resultados de busqueda");
        }

    }

    /**
     * Method that handle the importation of an index directory called when the user clicks on the importIndexButton
     *
     * @see {@link https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html}
     */
    private void importIndex() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione el directorio del índice");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        int returnVal = fileChooser.showOpenDialog(fileChooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            LOGGER.info("Importando el índice " + file);
            if (!(file != null && file.exists() && file.isDirectory())) {
                LOGGER.warning("Se ha intentado importar un índice que no es un directorio");
                showMessageDialog(panel, "El fichero " + file + " no es un directorio válido para contener un índice.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Searcher.setIndexFile(file);

            //After import the index
            importIndexButton.setEnabled(false);

            LOGGER.info("Índice importado correctamente");

        } else {
            LOGGER.info("Importación de índice cancelada por el usuario");

        }
    }

    /**
     * Method that launch a search using the Searcher class an the input query in searchField. It is called when the user
     * clicks on searchButton or when it press ENTER with the focus in the searchField.
     *
     * @see {@link http://docs.oracle.com/javase/tutorial/uiswing/components/list.html}
     */
    private void search() {
        if (Searcher.getIndexFile() == null) {
            LOGGER.warning("Se ha intentado realizar una búsqueda sin cargar un índice");
            showMessageDialog(panel, "Es necesario importar antes un índice para realizar una búsqueda.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (searchField.getText().isEmpty()) {
            LOGGER.warning("Se ha intentado realizar una búsqueda sin introducir ningun caracter para la misma");
            showMessageDialog(panel, "Es necesario introducir una consulta para buscar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String query = searchField.getText();

        LOGGER.info("Lanzando una búsqueda para " + query);

        long tIni = 0, tFin = 0;
        try {
            tIni = System.currentTimeMillis();
            searchResults = Searcher.search(query);
            tFin = System.currentTimeMillis();
        } catch (IOException | ParseException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            String errorMsg = e.toString();

            // Format the error message
            if (errorMsg.length() > 80) {
                for (int i = 0; i < errorMsg.length(); i++) {
                    if (i > 0 && i % 80 == 0) {
                        errorMsg = errorMsg.substring(0, i) + "\n" + errorMsg.substring(i, errorMsg.length());
                    }
                }
            }
            showMessageDialog(panel, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);

            //The index directory is incorrect
            if (e instanceof IndexNotFoundException) {
                LOGGER.log(Level.SEVERE, "Directorio de índice incorrecto");
                showMessageDialog(panel, "El directorio seleccionado como índice no contiene un índice creado por el programa\n"
                        + "Indexer.jar por favor selecione un directorio correcto o genere el índice si no\nlo ha hecho ya.", "Info", JOptionPane.INFORMATION_MESSAGE);
                Searcher.closeIndex();
                Searcher.setIndexFile(null);
                importIndexButton.setEnabled(true);

            }
            return;
        }
        float searchTime = (float) (tFin - tIni) / 1000;
        LOGGER.info("Búsqueda realizada en " + searchTime + "s, recuperadas " + searchResults.size() + " noticias");

        DefaultListModel<String> listModel = new DefaultListModel<String>();


        for (News n : searchResults) {
            String title = n.getTitle();
            LOGGER.fine("Noticia: " + title);
            listModel.addElement(title);
        }

        listNews.setModel(listModel);

    }

    /**
     * Method to setup the logger to the desired Level
     *
     * @see {@link http://www.vogella.com/tutorials/Logging/article.html}
     * @see {@link http://stackoverflow.com/questions/6315699/why-are-the-level-fine-logging-messages-not-showing}
     */
    private static void setupLogger() {


        LOGGER.setLevel(logLvl);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(logLvl);
        LOGGER.addHandler(handler);

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(10, 10, 10, 10), -1, -1));
        searchField = new JTextField();
        searchField.setEnabled(true);
        searchField.setToolTipText("Intro");
        panel.add(searchField, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        searchButton = new JButton();
        searchButton.setText("Buscar");
        panel.add(searchButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listNews = new JList();
        listNews.setSelectionMode(0);
        scrollPane1.setViewportView(listNews);
        importIndexButton = new JButton();
        importIndexButton.setText("Importar Índice \nde busqueda");
        panel.add(importIndexButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
