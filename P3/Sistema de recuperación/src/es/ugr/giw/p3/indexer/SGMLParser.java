package es.ugr.giw.p3.indexer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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

public class SGMLParser {
    private static DocumentBuilder docBuilder = null;
    private static final Logger logger = Indexer.setupLogger(Logger.getLogger(SGMLParser.class.getName()));

gi
    public static ArrayList<News> parseNews(File dir) throws ParserConfigurationException, SAXException, IOException {
        ArrayList<News> newsArray = new ArrayList<>();
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();

            logger.log(Level.INFO, "Leyendo noticias...");

            for (File file : files) {
                if (file.getName().matches("^.+\\.sgml$")) {
                    ArrayList<News> partialNewsArray = parseSingleFile(file);

                    newsArray.addAll(partialNewsArray);
                }

            }
            logger.log(Level.INFO, "Noticias leídas: {0}", newsArray.size());

        } else {
            System.err.println("Error: la ruta " + dir + " no existe o no es un directorio");
            return null;
        }
        return newsArray;

    }

    /**
     * @param f
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @see {@link http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/}
     * @see {@link https://github.com/fblupi/master_informatica-GIW/blob/master/P3/InformationRetrievalSystem/src/fblupi/irs/Indexer.java}
     */
    private static ArrayList<News> parseSingleFile(File f)
            throws ParserConfigurationException, SAXException, IOException {


        String fContent = new String(Files.readAllBytes(Paths.get(f.toURI())), "ISO-8859-1");

        //Change elements of the document due to parsing problems
        fContent = fContent.replace("&", "");
        fContent = fContent.replace("< ", " ");
        fContent = fContent.replace("<\n<", "\n<");
        fContent = fContent.replace(";<", ";");
        fContent = "<SGML>" + fContent + "</SGML>";

        logger.log(Level.FINE, "Opening file: {0}", f.getPath());

        if (docBuilder == null) {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        Document doc = docBuilder.parse(new ByteArrayInputStream(fContent.getBytes("UTF-8")));

        doc.getDocumentElement().normalize();


        ArrayList<News> newsArray = new ArrayList<>();

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
}