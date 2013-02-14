package connectfour.model;

import java.util.Stack;

public class GameBoard {

	private int[][] board;
	private int winner;//0 means a draw if game is over
	private int countersPlaced;
	private boolean isGameOver;
	private int lastCounterPlaced;
	private Stack<Move> moves;
	
	public int getLastCounterPlaced() {
		return lastCounterPlaced;
	}
	
	public GameBoard() {
		board = new int[6][7];
		winner = 0;
		countersPlaced = 0;
		isGameOver = false;
		lastCounterPlaced = 2;
		moves = new Stack<Move>();
		initializeBoard();
	}
	
	public GameBoard deepCopy() {
		GameBoard gb = new GameBoard();
		copyBoard(this.board, gb.board);
		gb.winner = this.winner;
		gb.countersPlaced = this.countersPlaced;
		gb.isGameOver = this.isGameOver;
		gb.lastCounterPlaced = this.lastCounterPlaced;
		copyMoveStack(this.moves, gb.moves);
		return gb;
	}
	
	private void copyBoard(int[][] source, int[][] destination) {
		for (int row=0;row < 6;row++) {
			for (int col=0;col < 7;col++) {
				destination[row][col] = source[row][col];
			}
		}
	}
	
	private void copyMoveStack(Stack<Move> source, Stack<Move> destination) {
		for (Move m : source) {
			destination.add(new Move(m.getRow(),m.getCol()));
		}
	}

	private void initializeBoard() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				board[i][j] = 0;
			}
		}
	}
	
	public void resetGameBoard() {
		winner = 0;
		countersPlaced = 0;
		isGameOver = false;
		lastCounterPlaced = 2;
		moves.removeAllElements();
		initializeBoard();
	}

	public int[][] getBoard() {
		return board;
	}

	//for testing
	public void setBoard(int[][] b) {
		this.board = b;
	}
	
	public int getWinner() {
		return winner;
	}

	public int getCountersPlaced() {
		return countersPlaced;
	}

	public void undoMove() {
		if (countersPlaced > 0) {
			Move m = moves.pop();
			board[m.getRow()][m.getCol()] = 0;
			winner = 0;
			countersPlaced--;
			isGameOver = false;
			lastCounterPlaced = 3 - lastCounterPlaced;
		}
	}
	
	public boolean placeCounter(int col, int counter) {

		if ((col < 0) || (col > 6)) {
			System.out.println("invalid column\n\n");
			return false;
		}
		else {
			int row = findDepth(col);
			if (row == -1) {
				System.out.println("column is full");
				return false;
			}
			else {
				board[row][col] = counter;
				lastCounterPlaced = counter;
				moves.push(new Move(row, col));
				countersPlaced++;
				if (checkWin(counter)) {
					winner = counter;
					isGameOver = true;
				} else if (countersPlaced == 42) {
					isGameOver = true;
				}
				return true;
			}
		}
	}

	public int findDepth(int col) {
		int depth = 0;
		while (depth < 6 && board[depth][col] == 0) {
			depth++;
		}
		depth--;
		return depth; // represents where the next counter should go (can be -1
					  // to 5)
	}

	public boolean gameOver() {
		return isGameOver;
	}

	private boolean checkWin(int counter) {
		
		int row = moves.peek().getRow();
		int col = moves.peek().getCol();
		
		int groupSize = countGroupSize(row + 1, col, Direction.S, counter);//VERT
		if (groupSize >= 3) {
			return true;
		}
		
		groupSize = countGroupSize(row, col + 1, Direction.E, counter) + //HORI
					countGroupSize(row, col - 1, Direction.W, counter);
		if (groupSize >= 3) {
			return true;
		}
		
		groupSize = countGroupSize(row - 1, col + 1, Direction.NE, counter) + //DIAG
				countGroupSize(row + 1, col - 1, Direction.SW, counter);
		if (groupSize >= 3) {
			return true;
		}
		
		groupSize = countGroupSize(row - 1, col - 1, Direction.NW, counter) + //ODIAG
				countGroupSize(row + 1, col + 1, Direction.SE, counter);
		if (groupSize >= 3) {
			return true;
		}

		return false;
	}

	public int countGroupSize(int row, int col, Direction dir,
			int counter) {

		if (row < 6 && row > -1 && col < 7 && col > -1
				&& board[row][col] == counter) {
			switch (dir) {
			case N:
				return 1 + countGroupSize(row - 1, col, dir, counter);
			case S:
				return 1 + countGroupSize(row + 1, col, dir, counter);
			case E:
				return 1 + countGroupSize(row, col + 1, dir, counter);
			case W:
				return 1 + countGroupSize(row, col - 1, dir, counter);
			case NE:
				return 1 + countGroupSize(row - 1, col + 1, dir, counter);
			case NW:
				return 1 + countGroupSize(row - 1, col - 1, dir, counter);
			case SE:
				return 1 + countGroupSize(row + 1, col + 1, dir, counter);
			case SW:
				return 1 + countGroupSize(row + 1, col - 1, dir, counter);
			default:
				return 0;
			}

		} else {
			return 0;
		}
	}
	
	//for visualization
	public String toString() {
		String str = "   0 1 2 3 4 5 6\n";
		for (int i = 0; i < 6; i++) {
			str += (new Integer(i)).toString() + ": ";
			for (int j = 0; j < 7; j++) {
				str += (new Integer(board[i][j])).toString();
				str += " ";
			}
			str += "\n";
		}
		return str;
	}
	
	public int getAnalysis(int counter) {
		
		int other = 3 - counter;
		int val = 0;
		int winningScore = 8888888;

		if (this.gameOver()) {
			if (this.getWinner() == counter) {
				return winningScore;
			}
			else if (this.getWinner() == other) {
				return -winningScore;
			}
			else {
				return 0;//neither has won = draw
			}
		}

		//GENERIC BOARD POSITION
		int valueGroup3 = 60;
		int valueGroup2 = 20;
		
		for (int r=0;r<6;r++) {
			for (int c=0;c<7;c++) {
		
				//values (60, 20) determine how much more a group of 3 is worth than a group of 2
				//right now 60*3 = 180 and 20*2 = 40 (double/triple-counting, since we iterate over every cell)
				//a group of 3 equals 180/40 = 4.5 groups of 2
				
				//FOR COUNTER
				if (board[r][c] == counter) {

					int groupSizeVert = countGroupSize(r + 1, c, Direction.S,
							counter) + // VERT
							countGroupSize(r - 1, c, Direction.N, counter);
					if (groupSizeVert == 2) {
						val += valueGroup3;
					} else if (groupSizeVert == 1) {
						val += valueGroup2;
					}

					int groupSizeHori = countGroupSize(r, c + 1, Direction.E,
							counter) + // HORI
							countGroupSize(r, c - 1, Direction.W, counter);
					if (groupSizeHori == 2) {
						val += valueGroup3;
					} else if (groupSizeHori == 1) {
						val += valueGroup2;
					}

					int groupSizeDiag = countGroupSize(r - 1, c + 1,
							Direction.NE, counter) + // DIAG
							countGroupSize(r + 1, c - 1, Direction.SW, counter);
					if (groupSizeDiag == 2) {
						val += valueGroup3;
					} else if (groupSizeDiag == 1) {
						val += valueGroup2;
					}

					int groupSizeOdiag = countGroupSize(r - 1, c - 1,
							Direction.NW, counter) + // ODIAG
							countGroupSize(r + 1, c + 1, Direction.SE, counter);
					if (groupSizeOdiag == 2) {
						val += valueGroup3;
					} else if (groupSizeOdiag == 1) {
						val += valueGroup2;
					}

				}
				//FOR OTHER
				else if (board[r][c] == other) {

					int groupSizeVert = countGroupSize(r + 1, c, Direction.S,
							other) + // VERT
							countGroupSize(r - 1, c, Direction.N, other);
					if (groupSizeVert == 2) {
						val -= valueGroup3;
					} else if (groupSizeVert == 1) {
						val -= valueGroup2;
					}

					int groupSizeHori = countGroupSize(r, c + 1, Direction.E,
							other) + // HORI
							countGroupSize(r, c - 1, Direction.W, other);
					if (groupSizeHori == 2) {
						val -= valueGroup3;
					} else if (groupSizeHori == 1) {
						val -= valueGroup2;
					}

					int groupSizeDiag = countGroupSize(r - 1, c + 1,
							Direction.NE, other) + // DIAG
							countGroupSize(r + 1, c - 1, Direction.SW, other);
					if (groupSizeDiag == 2) {
						val -= valueGroup3;
					} else if (groupSizeDiag == 1) {
						val -= valueGroup2;
					}

					int groupSizeOdiag = countGroupSize(r - 1, c - 1,
							Direction.NW, other) + // ODIAG
							countGroupSize(r + 1, c + 1, Direction.SE, other);
					if (groupSizeOdiag == 2) {
						val -= valueGroup3;
					} else if (groupSizeOdiag == 1) {
						val -= valueGroup2;
					}
				}
			}
		}
		
		return val;
	}

}
