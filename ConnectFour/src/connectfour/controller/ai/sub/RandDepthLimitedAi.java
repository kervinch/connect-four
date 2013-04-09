package connectfour.controller.ai.sub;

import java.util.Random;

import connectfour.controller.ai.ValCol;
import connectfour.controller.ai.abstrakt.AbstractDepthLimitedAi;
import connectfour.model.Model;

public class RandDepthLimitedAi extends AbstractDepthLimitedAi {

	public RandDepthLimitedAi(Model board) {
		super(board);
	}

	@Override
	protected Integer chooseMoveDepthLimited(int counter) {
		ValCol result;

		System.out.println("Choosing move depth limited, non-det");
		result = negaMaxWithRandomness/* DepthDiscounting */(0, counter, 1);

		return (result == null ? null : result.getCol());
	}
	
	private ValCol negaMaxWithRandomness(int depth, int counter, int sign) {
		if (isStopSignaled) {
			return null;
		}
		if (boardCopy.gameOver() || depth == depthLimit) {
			int util = (int) (Math.pow(discountFactor, depth) * sign * boardCopy.getAnalysis(counter));
			return new ValCol(util, -1);// col doesn't matter since search depth
										// will never be 0
		} 

		int max = Integer.MIN_VALUE;
		int col = 0;

		for (int i = 0; i < 7; i++) {
			if (boardCopy.findDepth(i) > -1) {
				if (sign == 1) {
					boardCopy.placeCounter(i, counter);
				} else {
					boardCopy.placeCounter(i, 3 - counter);
				}
				ValCol vc = negaMaxWithRandomness(depth + 1, counter, -sign);
				if (vc == null) {
					return null;
				}
				boardCopy.undoMove();
				int x = -vc.getVal();

				// adds randomness
				if (depth == 0 && x == max) {
					Random rand = new Random();
					boolean replace = rand.nextBoolean();
					if (replace) {
						col = i;
					}
				}
				// end
				
				if (x > max) {
					max = x;
					col = i;
				}
			}
		}
		return new ValCol(max, col);
	}

}
