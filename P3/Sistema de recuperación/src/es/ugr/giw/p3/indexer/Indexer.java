package es.ugr.giw.p3.indexer;

import es.ugr.giw.p3.common.News;
import jdk.internal.org.objectweb.asm.Handle;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.*;

/**
 * Created by aythae on 2/05/17.
 */
public class Indexer {
    private static final Level logLvl = Level.ALL;

    public static void main(String[] args) {

        File newsDir = new File("efe/");

        File[] files = newsDir.listFiles();


        File singleFile = new File(newsDir, "19940101.sgml");
        try {
            ArrayList<News> newsArray = SGMLParser.parseNews(newsDir);
            //parseSingleFile(singleFile);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @see {@link http://www.vogella.com/tutorials/Logging/article.html}
     * @see {@link http://stackoverflow.com/questions/6315699/why-are-the-level-fine-logging-messages-not-showing}
     */
    public static Logger setupLogger(Logger logger) {
        logger.setLevel(logLvl);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(logLvl);
        logger.addHandler(handler);

        return logger;


    }
}
