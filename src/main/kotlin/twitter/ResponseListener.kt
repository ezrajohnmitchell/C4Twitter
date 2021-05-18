package twitter

import java.util.*
import twitter4j.Status
import twitter4j.Twitter
import java.lang.Exception

class ResponseListener(private val tweet : Status, private val twitter : Twitter) : TimerTask() {
    private var votes : MutableList<Int> = MutableList(7) { 0 }
    override fun run() {
        val mentions = twitter.mentionsTimeline
        for (reply in mentions){
            try{
                if(reply.inReplyToStatusId == tweet.id) {
                    val vote = reply.text[reply.text.indexOf("!vote") + 5].toString().toInt() - 1
                    if(vote in 0..6) votes[vote] += 1
                }
            } catch (ignored : Exception){
            }
        }
    }

    fun getVote() : Int {
        var max = -1
        var maxI = -1
        votes.forEachIndexed { index, num ->
            if(num > max){
                max = num
                maxI = index
            }
        }
        return maxI + 1
    }

    fun hasVotes() : Boolean {
        return votes.filter { it == 0 }.size != 7
    }
}