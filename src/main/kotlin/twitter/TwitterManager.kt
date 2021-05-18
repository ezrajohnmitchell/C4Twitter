package twitter

import twitter4j.Status
import twitter4j.TwitterFactory
import twitter4j.Twitter
import java.util.*

class TwitterManager(boardString: String){
    private val twit: Twitter = TwitterFactory.getSingleton()
    private var tweetListener : ResponseListener
    private val timer = Timer()

    init {
        val tweet = twit.updateStatus(boardString)
        tweetListener = ResponseListener(tweet, twit)
        timer.scheduleAtFixedRate(tweetListener, 0, 10000)
    }

    fun postBoard(boardString: String){
        tweetListener.cancel()
        val tweet = twit.updateStatus(boardString)
        tweetListener = ResponseListener(tweet, twit)
        timer.scheduleAtFixedRate(tweetListener, 0, 10000)
    }

    fun getMove() : Int{
        return tweetListener.getVote()
    }

    fun close(){
        tweetListener.cancel()
        twit.updateStatus("Im not active right now :(")
    }

    fun hasVotes() : Boolean{
        return tweetListener.hasVotes()
    }
}