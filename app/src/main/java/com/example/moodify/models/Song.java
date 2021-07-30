package com.example.moodify.models;

public class Song {

    private String id;
    private String name;
    private String mood;
    private String artist;

    public Song(String id, String name) {
        this.name = name;
        this.id = id;
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
    }


