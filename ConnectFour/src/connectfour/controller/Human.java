package connectfour.controller;

import connectfour.model.GameBoard;

public class Human implements Player {

	private GameBoard board;

	public Human(GameBoard board) {
		this.board = board;
	}

	public int move(int col, int counter) {
		if (!board.placeCounter(col, counter)) {
			return -2;// move was not successful
		}
		if (board.gameOver()) {
			return board.getWinner();
		}
		return -1;// game not over
	}

}
