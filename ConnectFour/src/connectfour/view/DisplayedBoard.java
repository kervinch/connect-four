package connectfour.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

import connectfour.model.Model;

public class DisplayedBoard extends JPanel {

	private static final long serialVersionUID = 1L;
	private Model board;
	private final double COUNTER_SIZE_FRACTION = 0.8;
	private final Color COLOR1 = new Color(255,0,0);//RED
	private final Color COLOR2 = new Color(255,255,0);//YELLOW
	
	public DisplayedBoard(Model board) {
		super();
		this.board = board;	
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		changeDisplayedBoard();
	}

	@Override
	public void repaint() {
		super.repaint();
		changeDisplayedBoard();
	}	
	
	private void changeDisplayedBoard() {

		if (board != null) {
			Rectangle rect = this.getBounds();
			int x_cell = rect.width / 7;
			int y_cell = rect.height / 6;
			int x_counter = (int) (x_cell * (COUNTER_SIZE_FRACTION));
			int y_counter = (int) (y_cell * (COUNTER_SIZE_FRACTION));

			Counter.setX_counter(x_counter);
			Counter.setY_counter(y_counter);

			int x_offset = ((x_cell - x_counter) / 2) + 2;
			int y_offset = (y_cell - y_counter) / 2;
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 7; j++) {
					if (board.getBoard()[i][j] == 1)
						Counter.draw(this.getGraphics(), x_offset
								+ (j * x_cell), y_offset + (i * y_cell),
								COLOR1);
					else if (board.getBoard()[i][j] == 2)
						Counter.draw(this.getGraphics(), x_offset
								+ (j * x_cell), y_offset + (i * y_cell),
								COLOR2);
					// counter (1/2) is COLOR(1/2)
				}

			}
		}
	}
	
}
