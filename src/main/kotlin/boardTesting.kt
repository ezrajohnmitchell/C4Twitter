import c4.Board
import c4.Piece

fun main() {
    val board = Board(
        "eeeeeee",
        "eeeeeee",
        "eeeeeee",
        "eeeeeee",
        "eeeceee",
        "eeccccp"
    )
    println(board)
    println(board.evaluate())
}

