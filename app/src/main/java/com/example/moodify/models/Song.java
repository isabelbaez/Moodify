package com.example.moodify.models;

public class Song {

    private String id;
    private String name;
    private String mood;
    private String artist;
    private String uri;
    private String AlbumId;
    private String ArtistId;
    private String genres;

    public Song(String id, String name, String uri) {
        this.name = name;
        this.id = id;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String name) {
        this.artist = name;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public String getArtistId() {
        return ArtistId;
    }

    public void setArtistId(String ArtistId) {
        this.ArtistId = ArtistId;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }



}


