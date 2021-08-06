# Moodify

Original App Design Project - README Template
===

# App Name: Moodify

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
- Syncs with your Spotify account and measures out the "moods" of your listening pattern. 
- Use song pitch/beat to calibrate its "mood", create different mood clusters, measure out a user's mood during the day (in percentages).
- Shareable with friends! You can add people and see each other's "mood swings".
- Recomend playlists based on current moods? Send song suggestions to friends? Add songs to a friend's queue?
- Uses Spotify Login and Spotify API.

### App Evaluation
- **Category:** Entertainment/Social
- **Mobile:** Social media app on phone/allows for easy audio sharing (connecting to auxes, etc.).
- **Story:** Fun idea to analyze the mood of your listening patterns, and your friends'. Get song recommendations based on moods. 
- **Market:** Spotify users/mostly a young audience. 
- **Habit:** Analyzing moods and sharing with friends will maintain app use, but not too habit-forming. Users consume mood analyzer and song recommendations, but can create own playlists and sync with friends. 
- **Scope:** 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create account and link to Spotify.
* User can log in into app (login persists with app quitting).
* When user listens to songs on Spotify, app tracks mood of songs and records in in User's profile.
* User can view their mood percentages for the day and overall statistics, as well as current mood (based on last song they listened to).
* User can add friends on app and see their current statuses in their feed.

**Optional Nice-to-have Stories**

* Filter friends feed by mood.
* User gets a playlist of song reccomendations for each existing mood based on their song history (recently-played songs).
* User can click and play each song on their recommendation playlists.
* * User can long press on a friend's status to play their included song.
* User can click on friend's profiles to see their recent mood percentages and current status. 

### 2. Screen Archetypes

* **Log In/Sign Up Page**
   * User can create account and link to Spotify.
   * User can log in into app (login persists with app quitting).
* **Profile (User Status Page)**
   * When user listens to songs on Spotify, app tracks mood of songs and records in User's profile.
   * User can view their mood percentages for their recently-played tracks, as well as current mood (based on last song they listened to).
* **Search (Friends)**
    * Search + add friends on app.
* **Stream (Displays people's current moods)**
    * User can view friends' current statuses (+ can long press a friend's status to play that specific song).
    * Filter friends feed by mood.
* **Stream (Discover Page)**
    * User gets song reccomendations for each existing mood based on their song history (recently-played songs).
    
### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Feed (Friend Stats)
* Explore/Search (songs recs)
* Profile (own stats)
* Friend's Profile
* Mood Playlist
* Friend Profile

**Flow Navigation** (Screen to Screen)

* Home Feed
   * Friend Profile Page
   * Profile page
   * Explore
* Friend Profile Page
  * Home Feed
* Log In/Register
   * Home Feed
* Explore
    * Mood Playlist
    * Profile page
    * Home Feed
* Mood Playlist
    * Explore
* Profile page
  * Home Feed
  * Explore 

## Wireframes

https://www.figma.com/file/8UO2SOtI10DGsosmGCaiQM/Untitled?node-id=0%3A1

## Schema 

### Models

| Model | Description |
| -------- | -------- |
| Song | Each song has its title, artist, album, etc. and also has its mood measurement. |
| User | Users have their usernames, passwords, current status, stats, and playlists |

### Networking
- Home Feed Screen
    - (Read/GET) Query all friend's statuses (from Parse)
    - (Read/GET) Friend status song uri (if played) (from Parse)
- Explore Screen
    - (Read/GET) Query all recommended songs (divided by moods) (Spotify API call)
- Profile Screen
    - (Read/GET) Query logged in user object
    - (Read/GET) Recently played songs (Spotify API call) and moods (from Parse)
    - (Read/GET) Current status (Spotify API call) and mood (from Parse)
    
#### Create basic snippets for each Parse network request
- Query logged in user object:
    -  Parseuser currentUser = Parseuser.getCurrentUser();
- To play a user's status: 
    - user.get("status");
- To play a user's status song: 
    - user.get("lastSongURI");
- To set current user's mood measurements: 
    - currentUser.put("happiness", some_value);
    - currentUser.get("sadness", some_value);
    - currentUser.get("anger", some_value);
    - currentUser.get("chill", some_value);
    - currentUser.get("energy", some_value);
    - currentUser.saveInBackground(); 
- To get a user's mood measurements: 
    - user.get("happiness");
    - user.get("sadness");
    - user.get("anger");
    - user.get("chill");
    - user.get("energy");
 - To get a user's feed:
   - ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
   - query.include("username");
   - ArrayList<String> friends = (ArrayList<String>) currentUser.get("friends");
   - query.whereContainedIn("username", friends);
  
#### OPTIONAL: List endpoints if using existing API such as Yelp
  - Get a user's information: "https://api.spotify.com/v1/me"
  - Get a user's recently played tracks: "https://api.spotify.com/v1/me/player/recently-played"
  - Get a song's audio features (for mood calculaions): "https://api.spotify.com/v1/audio-features/" + songId
  - Get a user's recommended songs: "https://api.spotify.com/v1/recommendations?seed_tracks=" + songIds +
                "&seed_artists=&seed_genres=&limit=50"

        
    
