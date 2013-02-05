package connectfour.ai;

import java.util.Random;

import connectfour.game.GameBoard;
import connectfour.game.Player;

public class Computer implements Player {

	private int searchDepth;// how many moves in the future to look - 0 at tree
							// root - TODO - make it a time constraint
	private boolean deterministicAI;// whether the AI picks randomly between equally good moves
									// or always picks the first one
	private long timeLimit;
	private GameBoard board;
	private double discountFactor = 0.95;// TODO - pass in a const

	public Computer(GameBoard board, int searchDepth, boolean deterministicAI) {
		this.board = board;
		this.searchDepth = searchDepth;
		this.deterministicAI = deterministicAI;
	}
	
	public void setDeterministicAI(boolean deterministicAI) {
		this.deterministicAI = deterministicAI;
	}
	
	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	@Override
	public int move(int col, int counter) {
		if (!board.placeCounter(col, counter)) {
			throw new IllegalArgumentException(
					"Computer chose a full/invalid column");
		}
		if (board.gameOver()) {
			return board.getWinner();
		}
		return -1;// game not over
	}

	public int chooseMove(int counter) {
		if (deterministicAI) {
			int offset = 10;
			return negaMaxWithABPruning/*DepthDiscounting*/(0, counter, 1, Integer.MIN_VALUE + offset,
					Integer.MAX_VALUE - offset).col;
		}
		else {
			return negaMaxWithRandomness/*DepthDiscounting*/(0, counter, 1).col;
		}
	}
	
	// too slow - ideally should cut off in the middle of a negaMax for a given depth
	// and use the col value obtained from the previous iteration
	/*
	public int chooseMoveIterativeDeepening(int counter) {
		long initialTime = System.currentTimeMillis();
		this.searchDepth = 0;
		int col = 0;
		while (System.currentTimeMillis() - initialTime < timeLimit) {
			this.searchDepth++;
			if (deterministicAI) {
				int offset = 10;
				col = negaMaxWithABPruning/*DepthDiscounting*//*(0, counter, 1,
						Integer.MIN_VALUE + offset, Integer.MAX_VALUE - offset).col;
			} else {
				col = negaMaxWithRandomness/*DepthDiscounting*//*(0, counter,
						1).col;
			}
		}
		return col;
	}
	*/

	public static class Pair {
		private int val;
		private int col;// make Integer? TODO - think

		public Pair(int val, int col) {
			this.val = val;
			this.col = col;
		}
	}

	// negaMax changes the board and undoes every move
	private Pair negaMax(int depth, int counter, int sign) {
		if (board.gameOver() || depth == searchDepth) {
			int util = sign * board.getAnalysis(counter);
			return new Pair(util, -1);// col doesn't matter since search depth
										// will never be 0
		} 

		int max = Integer.MIN_VALUE;
		int col = 0;

		for (int i = 0; i < 7; i++) {
			if (board.findDepth(i) > -1) {
				if (sign == 1) {
					board.placeCounter(i, counter);
				} else {
					board.placeCounter(i, 3 - counter);
				}
				Pair p = negaMax(depth + 1, counter, -sign);
				int x = -p.val;
				board.undoMove();

				if (x > max) {
					max = x;
					col = i;
				}
			}
		}
		return new Pair(max, col);
	}

	// improved with alpha beta pruning
	private Pair negaMaxWithABPruning(int depth, int counter, int sign,
			int alpha, int beta) {
		if (board.gameOver() || depth == searchDepth) {
			int util = sign * board.getAnalysis(counter);
			return new Pair(util, -1);// col doesn't matter since search depth
										// will never be 0
		}

		int col = 0;
		int i = 0;

		while (i < 7 && alpha < beta) {
			if (board.findDepth(i) > -1) {
				if (sign == 1) {
					board.placeCounter(i, counter);
				} else {
					board.placeCounter(i, 3 - counter);
				}
				Pair p = negaMaxWithABPruning(depth + 1, counter, -sign, -beta,
						-alpha);
				int x = -p.val;
				board.undoMove();
		
				if (x > alpha) {
					alpha = x;
					col = i;
				}
			}
			i++;
		}
		return new Pair(alpha, col);
	}
	
	// added randomness
	private Pair negaMaxWithRandomness(int depth, int counter, int sign) {
		if (board.gameOver() || depth == searchDepth) {
			int util = sign * board.getAnalysis(counter);
			return new Pair(util, -1);// col doesn't matter since search depth
										// will never be 0
		} 

		int max = Integer.MIN_VALUE;
		int col = 0;

		for (int i = 0; i < 7; i++) {
			if (board.findDepth(i) > -1) {
				if (sign == 1) {
					board.placeCounter(i, counter);
				} else {
					board.placeCounter(i, 3 - counter);
				}
				Pair p = negaMaxWithRandomness(depth + 1, counter, -sign);
				int x = -p.val;
				board.undoMove();

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
		return new Pair(max, col);
	}
		
	// improved with depth discounting - TODO - test/think/compare
	private Pair negaMaxWithABPruningDepthDiscounting(int depth, int counter, int sign,
			int alpha, int beta) {
		if (board.gameOver() || depth == searchDepth) {
			int util = (int) ((Math.pow(discountFactor, depth)) * sign * board.getAnalysis(counter));
			return new Pair(util, -1);// col doesn't matter since search depth
										// will never be 0
		}

		int col = 0;
		int i = 0;

		while (i < 7 && alpha < beta) {
			if (board.findDepth(i) > -1) {
				if (sign == 1) {
					board.placeCounter(i, counter);
				} else {
					board.placeCounter(i, 3 - counter);
				}
				Pair p = negaMaxWithABPruningDepthDiscounting(depth + 1, counter, -sign, -beta,
						-alpha);
				int x = -p.val;
				board.undoMove();

				if (x > alpha) {
					alpha = x;
					col = i;
				}
			}
			i++;
		}
		return new Pair(alpha, col);
	}
	
}
