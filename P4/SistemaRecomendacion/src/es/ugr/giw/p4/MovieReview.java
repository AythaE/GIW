package es.ugr.giw.p4;

/**
 * Created by aythae on 12/05/17.
 */
public class MovieReview {

    private int idUser;
    private int idMovie;
    private int stars;


    public MovieReview(int idUser, int idMovie, int stars) {
        this.idUser = idUser;
        this.idMovie = idMovie;
        this.stars = stars;

    }

    public int getIdUser() {
        return idUser;
    }

    public int getIdMovie() {
        return idMovie;
    }

    public int getStars() {
        return stars;
    }

    @Override
    public String toString() {
        return "{ID User: "+idUser+ ", ID Movie: "+idMovie+", Stars: "+stars+"}";
    }
}
