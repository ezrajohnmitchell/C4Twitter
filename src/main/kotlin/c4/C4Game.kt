package c4

class C4Game(private val board : Board = Board(), var isTerminal : Boolean= false) {

    /**
     * Input player column (1-7), updates player turn and runs ai turn, choosing a full row will result in loss of a turn
     * Returns string for tweet
     */
    fun runTurn(playerColumn: Int) : String{
        //update player turn
        board.addPiece(Piece.PLAYER, playerColumn)

        //check player win
        if(board.hasWon() == Piece.PLAYER) {
            isTerminal = true
            return "Player wins"
        }

        //do computer turn
        minimaxHelper()

        return board.toString()
    }

    /**
     * Start minimax function
     */
    private fun minimaxHelper(){
        var move = -1
        //min because computer is minimizer
        var min = 1000
        board.generateMoves().forEach {
            val value = minimax(board.createNew().apply { addPiece(Piece.COMPUTER, it) }, 0, false)
            if(value <= min){
                min = value
                move = it
            }
        }
        board.addPiece(Piece.COMPUTER, move)
    }

    /**
     * Minimax algorithm, player is maximizer
     */
    private fun minimax(virtualBoard: Board, depth : Int, minimizing: Boolean) : Int{

        //base conditions
        when(virtualBoard.hasWon()){
            Piece.PLAYER -> return 1000 - depth
            Piece.COMPUTER -> return -1000 + depth
            Piece.EMPTY -> Unit
        }
        //depth influences difficulty
        if(depth >= 4) {
            val score = virtualBoard.evaluate()
            return score + (depth + if (score < 0) 1 else -1)
        }

        //TODO Add check to see if boardState has already been evaluated
        return if(minimizing){
            var min = 1000
            virtualBoard.generateMoves().forEach {
                min = minimax(virtualBoard.createNew().apply { addPiece(Piece.PLAYER, it) }, depth + 1, false).coerceAtMost(min)
            }
            min
        } else{
            var max = -1000
            virtualBoard.generateMoves().forEach {
                max = minimax(virtualBoard.createNew().apply { addPiece(Piece.COMPUTER, it) }, depth + 1, true).coerceAtLeast(max)
            }
            max
        }
    }

    override fun toString() : String{
        return board.toString()
    }

}