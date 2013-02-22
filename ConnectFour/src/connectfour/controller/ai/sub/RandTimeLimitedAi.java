package connectfour.controller.ai.sub;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import connectfour.controller.ai.ValCol;
import connectfour.controller.ai.abstrakt.AbstractTimeLimitedAi;
import connectfour.model.Model;

public class RandTimeLimitedAi extends AbstractTimeLimitedAi {

	public RandTimeLimitedAi(Model board) {
		super(board);
	}

	@Override
	protected int chooseMoveTimeLimited(int counter) {
		initialTime = System.currentTimeMillis();
		this.timeLimitedSearchDepth = 1;
		Integer col = 0;
		ValCol ans;
		while (!timeIsUp() && timeLimitedSearchDepth <= 42 && !isStopSignaled) {
			
			System.out.println("Choosing move time limited, non-det");
			ans = negaMaxWithRandomnessTimed/* DepthDiscounting */(0, counter,
					1);

			if (ans != null) {
				col = ans.getCol();
				System.out.println("Finished searching time limited depth:"
						+ timeLimitedSearchDepth);
				this.timeLimitedSearchDepth++;
			}
		}
		if (col == null) {
			List<Integer> cols = new ArrayList<Integer>();
			Random rand = new Random();
			for (int i=0;i<7;i++) {
				if (board.findDepth(i) > -1) {
					cols.add(i);
				}
			}
			col = cols.get(rand.nextInt(cols.size()));
		}
		return col;
	}

	private ValCol negaMaxWithRandomnessTimed(int depth, int counter, int sign) {
		if (timeIsUp() || isStopSignaled) {
			return null;
		}
		if (boardCopy.gameOver() || depth == timeLimitedSearchDepth) {
			int util = sign * boardCopy.getAnalysis(counter);
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
				ValCol vc = negaMaxWithRandomnessTimed(depth + 1, counter,
						-sign);
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
