package connectfour.controller.ai.abstr;

import connectfour.model.GameBoard;

public abstract class AbstractTimeLimitedAi extends AbstractAi {

	private long timeLimit;
	protected long initialTime;
	protected int timeLimitedSearchDepth;// depth of a time limited move search
	
	public AbstractTimeLimitedAi(GameBoard board) {
		super(board);
	}
	
	@Override
	public void setLimit(Object limit) {
		this.timeLimit = (Long) limit;
		System.out.println("new timeLimit=" + timeLimit);
	}
	
	@Override
	public int move() {
		isStopSignaled = false;
		boardCopy = board.deepCopy();
		Integer moveCol;
		int counter = 3 - board.getLastCounterPlaced();
		moveCol = chooseMoveTimeLimited(counter);
		if (!board.placeCounter(moveCol, counter)) {
			throw new IllegalArgumentException(
					"Computer chose a full/invalid column");
		}
		if (board.gameOver()) {
			return board.getWinner();
		}
		return -1;// game not over
	}
	
	protected boolean timeIsUp() {
		return ((System.currentTimeMillis() - initialTime) >= timeLimit);
	}
	
	protected abstract int chooseMoveTimeLimited(int counter);

}
