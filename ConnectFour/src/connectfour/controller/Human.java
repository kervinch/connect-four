package connectfour.controller;

import connectfour.model.Model;

public class Human implements Player {

	private Model board;

	public Human(Model board) {
		this.board = board;
	}

	public int move(int col) {
		int counter = 3 - board.getLastCounterPlaced();
		if (!board.placeCounter(col, counter)) {
			return -2;// move was not successful
		}
		if (board.gameOver()) {
			return board.getWinner();
		}
		return -1;// game not over
	}

}
