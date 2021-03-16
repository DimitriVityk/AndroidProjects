# AndroidProjects
A collection of my android projects

Defense Commander
---------------
- This app is a game, a modern take on an 80’s era video game where the user defends their bases against
missile attacks
- Major entities in the app include bases, incoming ballistic missiles, and interceptors.
- The game increases in difficulty as time passes (i.e., Levels)
Walking Tours
- Incoming missiles appear from the top of the screen and head towards the bottom where they will
detonate, potentially destroying your bases
- You launch interceptors to destroy the incoming missiles in flight before they hit your bases
- Interceptors are launched by a finger-tap
- When your bases are gone, the game is over
- A remote database is used to track the top 10 scores among all players
- Scoring in the top 10 allows the user to enter their initials

Walking Tours
------------
- This app is your own personal walking tour guide for an extensive number of Chicago's famous
architectural "must-see" buildings such as Aqua Tower, Willis Tower, Chicago Theatre, or Rookery Building.
Use this self-guided walking tour to explore the most famous constructions of the Loop District, Chicago.
- The Walking Tours app notifies the user of nearby architectural wonders when they are near the building
location. Tapping on a notification displays information such as a photo of the building, the building name,
address, and a description of its architectural significance
- The user’s live location is used to update their map position.
- The Google Maps Activity makes use of location data provided continuously by a Location Listener to
determine the user’s current location and to plot their location on the map.
- The map continuously centers itself on the user’s current location. The current location is shown via an
icon. Their path on the map is displayed as a solid
line that marks where they have been.
- Geofences are loaded from a cloud-based source so they can change and update without requiring app
updates (http://www.christopherhield.com/data/WalkingTourContent.json). As the user moves, they are
alerted if they enter the range of any of the Geofences.

NewsGateway:
------------
* This app displays current news articles from a wide variety of news sources covering a range of news
categories.  
* NewsAPI.org is used to acquire the news sources and news articles.  
* Selecting a news source (i.e., CNN, Time, etc.) will display up to 10 top stories from that news source.  
* Selecting a topic will limit the news source choices to only those offering that topic of news.  
* Selecting a country will limit the news source choices to those from the selected country.  
* Selecting a language will limit the news source choices to only those offering news in the selected  
language.
* News articles are viewed by swiping right to read the next article, and left to go back to the previous
article
* The user can go to the complete extended article on the news source’s website by clicking on the
article title, text, or image content.

RewardsApp:
-----------
* This app allows users within a company or organization to award each other “reward points” (and
comments) as a thank you or a commendation for their performance on a task.
* Users can create a profile for themselves and interact with the other users. Users can later edit/update
their profile data and delete their profile.
* Users start with a fixed number of points that they can award to other users.
* When a user awards points to another user, their point value is reduced by the amount they give. Once a
user is out of points to give, they can no longer add new rewards until their points are renewed. Point
renewal is not within the scope of this project.
* Assume the process of resetting the points-to-award value (and the actual act of rewarding the
employee) are out of scope for this project.
* Classes were created to represent the user profiles and the reward assignments.
* Instructor's API was used as the “back end” which holds user and reward data.  

***IMPORTANT***  
When this application runs, it will prompt the user to request an API key. The application will not run without this API key.   
Use the credentials below to gain access to the rest of the application.    
First Name: Dimitri  
Last Name: Vityk  
Student Email: dvityk@depaul.edu  
StudentID: 1902614  

KnowYourGov:
------------
* This app will acquire and display an interactive list of political officials that represent the current
location (or a specified location) at each level of government.
* Android location services will be used to determine the user’s location.
* The Google Civic Information API will be used to acquire the government official data (via REST service
and JSON results).
* Clicking on an official’s list entry opens a detailed view of that individual government representative.
* Clicking on the photo of an official will display a Photo Activity, showing a larger version of the photo.
* An “About” activity will show application information (Author, Copyright data & Version)

StockWatch:
----------
* This app allows the user to display a sorted list of selected stocks. List entries include the stock symbol,
company name, the current price, the daily price change amount and price percent change.
* Selected stock symbols and the related names are stored in a JSON file on the device.
* A Stock class was created to represent each individual stock in the application. Data includes:
Stock Symbol (String), Company Name (String), Price (double), Price Change (double), and Change Percentage
(double).
* Clicking on a stock entry opens a browser displaying the Market Watch web page for the selected stock
* Swipe-Refresh (pull-down) refreshes stock data

Notepad:
--------
* This app allows the creation and maintenance of multiple notes. Any number of notes are allowed (including no
notes at all). Notes are made up of a title, a note text, and a last-update time.
* One layout works fine in either orientation.
* Notes are saved to (and loaded from) the internal file system in JSON format. If no file is found upon
loading, the application should start with no existing notes and no errors. (A new JSON file would then be
created when new notes are saved).
* JSON file loading happens in the onCreate method. Saving happens in the onPause method.
* A simple java Note class (with title, note text, and last save date) was created to represent each individual
note in the application.

HomeworkOne:
---------
* This is the first Android application I have ever created.
The app should allow the user to select either Miles-to-Kilometers or Kilometers-to-Miles
conversions. Conversion is selected using Radio Buttons. Miles-to-Kilometers should be the default
if there is no saved preference.
* Uses the following formulae for conversion:
    Miles to Kilometers: Mi * 1.60934 Example: 25mi * 1.60934 = 40.2 km
    Kilometers to Miles: Km * 0.621371Example: 60km * 0.621371 = 37.3 mi
* The distance value (the value to be converted) is entered by the user. Values can be
positive or negative. Only numeric whole or decimal values are allowed.
* Pressing the Convert button will clear the input field, generate the converted distance value and
display it on the screen.
* All conversion operations and their results are added to the “history” – a list of converted
values. The history of converted values is scrollable and displayed with the most recent
conversions at the top of the list.
