package connectfour.controller;

import connectfour.common.AsyncCallback;
import connectfour.controller.ai.MultiAi;
import connectfour.controller.ai.util.Ai;
import connectfour.model.GameBoard;

public class GameController {

	private Player human;
	private Ai ai;
	private GameBoard board;
	
	public GameController(GameBoard board, boolean INIT_DET_AI, boolean INIT_TIME_LIMITED, long INIT_TIME_LIMIT, int INIT_DEPTH_LIMIT) {

		this.board = board;
		human = new Human(this.board);
		ai = new MultiAi(this.board, INIT_DET_AI, INIT_TIME_LIMITED,
				INIT_TIME_LIMIT, INIT_DEPTH_LIMIT);

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
			result = ai.move();
		} catch (Exception e) {
			callback.onFailure(e);
		}
		callback.onSuccess(result);
		
	}
	
	public void setDeterministicAi(boolean deterministicAi) {
		ai.setDeterministic(deterministicAi);
	}
	
	public void setTimeLimitedAi(boolean timeLimited) {
		ai.setTimeLimited(timeLimited);
	}
	
	public void setAiDepthLimit(int depthLimit) {
		ai.setDepthLimit(depthLimit);
	}
	
	public void setAiTimeLimit(long timeLimit) {
		ai.setTimeLimit(timeLimit);
	}
	
	public void cancelAiMove() {
		ai.stop();
	}
	
	public int makeHumanMove(int col) {
		return human.move(col);
	}

}