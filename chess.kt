package chess

import kotlin.math.abs

fun main() {
    val board = Chessboard
    board.gameLoop()
}

object Chessboard {
    private const val WHITE = 1
    private const val BLACK = -1

    data class Move(val sf: Int, val rs: Int, val ef: Int, val er: Int)
    data class Pawn(val file: Int, val rank: Int, val color: Int)

    private var pawnSet = mutableListOf<Pawn>()
    private var movesList = mutableListOf<Move>()
    private var captureList = mutableListOf<Move>()
    private val NO_GHOST = Pawn(0,0,0)
    private var ghost = NO_GHOST

    init {
        for (i in 1..8) {
            pawnSet.add(Pawn(i, 2, WHITE))
            pawnSet.add(Pawn(i, 7, BLACK))
        }
    }

    private fun hasLost(player: Int): Boolean {
        for (pawn in pawnSet) if (pawn.color == player) return false
        return true
    }

    private fun hasCross(player: Int): Boolean {
        val winRank = if (player == WHITE) 8 else 1
        for (pawn in pawnSet) if (pawn.rank == winRank) return true
        return false
    }

    private fun possibleMoves(player: Int): Int {
        movesList.clear()
        captureList.clear()
        for (pawn in pawnSet) {
            if (pawn.color == -player) continue
            val sf = pawn.file
            val sr = pawn.rank
            // Déplacements possible
            val startRank = if (player == WHITE) 2 else 7
            val max = if (sr == startRank) 2 else 1
            for (i in 1..max) {
                val pr = sr + i * player
                if (!pawnSet.contains(Pawn(sf, pr, -player)) && !pawnSet.contains(Pawn(sf, pr, player))) {
                    movesList.add(Move(sf, sr, sf, pr))
                }
                else break
            }
            // Captures possible
            val pr = sr + 1 * player
            if (pawnSet.contains(Pawn(sf - 1, pr, -player)) || ghost == Pawn(sf - 1, pr, -player) )
                captureList.add(Move(sf, sr,sf - 1, pr))
            if (pawnSet.contains(Pawn(sf + 1, pr, -player)) || ghost == Pawn(sf + 1, pr, -player) )
                captureList.add(Move(sf, sr,sf + 1, pr))
        }

        return movesList.size + captureList.size
    }

    private fun move(input: String, player: Int): Boolean {
        val (a, b, c, d) = input.toCharArray()
        val sf = a.code - 96
        val sr = b.code - 48
        val ef = c.code - 96
        val er = d.code - 48
        if (!pawnSet.contains(Pawn(sf, sr, player))) {
            println("No ${if (player == WHITE) "white" else "black"} pawn at $a$b")
            return false
        }
        if (Move(sf, sr, ef, er) in movesList) {
            pawnSet.remove(Pawn(sf, sr, player))
            pawnSet.add(Pawn(ef, er, player))
        } else if (Move(sf, sr, ef, er) in captureList) {
            pawnSet.remove(Pawn(sf, sr, player))
            pawnSet.add(Pawn(ef, er, player))
            if (Pawn(ef, er, -player) == ghost) pawnSet.remove(Pawn(ef, er + 1 * player, -player))
            else pawnSet.remove(Pawn(ef, er, -player))
        } else {
                println("Invalid input")
                return false
            }
        printBoard()
        return true
    }


    fun gameLoop() {
        val regex = "[a-h][1-8][a-h][1-8]".toRegex()
        println(" Pawns-Only Chess")
        println("First Player's name:")
        val p1 = readln()
        println("Second Player's name:")
        val p2 = readln()
        printBoard()
        print("\n")
        var player = WHITE
        possibleMoves(player)
        loop@ while (true) {
            println("${if (player == WHITE) p1 else p2}'s turn:")
            val input = readln()
            if (input == "exit") break
            if (input.matches(regex)) {
                if (move(input, player)) {
                    if (hasLost(-player) || hasCross(player)) {
                        println("${if (player == WHITE) "White" else "Black"} Wins!")
                        break@loop
                    }
                    if (player == -ghost.color) ghost = NO_GHOST
                    player = -player
                    if (possibleMoves(player) == 0) {
                        println("Stalemate!")
                        break@loop
                    }
                }
            } else println("Invalid Input")
        }
        println("Bye!")
    }

    private fun printBoard() {
        val line = "  +---+---+---+---+---+---+---+---+"
        val bottom = "    a   b   c   d   e   f   g   h"
        val white = " W |"
        val black = " B |"
        val empty = "   |"
        println(line)
        for (r in 1..8) {
            print("${9 - r} |")
            for (f in 1..8) {
                if (pawnSet.contains(Pawn(f, 9 - r, WHITE))) print(white)
                else if (pawnSet.contains(Pawn(f, 9 - r, BLACK))) print(black)
                else print(empty)
            }
            println("\n$line")
        }
        println(bottom)
    }

}

