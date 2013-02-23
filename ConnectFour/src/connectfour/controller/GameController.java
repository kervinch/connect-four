package connectfour.controller;

import java.util.ArrayList;
import java.util.List;

import connectfour.controller.ai.MultiAi;
import connectfour.controller.ai.MultiAiImpl;
import connectfour.model.Model;
import connectfour.view.View;

public class GameController implements Controller {

	private Player human;
	private MultiAi ai;
	private Model board;
	private List<View> views;
	private int lastMoveResult;
	
	public GameController(Model board, boolean INIT_DET_AI, boolean INIT_TIME_LIMITED, long INIT_TIME_LIMIT, int INIT_DEPTH_LIMIT) {

		this.board = board;
		human = new Human(this.board);
		ai = new MultiAiImpl(this.board, INIT_DET_AI, INIT_TIME_LIMITED,
				INIT_TIME_LIMIT, INIT_DEPTH_LIMIT);
		
		views = new ArrayList<View>();

	}
	
	public void addView(View view) {
		views.add(view);
	}
	
	public void removeView(View view) {
		views.remove(view);
	}
	
	public void reset() {
		board.resetGameBoard();
	}
	
	public void undo() {
		board.undoMove();
	}
	
	public void makeComputerMove() {
		
		for (View view : views) {
			view.onComputerStartMove();
		}
		
		lastMoveResult = ai.move();
		
		for (View view : views) {
			view.onComputerEndMove();
		}
		
	}
	
	public int getLastMoveResult() {
		return lastMoveResult;
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
	
	public void makeHumanMove(int col) {
		
		for (View view : views) {
			view.onHumanStartMove();
		}
		
		lastMoveResult = human.move(col);
		
		for (View view : views) {
			view.onHumanEndMove();
		}
		
	}

}