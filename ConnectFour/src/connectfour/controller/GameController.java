package connectfour.controller;

import connectfour.common.AsyncCallback;
import connectfour.controller.ai.Computer;
import connectfour.model.GameBoard;

public class GameController {

	private Human human;
	private Computer computer;
	private GameBoard board;
	
	public GameController(GameBoard board, boolean INIT_DET_AI, long INIT_TIME_LIMIT, int INIT_SEARCH_DEPTH) {

		this.board = board;
		human = new Human(this.board);
		computer = new Computer(this.board, INIT_DET_AI,
				INIT_TIME_LIMIT, INIT_SEARCH_DEPTH);

	}
	
	public void reset() {
		board.resetGameBoard();
	}
	
	public void undo() {
		board.undoMove();
	}
	
	public void makeComputerMove(AsyncCallback<Integer> callback) {
		
		int result = -1;
		try {
			result = computer.move();
		} catch (Exception e) {
			callback.onFailure(e);
		}
		callback.onSuccess(result);
		
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
		return human.move(col);
	}

}