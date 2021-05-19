# Connect 4 Twitter bot
Simple minimax connect 4 bot in kotlin
## Overview
This bot will post a tweet containing a connect 4 board and collect votes from the replies of that tweet in a background thread. Every 30 minutes the majority vote decides the next player move. The computer then uses minimax to determine its move and posts a new tweet.

<img src="https://user-images.githubusercontent.com/55267002/118886145-ce05a580-b8b5-11eb-9a4d-aed6135881cd.PNG" alt="One tweet example" width = "40%"/>
<img src="https://user-images.githubusercontent.com/55267002/118886209-e1b10c00-b8b5-11eb-8521-99e37e11be0a.PNG" alt="Two tweet example" width = "40%"/>


## Libraries
The only external library used is Twitter4j
https://github.com/Twitter4J/Twitter4J

## For the future
Optimizing the minimax algorithm is my main concern. Currently the evaluation function is weak and boardstates are sometimes evaluated multiple times. A boardstate check could potentially decrease the amount of time spent in the algorithm. A stronger evaluation function would allow for less depth during minimax.
