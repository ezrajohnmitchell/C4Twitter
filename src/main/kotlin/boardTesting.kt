import c4.Board
import c4.C4Game
import c4.Piece

fun main() {
    val game = C4Game()
    println(game.toString())
    while (!game.isTerminal) {
        println(game.runTurn(readLine()!!.toInt()))
    }
//    val board = Board()
//
//    board.addPiece(Piece.COMPUTER, 4)
//    board.addPiece(Piece.PLAYER, 4)
//    board.addPiece(Piece.COMPUTER, 5)
//    board.addPiece(Piece.PLAYER, 5)
//    board.addPiece(Piece.COMPUTER, 6)
//    board.addPiece(Piece.PLAYER, 6)
//
//    println(board)
//    println(board.evaluate())
}

