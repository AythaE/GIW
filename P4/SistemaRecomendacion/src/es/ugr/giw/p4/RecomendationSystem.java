package es.ugr.giw.p4;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by aythae on 12/05/17.
 */
public class RecomendationSystem {
    public static final String SEPARATOR = "================================================================================";
    public static final int NUM_INITIAL_REVIEWS = 20;
    public static final int CURRENT_USER_ID = 1000;
    public static final File MOVIE_LENS_FOLDER = new File("ml-data/");


    private static final int K = 5;

    public static void main(String[] args) {


        try {
            if(!MovieLensUtils.setMovieLensFolder(MOVIE_LENS_FOLDER) || !MovieLensUtils.loadCollection()){
                System.err.println("Error cargando la base de datos Movie Lens desde "+MOVIE_LENS_FOLDER.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        getCurrentUserReviews();
        long tIni = 0, tFin = 0;


        tIni = System.currentTimeMillis();
        Map<Integer, Double> sortedSimilarityMap = getSimilarityWithOtherUsers(CURRENT_USER_ID);

        //System.out.println(Arrays.toString(sortedSimilarityMap.entrySet().toArray()));


        Map<Integer, Double> closestNeighbours = new LinkedHashMap<>();
        int i = 0;


        for (Map.Entry<Integer, Double> similEntry : sortedSimilarityMap.entrySet()) {
            if (i < K) {
                closestNeighbours.put(similEntry.getKey(), similEntry.getValue());
                i++;
            } else
                break;
        }

        //System.out.println(Arrays.toString(closestNeighbours.entrySet().toArray()));

        Map<Integer, MovieReview> predictedReviews = new HashMap<>();
        Map<Integer, MovieReview> currentUserReviews = MovieLensUtils.getMovieReviewsByUser().get(CURRENT_USER_ID);
        for (int uID : closestNeighbours.keySet()) {


            Map<Integer, MovieReview> neighbourReviews = MovieLensUtils.getMovieReviewsByUser().get(uID);

            for (int mID : neighbourReviews.keySet()) {
                // If the user don't review a film of its neighbours and its not
                // previously predicted predict it
                if (!currentUserReviews.containsKey(mID) && !predictedReviews.containsKey(mID)) {
                    MovieReview predictedRev = predictReview(CURRENT_USER_ID, closestNeighbours, mID);
                    predictedReviews.put(mID, predictedRev);
                }
            }
        }

        Map<Integer, MovieReview> bestPredictedReviews = new LinkedHashMap<>();
        for (int mID : predictedReviews.keySet()) {
            if (predictedReviews.get(mID).getStars() > 3) {
                bestPredictedReviews.put(mID, predictedReviews.get(mID));
            }
        }

        bestPredictedReviews = sortReviewsByStars(bestPredictedReviews);

        //System.out.println(bestPredictedReviews.size());

        tFin = System.currentTimeMillis();

        float totalTime = (float) (tFin - tIni) / 1000;
        printRecomendationResults(bestPredictedReviews, totalTime);
    }



    /**
     * TODO Finnish this updating the structures in MovieLensUtils
     */
    private static void getCurrentUserReviews() {
        System.out.println("Sistema de recomendación de películas");
        System.out.println(SEPARATOR);
        System.out.println("Autor: Aythami Estévez Olivas");
        System.out.println("Fecha: Mayo, 2017");
        System.out.println("Licencia: GPL v3");
        System.out.println(SEPARATOR);


        List<Movie> intialMoviesToReviews = new ArrayList<>(MovieLensUtils.getMovies().values());

        Collections.shuffle(intialMoviesToReviews);

        intialMoviesToReviews = intialMoviesToReviews.subList(0, NUM_INITIAL_REVIEWS - 1);

        Map<Integer, MovieReview> intialReviews = new HashMap<>();
        float meanRating = 0;
        Scanner sc = new Scanner(System.in);

        System.out.println("\nPor favor, valore las siguientes películas para que el sistema le recomiende otras similares.\n");

        for (int i = 0; i < intialMoviesToReviews.size(); i++) {
            Movie m = intialMoviesToReviews.get(i);
            System.out.println("Película: " + m.getTitle());
            System.out.printf("Valoración (desde * hasta *****): ");
            System.out.flush();
            String review = sc.nextLine();
            if (!review.matches("\\*{1,5}")) {
                System.err.println("Error: valoración incorrecta, por favor introduzca únicamente de 1 a 5 *.\n\n");
                System.err.flush();
                i--;
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ignored) {
                }
                continue;
            }
            System.out.println();
            int rating=review.length();
            meanRating+=rating;
            MovieReview mr = new MovieReview(CURRENT_USER_ID, m.getIdMovie(), rating);
            intialReviews.put(m.getIdMovie(), mr);
        }
        sc.close();
        sc= null;

        meanRating /= NUM_INITIAL_REVIEWS;
        Map<Integer, Map<Integer,MovieReview>> currentUserReviews = new HashMap<>();

        currentUserReviews.put(CURRENT_USER_ID, intialReviews);

        MovieLensUtils.putMovieReviewsByUser(currentUserReviews);

        MovieLensUtils.putMeanRatingByUser(CURRENT_USER_ID, meanRating);

    }

    private static void printRecomendationResults(Map<Integer, MovieReview> bestPredictedReviews, float totalTime) {

        System.out.println("Las siguientes peliculas le podrían ser de su agrado según las valoraciones que ha realizado:");
        for (int mId : bestPredictedReviews.keySet()) {
            Movie m = MovieLensUtils.getMoviesByID(mId);

            System.out.println("\t- Película: " + m.getTitle());
            System.out.println("\t- Valoración predicha por el sistema: " + bestPredictedReviews.get(mId).getStars() + "\n");
        }
        System.out.println(SEPARATOR);

        System.out.println("Tiempo para generar las recomendaciones: " + totalTime + "s.");

    }
    private static double similarityPearson(int userU_ID, int userV_ID) {
        Map<Integer, MovieReview> userURev = MovieLensUtils.getMovieReviewsByUserID(userU_ID);
        Map<Integer, MovieReview> userVRev = MovieLensUtils.getMovieReviewsByUserID(userV_ID);
        float userUMean = MovieLensUtils.getMeanRatingByUser().get(userU_ID);
        float userVMean = MovieLensUtils.getMeanRatingByUser().get(userV_ID);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        Future<Double> numeratorFut = executor.submit(new Callable<Double>() {
            double numeratorInner = Double.MIN_VALUE;

            @Override
            public Double call() throws Exception {
                for (int mID : userURev.keySet()) {
                    if (userVRev.containsKey(mID)) {
                        numeratorInner += (userURev.get(mID).getStars() - userUMean) * (userVRev.get(mID).getStars() - userVMean);
                    }
                }
                return numeratorInner;
            }
        });
        Future<Double> denominatorUFut = executor.submit(new Callable<Double>() {
            double denominatorUInner = Double.MIN_VALUE;

            @Override
            public Double call() throws Exception {
                for (int mID : userURev.keySet()) {
                    if (userVRev.containsKey(mID)) {
                        denominatorUInner += Math.pow((userURev.get(mID).getStars() - userUMean), 2);
                    }
                }
                denominatorUInner = Math.sqrt(denominatorUInner);

                return denominatorUInner;
            }
        });
        Future<Double> denominatorVFut = executor.submit(new Callable<Double>() {
            double denominatorVInner = Double.MIN_VALUE;

            @Override
            public Double call() throws Exception {
                for (int mID : userVRev.keySet()) {
                    if (userURev.containsKey(mID)) {

                        denominatorVInner += Math.pow((userVRev.get(mID).getStars() - userVMean), 2);
                    }
                }
                denominatorVInner = Math.sqrt(denominatorVInner);

                return denominatorVInner;
            }
        });

        double numerator = Double.MIN_VALUE, denominatorV = Double.MIN_VALUE, denominatorU = Double.MIN_VALUE;
        try {
            numerator = numeratorFut.get();
            denominatorU = denominatorUFut.get();
            denominatorV = denominatorVFut.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // If any of the values don't change it means than this users haven't
        // any common film so return the most negative possible value
        if (numerator == Double.MIN_VALUE || denominatorU == Double.MIN_VALUE || denominatorV == Double.MIN_VALUE)
            return Double.MIN_VALUE;

        double similarity = numerator / (denominatorU * denominatorV);
        return similarity;
    }

    /**
     * @param unsortMap
     * @return
     * @see <a href="http://www.mkyong.com/java/how-to-sort-a-map-in-java/">http://www.mkyong.com/java/how-to-sort-a-map-in-java/</a>
     */
    private static Map<Integer, Double> sortSimilartyMap(Map<Integer, Double> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<Integer, Double>> list =
                new LinkedList<Map.Entry<Integer, Double>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new SimilartyComparator());

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }


        return sortedMap;
    }

    /**
     * @param unsortMap
     * @return
     * @see <a href="http://www.mkyong.com/java/how-to-sort-a-map-in-java/">http://www.mkyong.com/java/how-to-sort-a-map-in-java/</a>
     */
    private static Map<Integer, MovieReview> sortReviewsByStars(Map<Integer, MovieReview> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<Integer, MovieReview>> list =
                new LinkedList<Map.Entry<Integer, MovieReview>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new ReviewComparator());

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Integer, MovieReview> sortedMap = new LinkedHashMap<Integer, MovieReview>();
        for (Map.Entry<Integer, MovieReview> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }


        return sortedMap;
    }


    public static Map<Integer, Double> getSimilarityWithOtherUsers(int userID) {
        Map<Integer, Map<Integer, MovieReview>> movieReviewsByUser = MovieLensUtils.getMovieReviewsByUser();
        Map<Integer, Double> similarityByUsers = new HashMap<>();
        for (int uID : movieReviewsByUser.keySet()) {
            if (uID != userID) {
                similarityByUsers.put(uID, similarityPearson(userID, uID));
            }
        }

        similarityByUsers = sortSimilartyMap(similarityByUsers);
        return similarityByUsers;
    }

    private static MovieReview predictReview(int currentUserID, Map<Integer, Double> closestNeighbours, int movieID) {


        float numerator = 0, denominator = 0;
        for (int uID : closestNeighbours.keySet()) {
            // If the neighbour has seen the movie that i'm predicting use him
            // review for the prediction
            if (MovieLensUtils.getMovieReviewsByUserID(uID).containsKey(movieID)) {
                numerator += closestNeighbours.get(uID) * MovieLensUtils.getMovieReviewsByUserID(uID).get(movieID).getStars();
                denominator += Math.abs(closestNeighbours.get(uID));
            }
        }

        int predictedStars = Math.round(numerator / denominator);
        MovieReview mr = new MovieReview(currentUserID, movieID, predictedStars);

        return mr;
    }
}

class SimilartyComparator implements Comparator<Map.Entry<Integer, Double>> {
    @Override
    public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
        return (o2.getValue()).compareTo(o1.getValue());
    }
}

class ReviewComparator implements Comparator<Map.Entry<Integer, MovieReview>> {
    @Override
    public int compare(Map.Entry<Integer, MovieReview> o1, Map.Entry<Integer, MovieReview> o2) {
        return Integer.compare(o2.getValue().getStars(), o1.getValue().getStars());
    }
}