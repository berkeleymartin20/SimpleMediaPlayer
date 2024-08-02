# SimpleMediaPlayer

## Functionalities
* Library to view all of the songs on the application
* Shuffle, Repeat All/One, Previous/Next, and Play/Pause functionalities
* Different icons to indicate status of each functionality (e.g. - shuffle, repeats, play/pause)
* Display for current song's title and artist, which could also serve as a informative tool from the media player (In case we have run out of songs, it will display a text)
* Utilizing fragment-activity architecture, adding additional features in the future should come at ease (such as displaying a consistent "now playing" bar at the bottom of the screen). It consumes less resources than activity-only structure, and having the Library screen as an overlay fragment prevents to need to keep track of state of NowPlaying screen.

### Shuffle
* Utilizing Random class, a random song is played within the library. In case repeat all is not turned on, application will maintain a hashset of previously played songs since repeat all has been turned off, to ensure there will not be a repeat of songs
* Turning on Shuffle will automatically disable Repeat One 
### Repeat One And All
* Repeat All will allow users to play songs in their libraries without being bound by the number of songs
* Repeat One will allow users to listen to the same song over and over again, even if they were to press Next (this will simply restart the song)
* Repeat All and Repeat One are mutually exclusive. Turning one on will automatically turn off the other
* Turning on Repeat One will automatically turn off shuffle
### Play and Pause
* Play and Pause functionalities are combined into one icon. Play/Pause icon will change depending on the mediaplayer's state
### Previous and Next
* Previous functionality utilizes Stack (LIFO) to keep track of what was played previously and polls the recently played songs when "Previous" button is pressed
* For user's convenience, I've added a little text to say "Nothing else on the stack!" to notify the stack has run out and there are no more "previous" songs to listen to
* Next functionality utilizes ArrayList to keep track of which music we are playing from the music list. With the addition of Random class, we are able to add shuffle functionality to the application and using the index of the arraylist allows us to stop playing music when end of the library is reached.
### Library
* At the time of Library Fragment initialization (onViewCreated), the app scans dynamically scans through all of the media files that were used to compile the application in the raw folder. These media (mp3) files are then processed to output title, artist, and duration information in text views of relative layouts
* Added a scrollview in case there were more songs to be added
 
## Possible Future Improvements 
### Code Clean up
* Due to limited time (4 hrs), I disregarded putting constants, such as values for margins, text sizes, etc. in more organized manner (such as static final variables, or with in a constants.xml file, etc.)
* There are some repeated codes within the playNextSong() method in the NowPlaying.kt class. Encapsulation of such repeated code could increase readability of the code

### Functionalities to add
* Add functionality to keep track of previously played music data even after destruction saved instance
* Add functionality to play music via clicking on titles in the library
* Add functionality to see current playback on the track
* Add functionality to queue music 

