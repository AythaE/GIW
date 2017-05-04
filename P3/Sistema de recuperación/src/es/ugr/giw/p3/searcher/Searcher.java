package es.ugr.giw.p3.searcher;

import es.ugr.giw.p3.common.News;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Searcher that load the previously created index and make a search against it
 */
public class Searcher {

    private static File indexFile = null;
    private static Directory directory = null;
    private static DirectoryReader ireader = null;
    private static IndexSearcher isearcher = null;
    private static SpanishAnalyzer analyzer = null;
    private static QueryParser parser = null;

    /**
     * Method to load the index
     *
     * @throws IOException
     */
    private static void intializeSearch() throws IOException {
        directory = FSDirectory.open(indexFile);
        ireader = DirectoryReader.open(directory);
        isearcher = new IndexSearcher(ireader);
        analyzer = new SpanishAnalyzer(Version.LUCENE_43);

        // Parse a simple query that searches for "text":
        parser = new QueryParser(Version.LUCENE_43, "text", analyzer);
    }

    /**
     * Method to make a search over the index.
     *
     * @param qStr The query to look for in the index
     * @return List of news that satisfy the query
     * @throws IOException
     * @throws ParseException
     */
    static List<News> search(String qStr) throws IOException, ParseException {

        if (directory == null) {
            intializeSearch();
        }

        Query query = parser.parse(qStr);
        ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;

        List<News> searchResults = new ArrayList<>();
        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            String title = hitDoc.get("title");
            String text = hitDoc.get("text");
            News n = new News(title, text);
            searchResults.add(n);
        }


        return searchResults;

    }

    /**
     * Method to close the index on error or when the program finnish
     */
    static void closeIndex() {
        try {
            if (ireader != null)
                ireader.close();
            if (directory != null)
                directory.close();
        } catch (IOException ignored) {
        } finally {
            ireader = null;
            directory = null;
        }
    }

    static File getIndexFile() {
        return indexFile;
    }

    static void setIndexFile(File indexFile) {
        Searcher.indexFile = indexFile;
    }
}
