package connectfour.controller.ai.abstr;

import connectfour.model.GameBoard;

public abstract class AbstractAi {

	protected GameBoard board;
	protected GameBoard boardCopy;
	protected volatile boolean isStopSignaled = false;
	
	public AbstractAi(GameBoard board) {
		this.board = board;
	}
	
	public void stop() {
		this.isStopSignaled = true;
		System.out.println("computer stop signaled");
	}
	
	public abstract void setLimit(Object limit);
	
	public abstract int move();
	
}
