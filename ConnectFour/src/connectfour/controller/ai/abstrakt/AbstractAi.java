package connectfour.controller.ai.abstrakt;

import connectfour.controller.ai.Ai;
import connectfour.model.Model;

public abstract class AbstractAi implements Ai {

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

	public abstract int move();
	
}
