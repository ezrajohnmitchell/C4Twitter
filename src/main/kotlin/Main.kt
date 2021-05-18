import c4.C4Game
import twitter.TwitterManager
import kotlin.system.exitProcess


fun main(){
    val game = C4Game()
    val twitterManager = TwitterManager(game.toString())

    var noVotesCount = 0

    while(!game.isTerminal && noVotesCount <= 10){
        //gather votes
        Thread.sleep(60000)
        if(twitterManager.hasVotes()) {
            //make move
            game.runTurn(twitterManager.getMove())
            //update tweet
            twitterManager.postBoard(game.toString())
            noVotesCount = 0
        } else noVotesCount++
    }

    twitterManager.close()

    exitProcess(0)
}