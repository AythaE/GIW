package es.ugr.giw.p4;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by aythae on 12/05/17.
 */
public class RecomendationSystem {
    private static final String SEPARATOR = "================================================================================";
    private static final int NUM_INITIAL_REVIEWS = 20;
    private static int CURRENT_USER_ID = 1000;
    private static final File MOVIE_LENS_FOLDER = new File("ml-data/");
    private static final int K = 10;

    private static boolean DEBUG = false;

    private static Scanner sc = null;
    private static boolean pearsonSimilarity = true;
    private static boolean advancedPrediction = true;
    private static boolean interactiveCurrentUserReviews = true;

    public static void main(String[] args) {

        if (args != null && args.length > 0) {
            if (args.length == 1) {
                if (args[0].equals("-d"))
                    DEBUG = true;
                else {
                    System.err.println("Error: argumento no reconocido.");
                    printUse();
                    return;
                }
            } else {
                System.err.println("Error solo se admiten 0 o 1 argumentos.");
                printUse();
                return;

            }
        }

        printProgramHeader();

        try {
            if (!MovieLensUtils.setMovieLensFolder(MOVIE_LENS_FOLDER) || !MovieLensUtils.loadCollection()) {
                System.err.println("Error cargando la base de datos Movie Lens desde " + MOVIE_LENS_FOLDER.getAbsolutePath());
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        sc = new Scanner(System.in);
        boolean correctOpt = false;
        do {


            System.out.printf("\nSeleccione la medida de similaridad a utilizar coseno o pearson: [c/p] ");
            System.out.flush();
            String opt = sc.nextLine();
            if (opt.toLowerCase().matches("c")) {
                pearsonSimilarity = false;
                correctOpt = true;
            } else if (opt.toLowerCase().matches("p")) {
                pearsonSimilarity = true;
                correctOpt = true;
            } else {
                System.err.println("Error: opción inválida, introduzca \"c\" o \"p\".\n\n");
                System.err.flush();

                try {
                    Thread.sleep(250);
                } catch (InterruptedException ignored) {
                }

            }
        } while (!correctOpt);

        correctOpt = false;
        do {


            System.out.printf("\nSeleccione si desea realizar las predicciones compensando las diferencias " +
                    "de interpretación y escala o no: [s/n] ");
            System.out.flush();
            String opt = sc.nextLine();
            if (opt.toLowerCase().matches("s")) {
                advancedPrediction = true;
                correctOpt = true;
            } else if (opt.toLowerCase().matches("n")) {
                advancedPrediction = false;
                correctOpt = true;
            } else {
                System.err.println("Error: opción inválida, introduzca \"s\" o \"n\".\n\n");
                System.err.flush();

                try {
                    Thread.sleep(250);
                } catch (InterruptedException ignored) {
                }

            }
        } while (!correctOpt);

        if (DEBUG) {
            correctOpt = false;
            do {


                System.out.printf("\nDesea introducir sus propias valoraciones o utilizar un usuario existente " +
                        "en la colección: [(p)ropias/(e)xistente] ");
                System.out.flush();
                String opt = sc.nextLine();
                if (opt.toLowerCase().matches("p")) {
                    interactiveCurrentUserReviews = true;
                    correctOpt = true;
                } else if (opt.toLowerCase().matches("e")) {
                    interactiveCurrentUserReviews = false;
                    correctOpt = true;
                } else {
                    System.err.println("Error: opción inválida, introduzca \"p\" o \"e\".\n\n");
                    System.err.flush();

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ignored) {
                    }

                }
            } while (!correctOpt);

            if (!interactiveCurrentUserReviews) {
                correctOpt = false;

                do {

                    System.out.printf("\nSeleccione el ID del usuario a utilizar como usuario activo (entre 1 y 943): ");
                    System.out.flush();
                    try {

                        String opt = sc.nextLine();
                        int uID = Integer.parseInt(opt);

                        if (uID >= 1 && uID <= 943) {
                            CURRENT_USER_ID = uID;
                            correctOpt = true;
                        } else {
                            System.err.println("Error: opción inválida, introduzca un número entero entre 1 y 943.\n\n");
                            System.err.flush();

                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException ignored) {
                            }

                        }
                    } catch (Exception e) {
                        System.err.println("Error: opción inválida, introduzca un número entero entre 1 y 943.\n\n");
                        System.err.flush();

                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException ignored) {
                        }
                    }

                } while (!correctOpt);
            }
        }

        if (interactiveCurrentUserReviews) {
            getCurrentUserReviews();
        }
        long tIni = 0, tFin = 0;


        tIni = System.currentTimeMillis();
        Map<Integer, Double> sortedSimilarityMap = getSimilarityWithOtherUsers(CURRENT_USER_ID);

        if (DEBUG) {
            System.out.println("\nArray de similaridad ordenado: " + Arrays.toString(sortedSimilarityMap.entrySet().toArray()) + "\n");
        }

        Map<Integer, Double> closestNeighbours = getNeighbourhood(sortedSimilarityMap);


        if (DEBUG) {
            System.out.println("\nVecinos más cercanos:" + Arrays.toString(closestNeighbours.entrySet().toArray()) + "\n");
        }
        Map<Integer, MovieReview> bestPredictedReviews = getBestPredictedReviews(closestNeighbours);

        bestPredictedReviews = sortReviewsByStars(bestPredictedReviews);
        if (DEBUG) {
            System.out.println("\nNúmero de películas predichas: " + bestPredictedReviews.size() + "\n");
        }
        tFin = System.currentTimeMillis();

        float totalTime = (float) (tFin - tIni) / 1000;
        printRecomendationResults(bestPredictedReviews, totalTime);

        closeScanner();
        System.exit(0);
    }

    private static void printUse() {
        System.out.println("\nUso correcto: java -jar SistemaRecomendacion.jar [-d]");
        System.out.println("\t-d: activar modo depuración.");
    }

    private static void printProgramHeader() {
        System.out.println("Sistema de recomendación de películas");
        System.out.println(SEPARATOR);
        System.out.println("Autor: Aythami Estévez Olivas");
        System.out.println("Fecha: Mayo, 2017");
        System.out.println("Licencia: GPL v3");
        System.out.println(SEPARATOR);
    }

    private static void getCurrentUserReviews() {


        List<Movie> intialMoviesToReviews = new ArrayList<>(MovieLensUtils.getMovies().values());

        Collections.shuffle(intialMoviesToReviews);

        intialMoviesToReviews = intialMoviesToReviews.subList(0, NUM_INITIAL_REVIEWS - 1);

        Map<Integer, MovieReview> intialReviews = new HashMap<>();
        float meanRating = 0;

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
            int rating = review.length();
            meanRating += rating;
            MovieReview mr = new MovieReview(CURRENT_USER_ID, m.getIdMovie(), rating);
            intialReviews.put(m.getIdMovie(), mr);
        }
        closeScanner();

        meanRating /= NUM_INITIAL_REVIEWS;
        Map<Integer, Map<Integer, MovieReview>> currentUserReviews = new HashMap<>();

        currentUserReviews.put(CURRENT_USER_ID, intialReviews);

        MovieLensUtils.putMovieReviewsByUser(currentUserReviews);

        MovieLensUtils.putMeanRatingByUser(CURRENT_USER_ID, meanRating);

    }

    private static void closeScanner() {
        if (sc != null) {
            sc.close();
            sc = null;
        }
    }

    private static void printRecomendationResults(Map<Integer, MovieReview> bestPredictedReviews, float totalTime) {

        // There isn't any recomended film for this user
        if (bestPredictedReviews.size() == 0) {
            System.out.println("Lo sentimos, pero el sistema no ha podido predecir películas que le puedan gustar.");
            return;
        }
        System.out.println("Las siguientes películas le podrían ser de su agrado según las valoraciones que ha realizado:");
        for (int mId : bestPredictedReviews.keySet()) {
            Movie m = MovieLensUtils.getMoviesByID(mId);

            System.out.println("\t- Película: " + m.getTitle());
            System.out.println("\t- Valoración predicha por el sistema: " + bestPredictedReviews.get(mId).getStars() + "\n");
        }
        System.out.println(SEPARATOR);

        System.out.println("Tiempo para generar las recomendaciones: " + totalTime + "s.");

    }


    private static Map<Integer, Double> getSimilarityWithOtherUsers(int userID) {
        Map<Integer, Map<Integer, MovieReview>> movieReviewsByUser = MovieLensUtils.getMovieReviewsByUser();
        Map<Integer, Double> similarityByUsers = new HashMap<>();
        for (int uID : movieReviewsByUser.keySet()) {
            if (uID != userID) {
                if (pearsonSimilarity) {
                    similarityByUsers.put(uID, similarityPearson(userID, uID));
                } else {
                    similarityByUsers.put(uID, similarityCos(userID, uID));
                }
            }
        }

        similarityByUsers = sortSimilartyMap(similarityByUsers);
        return similarityByUsers;
    }

    private static Map<Integer, Double> getNeighbourhood(Map<Integer, Double> sortedSimilarityMap) {


        Map<Integer, Double> closestNeighbours = new LinkedHashMap<>();
        int i = 0;


        for (Map.Entry<Integer, Double> similEntry : sortedSimilarityMap.entrySet()) {
            if (i < K) {
                closestNeighbours.put(similEntry.getKey(), similEntry.getValue());
                i++;
            } else {
                break;
            }

        }

        return closestNeighbours;
    }

    private static Map<Integer, MovieReview> getBestPredictedReviews(Map<Integer, Double> closestNeighbours) {
        Map<Integer, MovieReview> predictedReviews = new HashMap<>();
        Map<Integer, MovieReview> currentUserReviews = MovieLensUtils.getMovieReviewsByUser().get(CURRENT_USER_ID);
        for (int uID : closestNeighbours.keySet()) {


            Map<Integer, MovieReview> neighbourReviews = MovieLensUtils.getMovieReviewsByUser().get(uID);

            for (int mID : neighbourReviews.keySet()) {
                // If the user don't review a film of its neighbours and its not
                // previously predicted predict it
                if (!currentUserReviews.containsKey(mID) && !predictedReviews.containsKey(mID)) {
                    MovieReview predictedRev;
                    if (advancedPrediction) {
                        predictedRev = predictReviewAdvanced(CURRENT_USER_ID, closestNeighbours, mID);
                    } else {
                        predictedRev = predictReviewBasic(CURRENT_USER_ID, closestNeighbours, mID);
                    }
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
        return bestPredictedReviews;
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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // If any of the values don't change it means than this users haven't
        // any common film so return the most negative possible value
        if (numerator == Double.MIN_VALUE || denominatorU == Double.MIN_VALUE || denominatorV == Double.MIN_VALUE)
            return 0;

        return numerator / (denominatorU * denominatorV);
    }

    private static double similarityCos(int userU_ID, int userV_ID) {
        Map<Integer, MovieReview> userURev = MovieLensUtils.getMovieReviewsByUserID(userU_ID);
        Map<Integer, MovieReview> userVRev = MovieLensUtils.getMovieReviewsByUserID(userV_ID);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        Future<Double> numeratorFut = executor.submit(new Callable<Double>() {
            double numeratorInner = Double.MIN_VALUE;

            @Override
            public Double call() throws Exception {
                for (int mID : userURev.keySet()) {
                    if (userVRev.containsKey(mID)) {
                        numeratorInner += userURev.get(mID).getStars() * userVRev.get(mID).getStars();
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
                        denominatorUInner += Math.pow(userURev.get(mID).getStars(), 2);
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

                        denominatorVInner += Math.pow(userVRev.get(mID).getStars(), 2);
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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // If any of the values don't change it means than this users haven't
        // any common film so return 0
        if (numerator == Double.MIN_VALUE || denominatorU == Double.MIN_VALUE || denominatorV == Double.MIN_VALUE)
            return 0;

        return numerator / (denominatorU * denominatorV);
    }

    private static MovieReview predictReviewBasic(int currentUserID, Map<Integer, Double> closestNeighbours, int movieID) {


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

        if (predictedStars > 5)
            predictedStars = 5;

        return new MovieReview(currentUserID, movieID, predictedStars);
    }

    private static MovieReview predictReviewAdvanced(int currentUserID, Map<Integer, Double> closestNeighbours, int movieID) {


        float numerator = 0, denominator = 0;
        float userUMean = MovieLensUtils.getMeanRatingByUser().get(CURRENT_USER_ID);

        for (int uID : closestNeighbours.keySet()) {
            float userVMean = MovieLensUtils.getMeanRatingByUser().get(uID);
            // If the neighbour has seen the movie that i'm predicting use him
            // review for the prediction
            if (MovieLensUtils.getMovieReviewsByUserID(uID).containsKey(movieID)) {
                numerator += closestNeighbours.get(uID) * (MovieLensUtils.getMovieReviewsByUserID(uID).get(movieID).getStars() - userVMean);
                denominator += Math.abs(closestNeighbours.get(uID));
            }
        }

        int predictedStars = Math.round(userUMean + numerator / denominator);

        if (predictedStars > 5)
            predictedStars = 5;

        return new MovieReview(currentUserID, movieID, predictedStars);
    }
    /**
     * @param unsortMap
     * @return
     * @see <a href="http://www.mkyong.com/java/how-to-sort-a-map-in-java/">http://www.mkyong.com/java/how-to-sort-a-map-in-java/</a>
     */
    private static Map<Integer, Double> sortSimilartyMap(Map<Integer, Double> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<Integer, Double>> list =
                new LinkedList<>(unsortMap.entrySet());

        // 2. Sort list with List.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        list.sort(new SimilartyComparator());

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Integer, Double> sortedMap = new LinkedHashMap<>();
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
                new LinkedList<>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        list.sort(new ReviewComparator());

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Integer, MovieReview> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, MovieReview> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }


        return sortedMap;
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