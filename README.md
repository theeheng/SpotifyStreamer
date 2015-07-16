# SpotifyStreamer
Nanodegree Project - Spotify Streamer

![](https://raw.githubusercontent.com/theeheng/samples/demo1.gif)
![](https://raw.githubusercontent.com/theeheng/samples/demo2.gif)
![](https://raw.githubusercontent.com/theeheng/samples/demo3.gif)


User Interface - Layout  
----------------------- 
 
[Phone] UI contains a screen for searching for an artist and displaying a list of artist results
Individual artist result layout contains - Artist Thumbnail , Artist name
 
[Phone] UI contains a screen for displaying the top tracks for a selected artist
Individual track layout contains - Album art thumbnail, track name, album name
 
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
 

