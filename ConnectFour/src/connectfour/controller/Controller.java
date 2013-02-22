package connectfour.controller;

import connectfour.view.View;

public interface Controller {

	void addView(View view);
	
	void removeView(View view);
	
	void reset();
	
	void undo();
	
	void makeComputerMove();
	
	int getLastMoveResult();
	
	void setDeterministicAi(boolean deterministicAi);
	
	void setTimeLimitedAi(boolean timeLimited);
	
	void setAiDepthLimit(int depthLimit);
	
	void setAiTimeLimit(long timeLimit);
	
	void cancelAiMove();
	
	void makeHumanMove(int col);
	
}
