package es.ugr.giw.p3.indexer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.ugr.giw.p3.common.News;

/**
 * Class SGMLParser to parse files from the EFE SGML news
 */
class SGMLParser {
    private static DocumentBuilder docBuilder = null;
    private static Logger LOGGER = null;

    /**
     * Method to parse all the news in the selected directory
     * @param dir File instance with the directory to look for the news
     * @return List of parsed news
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    static List<News> parseNews(File dir) throws ParserConfigurationException, SAXException, IOException {
        if (LOGGER == null) {
            setupLogger();
        }
        List<News> newsArray = new ArrayList<>();
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();

            LOGGER.log(Level.INFO, "Leyendo noticias...");

            for (File file : files) {
                if (file.getName().matches("^.+\\.sgml$")) {
                    List<News> partialNewsArray = parseSingleFile(file);

                    newsArray.addAll(partialNewsArray);
                }

            }
            LOGGER.log(Level.INFO, "Noticias leídas: {0}", newsArray.size());

        } else {
            System.err.println("\nError: la ruta " + dir + " no existe o no es un directorio");
            return null;
        }
        return newsArray;

    }

    /**
     * Method to parse the news on a single SGML file
     * @param f File instance to the desired SGML file
     * @return List of parsed news
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @see {@link http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/}
     */
    private static List<News> parseSingleFile(File f)
            throws ParserConfigurationException, SAXException, IOException {

        String fContent = new String(Files.readAllBytes(Paths.get(f.toURI())), "ISO-8859-1");

        //Change elements of the document due to parsing problems
        fContent = fContent.replace("&", "");
        fContent = fContent.replace("< ", " ");
        fContent = fContent.replace("<\n<", "\n<");
        fContent = fContent.replace(";<", ";");
        fContent = "<SGML>" + fContent + "</SGML>";

        LOGGER.log(Level.FINE, "Opening file: {0}", f.getPath());

        if (docBuilder == null) {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        Document doc = docBuilder.parse(new ByteArrayInputStream(fContent.getBytes("UTF-8")));

        doc.getDocumentElement().normalize();


        List<News> newsArray = new ArrayList<>();

        NodeList nList = doc.getElementsByTagName("DOC");


        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                String title = eElement.getElementsByTagName("TITLE").item(0).getTextContent();
                String text = eElement.getElementsByTagName("TEXT").item(0).getTextContent();
                News n = new News(title, text);
                newsArray.add(n);
            }


        }
        return newsArray;
    }

    /**
     * Method to setup the logger to the desired Level
     * @see {@link http://www.vogella.com/tutorials/Logging/article.html}
     * @see {@link http://stackoverflow.com/questions/6315699/why-are-the-level-fine-logging-messages-not-showing}
     */
    private static void setupLogger() {
        LOGGER = Logger.getLogger(SGMLParser.class.getName());
        LOGGER.setLevel(Indexer.logLvl);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Indexer.logLvl);
        LOGGER.addHandler(handler);

    }
}