package es.ugr.giw.p3.indexer;

import es.ugr.giw.p3.common.News;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class Indexer that create the index using lucene libs
 */
public class Indexer {
    static final Level logLvl = Level.INFO;
    private static Logger LOGGER = Logger.getLogger(Indexer.class.getName());
    private static IndexWriter iwriter = null;

    public static void main(String[] args) {

        // Read arguments
        if (args.length < 3) {
            System.err.println("Error: Argumentos invalidos.");
            System.err.println("\nUso: java -jar indexer.jar <Ruta al directorio de la coleccción> <Ruta fichero de palabras vacías> <Ruta al directorio donde alojar los índices>");
            return;
        }

        String pathNews = args[0];
        String pathStopWords = args[1];
        String pathOutputIndex = args[2];


        File newsDir = new File(pathNews);

        try {
            List<News> newsArray = SGMLParser.parseNews(newsDir);

            if (newsArray != null) {
                if (!(newsArray.size() > 0)){
                    System.err.println("\nError: el directorio de noticias no contiene ningún archivo .sgml con noticias.");
                    return;
                }
                index(newsArray, pathStopWords, pathOutputIndex);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            closeIndex();
        }
    }

    /**
     * Method to create and store the index.
     * @param newsArray List of parsed news to index it
     * @param pathStopWords File to the stop words file
     * @param pathOutputIndex File to the output directory where the index will be stored
     * @throws IOException
     */
    private static void index(List<News> newsArray, String pathStopWords, String pathOutputIndex) throws IOException {
        LOGGER.log(Level.INFO, "Creando el analizador...");
        File stopWordsF = new File(pathStopWords);
        if (stopWordsF == null || !stopWordsF.exists() || stopWordsF.isDirectory()){
            System.err.println("\nError: el fichero de palabras vacías "+pathStopWords+" no existe o es un directorio.");
            return;
        }
        List<String> stopWords = Files.readAllLines(Paths.get(pathStopWords), Charset.forName("UTF-8"));
        SpanishAnalyzer analyzer = new SpanishAnalyzer(Version.LUCENE_43, new CharArraySet(Version.LUCENE_43, stopWords, true));
        // Store the index in memory:
        // Directory directory = new RAMDirectory();

        LOGGER.log(Level.INFO, "Creando el IndexWriter...");
        // Store it in drive

        Directory directory = FSDirectory.open(new File(pathOutputIndex));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwriter = new IndexWriter(directory, config);

        LOGGER.log(Level.INFO, "Escribiendo el índice...");

        for (News n : newsArray) {
            Document doc = new Document();
            String title = n.getTitle();
            String text = n.getText();

            doc.add(new Field("title", title, TextField.TYPE_STORED));
            doc.add(new Field("text", text, TextField.TYPE_STORED));
            iwriter.addDocument(doc);

        }
        LOGGER.log(Level.INFO, "Todas las noticias indexadas correctamente");

        closeIndex();
    }

    /**
     * Method to close the index in case of error or correct finalization
     */
    private static void closeIndex() {
        if (iwriter != null) {
            try {
                iwriter.close();
            } catch (IOException ignored) {
            } finally {
                LOGGER.log(Level.INFO, "Saliendo...");
            }
        }
    }

}
