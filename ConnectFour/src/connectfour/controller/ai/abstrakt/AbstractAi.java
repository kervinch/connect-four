package connectfour.controller.ai.abstrakt;

import connectfour.model.Model;

public abstract class AbstractAi {

	protected Model board;
	protected Model boardCopy;
	protected volatile boolean isStopSignaled = false;
	
	public AbstractAi(Model board) {
		this.board = board;
	}
	
	public void stop() {
		this.isStopSignaled = true;
		System.out.println("computer stop signaled");
	}
	
	public abstract void setLimit(Object limit);
	
	public abstract int move();
	
}
