package connectfour.ai;

import java.util.Random;

import connectfour.game.GameBoard;
import connectfour.game.Player;

public class Computer implements Player {

	private int searchDepth;// how many moves in the future to look - 0 at tree root
	
	private boolean deterministicAI;// whether the AI picks randomly among equally good moves
									// or always picks the first one
	private long timeLimit;
	private long initialTime;
	private GameBoard board;
	private GameBoard boardCopy;
	private double discountFactor = 0.95;// TODO - pass in a const
	private boolean timeLimited = true;// false indicates depth limited
	private int timeLimitedSearchDepth;// depth of a time limited move search

	public Computer(GameBoard board, boolean deterministicAI, long timeLimit, int searchDepth) {
		this.board = board;
		this.deterministicAI = deterministicAI;
		this.timeLimit = timeLimit;
		this.searchDepth = searchDepth;
	}
	
	public void setDeterministicAI(boolean deterministicAI) {
		this.deterministicAI = deterministicAI;
	}
	
	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
		System.out.println("timeLimit changed to=" + timeLimit);
	}
	
	public void setSearchDepth(int searchDepth) {
		this.searchDepth = searchDepth;
		System.out.println("depthLimit changed to=" + searchDepth);
	}
	
	public void setTimeLimited(boolean timeLimited) {
		this.timeLimited = timeLimited;
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
		boardCopy = board.deepCopy();
		if (timeLimited) {
			return chooseMoveTimeLimited(counter);
		}
		else {
			return chooseMoveDepthLimited(counter);
		}
	}
	
	private int chooseMoveDepthLimited(int counter) {
		if (deterministicAI) {
			System.out.println("Choosing move depth limited, det, depthLimit=" + searchDepth);
			int offset = 10;
			return negaMaxWithABPruning/*DepthDiscounting*/(0, counter, 1, Integer.MIN_VALUE + offset,
					Integer.MAX_VALUE - offset).col;
		}
		else {
			System.out.println("Choosing move depth limited, non-det, depthLimit=" + searchDepth);
			return negaMaxWithRandomness/*DepthDiscounting*/(0, counter, 1).col;
		}
	}
	
	// TODO - timeLimit should be the upper limit -> don't deepen if move is obvious
	// i.e. simple win/loss
	// also don't deepen if the game is over far before that depth
	// e.g. : game is over in 42 moves, hence depth > 42 is useless
	// try to find a tighter bound - i.e. depth past which all branches game is over
	// time limited AI with Iterative Deepening
	private int chooseMoveTimeLimited(int counter) {
		initialTime = System.currentTimeMillis();
		this.timeLimitedSearchDepth = 0;
		int col = 0;
		Pair ans;
		while (!timeIsUp() && timeLimitedSearchDepth <= 42) {
			this.timeLimitedSearchDepth++;
			if (deterministicAI) {
				System.out.println("Choosing move time limited, det, timeLimit=" + timeLimit);
				int offset = 10;
				ans = negaMaxWithABPruningTimed/*DepthDiscounting*/(0, counter, 1,
						Integer.MIN_VALUE + offset, Integer.MAX_VALUE - offset);
			} else {
				System.out.println("Choosing move time limited, non-det, timeLimit=" + timeLimit);
				ans = negaMaxWithRandomnessTimed/*DepthDiscounting*/(0, counter,
						1);
			}
			if (ans != null) {
				col = ans.col;
				System.out.println("Finished searching time limited depth:" + timeLimitedSearchDepth);
			}
		}
		return col;
	}
	
	private boolean timeIsUp() {
		return ((System.currentTimeMillis() - initialTime) >= timeLimit);
	}

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
		if (boardCopy.gameOver() || depth == searchDepth) {
			int util = sign * boardCopy.getAnalysis(counter);
			return new Pair(util, -1);// col doesn't matter since search depth
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
				Pair p = negaMax(depth + 1, counter, -sign);
				int x = -p.val;
				boardCopy.undoMove();

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
		if (boardCopy.gameOver() || depth == searchDepth) {
			int util = sign * boardCopy.getAnalysis(counter);
			return new Pair(util, -1);// col doesn't matter since search depth
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
				Pair p = negaMaxWithABPruning(depth + 1, counter, -sign, -beta,
						-alpha);
				int x = -p.val;
				boardCopy.undoMove();
		
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
		if (boardCopy.gameOver() || depth == searchDepth) {
			int util = sign * boardCopy.getAnalysis(counter);
			return new Pair(util, -1);// col doesn't matter since search depth
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
				Pair p = negaMaxWithRandomness(depth + 1, counter, -sign);
				int x = -p.val;
				boardCopy.undoMove();

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
	
	// added time limit 
	private Pair negaMaxWithABPruningTimed(int depth, int counter, int sign,
			int alpha, int beta) {
		if (timeIsUp()) {
			return null;
		}
		if (boardCopy.gameOver() || depth == timeLimitedSearchDepth) {
			int util = sign * boardCopy.getAnalysis(counter);
			return new Pair(util, -1);// col doesn't matter since search depth
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
				Pair p = negaMaxWithABPruningTimed(depth + 1, counter, -sign, -beta,
						-alpha);
				boardCopy.undoMove();
				if (p == null) {
					return null;
				}
				int x = -p.val;
		
				if (x > alpha) {
					alpha = x;
					col = i;
				}
			}
			i++;
		}
		return new Pair(alpha, col);
	}
	
	// added time limit
	private Pair negaMaxWithRandomnessTimed(int depth, int counter, int sign) {
		if (timeIsUp()) {
			return null;
		}
		if (boardCopy.gameOver() || depth == timeLimitedSearchDepth) {
			int util = sign * boardCopy.getAnalysis(counter);
			return new Pair(util, -1);// col doesn't matter since search depth
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
				Pair p = negaMaxWithRandomnessTimed(depth + 1, counter, -sign);
				boardCopy.undoMove();
				if (p == null) {
					return null;
				}
				int x = -p.val;

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
		if (boardCopy.gameOver() || depth == searchDepth) {
			int util = (int) ((Math.pow(discountFactor, depth)) * sign * boardCopy.getAnalysis(counter));
			return new Pair(util, -1);// col doesn't matter since search depth
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
				Pair p = negaMaxWithABPruningDepthDiscounting(depth + 1, counter, -sign, -beta,
						-alpha);
				int x = -p.val;
				boardCopy.undoMove();

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
