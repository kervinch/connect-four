package connectfour.controller.ai.sub;

import connectfour.controller.ai.ValCol;
import connectfour.controller.ai.abstrakt.AbstractTimeLimitedAi;
import connectfour.model.GameBoard;

public class DetTimeLimitedAi extends AbstractTimeLimitedAi {

	public DetTimeLimitedAi(GameBoard board) {
		super(board);
	}

	@Override
	protected int chooseMoveTimeLimited(int counter) {
		initialTime = System.currentTimeMillis();
		this.timeLimitedSearchDepth = 0;
		int col = 0;
		ValCol ans;
		while (!timeIsUp() && timeLimitedSearchDepth <= 42 && !isStopSignaled) {
			this.timeLimitedSearchDepth++;

			System.out.println("Choosing move time limited, det");
			int offset = 10;
			ans = negaMaxWithABPruningTimed/* DepthDiscounting */(0, counter,
					1, Integer.MIN_VALUE + offset, Integer.MAX_VALUE - offset);

			if (ans != null) {
				col = ans.getCol();
				System.out.println("Finished searching time limited depth:"
						+ timeLimitedSearchDepth);
			}
		}
		return col;
	}

	private ValCol negaMaxWithABPruningTimed(int depth, int counter, int sign,
			int alpha, int beta) {
		if (timeIsUp() || isStopSignaled) {
			return null;
		}
		if (boardCopy.gameOver() || depth == timeLimitedSearchDepth) {
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
				ValCol vc = negaMaxWithABPruningTimed(depth + 1, counter, -sign, -beta,
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
