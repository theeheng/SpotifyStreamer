# SpotifyStreamer
Nanodegree Project - Spotify Streamer

![](https://raw.githubusercontent.com/theeheng/SpotifyStreamer/master/samples/demo1.gif)
![](https://raw.githubusercontent.com/theeheng/SpotifyStreamer/master/samples/demo2.gif)
![](https://raw.githubusercontent.com/theeheng/SpotifyStreamer/master/samples/demo3.gif)

User Interface - Layout  
----------------------- 
 
[Phone] UI contains a screen for searching for an artist and displaying a list of artist results
Individual artist result layout contains - Artist Thumbnail , Artist name
 
[Phone] UI contains a screen for displaying the top tracks for a selected artist
Individual track layout contains - Album art thumbnail, track name, album name
 
[Phone] UI contains a screen that represents the player. It contains  playback controls for the currently selected track
Tablet UI uses a Master-Detail layout implemented using fragments. The left fragment is for searching artists and the right fragment is for displaying top tracks of a selected artist. The Now Playing controls are displayed in a DialogFragment.
 
 
User Interface - Function 
------------------------- 
 
- App contains a search field that allows the user to enter in the name of an artist to search for 
- When an artist name is entered, app displays list of artist results 
- App displays a message (for example, a toast) if the artist name/top tracks list for an artist is not found (asks to refine search) 
- When an artist is selected, app launches the “Top Tracks” View
- App displays a list of top tracks
- Network API Implementation  
- App implements Artist Search + GetTopTracks API Requests (Using the Spotify wrapper or by making a HTTP request and deserializing the JSON data)
- App stores the most recent top tracks query results and their respective metadata (track name, artist name, album name) locally in list.
- The queried results are retained on rotation.
- When a track is selected, app uses an Intent to launch the Now playing screen and starts playback of the track.
 
Network API Implementation  
-------------------------- 
 
- App implements Artist Search + GetTopTracks API Requests (Using the Spotify wrapper or by making a HTTP request and deserializing the JSON data)
- App stores the most recent top tracks query results and their respective metadata (track name , artist name, album name) locally in list.
- The queried results are retained on rotation.
 
Media Playback 
-------------- 
- App implements streaming playback of tracks
- User is able to advance to the previous track
- User is able to advance to the next track
- Play button starts/resumes playback of currently selected track
- Pause button pauses playback of currently selected track
- If a user taps on another track while one is currently playing, playback is stopped on the currently playing track and the newly selected track (in other words, the tracks should not mix)
 

