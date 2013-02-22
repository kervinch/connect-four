package connectfour;

import connectfour.controller.Controller;
import connectfour.controller.GameController;
import connectfour.model.GameBoard;
import connectfour.model.Model;
import connectfour.view.GameView;
import connectfour.view.View;

public class Game {

	private final boolean INIT_DET_AI = true;
	private final boolean INIT_TIME_LIMITED = true;
	private final long INIT_TIME_LIMIT = 1500;// time limit for a computer move in milliseconds
	private final int INIT_SEARCH_DEPTH = 8; //6 = 137257 nodes (for 1st move)[=(7^0)+(7^1)+(7^2)+(7^3)+(7^4)+(7^5)+(7^6)]
    										 //with 6 & 5 comp only game red wins (no randomness)
											 //with 4 comp only game drawn (no randomness)
											 //with 3 & 2 comp only game yellow wins (no randomness)
											 //red always starts
											 //in general depth should be >= 4 for reliable human vs comp play
	
	private Model board;
	private View gv;
	private Controller gc;
	
	public Game() {
		
		board = new GameBoard();
		gc = new GameController(board, INIT_DET_AI, INIT_TIME_LIMITED, INIT_TIME_LIMIT, INIT_SEARCH_DEPTH);
		gv = new GameView(gc, board, INIT_DET_AI, INIT_TIME_LIMITED, INIT_TIME_LIMIT, INIT_SEARCH_DEPTH);
		
		board.addView(gv);
		gc.addView(gv);
		
	}
	
	public static void main(String[] args) {
		/*
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Game();
			}
		});
		*/
		/*
		new Thread(new Runnable() {
			public void run() {
				new Game();
			}
		}).start();
		*/
		new Game();
	}
}
