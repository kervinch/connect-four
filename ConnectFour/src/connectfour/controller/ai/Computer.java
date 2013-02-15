package connectfour.controller.ai;

import java.util.Random;

import connectfour.model.GameBoard;

public class Computer {

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
	
	private volatile boolean isStopSignaled = false;

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
	
	public void stop() {
		this.isStopSignaled = true;
		System.out.println("computer stop signaled");
	}

	public int move() {
		isStopSignaled = false;
		boardCopy = board.deepCopy();
		Integer moveCol;
		int counter = 3 - board.getLastCounterPlaced();
		if (timeLimited) {
			moveCol = chooseMoveTimeLimited(counter);
		}
		else {
			moveCol = chooseMoveDepthLimited(counter);
		}
		
		if (moveCol == null) {
			return -1;// no move to make (aborted depth limited search) and thus game not over
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

	private Integer chooseMoveDepthLimited(int counter) {
		ValCol result;
		if (deterministicAI) {
			System.out.println("Choosing move depth limited, det, depthLimit=" + searchDepth);
			int offset = 10;
			result = negaMaxWithABPruning/*DepthDiscounting*/(0, counter, 1, Integer.MIN_VALUE + offset,
					Integer.MAX_VALUE - offset);
		}
		else {
			System.out.println("Choosing move depth limited, non-det, depthLimit=" + searchDepth);
			result = negaMaxWithRandomness/*DepthDiscounting*/(0, counter, 1);
		}
		return (result == null ? null : result.getCol());
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
		ValCol ans;
		while (!timeIsUp() && timeLimitedSearchDepth <= 42 && !isStopSignaled) {
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
				col = ans.getCol();
				System.out.println("Finished searching time limited depth:" + timeLimitedSearchDepth);
			}
		}
		return col;
	}
	
	private boolean timeIsUp() {
		return ((System.currentTimeMillis() - initialTime) >= timeLimit);
	}

	// negaMax changes the board and undoes every move
	private ValCol negaMax(int depth, int counter, int sign) {
		if (boardCopy.gameOver() || depth == searchDepth) {
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
				ValCol vc = negaMax(depth + 1, counter, -sign);
				int x = -vc.getVal();
				boardCopy.undoMove();

				if (x > max) {
					max = x;
					col = i;
				}
			}
		}
		return new ValCol(max, col);
	}

	// improved with alpha beta pruning
	private ValCol negaMaxWithABPruning(int depth, int counter, int sign,
			int alpha, int beta) {
		if (isStopSignaled) {
			return null;
		}
		if (boardCopy.gameOver() || depth == searchDepth) {
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
	
	// added randomness
	private ValCol negaMaxWithRandomness(int depth, int counter, int sign) {
		if (isStopSignaled) {
			return null;
		}
		if (boardCopy.gameOver() || depth == searchDepth) {
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
	
	// added time limit 
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
	
	// added time limit
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
				ValCol vc = negaMaxWithRandomnessTimed(depth + 1, counter, -sign);
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
		
	// improved with depth discounting - TODO - test/think/compare
	private ValCol negaMaxWithABPruningDepthDiscounting(int depth, int counter, int sign,
			int alpha, int beta) {
		if (boardCopy.gameOver() || depth == searchDepth) {
			int util = (int) ((Math.pow(discountFactor, depth)) * sign * boardCopy.getAnalysis(counter));
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
				ValCol vc = negaMaxWithABPruningDepthDiscounting(depth + 1, counter, -sign, -beta,
						-alpha);
				int x = -vc.getVal();
				boardCopy.undoMove();

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
