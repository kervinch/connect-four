package connectfour.model;

import connectfour.view.View;

public interface Model {

	int getLastCounterPlaced();
	
	void addView(View view);
	
	void removeView(View view);
	
	Model deepCopy();
	
	void resetGameBoard();

	int[][] getBoard();
	
	int getWinner();
	
	int getNumCountersPlaced();

	void undoMove();
	
	boolean placeCounter(int col, int counter);
	
	int findDepth(int col);
	
	boolean gameOver();
	
	int getAnalysis(int counter);
	
}
