package es.ugr.giw.p4;

/**
 * Created by aythae on 12/05/17.
 */
public class Movie {
    private int idMovie;
    private String title;
    //TODO Check if it's interesting to use the movie genre


    public Movie(int idMovie, String title) {
        this.idMovie = idMovie;
        this.title = title;
    }

    public int getIdMovie() {
        return idMovie;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "{ID Movie: "+idMovie+", Title: "+title+"}";
    }
}
