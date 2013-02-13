package connectfour.controller;

import connectfour.common.AsyncCallback;
import connectfour.controller.ai.Computer;
import connectfour.model.GameBoard;
import connectfour.view.GameView;

public class GameController {

	private final int INIT_SEARCH_DEPTH = 8; //6 = 137257 nodes (for 1st move)[=(7^0)+(7^1)+(7^2)+(7^3)+(7^4)+(7^5)+(7^6)]
									    //with 6 & 5 comp only game red wins (no randomness)
										//with 4 comp only game drawn (no randomness)
										//with 3 & 2 comp only game yellow wins (no randomness)
										//red always starts
										//in general depth should be >= 4 for reliable human vs comp play
	private final boolean INIT_DET_AI = true;
	private final long INIT_TIME_LIMIT = 1500;// time limit for a computer move in milliseconds
	
	private Human human;
	private Computer computer;
	//private ExecutorService executor;
	
	private GameBoard board;
	private AsyncCallback<Integer> gv;
	
	private GameController() {

		board = new GameBoard();
		human = new Human(board);
		computer = new Computer(board, INIT_DET_AI,
				INIT_TIME_LIMIT, INIT_SEARCH_DEPTH);
		
		//executor = Executors.newSingleThreadExecutor();
		
		gv = new GameView(this, board, INIT_DET_AI, INIT_SEARCH_DEPTH, INIT_TIME_LIMIT);
		
		/*
		final GameController gc = this;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gv = new GameView(gc, board, INIT_DET_AI, INIT_SEARCH_DEPTH, INIT_TIME_LIMIT);
			}
		});
		*/

	}
	
	public void reset() {
		board.resetGameBoard();
	}
	
	public void undo() {
		board.undoMove();
	}
	
	public void makeComputerMove(/*AsyncCallback<Integer> callback*/) {
		
		int result = -1;
		try {
			int counter = 3 - board.getLastCounterPlaced();
			result = computer.move(computer.chooseMove(counter), counter);
		} catch (Exception e) {
			gv.onFailure(e);
		}
		gv.onSuccess(result);
		
	}
	
	public void setDeterministicAI(boolean deterministicAI) {
		computer.setDeterministicAI(deterministicAI);
	}
	
	public void setTimeLimitedAI(boolean timeLimited) {
		computer.setTimeLimited(timeLimited);
	}
	
	public void setAISearchDepth(int searchDepth) {
		computer.setSearchDepth(searchDepth);
	}
	
	public void setAITimeLimit(long timeLimit) {
		computer.setTimeLimit(timeLimit);
	}
	
	public void cancelAIMove() {
		computer.stop();
	}
	
	public int makeHumanMove(int col) {
		int counter = 3 - board.getLastCounterPlaced();
		return human.move(col, counter);
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
		new GameController();
	}

}