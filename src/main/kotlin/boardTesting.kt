import c4.Board
import c4.Piece

fun main() {
    val board = Board(
        "eeeeeee",
        "peeceee",
        "eeeeeee",
        "eeeeeee",
        "eeeeeee",
        "eeepeee"
    )
    println(board)
    println(board.getDirectionsMatrix(41))
}

