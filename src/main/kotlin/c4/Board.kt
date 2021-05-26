package c4

import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

object BoardConstants {
    const val columns = 7
    const val rows = columns - 1
    const val boardLength = columns * rows
    const val suffix = "Come play connect 4! To vote on the next move put \"!vote#\" in a reply to this tweet. # should be between 1 and 7. ⚫ is twitter (only latest tweet is active)"
}

data class Board(var board : MutableList<Piece> = MutableList(BoardConstants.boardLength) { Piece.EMPTY }){

    /** 7 columns, 6 rows
     * Accepted characters E/e empty, P/p player, C/c computer, anything else defaults to empty.
     * BoardString can be separated into columns for readability
     */
    constructor(vararg boardString: String) : this() {

        val bs = boardString.joinToString("").filter { !it.isWhitespace() }.toLowerCase()
        if(bs.length != BoardConstants.boardLength){
            throw java.lang.IllegalArgumentException("Invalid Board")
        }

        bs.forEachIndexed { index, c ->
            when(c){
                'c' -> board[index] = Piece.COMPUTER
                'p' -> board[index] = Piece.PLAYER
                else -> board[index] = Piece.EMPTY
            }
        }

        validateBoard()
    }

    constructor(b: Board) : this(){
        board = mutableListOf(*b.board.toTypedArray())
    }

    fun createNew() : Board {
        return Board(this)
    }

    private fun validateBoard(){
        for(i in ((BoardConstants.rows - 1) * BoardConstants.columns) until BoardConstants.boardLength){
            drop(i, 0)
        }
    }

    private fun drop(columnBottom : Int, depth: Int){
        //base cases
        if(depth >= BoardConstants.rows - 1/* -1 for board indexing, not needed for columns*/) return
        //skip any valid filled spots
        if(board[columnBottom - (depth * BoardConstants.columns)] != Piece.EMPTY) return drop(columnBottom, depth + 1)

        //loop from depth up, for rows - depth
        for(i in (columnBottom - depth * BoardConstants.columns) downTo 0 step BoardConstants.columns){
            //move invalid piece down
            if(board[i] != Piece.EMPTY){
                board[columnBottom - depth * BoardConstants.columns] = board[i]
                board[i] = Piece.EMPTY
                //check for any more pieces
                return drop(columnBottom, depth + 1)
            }
        }
    }

    /**
     * Adds piece, column is between  1 - BoardConstants.columns (7)
     */
    fun addPiece(type : Piece, column : Int){
        //drop piece down column, board is row major

        for(i in BoardConstants.rows - 1 downTo 0){
            val index = (BoardConstants.columns * i) + (column).coerceIn(1, BoardConstants.columns) - 1
            if(board[index] == Piece.EMPTY){
                board[index] = type
                break
            }
        }
    }

    fun reset(){
        board.replaceAll { Piece.EMPTY }
    }

    fun generateMoves() :List<Int>{
        val moves : MutableList<Int> = mutableListOf()
        for(i in  1..7){
            if(validColumn(i)) moves.add(i)
        }
        return moves.toList()
    }

    private fun validColumn(column: Int) : Boolean{
        if(column in 0..6 && board[column] == Piece.EMPTY) return true
        return false
    }

    override fun toString() : String{
        val sb = StringBuilder()

        for(i in board.indices){
            sb.append(board[i].character)
            if ((i + 1)% BoardConstants.columns == 0) sb.append("\n")
        }
        when(hasWon()){
            Piece.PLAYER -> sb.append("Twitter Wins!")
            Piece.COMPUTER -> sb.append("The bot wins!")
            else -> sb.append(BoardConstants.suffix)
        }
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        sb.append("\n")
        sb.append(currentDate)
        return sb.toString()
    }

    /**
     * Returns winning Piece and Piece.EMPTY on no win
     */
    fun hasWon() : Piece {

        var inRow = 0
        var lastPiece = Piece.EMPTY
        //check horizontal
        for(i in board.indices){

            if(lastPiece == board[i] && lastPiece != Piece.EMPTY) inRow++ else inRow = 0
            lastPiece = board[i]

            if(inRow >= 3) return lastPiece

            //reset at the end of each row
            if((i + 1) % BoardConstants.columns == 0) {
                inRow = 0
                lastPiece = Piece.EMPTY
            }
        }


        //check vertical
        inRow = 0
        lastPiece = Piece.EMPTY
        for(i in 0 until BoardConstants.columns){
            for(j in 0 until BoardConstants.rows){
                val index = (j * BoardConstants.columns) + i

                if(lastPiece == board[index] && lastPiece != Piece.EMPTY) inRow++ else inRow = 0
                lastPiece = board[index]

                if(inRow >= 3) return lastPiece
            }
            inRow = 0
            lastPiece = Piece.EMPTY
        }

        //down right upper half
        //iterates columns starts (except 0)
        var iterations = BoardConstants.rows - 1
        for(start in 1 until BoardConstants.columns){
            val piece = iDiagonal(start, iterations, BoardConstants.columns + 1)
            if(piece != Piece.EMPTY) return piece
            iterations--
        }

        //down right lower half
        iterations = BoardConstants.rows - 1
        //for loop traverses down the first column
        for(start in 0..(BoardConstants.columns * (BoardConstants.rows - 1)) step BoardConstants.columns){
            val piece = iDiagonal(start, iterations, BoardConstants.columns + 1)
            if(piece != Piece.EMPTY) return piece
            iterations--
        }


        //down left upper half
        iterations = BoardConstants.rows - 1

        //traverse column starts
        for(start in BoardConstants.columns - 2 downTo 0){
            val piece = iDiagonal(start, iterations, BoardConstants.columns - 1)
            if(piece != Piece.EMPTY) return piece
            iterations--
        }

        //down left lower half
        iterations = BoardConstants.rows - 1
        for(start in (BoardConstants.columns - 1)..(BoardConstants.columns * (BoardConstants.rows - 1)) step BoardConstants.columns){
            val piece = iDiagonal(start, iterations, BoardConstants.columns - 1)
            if(piece != Piece.EMPTY) return piece
            iterations--
        }

        return Piece.EMPTY
    }

    fun isDraw() : Boolean{
        return hasWon() == Piece.EMPTY && board.filter { it != Piece.EMPTY }.size < board.size
    }

    private fun iDiagonal(start: Int, iterations: Int, diagonalStep : Int) : Piece {

        var inRow = 0
        var lastPiece = Piece.EMPTY

        for (add in 0..(iterations * diagonalStep) step diagonalStep) {
            val index = start + add

            if (lastPiece == board[index] && lastPiece != Piece.EMPTY) inRow++ else inRow = 0
            lastPiece = board[index]

            if (inRow >= 3) return lastPiece
        }

        return Piece.EMPTY
    }


    /**
     * Evaluation function, computer is minimizer
     */
    fun evaluate() : Int {
        val win = hasWon()
        if(win == Piece.PLAYER) return 1000
        else if(win == Piece.COMPUTER) return -1000

        //scoring
        var score = 0

        //run countLine in all directions
        //horizontal + down right lower half start nodes
        for(i in 0 until BoardConstants.boardLength step BoardConstants.columns){
            //horizontal
            score += countLine(i, 1, Piece.PLAYER, Piece.COMPUTER)
            score -= countLine(i, 1, Piece.COMPUTER, Piece.PLAYER)
            //down right
            score += countLine(i, BoardConstants.columns + 1, Piece.PLAYER, Piece.COMPUTER)
            score -= countLine(i, BoardConstants.columns + 1, Piece.COMPUTER, Piece.PLAYER)
            //down left, transform to other side of board
            score += countLine(i + BoardConstants.columns - 1, BoardConstants.columns - 1, Piece.PLAYER, Piece.COMPUTER)
            score -= countLine(i + BoardConstants.columns - 1, BoardConstants.columns - 1, Piece.COMPUTER, Piece.PLAYER)
        }
        //vertical + down right upper half start nodes
        for(i in 0 until BoardConstants.columns){
            //vertical
            //score += countLine(i, BoardConstants.columns, Piece.PLAYER, Piece.COMPUTER)
            //score -= countLine(i, BoardConstants.columns, Piece.COMPUTER, Piece.PLAYER)
            //down right
            score += countLine(i, BoardConstants.columns + 1, Piece.PLAYER, Piece.COMPUTER)
            score -= countLine(i, BoardConstants.columns + 1, Piece.COMPUTER, Piece.PLAYER)
            //down left
            score += countLine(i, BoardConstants.columns - 1, Piece.PLAYER, Piece.COMPUTER)
            score -= countLine(i, BoardConstants.columns - 1, Piece.COMPUTER, Piece.PLAYER)
        }

        return score
    }

    /**
     * Returns score of
     */
    private fun countLine(start: Int, direction: Int, scoringPiece: Piece, blockingPiece: Piece) : Int {
        //construct row and parallel index array
        val row : MutableList<Piece> = mutableListOf()
        val rowI : MutableList<Int> = mutableListOf()

        var index = start
        var finished = false
        while(!finished){
            row.add(board[index])
            rowI.add(index)
            if(checkDirection(index, direction)) index += direction
            else finished = true
        }


        var score = 0
        for(i in 0 until (row.size - 3)){
            //construct subRow for every sequential group of 4
            val subRow = row.subList(i, i + 4)
            val subRowI = rowI.subList(i, i + 4)

            var subScore = 0
            //score subRow

            //1 point if not blocked
            //blocked if not all placeable or other piece exists
            var blocked : Boolean
            blocked = subRow.contains(blockingPiece)

            for(j in 0 until subRowI.size){
                if(subRow[j] == Piece.EMPTY && !placeable(subRowI[j])) blocked = true
            }

            //4 points if three in a row
            if(!blocked) {
                subScore = subRow.filter { it == scoringPiece }.count()
                subScore = when(subScore){
                    3 -> 10
                    else -> subScore
                }
            }
            score += subScore
        }

        return score
    }

    private fun checkDirection(i: Int, direction: Int) : Boolean{

        val c = BoardConstants.columns
        if(i + direction !in 0 until BoardConstants.boardLength) return false
        return when{
            (i + 1) % c == 0 -> when(direction){
                -c + 1, 1, c + 1 -> false
                else -> true
            }
            i % c == 0 -> when(direction){
                (-c - 1), -1, c - 1 -> false
                else -> true
            }
            else -> true
        }
    }



    /**
     * Returns list of indices in all valid directions
     */
    private fun getDirectionsMatrix(i : Int) : List<Int>{
        val c = BoardConstants.columns

        val tLeft = listOf(-c - 1, -1, c - 1)
        val tCenter = listOf(-c, c)
        val tRight = listOf(-c + 1, 1, c + 1)

        val tMatrix : MutableList<Int> = when{
            i % c == 0 -> (tRight + tCenter) as MutableList<Int>
            (i + 1) % c == 0 -> (tLeft + tCenter) as MutableList<Int>
            else -> (tLeft + tCenter + tRight) as MutableList<Int>
        }

        return tMatrix.filter { it + i >= 0 && it + i< BoardConstants.boardLength }.sorted()
    }

     fun placeable(index: Int) : Boolean{
        val check = index + BoardConstants.columns
        return when{
            check >= BoardConstants.boardLength -> true
            check < 0 -> false
            else -> board[check] != Piece.EMPTY
        }
    }
}



