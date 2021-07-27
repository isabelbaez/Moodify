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
* When user listens to songs on Spotify, app tracks mood of songs and records in in User's profile.
* User can view their mood percentages for the day and overall statistics, as well as current mood (based on last song they listened to).
* User can add friends on app and see their current statuses.
* User can click on friend's profiles to see their day mood percentages and their song history for that day. 

**Optional Nice-to-have Stories**

* User gets song reccomendations based on current mood.
* User can create playlists (and display mood percentages of said playlists).
* User can view friends' playlists and their mood percentages.
* User can like/comment on friend's live statuses. 
* User can travel to and view friends' profiles, including the playlists they've created. 
* User can like/add friends' playlists. 

### 2. Screen Archetypes

* **Log In/Sign Up Page**
   * User can create account and link to Spotify.
* **Profile (User Status Page)**
   * When user listens to songs on Spotify, app tracks mood of songs and records in in User's profile.
   * User can view their mood percentages for the day and overall statistics, as well as current mood (based on last song they listened to).
* **Search (Friends)**
    * Search + add friends on app.
* **Stream (Displays people's current moods)**
    * User can view friends current statuses.
* **Stream (Discover Page)**
    * User gets song reccomendations based on current mood + can search up songs and see their mood categories.
* **Create (Build Playlist)**
    * User can create playlists (and display mood percentages of said playlists).

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Feed (Friend Stats)
* Explore/Search (songs recs)
* Profile (own stats)
* Playlist Builder
* Friend Profile

**Flow Navigation** (Screen to Screen)

* Home Feed
   * Friend Profile Page
   * Profile page
   * Explore
* Log In/Register
   * Home Feed
* Explore
    * Creation screen
* Creation Screen
    * Explore (once playlist has been created)

## Wireframes

https://www.figma.com/file/8UO2SOtI10DGsosmGCaiQM/Untitled?node-id=0%3A1

## Schema 

### Models


| Model | Description |
| -------- | -------- |
| Song | Each song has its title, artist, album, etc. and also has its mood measurement. |
| User | Users have their usernames, passwords, current status, stats, and playlists |
| Playlists | Playlists have songs, author, mood stats |


### Networking
- Home Feed Screen
    - (Read/GET) Query all friend's statuses
    - (Create/POST) Create a new like on a friend's status
    - (Delete) Delete existing like
    - (Create/POST) Create a new comment on a friend status
    - (Delete) Delete existing comment
- Explore Screen
    - (Read/GET) Query all recommended songs (divided by moods)
    - (Read/GET) Query specifically searched songs
- Create Playlist Screen
    - (Create/POST) Create a new playlist object
- Profile Screen
    - (Read/GET) Query logged in user object
    
    
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]



# Schedule

- Week 4:
    - Create log in/sign up page with spotify accounts.
    - Spotify API implementation.
    - Begin Design models: users, songs.
        - Implement mood measurement for songs. 
 - Week 5:
    - Finish models
    - Implement Bottom Navigation View
    - Implement main screen archtypes (feed, profile, and explore)
        
    
