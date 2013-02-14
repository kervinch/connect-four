package connectfour.controller.ai;

// represents the value of a move and the column the move was made in for an AI
public class ValCol {
	private int val;
	private int col;

	public ValCol(int val, int col) {
		this.val = val;
		this.col = col;
	}

	public int getVal() {
		return val;
	}

	public int getCol() {
		return col;
	}
}

