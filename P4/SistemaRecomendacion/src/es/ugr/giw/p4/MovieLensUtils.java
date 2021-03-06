package es.ugr.giw.p4;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aythae on 12/05/17.
 */
class MovieLensUtils {
    private static File movieLensFolder = null;
    private static File fMovieReviews;
    private static File fMovies;

    private static final String fMovieReviewsName = "u.data";
    private static final String fMoviesName = "u.item";
    private static final String fMovieReviewsSep = "\t";
    private static final String fMoviesSep = "\\|";

    private static Map<Integer, Movie> movies = null;
    private static Map<Integer, Map<Integer, MovieReview>> movieReviewsByUser = null;
    private static Map<Integer, Float> meanRatingByUser = null;


    /**
     * @param folder
     * @return boolean indicating success or not.
     */
    static boolean setMovieLensFolder(File folder) {
        //Check if the MOVIE_LENS_FOLDER exists and is a directory
        if (folder != null && folder.exists() && folder.isDirectory()) {
            File[] folferFiles = folder.listFiles();
            //Check if the MOVIE_LENS_FOLDER contains the expected files before try to read it
            boolean containsFMovies = false, containsFMovieReviews = false;
            if (folferFiles == null)
                return false;
            for (File f : folferFiles) {
                if (f.getName().equals(fMovieReviewsName)) {
                    containsFMovieReviews = true;
                }
                if (f.getName().equals(fMoviesName)) {
                    containsFMovies = true;
                }

                if (containsFMovieReviews && containsFMovies) {
                    break;
                }
            }
            //If the MOVIE_LENS_FOLDER doesn't contains any of the files return a false
            if (!containsFMovieReviews || !containsFMovies) {
                return false;
            }

            movieLensFolder = folder;
            fMovieReviews = new File(movieLensFolder, "u.data");
            fMovies = new File(movieLensFolder, "u.item");

            return true;

        } else
            return false;
    }

    static boolean loadCollection() throws IOException {
        if (movieLensFolder == null) {
            return false;
        }

        List<String> movieReviewsLines = Files.readAllLines(fMovieReviews.toPath(), Charset.forName("UTF-8"));

        movieReviewsByUser = new HashMap<>();
        meanRatingByUser = new HashMap<>();
        for (String line : movieReviewsLines) {
            String[] fields = line.split(fMovieReviewsSep);
            MovieReview mr = new MovieReview(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));

            //If the user is in the dictionary add another review
            if (movieReviewsByUser.containsKey(mr.getIdUser())) {
                Map<Integer, MovieReview> reviews = movieReviewsByUser.get(mr.getIdUser());
                reviews.put(mr.getIdMovie(), mr);
                float newMean = (meanRatingByUser.get(mr.getIdUser()) + (float) mr.getStars());
                meanRatingByUser.put(mr.getIdUser(), newMean);

            }
            //Else create an ArrayList of reviews and add the first one
            else {
                Map<Integer, MovieReview> initialReview = new HashMap<>();
                initialReview.put(mr.getIdMovie(), mr);
                movieReviewsByUser.put(mr.getIdUser(), initialReview);

                meanRatingByUser.put(mr.getIdUser(), (float) mr.getStars());
            }
        }

        //Finnish the calculation of the means
        for (int uID : meanRatingByUser.keySet()) {
            Map<Integer, MovieReview> reviews = movieReviewsByUser.get(uID);

            float newMean = meanRatingByUser.get(uID) / reviews.size();
            meanRatingByUser.put(uID, newMean);
        }


        List<String> moviesLines = Files.readAllLines(fMovies.toPath(), Charset.forName("ISO-8859-1"));

        movies = new HashMap<>();
        for (String line : moviesLines) {
            String[] fields = line.split(fMoviesSep);
            Movie m = new Movie(Integer.parseInt(fields[0]), fields[1]);
            movies.put(m.getIdMovie(), m);
        }
        return true;
    }

    static Map<Integer, Movie> getMovies() {
        return movies;
    }

    static Movie getMoviesByID(int movieID) {
        if (movieLensFolder == null) {
            return null;
        }
        return movies.get(movieID);
    }

    static Map<Integer, Map<Integer, MovieReview>> getMovieReviewsByUser() {
        return movieReviewsByUser;
    }

    static Map<Integer, MovieReview> getMovieReviewsByUserID(int userID) {
        if (movieLensFolder == null) {
            return null;
        }
        return movieReviewsByUser.get(userID);
    }

    static void putMovieReviewsByUser(Map<Integer, Map<Integer, MovieReview>> newReviews) {
        if (movieLensFolder == null) {
            return;
        }
        movieReviewsByUser.putAll(newReviews);
    }

    static Map<Integer, Float> getMeanRatingByUser() {
        if (movieLensFolder == null) {
            return null;
        }
        return meanRatingByUser;
    }

    static void putMeanRatingByUser(int userID, float meanRating) {
        if (movieLensFolder == null) {
            return;
        }
        meanRatingByUser.put(userID, meanRating);
    }
}
