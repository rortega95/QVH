package com.roe.qvh;

/**
 * Created by r on 24/5/17.
 */

public class Movie {

    private String title;
    private String poster_path;
    private String video_path;
    private int id;
    private String overview;
    private String backdrop_path;

    public Movie(int id, String title, String poster_path, String video_path, String overview, String backdrop_path) {
        this.title = title;
        this.poster_path = poster_path;
        this.video_path = video_path;
        this.id = id;
        this.overview = overview;
        this.backdrop_path = backdrop_path;
    }

    public Movie() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", poster_path='" + poster_path + '\'' +
                ", video_path='" + video_path + '\'' +
                ", id=" + id +
                ", overview='" + overview + '\'' +
                '}';
    }
}
