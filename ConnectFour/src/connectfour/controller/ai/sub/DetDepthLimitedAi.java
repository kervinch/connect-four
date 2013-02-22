package connectfour.controller.ai.sub;

import connectfour.controller.ai.ValCol;
import connectfour.controller.ai.abstrakt.AbstractDepthLimitedAi;
import connectfour.model.Model;

public class DetDepthLimitedAi extends AbstractDepthLimitedAi {

	public DetDepthLimitedAi(Model board) {
		super(board);
	}

	@Override
	protected Integer chooseMoveDepthLimited(int counter) {
		ValCol result;

		System.out.println("Choosing move depth limited, det");
		int offset = 10;
		result = negaMaxWithABPruning/* DepthDiscounting */(0, counter, 1,
				Integer.MIN_VALUE + offset, Integer.MAX_VALUE - offset);

		return (result == null ? null : result.getCol());
	}
	
	private ValCol negaMaxWithABPruning(int depth, int counter, int sign,
			int alpha, int beta) {
		if (isStopSignaled) {
			return null;
		}
		if (boardCopy.gameOver() || depth == depthLimit) {
			int util = sign * boardCopy.getAnalysis(counter);
			return new ValCol(util, -1);// col doesn't matter since search depth
										// will never be 0
		}

		int col = 0;
		int i = 0;

		while (i < 7 && alpha < beta) {
			if (boardCopy.findDepth(i) > -1) {
				if (sign == 1) {
					boardCopy.placeCounter(i, counter);
				} else {
					boardCopy.placeCounter(i, 3 - counter);
				}
				ValCol vc = negaMaxWithABPruning(depth + 1, counter, -sign, -beta,
						-alpha);
				if (vc == null) {
					return null;
				}
				boardCopy.undoMove();
				int x = -vc.getVal();
		
				if (x > alpha) {
					alpha = x;
					col = i;
				}
			}
			i++;
		}
		return new ValCol(alpha, col);
	}

}
