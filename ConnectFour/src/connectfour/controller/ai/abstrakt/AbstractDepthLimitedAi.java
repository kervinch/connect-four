package connectfour.controller.ai.abstrakt;

import connectfour.model.Model;

public abstract class AbstractDepthLimitedAi extends AbstractLimitedAi {

	protected int depthLimit;// how many moves in the future to look - 0 at tree
								// root

	public AbstractDepthLimitedAi(Model board) {
		super(board);
	}

	@Override
	public void setLimit(Object limit) {
		this.depthLimit = (Integer) limit;
		System.out.println("new depthLimit=" + depthLimit);
	}

	@Override
	public int move() {
		isStopSignaled = false;
		boardCopy = board.deepCopy();
		Integer moveCol;
		int counter = 3 - board.getLastCounterPlaced();
		moveCol = chooseMoveDepthLimited(counter);

		if (moveCol == null) {
			return -1;// no move to make (aborted depth limited search) and thus
						// game not over
		}
		if (!board.placeCounter(moveCol, counter)) {
			throw new IllegalArgumentException(
					"Computer chose a full/invalid column");
		}
		if (board.gameOver()) {
			return board.getWinner();
		}
		return -1;// game not over
	}
	
	protected abstract Integer chooseMoveDepthLimited(int depthLimit);

}
